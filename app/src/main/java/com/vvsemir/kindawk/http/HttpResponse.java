package com.vvsemir.kindawk.http;

import org.json.JSONObject;

public class HttpResponse {
    private String response = new String();
    private int status;

    public HttpResponse setResponse(int statusCode, String res){
        status = statusCode;
        response = res;

        return this;
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
}
