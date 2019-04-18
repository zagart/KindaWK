package com.vvsemir.kindawk.provider;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;


public class NewsWall implements IRecyclerListData<NewsPost>, Parcelable {
    private final List<NewsPost> news = new ArrayList<>();

    public NewsWall() {
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

    @Override
    public int describeContents() {
        return 0;
    }

    public final List<NewsPost> getNews() {
        return news;
    }

    public synchronized void appendPosts(List<NewsPost> posts) {
        news.addAll(posts);
    }

    public synchronized void removeAllNews(){
        news.clear();
    }

    public synchronized void addPost(NewsPost post) {
        news.add( post);
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
