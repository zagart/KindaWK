package com.vvsemir.kindawk.Models;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class VKRequestParams {
    private HashMap<String, String> params;
    String method;

    public Params(String methodName){
        method = methodName;
        params = new HashMap<String, String>();
    }

    public boolean contains(String paramName){
        return params.containsKey(paramName);
    }
    public void put(String paramName, String paramValue) {
        params.put(paramName, paramValue);
    }
    public void put(String paramName, Integer paramValue) {
        params.put(paramName, Integer.toString(paramValue));
    }
    public void putDouble(String param_name, Double paramValue) {
        params.put(param_name, Double.toString(paramValue));
    }

    public String getParamsString() {
        String paramsString= new String();
        try {
            Iterator<Map.Entry<String, String>> it = params.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> pair = it.next();
                paramsString += pair.getKey() + "=" +
                                URLEncoder.encode(pair.getValue(), "utf-8");
                if(it.hasNext())
                    paramsString += "&";
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return params;
    }
}
