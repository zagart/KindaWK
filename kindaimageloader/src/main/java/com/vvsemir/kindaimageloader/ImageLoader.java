package com.vvsemir.kindaimageloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ImageLoader implements IImageLoader {
    private static ImageLoader instance;

    private final Executor executor = Executors.newCachedThreadPool();
    private final LruCache<String, Bitmap> lruCache = new LruCache<String, Bitmap>((int) (Runtime.getRuntime().maxMemory() / 1024 / 2)) {

        @Override
        protected int sizeOf(final String key, final Bitmap value) {
            return value.getByteCount() / 1024;
        }
    };


    public static synchronized ImageLoader getInstance() {
        if (instance == null) {
            instance = new ImageLoader();
        }
        return instance;
    }

    @Override
    public void loadAndShow(final ImageView imageView, final String uri) {
        if (uri.isEmpty() || isLoadAlreadyStarted(uri, imageView)) {
            return;
        }

        imageView.setTag(uri + imageView.hashCode());
        imageView.setImageBitmap(null);
        imageView.setImageResource(R.drawable.ic_default_photo);

        executor.execute(new Runnable() {

            @Override
            public void run() {
                loadFromMemoryCache(uri, new ILoaderCallback<Bitmap>() {

                    @Override
                    public void onResult(final Bitmap memoryBitmap) {
                        if (memoryBitmap == null) {
                            loadFromDiskCache(uri, new ILoaderCallback<Bitmap>() {

                                @Override
                                public void onResult(final Bitmap diskBitmap) {
                                    if (diskBitmap == null) {
                                        try {
                                            loadFromNetwork(uri, new ILoaderCallback<Bitmap>() {

                                                @Override
                                                public void onResult(final Bitmap networkBitmap) {
                                                    if (networkBitmap == null) {
                                                        showErrorImage(imageView);
                                                    } else {
                                                        showImage(imageView, networkBitmap);
                                                    }
                                                }

                                                @Override
                                                public void onError(final Throwable throwable) {
                                                    showErrorImage(imageView);
                                                }
                                            });
                                        } catch (IOException pE) {
                                            showErrorImage(imageView);
                                        }
                                    } else {
                                        showImage(imageView, diskBitmap);
                                    }
                                }

                                @Override
                                public void onError(final Throwable throwable) {
                                    //showErrorImage(pImageView);
                                }
                            });
                        } else {
                            showImage(imageView, memoryBitmap);
                        }
                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        //showErrorImage(pImageView);
                    }
                });
            }
        });
    }

    private boolean isLoadAlreadyStarted(final String uri, final ImageView imageView) {
        if (imageView.getTag() != null && imageView.getTag().equals(uri + imageView.hashCode())) {
            return true;
        }

        return false;
    }

    private void loadFromMemoryCache(final String uri, final ILoaderCallback<Bitmap> callback) {
        synchronized (lruCache) {
            callback.onResult(lruCache.get(uri));
        }
    }

    private void loadFromDiskCache(final String uri, final ILoaderCallback<Bitmap> callback) {
        callback.onResult(null);
    }

    private void loadFromNetwork(final String uri, final ILoaderCallback<Bitmap> callback) throws IOException {
        byte[] imageBytes = getBytesFromNetworkFile(new URL(uri));

        if (imageBytes != null && imageBytes.length > 0) {
            final Bitmap bitmap = getBitmapFromBytes(imageBytes);
            putInMemoryCache(uri, bitmap);
            callback.onResult(bitmap);

            return;
        }

        callback.onResult(null);
    }

    private void putInMemoryCache(final String uri, final Bitmap bitmap) {
        synchronized (lruCache) {
            lruCache.put(uri, bitmap);
        }
    }

    void showImage(final ImageView imageView, final Bitmap bitmap) {
        imageView.post(new Runnable() {
            @Override
            public void run() {
                imageView.setBackground(null);
                imageView.setImageBitmap(bitmap);
            }
        });
    }

    void showErrorImage(final ImageView pImageView) {
        pImageView.post(new Runnable() {
            @Override
            public void run() {
                pImageView.setImageResource(R.drawable.ic_error_photo);
            }
        });
    }

/////////////////////////////////////
    @Override
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

    @Override
    public byte[] getBytesFromNetworkFile(URL url) {
        return HttpFileLoader.downloadBytes(url);
    }

    @Override
    public Bitmap getBitmapFromBytes(byte[] imageBytes) {
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

    @Override
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
