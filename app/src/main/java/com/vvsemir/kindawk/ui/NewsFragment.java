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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.vvsemir.kindaimageloader.ImageLoader;
import com.vvsemir.kindawk.R;
import com.vvsemir.kindawk.UserActivity;
import com.vvsemir.kindawk.provider.NewsPost;
import com.vvsemir.kindawk.provider.NewsWall;
import com.vvsemir.kindawk.provider.NewsWallProvider;

import com.vvsemir.kindawk.service.ICallback;
import com.vvsemir.kindawk.service.ProviderService;
import com.vvsemir.kindawk.service.RequestParams;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class NewsFragment extends KindaFragment {
    public static final String FRAGMENT_TAG = "NewsFragmentTag";
    private static final String CURRENT_POSITION = "CurrentPosition";

    private NewsRecyclerAdapter newsRecyclerAdapter;
    private ProgressBar progressBar;
    private LinearLayoutManager layoutManager;
    RecyclerView recyclerView;
    BaseRecyclerViewScrollListener recyclerViewOnScrollListener;
    private int currentPosition;

    public static NewsFragment newInstance() {
        NewsFragment fragment = new NewsFragment();

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

        newsRecyclerAdapter = new NewsRecyclerAdapter(getActivity());
        recyclerViewOnScrollListener = new BaseRecyclerViewScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadMoreItems(totalItemsCount, totalItemsCount + NewsWallProvider.PAGE_SIZE);
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

        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewOnScrollListener.initLayoutManager(layoutManager);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(newsRecyclerAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.addOnScrollListener(recyclerViewOnScrollListener);
        loadData();

        if(savedInstanceState != null && savedInstanceState.containsKey(CURRENT_POSITION)){
            currentPosition = savedInstanceState.getInt(CURRENT_POSITION);
        }

        return view;
    }

    public void updateViewsWithData(final Parcelable data) {
        if(data == null) {
            return;
        }
        NewsWall freshNewsWall =  (NewsWall)data;
        showProgressView(false);

        if(freshNewsWall.getCount() > 0) {
            newsRecyclerAdapter.updateItems(freshNewsWall);
            newsRecyclerAdapter.notifyDataSetChanged();

            if(currentPosition > 0 && currentPosition < newsRecyclerAdapter.getItemCount()) {
                recyclerView.scrollToPosition(currentPosition);
            }
        }
    }

    @Override
    public void loadData() {
        if(getProviderService() == null){
            return;
        }

        loadMoreItems(0, NewsWallProvider.PAGE_SIZE);
    }

    private void loadMoreItems(final int startPosition, final int endPosition) {
        showProgressView(true);

        RequestParams params = new RequestParams();
        params.put(NewsWallProvider.PARAM_REQUEST_RANGE_START, startPosition);
        params.put(NewsWallProvider.PARAM_REQUEST_RANGE_END, endPosition);

        getProviderService().getWall( params, new ICallback<NewsWall>() {
            @Override
            public void onResult(NewsWall result) {
                updateViewsWithData(result);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d(FRAGMENT_TAG, NewsWallProvider.EXCEPTION_LOADING_API);
            }
        });
    }

    void showProgressView(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_refresh) {
            ImageLoader.getInstance(getContext()).cleanCache();
            updateNotificationBadge();

            getProviderService().cleanNewsWall(new ICallback<Integer>() {
                @Override
                public void onResult(Integer result) {
                    newsRecyclerAdapter.cleanWall();
                    loadData();
                }

                @Override
                public void onError(Throwable throwable) {

                }
            });
        }

        return super.onOptionsItemSelected(item);

    }

    public void updateNotificationBadge(){
        ((UserActivity)getActivity()).eraseNotificationBadge();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        currentPosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        outState.putInt(CURRENT_POSITION, currentPosition);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO: Use the ViewModel
    }

    @Override
    public String getFragmentTag() {
        return FRAGMENT_TAG;
    }

    private final ProviderService getProviderService(){
        return ((UserActivity)getActivity()).getProviderService();
    }
}
