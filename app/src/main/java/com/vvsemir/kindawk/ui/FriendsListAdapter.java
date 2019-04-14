package com.vvsemir.kindawk.ui;

import android.content.Context;
import android.util.Log;
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
            ((TextView) view.findViewById(R.id.friendCountryView)).setText(friend.getCountry().getTitle());

            ImageView avatarView = view.findViewById(R.id.friendPhotoView);
            //avatarView.setImageResource(R.drawable.ic_person);
            avatarView.setImageBitmap(
                    ImageLoader.getInstance().
                    getBitmapFromBytes( friend.getPhoto100Bytes().getAsByteArray(Friend.PHOTO_BYTES)));

            Log.d("ZZZadapter", "  view success" );
        }

        return view;
    }

    @Override
    public int getCount() {
        if(friends != null) {
            return friends.getCount();
        }

        return 0;
    }

    @Override
    public Object getItem(int position) {
        if(friends != null) {
            return friends.getItem(position);
        }

        return null;
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
