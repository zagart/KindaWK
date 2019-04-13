package com.vvsemir.kindawk.provider;

import android.os.Parcel;
import android.os.Parcelable;

public class DataCity implements Parcelable {
    private int id;
    private String title;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private DataCity(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
    }

    @Override
    public int describeContents() {
        return 0;
    }
    public static final Creator<DataCity> CREATOR = new Creator<DataCity>() {

        public DataCity createFromParcel(Parcel in) {
            return new DataCity(in);
        }

        public DataCity[] newArray(int size) {
            return new DataCity[size];
        }
    };
}
