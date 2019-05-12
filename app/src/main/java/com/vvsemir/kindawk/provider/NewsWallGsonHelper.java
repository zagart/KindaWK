package com.vvsemir.kindawk.provider;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import com.vvsemir.kindawk.http.HttpResponse;
import com.vvsemir.kindawk.service.CallbackExceptionFactory;
import com.vvsemir.kindawk.service.ProviderService;
import com.vvsemir.kindawk.utils.Utilits;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;



public class NewsWallGsonHelper {
    public static final String EXCEPTION_PARSING_API_RESPONSE = "Sorry, can not read posts from API";
    public static final int MAX_PHOTO_WIDTH = 400;
    public static final int MIN_PHOTO_WIDTH = 300;
    private final NewsWallProvider newsWallProvider;

    private NewsWallGsonHelper(final NewsWallProvider newsWallProvider) {
        this.newsWallProvider = newsWallProvider;
    }

    static NewsWallGsonHelper createInstance(final NewsWallProvider newsWallProvider){
        return new NewsWallGsonHelper(newsWallProvider);
    }

    List<NewsPost> getPostsFromHttp(final HttpResponse httpResponse){
        List<NewsPost> posts = null;

        try{
            Gson gson = new Gson().newBuilder().registerTypeAdapter(Date.class, new DateGsonAdapter()).create();
            JsonObject httpObj = gson.fromJson(((HttpResponse)httpResponse).getResponseAsString(), JsonObject.class);
            JsonObject response = httpObj.getAsJsonObject("response");
            JsonArray items = response.getAsJsonArray("items");

            posts = gson.fromJson(items, new TypeToken<ArrayList<NewsPost>>() {}.getType());

            if(posts == null) {
                throw new CallbackExceptionFactory.Companion.HttpException(EXCEPTION_PARSING_API_RESPONSE);
            }

            HashMap<Integer, DataIdPhotourl> mapIdsPhotos = getIdAvatarPhotoMap(response, gson);

            for (Iterator<NewsPost> iterator = posts.iterator(); iterator.hasNext();) {
                NewsPost post = iterator.next();
                int sourceId = post.getSourceId();

                if(mapIdsPhotos.containsKey(sourceId)){
                    post.setSourcePhotoUrl(mapIdsPhotos.get(sourceId).photo_100);
                    post.setSourceName(mapIdsPhotos.get(sourceId).name);
                }

                List<NewsPost.Attachment> attachments = post.getAttachments();
                List<NewsPost.CopyPost> copyHistory = post.getCopyHistory();

                if(attachments != null && attachments.size() > 0){
                    NewsPost.AttachPhoto photo = attachments.get(0).photo;

                    if(photo != null) {
                        int index = getPhotoIdxByWidth(photo.sizes);

                        if (index >= 0) {
                            post.setPostPhotoUrl(photo.sizes.get(index).url);
                        }
                    }
                } else if(copyHistory!= null && copyHistory.size() > 0) {
                    post.setPostText(copyHistory.get(0).text);
                    List<NewsPost.Attachment> copyAttachments = copyHistory.get(0).attachments;

                    if(copyAttachments != null && copyAttachments.size() > 0){
                        NewsPost.AttachPhoto copyPhoto = copyAttachments.get(0).photo;

                        if(copyPhoto != null) {
                            int index = getPhotoIdxByWidth(copyPhoto.sizes);

                            if (index >= 0) {
                                post.setPostPhotoUrl(copyPhoto.sizes.get(index).url);
                            }
                        }
                    }
                }

                if(post.getPostText() == null || post.getPostText().isEmpty() ||
                        post.getPostPhotoUrl() == null || post.getPostPhotoUrl().isEmpty()){
                    iterator.remove();
                }
            }

            String  nextFrom = "";
            JsonPrimitive element = response.getAsJsonPrimitive("next_from");

            if(element != null) {
                nextFrom = element.getAsString();
            }

            newsWallProvider.setNextFromChainRequest(nextFrom);

        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            return posts;
        }
    }

    private int getPhotoIdxByWidth(List<NewsPost.AttachPhotoSizes> list){
        if(list != null && list.size() > 0){
            for(int i = list.size() - 1; i >= 0; i --){
                if(list.get(i).width <= MAX_PHOTO_WIDTH && list.get(i).width >= MIN_PHOTO_WIDTH){
                    return i;
                }
            }

            return list.size() - 1;
        }

        return -1;
    }

    class DataProfileIdPhotourl{
        int id;
        String photo_100;
        String first_name;
        String last_name;
    }

    class DataIdPhotourl{
        public DataIdPhotourl(int id, String photo, String name) {
            this.id = id;
            this.photo_100 = photo;
            this.name = name;
        }
        int id;
        String photo_100;
        String name;
    }


    HashMap<Integer, DataIdPhotourl> getIdAvatarPhotoMap(JsonObject response, Gson gson) {
        HashMap<Integer, DataIdPhotourl> mapIdsPhotos = new HashMap<>();

        JsonArray profiles = response.getAsJsonArray("profiles");
        DataProfileIdPhotourl[] profilesPhotos = gson.fromJson(profiles, DataProfileIdPhotourl[].class);
        JsonArray groups = response.getAsJsonArray("groups");
        DataIdPhotourl[] groupsPhotos = gson.fromJson(groups, DataIdPhotourl[].class);

        for(DataIdPhotourl item : groupsPhotos){
            item.id *= -1;
        }

        for(DataProfileIdPhotourl item : profilesPhotos){
            DataIdPhotourl it = new DataIdPhotourl(item.id, item.photo_100, item.first_name + " " + item.last_name);
            groupsPhotos = Utilits.appendToArray(groupsPhotos, it);
        }

        for(DataIdPhotourl item : groupsPhotos){
            mapIdsPhotos.put(item.id, item);
        }

        return mapIdsPhotos;
    }
}
