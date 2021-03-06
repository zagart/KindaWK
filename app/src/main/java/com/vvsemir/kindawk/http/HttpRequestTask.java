package com.vvsemir.kindawk.http;

import com.vvsemir.kindawk.service.CallbackExceptionFactory;
import com.vvsemir.kindawk.service.ICallback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequestTask implements IHttpRequestTask <HttpRequest, HttpResponse, Integer>{
    private static final String EXCEPTION_NETWORK_CONNECTION = "Please, check network connection";
    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECTION_TIMEOUT = 7000;

    public HttpRequestTask(){
    }

    @Override
    public HttpResponse execute(final HttpRequest httpRequest, ICallback<Integer> callbackOnResult) throws Exception {
        HttpURLConnection connection = null;
        HttpResponse result = null;

        try {
            URL url = new URL(httpRequest.getStringUrl());
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setRequestMethod(httpRequest.getRequestMethod());
            connection.setDoOutput(httpRequest.isPostRequest());

            if (httpRequest.isPostRequest()) {
                connection.getOutputStream().write(httpRequest.getBodyRequest().getBytes("UTF-8"));
            }

            int responseCode = connection.getResponseCode();

            if (responseCode == -1) {
                throw new IOException();
            }

            StringBuilder stringBuilder = new StringBuilder();
            InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(streamReader);
            String lineBuf;

            while ((lineBuf = bufferedReader.readLine()) != null) {
                stringBuilder.append(lineBuf);
            }

            bufferedReader.close();
            streamReader.close();
            result = new HttpResponse(responseCode, stringBuilder.toString());

            if(callbackOnResult != null) {
                callbackOnResult.onResult(responseCode);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            throw new CallbackExceptionFactory.Companion.NetworkException(EXCEPTION_NETWORK_CONNECTION);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }

            return result;
        }
    }
}
