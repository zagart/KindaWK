package com.vvsemir.kindawk.Models;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.vvsemir.kindawk.Models.Constants.*;

public class HttpRequestTask implements Runnable {
    static final String TAG="DEBUG HttpRequestTask";

    HttpRequest httpRequest;
    HttpResponse httpResponse;

    public HttpRequestTask(HttpRequest request, HttpResponse response){
        httpRequest = request;
        httpResponse = response;
    }

    @Override
    public void run() {
        try {
            URL url = new URL(httpRequest.getStringUrl());
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod(httpRequest.isPostRquest()? APP_VKCLIENT_HTTP_REQUEST_POST : APP_VKCLIENT_HTTP_REQUEST_GET);
            connection.setDoOutput(httpRequest.isPostRquest());

            // Send post request
            if(httpRequest.isPostRquest())
                connection.getOutputStream().write(httpRequest.getBodyPostRequest().getBytes("UTF-8"));

            int code=connection.getResponseCode();

            Log.i(TAG, "http response code = " + code);
            if (code == -1)
                throw new IOException();

            //read response
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer strBuffer = new StringBuffer();
            String lineBuf;
            while ((lineBuf = bufferedReader.readLine()) != null) {
                strBuffer.append(lineBuf);
            }
            bufferedReader.close();
            httpResponse.setStringResponse(strBuffer.toString());

        }
        catch (IOException e) {
            Log.i(TAG, "Exception in  httprequest thread");
        }
    }
}
