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
import java.util.List;



public class NewsWallGsonHelper {
    public static final String EXCEPTION_PARSING_API_RESPONSE = "Sorry, can not read posts from API";
    private NewsWall newsWall;
    private final NewsWallProvider newsWallProvider;

    private NewsWallGsonHelper(@Nullable final NewsWall newsWall, final NewsWallProvider newsWallProvider) {
        this.newsWall = newsWall;
        this.newsWallProvider = newsWallProvider;
    }

    static NewsWallGsonHelper createInstance(final NewsWall newsWall, final NewsWallProvider newsWallProvider){
        return new NewsWallGsonHelper(newsWall, newsWallProvider);
    }

    void setFromHttp(final HttpResponse httpResponse){
        try{
            Gson gson = new Gson().newBuilder().registerTypeAdapter(Date.class, new DateGsonAdapter()).create();
            JsonObject httpObj = gson.fromJson(((HttpResponse)httpResponse).getResponseAsString(), JsonObject.class);
            JsonObject response = httpObj.getAsJsonObject("response");
            JsonArray items = response.getAsJsonArray("items");

            List<NewsPost> posts = gson.fromJson(items, new TypeToken<ArrayList<NewsPost>>() {}.getType());

            if(posts == null) {
                throw new CallbackExceptionFactory.Companion.HttpException(EXCEPTION_PARSING_API_RESPONSE);
            }

            HashMap<Integer, DataIdPhotourl> mapIdsPhotos = getIdAvatarPhotoMap(response, gson);

            for(NewsPost post : posts){
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
                        post.setPostPhotoUrl(photo.sizes.get(0).url);
                    }
                } else if(copyHistory!= null && copyHistory.size() > 0) {
                    post.setPostText(copyHistory.get(0).text);
                    List<NewsPost.Attachment> copyAttachments = copyHistory.get(0).attachments;
                    if(copyAttachments != null && copyAttachments.size() > 0){
                        NewsPost.AttachPhoto copyPhoto = copyAttachments.get(0).photo;
                        if(copyPhoto != null) {
                            post.setPostPhotoUrl(copyPhoto.sizes.get(0).url);
                            Log.d("WWW copyHistory", " setPostPhotoUrl" + copyPhoto.sizes.get(0).url);
                        }
                    }
                }
            }

            String  nextFrom = "";
            JsonPrimitive element = response.getAsJsonPrimitive("next_from");

            if(element != null) {
                nextFrom = element.getAsString();
            }

            newsWallProvider.setNextFromChainRequest(nextFrom);
            newsWall.appendPosts(posts);
        } catch (Exception ex){
            ex.printStackTrace();
        }
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
