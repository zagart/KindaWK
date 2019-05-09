package com.vvsemir.kindawk.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.vvsemir.kindaimageloader.ILoaderCallback;
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
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
    private final ImageLoader imageLoader;

    public NewsRecyclerAdapter(final Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = ImageLoader.getInstance(context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (news != null && getItemViewType(position) == ViewType.POST) {
            final NewsItemView itemView = (NewsItemView) viewHolder.itemView;
            final NewsPost newsPost = (NewsPost)news.getItem(position);

            if(newsPost.getDateUnixTime() != null ) {
                itemView.setPostDate(simpleDateFormat.format(newsPost.getDateUnixTime()));
            }

            itemView.setPostId(String.valueOf(newsPost.getPostId()));
            itemView.setSourceName(newsPost.getSourceName());

            //itemView.postUrlView.setText(newsPost.getPostPhotoUrl());
            //itemView.sourceUrlView.setText(newsPost.getSourcePhotoUrl());

            if(newsPost.getSourcePhotoUrl() != null && !newsPost.getSourcePhotoUrl().isEmpty()) {
                imageLoader.loadAndShow(itemView.getSourcePhotoView(), newsPost.getSourcePhotoUrl());
            }

            itemView.setPostBody(newsPost.getPostText());

            final ImageView postView = itemView.getPostPhotoView();
            postView.setImageBitmap(null);

            if(newsPost.getPostPhotoUrl() != null && !newsPost.getPostPhotoUrl().isEmpty()) {
                imageLoader.loadAndReturnBitmap(newsPost.getPostPhotoUrl(), new ILoaderCallback<Bitmap>() {
                    @Override
                    public void onResult(Bitmap result) {
                        postView.setImageBitmap(result);
                        postView.invalidate();
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });

            } else {
                postView.getLayoutParams().height = 0;
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
            return new BaseViewHolder<>(inflater.inflate( R.layout.layout_progress, viewGroup, false));
        }
    }

    @Override
    public int getItemCount() {
        if(news == null){
            return 0;
        }
        return news.getCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (news  != null && position < news.getCount()) {
            return ViewType.POST;
        } else {
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
            news.appendPosts(posts.getNews());
            Log.d("WWRR updateItems", "count new= " + news.getCount() + "hash =" + news.hashCode());
            //notifyDataSetChanged();
        }
    }

    public void cleanWall() {
        news.removeAllNews();
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
