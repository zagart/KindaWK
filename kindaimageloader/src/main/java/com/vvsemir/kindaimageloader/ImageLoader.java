package com.vvsemir.kindaimageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ImageLoader implements IImageLoader {
    private static ImageLoader instance;

    //private final Executor executor = Executors.newFixedThreadPool(5);
    private final Executor executor = Executors.newCachedThreadPool();
    private final IDiskCache<String, Bitmap, byte[]> diskCache;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final LruCache<String, Bitmap> lruCache = new LruCache<String, Bitmap>((int) (Runtime.getRuntime().maxMemory() / 1024 / 2)) {
        @Override
        protected int sizeOf(final String key, final Bitmap value) {
            return value.getByteCount() / 1024;
        }
    };

    public ImageLoader(final Context context) {
        diskCache = new BitmapDiskCache(context);
    }


    public static synchronized ImageLoader getInstance(final Context context) {
        if (instance == null) {
            instance = new ImageLoader(context);
        }
        return instance;
    }


    @Override
    public void loadAndShow(final ImageView imageView, final String uri) {
        if (uri == null || uri.isEmpty()) {
            imageView.setImageResource(R.drawable.ic_error_photo);
            Log.d("PHOT loadAndShow", "nO uri");
            return;
        }

        imageView.setTag(uri);
        imageView.setImageResource(R.drawable.ic_default_photo);

        loadFromMemoryCache(uri, new ILoaderCallback<Bitmap>() {

            @Override
            public void onResult(Bitmap cachedBitmap) {
                if (cachedBitmap == null) {
                    executor.execute(new Runnable() {
                         @Override
                         public void run() {
                             loadFromDiskCache(uri, new ILoaderCallback<Bitmap>(){
                                 @Override
                                 public void onResult(Bitmap diskBitmap) {
                                     if (diskBitmap == null) {
                                         try {
                                             loadFromNetwork(uri, new ILoaderCallback<Bitmap>() {

                                                 @Override
                                                 public void onResult(final Bitmap networkBitmap) {
                                                     if (networkBitmap == null) {
                                                         showErrorImage(uri, imageView);
                                                         Log.d("PHOT net", "err:" + uri);
                                                     } else {
                                                         showImage(uri, imageView, networkBitmap); // From network
                                                     }
                                                 }

                                                 @Override
                                                 public void onError(final Throwable throwable) {
                                                     showErrorImage(uri, imageView);
                                                     Log.d("PHOT net1", "ex" + throwable.getMessage());
                                                 }
                                             });
                                         } catch (IOException ex) {
                                             showErrorImage(uri, imageView);
                                             Log.d("PHOT net2", "ex" + ex.getMessage());
                                         }
                                     } else {
                                         showImage(uri, imageView, diskBitmap); //from disk cache
                                         Log.d("PHOT diskcach", "disk cash" + uri);
                                     }
                                 }

                                 @Override
                                 public void onError(Throwable throwable) {

                                 }
                             });
                         }
                    });
                } else {
                    showImage(uri, imageView, cachedBitmap);
                }
            }

            @Override
            public void onError(Throwable throwable) {
            }
        });
    }

    private void loadFromMemoryCache(final String uri, final ILoaderCallback<Bitmap> callback) {
        synchronized (lruCache) {
            Bitmap bitmap = null;
            try {
                bitmap =  lruCache.get(uri);
            } catch(Exception ex) {
                ex.printStackTrace();
            } finally {
                callback.onResult(bitmap);
            }
        }
    }

    private void loadFromDiskCache(final String uri, final ILoaderCallback<Bitmap> callback) {
        synchronized (diskCache) {
            Bitmap bitmap = null;
            try {
                bitmap =  diskCache.load(uri);
            } catch(Exception ex) {
                ex.printStackTrace();
            } finally {
                callback.onResult(bitmap);
            }
        }
    }


    private void loadFromNetwork(final String uri, final ILoaderCallback<Bitmap> callback) throws IOException {
        byte[] imageBytes = getBytesFromNetworkFile(new URL(uri));

        if (imageBytes != null && imageBytes.length > 0) {
            final Bitmap bitmap = getBitmapFromBytes(imageBytes);
            callback.onResult(bitmap);
            putInMemoryCache(uri, bitmap);
            putInDiskCache(uri, imageBytes);

            return;
        }

        callback.onResult(null);
    }

    private void putInMemoryCache(final String uri, final Bitmap bitmap) {
        synchronized (lruCache) {
            lruCache.put(uri, bitmap);
        }
    }

    private void putInDiskCache(final String uri, final byte[] imageBytes) {
        synchronized (diskCache) {
            diskCache.save(uri, imageBytes);
        }
    }


    void showImage(final String uri, final ImageView imageView, final Bitmap bitmap) {
        if ( isViewTagValid (uri, imageView)) {
            handler.post(new Runnable() {

                @Override
                public void run() {
                    imageView.setBackground(null);
                    imageView.setImageBitmap(bitmap);
                    imageView.invalidate();
                }
            });
        }
    }

    void showErrorImage(final String uri, final ImageView imageView) {
        if ( isViewTagValid (uri, imageView)) {
            handler.post(new Runnable() {

                @Override
                public void run() {
                    imageView.setImageResource(R.drawable.ic_error_photo);                }
            });
        }
    }

    private boolean isViewTagValid(final String uri, final ImageView imageView) {
        return imageView.getTag() == null || (imageView.getTag() != null && uri.equals(imageView.getTag()));

    }

    public void cleanCache() {
        synchronized (lruCache) {
            lruCache.evictAll();
        }
        synchronized (diskCache) {
            ((BitmapDiskCache)diskCache).clearCacheDir();
        }
    }

/////////////////////////////////////
    public static Uri createTempPhotoFile(URL url) {
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

    public static byte[] getBytesFromNetworkFile(URL url) {
        return HttpFileLoader.downloadBytes(url);
    }

    public static Bitmap getBitmapFromBytes(byte[] imageBytes) {
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, options);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return bitmap;
        }
    }

    public static Bitmap getBitmapFromFile(Uri uriFile) {
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
