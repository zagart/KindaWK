package com.vvsemir.kindawk.provider;

import android.content.ContentValues;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.vvsemir.kindaimageloader.ILoaderCallback;
import com.vvsemir.kindaimageloader.ImageLoader;
import com.vvsemir.kindawk.auth.AuthManager;
import com.vvsemir.kindawk.db.DbManager;
import com.vvsemir.kindawk.http.HttpRequest;
import com.vvsemir.kindawk.http.HttpRequestTask;
import com.vvsemir.kindawk.http.HttpResponse;
import com.vvsemir.kindawk.service.CallbackExceptionFactory;
import com.vvsemir.kindawk.service.ICallback;
import com.vvsemir.kindawk.service.ProviderService;
import com.vvsemir.kindawk.service.RequestParams;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PhotosProvider implements Runnable {
    public static final String EXCEPTION_LOADING_API = "Sorry, can not read photos from server";
    static final String ARG_PARAM_REQUEST_METHOD = "photos.getAll";
    static final String PARAM_REQUEST_ALBUMID = "profile";
    public static final String PARAM_REQUEST_OWNERID = "owner_id";

    ILoaderCallback<List<Photo>> callback;
    RequestParams requestParams;

    private List<Photo> photos = new ArrayList<>();

    public PhotosProvider(ILoaderCallback<List<Photo>> callback) {
        this.callback = callback;
    }


    public void setRequestParams(RequestParams request) {
        requestParams = request;

        if(requestParams == null) {
            requestParams = new RequestParams();
            requestParams.put("owner_id", AuthManager.getCurrentToken().getUserId());
        }
        //requestParams.put("album_id", PARAM_REQUEST_ALBUMID);
    }

    synchronized void loadData() {
        try {
            List<Photo> apiPhotos = loadApiData();

            if (apiPhotos != null && apiPhotos.size() > 0) {
                photos.addAll(apiPhotos);
            }

            ProviderService.getInstance().getHandler().post(new Runnable() {
                @Override
                public void run() {
                    callback.onResult(photos);
                }
            });
        } catch (Exception ex){
            ex.printStackTrace();
            final Throwable throwable;

            if(ex instanceof CallbackExceptionFactory.Companion.NetworkException) {
                throwable = ex;
            } else {
                throwable = CallbackExceptionFactory.Companion.createException
                        (CallbackExceptionFactory.THROWABLE_TYPE_ERROR, EXCEPTION_LOADING_API);
            }

            ProviderService.getInstance().getHandler().post(new Runnable() {
                @Override
                public void run() {
                    callback.onError(throwable);
                }
            });
        }
    }

    List<Photo> loadApiData() {
        List<Photo> photos = null;

        try {
            HttpResponse httpResponse = new HttpRequestTask().execute(
                    new HttpRequest(ARG_PARAM_REQUEST_METHOD, false, requestParams), null);

            photos = getPhotosFromHttp(httpResponse);
        } catch (CallbackExceptionFactory.Companion.NetworkException ex){
            throw ex;
        } catch (Exception ex){
            ex.printStackTrace();
            throw new Exception(EXCEPTION_LOADING_API);
        } finally {
            return photos;
        }
    }

    private List<Photo> getPhotosFromHttp(final HttpResponse httpResponse) {
        List<Photo> photos = null;

        try{
            Gson gson = new Gson().newBuilder().create();
            JsonObject httpObj = gson.fromJson(((HttpResponse)httpResponse).getResponseAsString(), JsonObject.class);
            JsonObject response = httpObj.getAsJsonObject("response");

            if(response != null) {
                JsonArray items = response.getAsJsonArray("items");
                photos = gson.fromJson(items, new TypeToken<ArrayList<Photo>>() {}.getType());
            }
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            return photos;
        }
    }

    private int getOwnerId() {
        int ownerId = 0;
        if ( requestParams != null && requestParams.contains(PARAM_REQUEST_OWNERID) ) {
            ownerId = Integer.parseInt(requestParams.getParam(PARAM_REQUEST_OWNERID));
        }

        if(ownerId == 0){
            ownerId = AuthManager.getCurrentToken().getUserId();
        }

        return ownerId;
    }


    public void resetData() {
    }

    @Override
    public void run() {
        loadData();
    }
}
