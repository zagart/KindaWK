package com.vvsemir.kindawk.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.vvsemir.kindaimageloader.ImageLoader;
import com.vvsemir.kindawk.R;
import com.vvsemir.kindawk.provider.Photo;

import java.util.ArrayList;
import java.util.List;

public class PhotosRecyclerAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder> {
    private final char PHOTO_TYPE_SMALL = 'p';
    private final char PHOTO_TYPE_BIG = 'z';

    private final LayoutInflater layoutInflater;
    private final List<Photo> photos = new ArrayList<>();
    private final List<Integer> photosIdsToDelete = new ArrayList<>();
    private final ImageLoader imageLoader;

    public PhotosRecyclerAdapter(final Context context) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = ImageLoader.getInstance(context);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ImageView imageView = viewHolder.itemView.findViewById(R.id.photoItemView);
        imageLoader.loadAndShow(imageView, photos.get(i).getUrlByType(PHOTO_TYPE_SMALL));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new BaseViewHolder<>(layoutInflater.inflate(R.layout.view_photos_recycler_item, viewGroup, false));
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public String getItemUriScreen(int position) {
        return photos.get(position).getUrlByType(PHOTO_TYPE_BIG);
    }

    public void updateItems(final List<Photo> items) {
        photos.clear();
        photos.addAll(items);
        notifyDataSetChanged(); //TODO DiffUtils
    }

    public void removeAllItems() {
        photos.clear();
        notifyDataSetChanged(); //TODO DiffUtils
    }

    public void addPhotoToDelete(int position, boolean add) {
        Integer photoId = photos.get(position).getPhotoId();
        if(add) {
            photosIdsToDelete.add(photoId);
        } else {
            photosIdsToDelete.remove(photoId);
        }
    }

    public boolean hasPhotoToDelete() {
        return photosIdsToDelete.size() > 0;
    }

    public void addItem(Photo photo) {
        photos.add(photo);
    }

}
