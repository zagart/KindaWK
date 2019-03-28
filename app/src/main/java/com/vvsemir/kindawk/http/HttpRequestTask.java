package com.vvsemir.kindawk.http;

import android.util.Log;

import com.vvsemir.kindawk.utils.ICallback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.vvsemir.kindawk.Models.Constants.*;

public class HttpRequestTask implements IHttpRequestTask <HttpRequest, HttpResponse>{

    public HttpRequestTask(){
    }

    @Override
    public void execute(final HttpRequest httpRequest, final ICallback<HttpResponse> callbackOnResult) {
        Thread thread = new Thread() {
            public void run(){
                HttpURLConnection connection = null;

                try {
                    HttpResponse httpResponse = new HttpResponse();
                    URL url = new URL(httpRequest.getStringUrl());
                    connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod(httpRequest.getRequestMethod());
                    connection.setDoOutput(httpRequest.isPostRequest());

                    // Send post request
                    if(httpRequest.isPostRequest()) {
                        connection.getOutputStream().write(httpRequest.getBodyRequest().getBytes("UTF-8"));
                    }

                    int responseCode = connection.getResponseCode();

                    if (responseCode == -1) {
                        throw new IOException();
                    }

                    //read response
                    StringBuffer strBuffer = new StringBuffer();

                    if(connection.getContentLength() > 0) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String lineBuf;

                        while ((lineBuf = bufferedReader.readLine()) != null) {
                            strBuffer.append(lineBuf);
                        }

                        bufferedReader.close();
                    }

                    callbackOnResult.onResult( httpResponse.setResponse( responseCode, strBuffer.toString() ) );

                } catch (IOException ex) {
                    ex.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }

            }
        };

        thread.start();
    }
}
