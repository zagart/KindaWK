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

    private final LayoutInflater layoutInflater;
    private final List<Photo> photos = new ArrayList<>();
    private final ImageLoader imageLoader = ImageLoader.getInstance();

    public PhotosRecyclerAdapter(final Context context) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Log.d("FF PHOTO", "onBindViewHolder= " + photos.get(i).getPhotoId() + " " + photos.get(i).getUrlByType(PHOTO_TYPE_SMALL));
        imageLoader.loadAndShow((ImageView) viewHolder.itemView, photos.get(i).getUrlByType(PHOTO_TYPE_SMALL));
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

    public void updateItems(final List<Photo> items) {
       //photos.clear();
        photos.addAll(items);
        for(Photo photo : photos){
            Log.d("FF UPDATEITs", "updateItems= " + photo.getPhotoId() + " " + photo.getUrlByType(PHOTO_TYPE_SMALL));
        }
        notifyDataSetChanged(); //TODO DiffUtils
    }
}
