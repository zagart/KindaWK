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

public class BitmapDiskCache implements IDiskCache<String, Bitmap, byte[]> {
    private final String diskCachePath;

    public BitmapDiskCache(final Context context) {
        diskCachePath = context.getCacheDir().toString();
        //Environment.getExternalStorageDirectory().toString();
    }

    @Override
    public boolean save(String key, byte[] data) {
        try {
            final File file = new File(diskCachePath, Uri.parse(key).getLastPathSegment());
            final FileOutputStream stream = new FileOutputStream(file);
            stream.write(data);
            stream.flush();
            stream.close();

            return true;
        } catch (final IOException ex) {
            Log.e("BitmapDiskCache", ex.getMessage());
            return false;
        }
    }

    @Override
    public Bitmap load(String key) {
        return BitmapFactory.decodeFile(new File(diskCachePath, Uri.parse(key).getLastPathSegment()).getPath());
    }

    public void clearCacheDir(){
        deleteTempFiles(new File(diskCachePath), false);
    }

    public static void deleteTempFiles(File file, boolean deleteRoot) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        deleteTempFiles(f, true);
                    } else {
                        f.delete();
                    }
                }
            }
        }

        if(deleteRoot){
            file.delete();
        }
    }
}
