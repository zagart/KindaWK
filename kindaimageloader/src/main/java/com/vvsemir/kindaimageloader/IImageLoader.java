package com.vvsemir.kindaimageloader;

import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

import java.net.URL;

public interface IImageLoader {
    void loadAndShow(final ImageView pImageView, final String uri);
}
