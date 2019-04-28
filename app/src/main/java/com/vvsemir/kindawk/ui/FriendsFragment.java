package com.vvsemir.kindawk.ui;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.vvsemir.kindawk.R;
import com.vvsemir.kindawk.UserActivity;
import com.vvsemir.kindawk.provider.Friend;
import com.vvsemir.kindawk.provider.FriendsList;
import com.vvsemir.kindawk.service.ICallback;
import com.vvsemir.kindawk.service.ProviderService;

public class FriendsFragment extends KindaFragment {
    public static final String FRAGMENT_TAG = "FriendsFragmentTag";
    private ListView friendsListView;
    FriendsListAdapter adapter;

    public FriendsFragment() {
    }

    public static FriendsFragment newInstance() {
        FriendsFragment fragment = new FriendsFragment();

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_friends, container, false);
        friendsListView = (ListView) view.findViewById(R.id.friendsListView);
        adapter = new FriendsListAdapter(getContext());
        friendsListView.setAdapter(adapter);
        friendsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Friend friend = (Friend)adapter.getItem(position);

                if( friend != null && friend.getUserId() != 0) {
                    ((UserActivity)getActivity()).loadProfile(friend);

                    return true;
                }

                return false;
            }
        });


        loadData();

        return view;
    }

    public void updateViewsWithData(Parcelable data) {
        if (data == null) {
            return;
        }

        adapter.updateData((FriendsList)data);
    }

    @Override
    public void loadData() {
        ProviderService.getFriends(new ICallback<FriendsList>() {
            @Override
            public void onResult(FriendsList result) {
                updateViewsWithData(result);
            }

            @Override
            public void onNotify(FriendsList result) {
                //to do
                Log.d("getFriends", "getFriends : notification refresh!!!");
            }

            @Override
            public void onError(Throwable throwable) {
                //to do
                Log.d("getFriends", "getFriends : loading exception!!!" + throwable.getMessage() );
            }
        } );

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public String getFragmentTag() {
        return FRAGMENT_TAG;
    }
}
