package com.vvsemir.kindawk.provider;

import android.os.Parcel;
import android.os.Parcelable;

public class DataIdTitle implements Parcelable {
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

    public DataIdTitle() {
    }

    public DataIdTitle(Parcel in) {
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
    public static final Creator<DataIdTitle> CREATOR = new Creator<DataIdTitle>() {

        public DataIdTitle createFromParcel(Parcel in) {
            return new DataIdTitle(in);
        }

        public DataIdTitle[] newArray(int size) {
            return new DataIdTitle[size];
        }
    };
}
