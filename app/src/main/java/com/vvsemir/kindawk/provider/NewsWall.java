package com.vvsemir.kindawk.provider;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vvsemir.kindawk.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
