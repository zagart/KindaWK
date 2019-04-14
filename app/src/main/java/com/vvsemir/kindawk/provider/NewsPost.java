package com.vvsemir.kindawk.provider;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class NewsPost implements Parcelable {

    @SerializedName("type")
    private String type;

    @SerializedName("source_id")
    private int sourceId;

    @SerializedName("date")
    private Long dateUnixTime;

    @SerializedName("post_id")
    private int postId;

    @SerializedName("text")
    private String postText;

    private String sourcePhotoUrl;
    private String postPhotoUrl;
    private ContentValues sourcePhoto;
    private ContentValues postPhoto;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public Long getDateUnixTime() {
        return dateUnixTime;
    }

    public void setDateUnixTime(Long dateUnixTime) {
        this.dateUnixTime = dateUnixTime;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public String getSourcePhotoUrl() {
        return sourcePhotoUrl;
    }

    public void setSourcePhotoUrl(String sourcePhotoUrl) {
        this.sourcePhotoUrl = sourcePhotoUrl;
    }

    public String getPostPhotoUrl() {
        return postPhotoUrl;
    }

    public void setPostPhotoUrl(String postPhotoUrl) {
        this.postPhotoUrl = postPhotoUrl;
    }

    public ContentValues getSourcePhoto() {
        return sourcePhoto;
    }

    public void setSourcePhoto(ContentValues sourcePhoto) {
        this.sourcePhoto = sourcePhoto;
    }

    public ContentValues getPostPhoto() {
        return postPhoto;
    }

    public void setPostPhoto(ContentValues postPhoto) {
        this.postPhoto = postPhoto;
    }


    public NewsPost() {
    }

    private NewsPost(Parcel in) {
        this.type = in.readString();
        this.sourceId = in.readInt();
        this.dateUnixTime = in.readLong();
        this.postId = in.readInt();
        this.postText = in.readString();
        this.sourcePhotoUrl = in.readString();
        this.postPhotoUrl = in.readString();
        this.sourcePhoto = in.readParcelable(ContentValues.class.getClassLoader());
        this.postPhoto = in.readParcelable(ContentValues.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeInt(sourceId);
        dest.writeLong(dateUnixTime);
        dest.writeInt(postId);
        dest.writeString(postText);
        dest.writeString(sourcePhotoUrl);
        dest.writeString(postPhotoUrl);
        dest.writeParcelable(sourcePhoto, 0);
        dest.writeParcelable(postPhoto, 0);
    }

    public static final Parcelable.Creator<NewsPost> CREATOR = new Parcelable.Creator<NewsPost>() {

        public NewsPost createFromParcel(Parcel in) {
            return new NewsPost(in);
        }

        public NewsPost[] newArray(int size) {
            return new NewsPost[size];
        }
    };


}
