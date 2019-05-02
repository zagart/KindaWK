package com.vvsemir.kindawk.provider;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Friend implements Parcelable {
    public static final String PHOTO_BYTES = "FriendPhotoBytes";

    @SerializedName("id")
    private int userId;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("bdate")
    private String birthDate;
    @SerializedName("city")
    private DataIdTitle city;
    @SerializedName("country")
    private DataIdTitle country;
    @SerializedName("status")
    private String status;
    @SerializedName("photo_100")
    private String photoUrl;

    private ContentValues photoBytes;


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public DataIdTitle getCity() {
        return city;
    }

    public void setCity(DataIdTitle city) {
        this.city = city;
    }

    public DataIdTitle getCountry() {
        return country;
    }

    public void setCountry(DataIdTitle country) {
        this.country = country;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public ContentValues getPhotoBytes() {
        return photoBytes;
    }

    public void setPhotoBytes(ContentValues photoBytes) {
        this.photoBytes = photoBytes;
    }

    public Friend() {
        city = new DataIdTitle();
        country = new DataIdTitle();
    }


    private Friend(Parcel in) {
        this.userId = in.readInt();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.birthDate = in.readString();
        this.city = in.readParcelable(DataIdTitle.class.getClassLoader());
        this.country = in.readParcelable(DataIdTitle.class.getClassLoader());
        this.status = in.readString();
        this.photoUrl = in.readString();
        this.photoBytes = in.readParcelable(ContentValues.class.getClassLoader());
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(userId);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(birthDate);
        dest.writeParcelable(city, 0);
        dest.writeParcelable(country, 0);
        dest.writeString(status);
        dest.writeString(photoUrl);
        dest.writeParcelable(photoBytes, 0);
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
