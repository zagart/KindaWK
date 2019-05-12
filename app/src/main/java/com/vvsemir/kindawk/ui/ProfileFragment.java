package com.vvsemir.kindawk.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vvsemir.kindaimageloader.ILoaderCallback;
import com.vvsemir.kindaimageloader.ImageLoader;
import com.vvsemir.kindawk.UserActivity;
import com.vvsemir.kindawk.auth.AuthManager;
import com.vvsemir.kindawk.provider.Friend;
import com.vvsemir.kindawk.provider.Photo;
import com.vvsemir.kindawk.provider.PhotosProvider;
import com.vvsemir.kindawk.provider.UserProfile;
import com.vvsemir.kindawk.provider.UserProfileProvider;
import com.vvsemir.kindawk.service.CallbackExceptionFactory;
import com.vvsemir.kindawk.service.ICallback;
import com.vvsemir.kindawk.R;
import com.vvsemir.kindawk.service.ProviderService;
import com.vvsemir.kindawk.service.RequestParams;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends KindaFragment  {
    public static final String FRAGMENT_TAG = "ProfileFragmentTag";
    private static final String CURRENT_USER = "CurrentUser";
    private static final String CURRENT_POSITION = "CurrentPosition";
    static final int REQUEST_IMAGE_CAPTURE = 111;



    ImageView profilePhotoView;
    TextView firstNameView;
    TextView lastNameView;
    TextView countryView;
    TextView cityView;
    TextView statusView;
    TextView phoneView;
    RecyclerView recyclerView;
    View progressView;
    int currentUserId = 0;
    int currentPhotoPosition = 0;
    Friend friend = null;

    private LinearLayoutManager layoutManager;
    PhotosRecyclerAdapter photosRecyclerAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey(CURRENT_USER)) {
            Parcelable data = savedInstanceState.getParcelable(CURRENT_USER);

             if(data instanceof Friend) {
                 friend = (Friend)data;
                 currentUserId = friend.getUserId();
             }

             if (savedInstanceState.containsKey(CURRENT_POSITION)) {
                 currentPhotoPosition = savedInstanceState.getInt(CURRENT_POSITION);
             }
        }

        if(currentUserId == 0) {
            currentUserId = AuthManager.getCurrentToken().getUserId();
            friend = null;
        }

        photosRecyclerAdapter = new PhotosRecyclerAdapter(getActivity());
    }

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        profilePhotoView = view.findViewById(R.id.profilePhotoView);
        firstNameView = view.findViewById(R.id.profFirstNameView);
        lastNameView = view.findViewById(R.id.profLastNameView);
        cityView = view.findViewById(R.id.profCityView);
        countryView = view.findViewById(R.id.profCountryView);
        statusView  = view.findViewById(R.id.profStatusView);
        phoneView  = view.findViewById(R.id.profPhoneView);
        recyclerView = view.findViewById(R.id.profPhotoList);
        progressView = view.findViewById(R.id.profPhotoProgress);


        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(photosRecyclerAdapter);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        } else {
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        }

        int[] resIds = {R.id.selectPhotoBox};
        recyclerView.addOnItemTouchListener(new PhotoRecyclerViewItemTouchListener(recyclerView,
                resIds, new PhotoRecyclerViewItemTouchListener.SelectionClickListener() {
            @Override
            public void onSelectClick(@NotNull View view, int position) {
                final CheckBox checkBox = (CheckBox) view.findViewById(R.id.selectPhotoBox);
                checkBox.setChecked(!checkBox.isChecked());
                photosRecyclerAdapter.addPhotoToDelete(position, checkBox.isChecked());
                updateMenuOnSelectPhotos();
            }

            @Override
            public void onClick(@NotNull View view, int position) {
                ((UserActivity)getActivity()).loadPhotoFragment(photosRecyclerAdapter.getItemUriScreen(position));
            }

            @Override
            public void onLongClick(@NotNull View view, int position) {

            }
        }));

        loadData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void updateViewsWithData(Parcelable data) {
        if(data == null) {
            return;
        }

        try{
            UserProfile  profile = (UserProfile)data;
            checkSetText(firstNameView, profile.getFirstName());
            checkSetText(lastNameView, profile.getLastName());
            checkSetText(cityView, profile.getCity().getTitle());
            checkSetText(countryView, profile.getCountry().getTitle());
            checkSetText(statusView, profile.getStatus());
            checkSetText(phoneView, profile.getPhone());

            if(((UserProfile)data).getProfilePhotoBytes() != null) {
                byte[] imageBytes = ((UserProfile) data).getProfilePhotoBytes().getAsByteArray(UserProfile.PHOTO_BYTES);

                if (imageBytes != null && imageBytes.length > 0) {
                    profilePhotoView.setImageBitmap(ImageLoader.getBitmapFromBytes(imageBytes));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updateViewsWithFriendData(Friend friend) {
        checkSetText(firstNameView, friend.getFirstName());
        checkSetText(lastNameView, friend.getLastName());
        checkSetText(cityView, friend.getCity().getTitle());
        checkSetText(countryView, friend.getCountry().getTitle());
        checkSetText(statusView, friend.getStatus());

        if(friend.getPhotoBytes() != null) {
            byte[] imageBytes = friend.getPhotoBytes().getAsByteArray(Friend.PHOTO_BYTES);

            if (imageBytes != null && imageBytes.length > 0) {
                profilePhotoView.setImageBitmap(ImageLoader.getBitmapFromBytes(imageBytes));
            }
        }
    }

    private void checkSetText(TextView view, String txt) {
        if(txt != null && txt.isEmpty() == false){
            view.setText(txt);
        }
    }

    @Override
    public void loadData() {
        if(!isCurrentUserFriend()) {
            RequestParams params = null;
            ProviderService.getAccountProfileInfo(params, new ICallback<UserProfile>() {
                @Override
                public void onResult(UserProfile result) {
                    updateViewsWithData(result);
                }

                @Override
                public void onError(Throwable throwable) {
                    if (throwable instanceof CallbackExceptionFactory.Companion.ServiceException) {
                        ((UserActivity) getActivity()).bottomNavigationView.setSelectedItemId(R.id.action_profile);

                        return;
                    }
                }
            });

            showProgress();
        } else if(currentUserId != 0 && friend != null) {
            updateViewsWithFriendData(friend);
        }

        RequestParams photoParams = new RequestParams();
        photoParams.put(PhotosProvider.PARAM_REQUEST_OWNERID, currentUserId);

        ProviderService.getPhotosByOwner(photoParams, new ILoaderCallback<List<Photo>>() {

            @Override
            public void onResult(final List<Photo> result) {
                photosRecyclerAdapter.updateItems(result);
                hideProgress();

                if(currentPhotoPosition > 0 && currentPhotoPosition < photosRecyclerAdapter.getItemCount()) {
                    recyclerView.scrollToPosition(currentPhotoPosition);
                }

                updateMenuOnSelectPhotos();
            }

            @Override
            public void onError(Throwable throwable) {
                //To do show in recyle error
                photosRecyclerAdapter.removeAllItems();
                Toast toast = Toast.makeText(getActivity(), throwable.getMessage(),Toast.LENGTH_SHORT);
                toast.show();
                hideProgress();
            }
        } );
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.user_top_profile, menu);
        super.onCreateOptionsMenu(menu, inflater);

        boolean ownerMenuVsisble = (isCurrentUserFriend() == false);
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.getMenu().findItem(R.id.action_delete_photo).setVisible(ownerMenuVsisble);
        toolbar.getMenu().findItem(R.id.action_add_photo).setVisible(ownerMenuVsisble);
        toolbar.getMenu().findItem(R.id.action_refresh).setVisible(ownerMenuVsisble);
    }

    public void updateMenuOnSelectPhotos(){
        if(isCurrentUserFriend()) {
            return;
        }

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        if(toolbar.getMenu() != null) {
            toolbar.getMenu().findItem(R.id.action_delete_photo).setVisible(photosRecyclerAdapter.hasPhotoToDelete());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            //imageView.setImageBitmap(imageBitmap);
            //to do
            Context context = getActivity();
            Toast ImageToast = new Toast(context);
            LinearLayout toastLayout = new LinearLayout(context);
            toastLayout.setOrientation(LinearLayout.HORIZONTAL);
            ImageView image = new ImageView(context);
            TextView text = new TextView(context);
            image.setImageBitmap(imageBitmap);
            text.setText("Capture!!!");
            toastLayout.addView(image);
            toastLayout.addView(text);
            ImageToast.setView(toastLayout);
            ImageToast.setDuration(Toast.LENGTH_LONG);
            ImageToast.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_photo) {

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }

            return true;
        } else if(id == R.id.action_refresh) {
            ProviderService.cleanProfileData(new ICallback<Integer>() {
                @Override
                public void onResult(Integer result) {
                    photosRecyclerAdapter.removeAllItems();
                    loadData();
                }

                @Override
                public void onError(Throwable throwable) {

                }
            });
        } else if(id == R.id.action_delete_photo) {
            showAlertDeletePhoto();
        }


        return super.onOptionsItemSelected(item);
    }

    private void hideProgress() {
        if (progressView.getVisibility() != View.GONE) {
            progressView.setVisibility(View.GONE);
        }
    }

    private void showProgress() {
        if (progressView.getVisibility() != View.VISIBLE) {
            progressView.setVisibility(View.VISIBLE);
        }
    }

    private void showAlertDeletePhoto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.confirm_delete_photos);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public String getFragmentTag() {
        return FRAGMENT_TAG;
    }

    public void setFriendProfile(Friend friend) {
        currentUserId = friend.getUserId();
        this.friend = friend;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(CURRENT_USER, friend);
        currentPhotoPosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        outState.putInt(CURRENT_POSITION, currentPhotoPosition);
    }

    private boolean isCurrentUserFriend() {
        return currentUserId != AuthManager.getCurrentToken().getUserId();
    }
}


