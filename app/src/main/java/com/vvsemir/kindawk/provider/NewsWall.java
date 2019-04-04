package com.vvsemir.kindawk.provider;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.vvsemir.kindawk.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NewsWall implements IRecyclerListData<NewsWall.Post>, Parcelable {

    private final List<Post> news = new ArrayList<>();

    public NewsWall() {
    }

    public NewsWall( final List<Post> copyNews) {
        news.clear();
        this.news.addAll(0, copyNews);
    }

    private NewsWall(Parcel in) {
        Bundle bundle = in.readBundle(List.class.getClassLoader());

        for (String key : bundle.keySet()) {
            params.put(key, bundle.getString(key));
        }
    }

    @Override
    public Post getItem(int index) {
        return news.get(index);
    }

    @Override
    public int getCount() {
        return news.size();
    }

    void setFromHttp(final HttpResponse httpResponse){
        try{
            JSONObject jsonResponse = ((HttpResponse)httpResponse).GetResponseAsJSON().getJSONObject("response");

            Iterator<String> keysIterator = jsonResponse.keys();
            int size = jsonResponse.getInt("count");
            JSONArray array = jsonResponse.getJSONArray("items");
            for (int i = 0; i < array.length(); i++) {
                JSONObject row = array.getJSONObject(i);
                Post post = new Post();
                news.add(post.setPostText( row.getString("text") ) );
            }
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
        return new NewsWall(news.subList(startRange, endRange));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public static final Parcelable.Creator<NewsWall> CREATOR = new Parcelable.Creator<NewsWall>() {

        public NewsWall createFromParcel(Parcel in) {
            return new NewsWall(in);
        }

        public NewsWall[] newArray(int size) {
            return new NewsWall[size];
        }
    };


    public class Post {
        private String postText;

        public String getPostText() {
            return postText;
        }

        public Post setPostText(String postText) {
            this.postText = postText;

            return this;
        }
    }
}
