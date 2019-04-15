package com.vvsemir.kindawk.provider;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.vvsemir.kindawk.http.HttpResponse;
import com.vvsemir.kindawk.utils.Utilits;

import static com.vvsemir.kindawk.provider.NewsPost.makeMapFromDataIdPhotoList;

public class NewsWall implements IRecyclerListData<NewsPost>, Parcelable {
    private final List<NewsPost> news = new ArrayList<>();

    public NewsWall() {
    }

    public NewsWall( final List<NewsPost> copyNews) {
        news.clear();
        this.news.addAll(0, copyNews);
    }

    private NewsWall(Parcel in) {
        in.readTypedList(news, NewsPost.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(news);
    }


    @Override
    public NewsPost getItem(int index) {
        return news.get(index);
    }

    @Override
    public int getCount() {
        return news.size();
    }

    void setFromHttp(final HttpResponse httpResponse){
        try{
            Gson gson = new Gson().newBuilder().registerTypeAdapter(Date.class, new DateGsonAdapter()).create();

            JsonObject httpObj = gson.fromJson(((HttpResponse)httpResponse).getResponseAsString(), JsonObject.class);
            JsonObject response = httpObj.getAsJsonObject("response");
            JsonArray items = response.getAsJsonArray("items");

            List<NewsPost> posts = gson.fromJson(items, new TypeToken<ArrayList<NewsPost>>() {}.getType());
            if(posts != null) {
                news.addAll(0, posts);
            }


            HashMap<Integer, NewsPost.DataIdPhotourl> mapIdsPhotos = new HashMap<>();

            JsonArray profiles = response.getAsJsonArray("profiles");
            NewsPost.DataProfileIdPhotourl[] profilesPhotos = gson.fromJson(profiles, NewsPost.DataProfileIdPhotourl[].class);
            JsonArray groups = response.getAsJsonArray("groups");
            NewsPost.DataIdPhotourl[] groupsPhotos = gson.fromJson(groups, NewsPost.DataIdPhotourl[].class);

            for(NewsPost.DataIdPhotourl item : groupsPhotos){
                item.id *= -1;
            }

            for(NewsPost.DataProfileIdPhotourl item : profilesPhotos){
                NewsPost.DataIdPhotourl it = new NewsPost.DataIdPhotourl(item.id, item.photo_100, item.first_name + " " + item.last_name);
                groupsPhotos = Utilits.appendToArray(groupsPhotos, it);
            }

            makeMapFromDataIdPhotoList(groupsPhotos, mapIdsPhotos);

            for(NewsPost post : posts){
                int sourceId = post.getSourceId();

                if(mapIdsPhotos.containsKey(sourceId)){
                    post.setSourcePhotoUrl(mapIdsPhotos.get(sourceId).photo_100);
                    post.setSourceName(mapIdsPhotos.get(sourceId).name);
                }

                List<NewsPost.Attachment> attachments = post.getAttachments();
                if(attachments != null && attachments.size() > 0){
                    NewsPost.AttachPhoto photo = attachments.get(0).photo;
                    if(photo != null) {
                        post.setPostPhotoUrl(photo.sizes.get(0).url);
                    }
                }
            }

            //news.
            /*
            JSONObject jsonResponse = ((HttpResponse)httpResponse).GetResponseAsJSON().getJSONObject("response");
            Iterator<String> keysIterator = jsonResponse.keys();
            JSONArray array = jsonResponse.getJSONArray("items");
            for (int i = 0; i < array.length(); i++) {
                JSONObject row = array.getJSONObject(i);
                NewsPost post = new NewsPost();
                post.setPostText( row.getString("text"));
                news.add( post );
            }*/

        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void append(NewsWall addNews) {
        news.addAll(addNews.news);
    }

    public void removeAllNews(){
        news.clear();
    }

    public NewsWall getNewsRange(final int startRange, final int endRange) {
        //return new NewsWall(news.subList(startRange, endRange));
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<NewsWall> CREATOR = new Parcelable.Creator<NewsWall>() {

        public NewsWall createFromParcel(Parcel in) {
            return new NewsWall(in);
        }

        public NewsWall[] newArray(int size) {
            return new NewsWall[size];
        }
    };

}
