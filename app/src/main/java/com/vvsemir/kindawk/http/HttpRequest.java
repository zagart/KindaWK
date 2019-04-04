package com.vvsemir.kindawk.http;

import com.vvsemir.kindawk.service.RequestParams;
import com.vvsemir.kindawk.auth.AuthManager;

public class HttpRequest {
    private static final String VKCLIENT_HTTP_REQUEST_URL = "https://api.vk.com/method/";
    private static final String APP_VKCLIENT_HTTP_REQUEST_POST = "POST";
    private static final String APP_VKCLIENT_HTTP_REQUEST_GET = "GET";


    String method;
    boolean post = false;
    RequestParams requestParams;

    public HttpRequest(String methodName, boolean isPost, RequestParams reqParams) {
        this.method = methodName;
        this.post = isPost;
        this.requestParams = reqParams;
    }

    public String getStringUrl(){
        String result = new String(VKCLIENT_HTTP_REQUEST_URL + method );

        if(post == false){
            result += "?" + getBodyRequest() ;
        }

        return result;
    }

    public boolean isPostRequest(){
        return post;
    }

    public String getBodyRequest(){
        if(requestParams == null) {

            return AuthManager.getTokenVersionString();
        }

        return requestParams.getParamsString() + "&" + AuthManager.getTokenVersionString();
    }

    public String getRequestMethod(){
        return post? APP_VKCLIENT_HTTP_REQUEST_POST : APP_VKCLIENT_HTTP_REQUEST_GET;
    }
}
