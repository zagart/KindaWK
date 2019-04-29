package com.vvsemir.kindawk.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vvsemir.kindawk.R;

public class NewsItemView extends RelativeLayout {
    private ImageView sourcePhotoView;
    private ImageView postPhotoView;
    private TextView sourceNameView;
    private TextView postIdView;
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
            postIdView = findViewById(R.id.postIdView);
            sourceNameView = findViewById(R.id.postSourceNameView);
            sourcePhotoView = findViewById(R.id.userPhotoView);
            postPhotoView = findViewById(R.id.postPhotoView);
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

    public NewsItemView setSourceName(final String name) {
        sourceNameView.setText(name);
        return this;
    }

    public NewsItemView setPostId(final String id) {
        postIdView.setText(id);
        return this;
    }

    public NewsItemView setSourcePhoto(final Bitmap avatar) {
        sourcePhotoView.setImageBitmap(avatar);
        return this;
    }

    public NewsItemView setPostPhoto(final Bitmap photo) {
        if(photo != null) {
            postPhotoView.setMaxWidth(140);
            postPhotoView.setImageBitmap(photo);
        } else {
            postPhotoView.setMaxWidth(0);
        }
        return this;
    }
}
