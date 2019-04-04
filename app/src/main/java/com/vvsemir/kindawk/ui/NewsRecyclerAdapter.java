package com.vvsemir.kindawk.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vvsemir.kindawk.R;
import com.vvsemir.kindawk.provider.NewsWall;

public class NewsRecyclerAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder> {
    private final LayoutInflater inflater;
    private final NewsWall news = new NewsWall();
    private boolean showLoading = false;

    public NewsRecyclerAdapter(final Context context) {
        super();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == ViewType.POST) {
            ((NewsItemView) viewHolder.itemView).setPostText(((NewsWall.Post)news.getItem(position)).getPostText());
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
            return new BaseViewHolder<>(inflater.inflate(R.layout.layout_progress, viewGroup, false));
        }
    }

    @Override
    public int getItemCount() {
        if (showLoading) {
            return news.getCount() + 1;
        } else {
            return news.getCount();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position < news.getCount()) {
            return ViewType.POST;
        } else {
            return ViewType.LOADING;
        }
    }

    public void setShowLoadingProgress(final boolean show) {
        if (show != showLoading) {
            showLoading = show;
            notifyDataSetChanged();
        }
    }

    public void addItems(final NewsWall posts) {
        news.append(posts);
        notifyDataSetChanged();
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
