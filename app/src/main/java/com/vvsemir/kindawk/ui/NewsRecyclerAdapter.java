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

import com.vvsemir.kindaimageloader.ImageLoader;
import com.vvsemir.kindawk.R;
import com.vvsemir.kindawk.provider.NewsPost;
import com.vvsemir.kindawk.provider.NewsWall;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class NewsRecyclerAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder> {
    private final LayoutInflater inflater;
    private NewsWall news;
    private boolean showLoading = false;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault());

    public NewsRecyclerAdapter(final Context context) {
        super();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (news != null && getItemViewType(position) == ViewType.POST) {
            NewsItemView itemView = (NewsItemView) viewHolder.itemView;
            NewsPost newsPost = (NewsPost)news.getItem(position);

            itemView.setPostText(newsPost.getPostText());
            itemView.setPostDate(simpleDateFormat.format(newsPost.getDateUnixTime()));
            itemView.setPostId(String.valueOf(newsPost.getPostId()));
            itemView.setSourceName(newsPost.getSourceName());

            if(newsPost.getSourcePhoto() != null) {
                byte[] imageBytes = newsPost.getSourcePhoto().getAsByteArray(NewsPost.PHOTO_BYTES);
                if(imageBytes != null && imageBytes.length > 0){
                    itemView.setSourcePhoto(ImageLoader.getInstance().getBitmapFromBytes(imageBytes));
                }
            }
            if(newsPost.getPostPhoto() != null) {
                byte[] imageBytes = newsPost.getPostPhoto().getAsByteArray(NewsPost.PHOTO_BYTES);
                if(imageBytes != null && imageBytes.length > 0) {
                    itemView.setPostPhoto(ImageLoader.getInstance().getBitmapFromBytes(imageBytes));
                }
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
        /*if(news == null){
            return showLoading ? 1 : 0;
        }

        if (showLoading) {
            return news.getCount() + 1;
        } else {
            return news.getCount();
        }*/
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
            news = posts;
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
