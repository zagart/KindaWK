package com.vvsemir.kindawk.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vvsemir.kindawk.R;

public class NewsItemView extends RelativeLayout {

    private TextView postTextView;
    private TextView postDateView;

    public NewsItemView(Context context) {
        super(context);
        initInflating(context);
    }

    public NewsItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initInflating(context);
    }

    public NewsItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initInflating(context);
    }

    void initInflating(Context context) {
        if (context != null) {
            View.inflate(context, R.layout.view_news_recycler_item, this);
            postTextView = findViewById(R.id.postTextView);
            postDateView = findViewById(R.id.postDateView);
        }
    }

    public NewsItemView setPostText(final String post) {
        postTextView.setText(post);
        return this;
    }

    public NewsItemView setPostDate(final String post) {
        postDateView.setText(post);
        return this;
    }


}
