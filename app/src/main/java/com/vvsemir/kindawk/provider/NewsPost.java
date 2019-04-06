package com.vvsemir.kindawk.provider;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class NewsPost implements Parcelable {

    @SerializedName("type")
    private String type;

    @SerializedName("source_id")
    private int sourceId;

    @SerializedName("date")
    private String dateUnixTime;
    //private Long dateUnixTime;

    @SerializedName("text")
    private String postText;

    private String sourcePhotoUrl;
    private String postPhotoUrl;
    private Bitmap sourcePhoto;
    private Bitmap postPhoto;

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

    public String getDateUnixTime() {
    //public Long getDateUnixTime() {
        return dateUnixTime;
    }

    //public void setDateUnixTime(Long dateUnixTime) {
    public void setDateUnixTime(String dateUnixTime) {
        this.dateUnixTime = dateUnixTime;
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

    public Bitmap getSourcePhoto() {
        return sourcePhoto;
    }

    public void setSourcePhoto(Bitmap sourcePhoto) {
        this.sourcePhoto = sourcePhoto;
    }

    public Bitmap getPostPhoto() {
        return postPhoto;
    }

    public void setPostPhoto(Bitmap postPhoto) {
        this.postPhoto = postPhoto;
    }

    public NewsPost() {
    }


    private NewsPost(Parcel in) {
        this.type = in.readString();
        this.sourceId = in.readInt();
        //this.dateUnixTime = (Long) in.readValue(Long.class.getClassLoader());
        this.dateUnixTime = in.readString();
        this.postText = in.readString();
        this.sourcePhotoUrl = in.readString();
        this.postPhotoUrl = in.readString();
        this.sourcePhoto = in.readParcelable(Bitmap.class.getClassLoader());
        this.postPhoto = in.readParcelable(Bitmap.class.getClassLoader());
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
        dest.writeInt(this.sourceId);
        //dest.writeValue(this.dateUnixTime);
        dest.writeString(this.dateUnixTime);
        dest.writeString(this.postText);
        dest.writeString(this.sourcePhotoUrl);
        dest.writeString(this.postPhotoUrl);
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
