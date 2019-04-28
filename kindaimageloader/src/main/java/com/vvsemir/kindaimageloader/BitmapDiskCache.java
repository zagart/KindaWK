package com.vvsemir.kindaimageloader;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapDiskCache implements IDiskCache<String, Bitmap> {
    private final String diskCachePath;

    public BitmapDiskCache(final Context context) {
        diskCachePath = context.getCacheDir().toString();
        //Environment.getExternalStorageDirectory().toString();
    }

    @Override
    public boolean save(final String key, final Bitmap value) {
        try {
            final File file = new File(diskCachePath, Uri.parse(key).getLastPathSegment());
            final FileOutputStream stream = new FileOutputStream(file);

            value.compress(Bitmap.CompressFormat.JPEG, 100, stream); //TODO Handle image size compression
            stream.flush();
            stream.close();

            return true;
        } catch (final IOException ex) {
            Log.e("BitmapDiskCache", ex.getMessage());
            return false;
        }
    }

    @Override
    public Bitmap load(final String key) {
        return BitmapFactory.decodeFile(new File(diskCachePath, Uri.parse(key).getLastPathSegment()).toString());
    }
}
