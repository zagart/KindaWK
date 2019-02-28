package com.vvsemir.kindawk.Models;

import static com.vvsemir.kindawk.Models.Constants.*;

public class HttpRequest {

    String method;
    boolean post = false;

    public String getStringUrl(){
        return APP_VKCLIENT_HTTP_REQUEST ;
    }

    public boolean isPostRquest(){
        return post;
    }

    public void setPostRquest(boolean b){
        post = b;
    }
    public String getBodyPostRequest(){

        return
    }
}
