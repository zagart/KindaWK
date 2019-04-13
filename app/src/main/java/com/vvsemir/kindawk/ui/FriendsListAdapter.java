package com.vvsemir.kindawk.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vvsemir.kindaimageloader.ImageLoader;
import com.vvsemir.kindawk.R;
import com.vvsemir.kindawk.provider.Friend;
import com.vvsemir.kindawk.provider.FriendsList;

public class FriendsListAdapter extends BaseAdapter {
    private FriendsList friends;
    private final LayoutInflater inflater;

    public FriendsListAdapter(final Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = inflater.inflate(R.layout.friends_list_item, parent, false);
        }

        if(friends != null){
            Friend friend = friends.getItem(position);
            ((TextView) view.findViewById(R.id.friendFirstNameView)).setText(friend.getFirstName());
            ImageView avatarView = view.findViewById(R.id.friendPhotoView);
            avatarView.setImageBitmap(
                    ImageLoader.getInstance().
                    getBitmapFromBytes( friend.getPhoto100Bytes().getAsByteArray(Friend.PHOTO_BYTES)));
        }

        return view;
    }

    @Override
    public int getCount() {
        return friends.getCount();
    }

    @Override
    public Object getItem(int position) {
        return friends.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public void updateData(FriendsList data){
        friends = data;
        notifyDataSetChanged();
    }
}
