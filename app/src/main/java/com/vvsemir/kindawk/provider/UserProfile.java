package com.vvsemir.kindawk.provider;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.vvsemir.kindawk.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.Iterator;

public class UserProfile implements Parcelable {
    public static final String PHOTO_BYTES = "ProfilePhotoBytes";

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
    @SerializedName("phone")
    private String phone;

    private String profilePhoto;
    private ContentValues profilePhotoBytes;

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public ContentValues getProfilePhotoBytes() {
        return profilePhotoBytes;
    }

    public void setProfilePhotoBytes(ContentValues profilePhotoBytes) {
        this.profilePhotoBytes = profilePhotoBytes;
    }

    public UserProfile() {
        city = new DataIdTitle();
        country = new DataIdTitle();
    }

    private UserProfile(Parcel in) {
        this.userId = in.readInt();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.birthDate = in.readString();
        this.city = in.readParcelable(DataIdTitle.class.getClassLoader());
        this.country = in.readParcelable(DataIdTitle.class.getClassLoader());
        this.status = in.readString();
        this.phone = in.readString();
        this.profilePhoto = in.readString();
        this.profilePhotoBytes = in.readParcelable(ContentValues.class.getClassLoader());
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.userId);
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.birthDate);
        dest.writeParcelable(city, 0);
        dest.writeParcelable(country, 0);
        dest.writeString(this.status);
        dest.writeString(this.phone);
        dest.writeValue(this.profilePhoto);
        dest.writeParcelable(this.profilePhotoBytes, flags);
    }

    void copy(final UserProfile copy) {
        this.firstName = copy.firstName;
        this.lastName = copy.lastName;
        this.birthDate = copy.birthDate;
        this.city = copy.city;
        this.country = copy.country;
        this.status = copy.status;
        this.phone = copy.phone;
        this.userId = copy.userId;
        this.profilePhoto = copy.profilePhoto;

        if(this.profilePhotoBytes != null) {
            this.profilePhotoBytes.clear();
        } else {
            this.profilePhotoBytes = new ContentValues();
        }

        if(copy.profilePhotoBytes != null) {
            this.profilePhotoBytes.putAll(copy.profilePhotoBytes);
        }
    }

    public static final Creator<UserProfile> CREATOR = new Creator<UserProfile>() {

        public UserProfile createFromParcel(Parcel in) {
            return new UserProfile(in);
        }

        public UserProfile[] newArray(int size) {
            return new UserProfile[size];
        }
    };


}
