package com.vvsemir.kindawk.service;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RequestParams implements Parcelable {

    private HashMap<String, String> params = new HashMap<>();

    public RequestParams(){
    }

    private RequestParams(Parcel in) {

        Bundle bundle = in.readBundle(HashMap.class.getClassLoader());

        for (String key : bundle.keySet()) {
            params.put(key, bundle.getString(key));
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        final Bundle bundle = new Bundle();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            bundle.putString(entry.getKey(), entry.getValue());
        }

        dest.writeBundle(bundle);
    }

    public boolean contains(String paramKey){
        return params.containsKey(paramKey);
    }

    public void removeParam(String paramKey){
        params.remove(paramKey);
    }

    public String getParam(String paramKey){
        return params.get(paramKey);
    }


    public void put(String paramName, String paramValue) {
        params.put(paramName, paramValue);
    }

    public void put(String paramName, Integer paramValue) {
        params.put(paramName, Integer.toString(paramValue));
    }

    public void put(String paramName, Long paramValue) {
        params.put(paramName, Long.toString(paramValue));
    }

    public void put(String param_name, Double paramValue) {
        params.put(param_name, Double.toString(paramValue));
    }

    public String getParamsString() {
        String result = new String();
        try {
            Iterator<Map.Entry<String, String>> it = params.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> pair = it.next();
                result += pair.getKey() + "=" + URLEncoder.encode(pair.getValue(), "utf-8");
                if (it.hasNext()) {
                    result += "&";
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static final Parcelable.Creator<RequestParams> CREATOR = new Parcelable.Creator<RequestParams>() {

        public RequestParams createFromParcel(Parcel in) {
            return new RequestParams(in);
        }

        public RequestParams[] newArray(int size) {
            return new RequestParams[size];
        }
    };


}

