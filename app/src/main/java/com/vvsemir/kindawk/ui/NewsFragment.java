package com.vvsemir.kindawk.ui;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.vvsemir.kindawk.R;
import com.vvsemir.kindawk.provider.NewsWall;
import com.vvsemir.kindawk.provider.NewsWallProvider;

import com.vvsemir.kindawk.service.ICallback;
import com.vvsemir.kindawk.service.ProviderService;
import com.vvsemir.kindawk.service.RequestParams;

public class NewsFragment extends Fragment {
    private NewsRecyclerAdapter newsRecyclerAdapter;
    private ProgressBar progressBar;
    private LinearLayoutManager layoutManager;
    RecyclerView recyclerView;
    BaseRecyclerViewScrollListener recyclerViewOnScrollListener;
    private boolean isLoading = false;

    public static NewsFragment newInstance() {
        NewsFragment fragment = new NewsFragment();

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newsRecyclerAdapter = new NewsRecyclerAdapter(getActivity());
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        recyclerViewOnScrollListener = new BaseRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadMoreItems(page, page + NewsWallProvider.PAGE_SIZE);
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        };

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_news, container, false);
        progressBar =  view.findViewById(R.id.progressBarNews);
        recyclerView = view.findViewById(R.id.newsList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(newsRecyclerAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.addOnScrollListener(recyclerViewOnScrollListener);
        loadData();

        return view;
    }

    public void updateViewsWithData(Parcelable data) {
        if(data == null) {
            return;
        }

        //newsRecyclerAdapter.setShowLoadingProgress(false);
        showProgressView(false);
        newsRecyclerAdapter.updateItems((NewsWall)data);
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                //newsRecyclerAdapter.notifyItemRangeInserted(curSize, allContacts.size() - 1);
                newsRecyclerAdapter.notifyDataSetChanged();
            }
        });
        isLoading = false;
    }

    private void loadData() {
        loadMoreItems(0, NewsWallProvider.PAGE_SIZE);
    }

    private void loadMoreItems(final int startPosition, final int endPosition) {
        isLoading = true;
        //newsRecyclerAdapter.setShowLoadingProgress(true);
        showProgressView(true);

        RequestParams params = new RequestParams();
        params.put(NewsWallProvider.PARAM_REQUEST_RANGE_START, startPosition);
        params.put(NewsWallProvider.PARAM_REQUEST_RANGE_END, endPosition);

        ProviderService.getWall(new ICallback<NewsWall>() {
            @Override
            public void onResult(NewsWall result) {
                updateViewsWithData(result);
            }

            @Override
            public void onNotify(NewsWall result) {
                //to do
                Log.d("getNewsFeed", "getNewsFeed: notification refresh!!!");
            }

            @Override
            public void onError(Throwable throwable) {

                //to do
                Log.d("getWall", "getWall : loading exception!!!" + throwable.getMessage() );
            }
        }, params);
    }

    void showProgressView(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO: Use the ViewModel
    }

}
