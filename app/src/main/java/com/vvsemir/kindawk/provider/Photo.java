package com.vvsemir.kindawk.provider;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Photo {
    @SerializedName("id")
    private int photoId;
    @SerializedName("album_id")
    private int albumId;
    @SerializedName("owner_id")
    private int ownerId;
    @SerializedName("sizes")
    private List<PhotoSize> photoSizes;

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public List<PhotoSize> getPhotoSizes() {
        return photoSizes;
    }

    public void setPhotoSizes(List<PhotoSize> photoSizes) {
        this.photoSizes = photoSizes;
    }


    public Photo() {
        photoSizes = new ArrayList<PhotoSize>();
    }

    public String getUrlByType(final char type){
        for(PhotoSize photoSize : photoSizes){
            if(Character.compare(photoSize.type.charAt(0),type) == 0){
                return photoSize.url;
            }
        }

        return null;
    }

    class PhotoSize{
        String type;
        String url;
        int width;
        int height;
    }
}
