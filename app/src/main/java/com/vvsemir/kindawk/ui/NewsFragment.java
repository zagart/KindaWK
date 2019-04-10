package com.vvsemir.kindawk.ui;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vvsemir.kindawk.R;
import com.vvsemir.kindawk.provider.NewsWall;
import com.vvsemir.kindawk.provider.NewsWallProvider;

import com.vvsemir.kindawk.service.ICallback;
import com.vvsemir.kindawk.service.ProviderService;
import com.vvsemir.kindawk.service.RequestParams;

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
        newsRecyclerAdapter = new NewsRecyclerAdapter(getActivity());
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_news, container, false);
        final RecyclerView recyclerView = view.findViewById(R.id.newsList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(newsRecyclerAdapter);
        recyclerView.addOnScrollListener(recyclerViewOnScrollListener);
        return view;
    }

    @Override
    public void updateViews(Parcelable data) {
        newsRecyclerAdapter.addItems((NewsWall)data);
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

        ProviderService.getWall(new ICallback<NewsWall>() {
            @Override
            public void onResult(NewsWall result) {
                updateViews(result);
            }

            @Override
            public void onError(Throwable throwable) {
                //to do
                Log.d("getWall", "getWall : loading exception!!!" + throwable.getMessage() );
            }
        }, params);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO: Use the ViewModel
    }

}
