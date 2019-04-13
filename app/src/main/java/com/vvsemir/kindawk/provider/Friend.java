package com.vvsemir.kindawk.provider;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Friend implements Parcelable {
    public static final String PHOTO_BYTES = "FriendPhotoBytes";

    @SerializedName("id")
    private int uid;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("bdate")
    private String birthDate;
    @SerializedName("city")
    private DataCity city;
    @SerializedName("country")
    private Integer country;
    @SerializedName("status")
    private String status;
    @SerializedName("photo_100")
    private String photo100Url;

    private ContentValues photo100Bytes;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public DataCity getCity() {
        return city;
    }

    public void setCity(DataCity city) {
        this.city = city;
    }

    public Integer getCountry() {
        return country;
    }

    public void setCountry(Integer country) {
        this.country = country;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhoto100Url() {
        return photo100Url;
    }

    public void setPhoto100Url(String photo100Url) {
        this.photo100Url = photo100Url;
    }

    public ContentValues getPhoto100Bytes() {
        return photo100Bytes;
    }

    public void setPhoto100Bytes(ContentValues photo100Bytes) {
        this.photo100Bytes = photo100Bytes;
    }

    public Friend() {
    }


    private Friend(Parcel in) {
        this.uid = in.readInt();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.birthDate = in.readString();
        this.city = in.readParcelable(DataCity.class.getClassLoader());
        this.country = in.readInt();
        this.status = in.readString();
        this.photo100Url = in.readString();
        this.photo100Bytes = in.readParcelable(ContentValues.class.getClassLoader());
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(uid);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(birthDate);
        dest.writeParcelable(city, 0);
        dest.writeInt(country);
        dest.writeString(status);
        dest.writeString(photo100Url);
        dest.writeParcelable(photo100Bytes, 0);
    }

    public static final Creator<Friend> CREATOR = new Creator<Friend>() {

        public Friend createFromParcel(Parcel in) {
            return new Friend(in);
        }

        public Friend[] newArray(int size) {
            return new Friend[size];
        }
    };
}
