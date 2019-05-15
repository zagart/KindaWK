package com.vvsemir.kindawk.ui;

import android.content.Context;
import android.graphics.Bitmap;
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
    private FriendsList friendsList = new FriendsList();
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

        if(friendsList != null){
            Friend friend = friendsList.getItem(position);
            ((TextView) view.findViewById(R.id.friendFirstNameView)).setText(friend.getFirstName());
            ((TextView) view.findViewById(R.id.friendLastNameView)).setText(friend.getLastName());
            ((TextView) view.findViewById(R.id.friendCityView)).setText(friend.getCity().getTitle());
            ((TextView) view.findViewById(R.id.friendCountryView)).setText(friend.getCountry().getTitle());
            ((TextView) view.findViewById(R.id.friendStatusView)).setText(friend.getStatus());

            ImageView avatarView = view.findViewById(R.id.friendPhotoView);
            //avatarView.setImageResource(R.drawable.ic_person);

            Bitmap bitmap = ImageLoader.getBitmapFromBytes( friend.getPhotoBytes().getAsByteArray(Friend.PHOTO_BYTES));
            avatarView.setImageDrawable(ImageLoader.getCircledDrawable(bitmap));

            //avatarView.setImageBitmap(
            //        ImageLoader.getBitmapFromBytes( friend.getPhotoBytes().getAsByteArray(Friend.PHOTO_BYTES)));
        }

        return view;
    }

    @Override
    public int getCount() {
        if(friendsList != null) {
            return friendsList.getCount();
        }

        return 0;
    }

    @Override
    public Object getItem(int position) {
        if(friendsList != null) {
            return friendsList.getItem(position);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void updateData(FriendsList data){
        friendsList.removeAllFriends();
        friendsList.append(data.getList());
        notifyDataSetChanged();
    }
}
