package com.vvsemir.kindawk.http;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class HttpResponse implements Parcelable {
    private int status;
    private String response;

    public HttpResponse(int statusCode, String res){
        status = statusCode;
        response = res;
    }

    private HttpResponse(Parcel in) {
        status = in.readInt();
        response = in.readString();
    }

    public String getResponseAsString(){
        return response;
    }

    public JSONObject GetResponseAsJSON() {
        try {
            JSONObject rootJson = new JSONObject(response);

            return rootJson;
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(status);
        dest.writeString(response);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<HttpResponse> CREATOR = new Parcelable.Creator<HttpResponse>() {

        public HttpResponse createFromParcel(Parcel in) {
            return new HttpResponse(in);
        }

        public HttpResponse[] newArray(int size) {
            return new HttpResponse[size];
        }
    };

}
