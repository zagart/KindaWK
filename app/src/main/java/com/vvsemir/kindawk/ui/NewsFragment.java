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

import com.vvsemir.kindawk.R;
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
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        newsRecyclerAdapter = new NewsRecyclerAdapter(getActivity());
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        recyclerViewOnScrollListener = new BaseRecyclerViewScrollListener(layoutManager) {
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
        //progressBar =  view.findViewById(R.id.progressBarNews);
        recyclerView = view.findViewById(R.id.newsList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(newsRecyclerAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.addOnScrollListener(recyclerViewOnScrollListener);
        loadData();

        return view;
    }

    public void updateViewsWithData(final Parcelable data) {
        if(data == null) {
            return;
        }
        NewsWall freshNewsWall =  (NewsWall)data;
        //newsRecyclerAdapter.setShowLoadingProgress(false);
        showProgressView(false);
        Log.d("WWR updateViewsWithData", "here!!! " + data.hashCode());
        //recyclerView.post(new Runnable() {
        //    @Override
        //    public void run() {
        if(freshNewsWall.getCount() > 0) {
            newsRecyclerAdapter.updateItems(freshNewsWall);
            newsRecyclerAdapter.notifyDataSetChanged();
        }
                //newsRecyclerAdapter.notifyItemRangeInserted(curSize, allContacts.size() - 1);

          //  }
        //});
        isLoading = false;
    }

    @Override
    public void loadData() {
        loadMoreItems(0, NewsWallProvider.PAGE_SIZE);
    }

    private void loadMoreItems(final int startPosition, final int endPosition) {
        isLoading = true;
        //newsRecyclerAdapter.setShowLoadingProgress(true);
        showProgressView(true);

        RequestParams params = new RequestParams();
        params.put(NewsWallProvider.PARAM_REQUEST_RANGE_START, startPosition);
        params.put(NewsWallProvider.PARAM_REQUEST_RANGE_END, endPosition);

        ProviderService.getWall( params, new ICallback<NewsWall>() {
            @Override
            public void onResult(NewsWall result) {
                updateViewsWithData(result);
            }

            @Override
            public void onNotify(NewsWall result) {
                //to do
                Log.d("WWRgetWall", "getNewsFeed: notification refresh!!!");
            }

            @Override
            public void onError(Throwable throwable) {

                NewsWall errorNews = new NewsWall();
                NewsPost errorPost = new NewsPost();
                errorPost.setPostText(NewsWallProvider.EXCEPTION_LOADING_API);
                errorNews.addPost(errorPost);
                updateViewsWithData(errorNews);

                Log.d("WWRonError", "getWall : loading exception!!!" + throwable.getMessage() );
            }
        });
    }

    void showProgressView(boolean show) {
        //progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_refresh) {
            ProviderService.cleanNewsWall();
            loadData();
        }

        return super.onOptionsItemSelected(item);

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
}
