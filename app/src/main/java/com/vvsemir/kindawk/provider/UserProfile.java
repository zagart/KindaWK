package com.vvsemir.kindawk.provider;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.vvsemir.kindawk.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

public class UserProfile implements Parcelable {
    private String firstName;
    private String lastName;
    private String birthDate;
    private String homeTown;
    private String country;
    private String status;
    private String phone;
    private Uri profilePhoto;

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

    public String getHomeTown() {
        return homeTown;
    }

    public void setHomeTown(String homeTown) {
        this.homeTown = homeTown;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
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

    public Uri getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(Uri profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public UserProfile() {
    }

    private UserProfile(Parcel in) {
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.birthDate = in.readString();
        this.homeTown = in.readString();
        this.country = in.readString();
        this.status = in.readString();
        this.phone = in.readString();
        this.profilePhoto = (Uri)in.readValue(Uri.class.getClassLoader());
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.birthDate);
        dest.writeString(this.homeTown);
        dest.writeString(this.country);
        dest.writeString(this.status);
        dest.writeString(this.phone);
        dest.writeValue(this.profilePhoto);
    }

    void setFromHttp(final HttpResponse httpResponse) {
        try {
            JSONObject jsonResponse = ((HttpResponse) httpResponse).GetResponseAsJSON().getJSONObject("response");
            firstName = jsonResponse.getString("first_name");
        } catch (Exception ex) {
            ex.printStackTrace();
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
