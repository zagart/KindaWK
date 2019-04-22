package com.vvsemir.kindawk.provider;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.vvsemir.kindawk.http.HttpResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FriendsList implements Parcelable {
    private final List<Friend> friends = new ArrayList<>();

    public FriendsList() {
    }

    private FriendsList(Parcel in) {
        in.readTypedList(friends, Friend.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(friends);
    }

    public Friend getItem(int index) {
        return friends.get(index);
    }

    public int getCount() {
        return friends.size();
    }

    public final List<Friend> getList() {
        return friends;
    }


    public synchronized void append(List<Friend> addFriends) {
        friends.addAll(addFriends);
    }

    public synchronized void addFriend(Friend friend) {
        friends.add( friend);
    }

    public synchronized void removeAllFriends(){
        friends.clear();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FriendsList> CREATOR = new Creator<FriendsList>() {

        public FriendsList createFromParcel(Parcel in) {
            return new FriendsList(in);
        }

        public FriendsList[] newArray(int size) {
            return new FriendsList[size];
        }
    };
}
