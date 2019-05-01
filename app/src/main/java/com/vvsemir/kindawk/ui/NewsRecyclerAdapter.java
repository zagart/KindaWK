package com.vvsemir.kindawk.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.vvsemir.kindaimageloader.ImageLoader;
import com.vvsemir.kindawk.R;
import com.vvsemir.kindawk.provider.NewsPost;
import com.vvsemir.kindawk.provider.NewsWall;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class NewsRecyclerAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder> {
    private final LayoutInflater inflater;
    private NewsWall news = new NewsWall();
    private boolean showLoading = false;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault());
    private final ImageLoader imageLoader;

    public NewsRecyclerAdapter(final Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = ImageLoader.getInstance(context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (news != null && getItemViewType(position) == ViewType.POST) {
            Log.d("WWRR onBindViewHolder", "position = " + position + " total = " + news.getCount());
            NewsItemView itemView = (NewsItemView) viewHolder.itemView;
            NewsPost newsPost = (NewsPost)news.getItem(position);

            itemView.setPostText(newsPost.getPostText());
            itemView.setPostDate(simpleDateFormat.format(newsPost.getDateUnixTime()));
            itemView.setPostId(String.valueOf(newsPost.getPostId()));
            itemView.setSourceName(newsPost.getSourceName());

            /*if(newsPost.getSourcePhoto() != null) {
                byte[] imageBytes = newsPost.getSourcePhoto().getAsByteArray(NewsPost.PHOTO_BYTES);
                if(imageBytes != null && imageBytes.length > 0){
                    itemView.setSourcePhoto(ImageLoader.getBitmapFromBytes(imageBytes));
                }
            }
            if(newsPost.getPostPhoto() != null) {
                byte[] imageBytes = newsPost.getPostPhoto().getAsByteArray(NewsPost.PHOTO_BYTES);
                if(imageBytes != null && imageBytes.length > 0) {
                    itemView.setPostPhoto(ImageLoader.getBitmapFromBytes(imageBytes));
                } else {
                    itemView.setPostPhoto(null);
                }
            }*/

            if(newsPost.getSourcePhotoUrl() != null && !newsPost.getSourcePhotoUrl().isEmpty()) {
                imageLoader.loadAndShow(itemView.getSourcePhotoView(), newsPost.getSourcePhotoUrl());
            }

            ImageView postView = itemView.getPostPhotoView();

            if(newsPost.getPostPhotoUrl() != null && !newsPost.getPostPhotoUrl().isEmpty()) {
                postView.setMaxWidth(140);
                imageLoader.loadAndShow(postView, newsPost.getPostPhotoUrl());
            } else {
                postView.setMaxWidth(0);
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == ViewType.POST) {
            NewsItemView itemView = new NewsItemView(viewGroup.getContext());
            final NewsItemViewHolder viewHolder = new NewsItemViewHolder(itemView);

            return viewHolder;
        } else {
            Log.d("WWRR HolderALARM ", "NO PROGRESS holder");
            return new BaseViewHolder<>(inflater.inflate( R.layout.layout_progress, viewGroup, false));
        }
    }

    @Override
    public int getItemCount() {
        if(news == null){
            Log.d("WWRR getItemCount", "count = 0");
            return 0;
        }
        Log.d("WWRR getItemCount", "count = " + news.getCount());
        return news.getCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (news  != null && position < news.getCount()) {
            Log.d("WWRR getItemViewType ", "position " +  position);
            return ViewType.POST;
        } else {
            Log.d("WWRR getItemViewType ", "ALARM NO PROGRESS TYPE");
            return ViewType.LOADING;
        }
    }

    public void setShowLoadingProgress(final boolean show) {
        if (show != showLoading) {
            showLoading = show;

            final int position = news == null ? 0 : news.getCount();
            notifyItemInserted(position);
        }
    }

    public void updateItems(final NewsWall posts) {
        if(posts !=  null) {
            if(news != null )
                Log.d("WWRR updateItems", "count old= " + news.getCount() + "hash =" + news.hashCode());
            else
                Log.d("WWRR updateItems", "news old= 0");
            news.appendPosts(posts.getNews());
            Log.d("WWRR updateItems", "count new= " + news.getCount() + "hash =" + news.hashCode());
            //notifyDataSetChanged();
        }
    }


    @interface ViewType {
        int POST = 0;
        int LOADING = 1;
    }


    public class NewsItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        public NewsItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (getAdapterPosition() == RecyclerView.NO_POSITION) {
                return;
            }
            //notifyItemChanged(selectPosition);
            //selectPosition = getAdapterPosition();
            //notifyItemChanged(selectPosition);
        }
    }
}
