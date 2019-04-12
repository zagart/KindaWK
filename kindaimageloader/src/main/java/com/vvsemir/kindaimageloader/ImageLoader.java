package com.vvsemir.kindaimageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ImageLoader {
    private static ImageLoader instance;

    public static synchronized ImageLoader getInstance() {
        if (instance == null) {
            instance = new ImageLoader();
        }
        return instance;
    }

    public Uri createTempPhotoFile(URL url) {
        File file;
        try {
            String fileName = Uri.parse(url.toString()).getLastPathSegment();
            file = File.createTempFile(fileName, null); //, Context.getCacheDir());
            HttpFileLoader.downloadToFile(file, url);

            return Uri.fromFile(file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public byte[] getBytesFromFile(URL url) {
        return HttpFileLoader.downloadBytes(url);
    }

    public Bitmap getBitmapFromBytes(byte[] imageBytes) {
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, options);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return bitmap;
    }

    public Bitmap getBitmapFromFile(Uri uriFile) {
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeFile(uriFile.getPath(), options);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return bitmap;
    }
}
