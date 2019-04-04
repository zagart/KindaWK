package com.vvsemir.kindawk.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vvsemir.kindawk.R;
import com.vvsemir.kindawk.provider.NewsWall;
import com.vvsemir.kindawk.provider.NewsWallProvider;

import com.vvsemir.kindawk.service.ProviderIntentService;
import com.vvsemir.kindawk.service.RequestParams;
import com.vvsemir.kindawk.utils.ICallback;

public class NewsFragment extends ReceiverFragment {
    private NewsRecyclerAdapter newsRecyclerAdapter;
    private LinearLayoutManager layoutManager;
    public static final int PAGE_SIZE = 12;
    public static final int MAX_VISIBLE_POSTS = 100;
    private boolean isLoading = false;

    public static NewsFragment newInstance(String responseAction, Parcelable data, Boolean preserveProviderData) {
        NewsFragment fragment = new NewsFragment();
        fragment.setArguments(initBundle(responseAction, data, preserveProviderData));
        return fragment;
    }

    @Override
    public void onPostCreate(){
        final RecyclerView recyclerView = getActivity().findViewById(R.id.newsList);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        newsRecyclerAdapter = new NewsRecyclerAdapter(getActivity());
        recyclerView.setAdapter(newsRecyclerAdapter);
        recyclerView.addOnScrollListener(recyclerViewOnScrollListener);
    }

    @Override
    public void updateViews(Parcelable data) {
        newsRecyclerAdapter.addItems(data);
        isLoading = false;
        newsRecyclerAdapter.setShowLoadingProgress(false);
    }

    @Override
    public void loadData() {
        loadMoreItems(0, PAGE_SIZE);
    }

    private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = layoutManager.getChildCount();
            int totalItemCount = layoutManager.getItemCount();
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

            if (!isLoading && totalItemCount < MAX_VISIBLE_POSTS) {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= PAGE_SIZE) {
                    loadMoreItems(totalItemCount, totalItemCount + PAGE_SIZE);
                }
            }
        }
    };

    private void loadMoreItems(final int startPosition, final int endPosition) {
        isLoading = true;
        newsRecyclerAdapter.setShowLoadingProgress(true);

        RequestParams params = new RequestParams();
        params.put(NewsWallProvider.ARG_PARAM_REQUEST_RANGE_START, startPosition);
        params.put(NewsWallProvider.ARG_PARAM_REQUEST_RANGE_END, endPosition);

        ProviderIntentService.getWall(context, params);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO: Use the ViewModel
    }

}
