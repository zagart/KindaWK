package com.vvsemir.kindawk.provider;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;

public class NewsPost implements Parcelable {
    public static final String PHOTO_BYTES = "PostPhotoBytes";

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

    @SerializedName("attachments")
    private List<Attachment> attachments;


    private String sourceName;
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

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
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

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public NewsPost() {
    }

    private NewsPost(Parcel in) {
        this.type = in.readString();
        this.sourceId = in.readInt();
        this.dateUnixTime = in.readLong();
        this.postId = in.readInt();
        this.postText = in.readString();
        this.sourceName = in.readString();
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
        dest.writeString(sourceName);
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


    class Attachment{
        String type;
        AttachPhoto photo;
    }

    class AttachPhoto{
        int id;
        List<AttachPhotoSizes> sizes;
    }

    class AttachPhotoSizes{
        String type;
        String url;
        int width;
        int height;
    }

    class DataProfileIdPhotourl{
        int id;
        String photo_100;
        String first_name;
        String last_name;
    }

    static class DataIdPhotourl{
        public DataIdPhotourl(int id, String photo, String name) {
            this.id = id;
            this.photo_100 = photo_100;
            this.name = name;
        }
        int id;
        String photo_100;
        String name;
    }

    static void makeMapFromDataIdPhotoList(DataIdPhotourl[] list, HashMap<Integer, DataIdPhotourl> map){
        for(DataIdPhotourl item : list){
            map.put(item.id, item);
        }
    }
}
