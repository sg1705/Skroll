package com.skroll.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by saurabh on 12/22/14.
 */
public class ModelHelper {

    public static String getJson(HtmlDocument doc) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(doc);
        return jsonString;
    }

    public static HtmlDocument getModel(String jsonString) {
        Gson gson = new Gson();
        Type docType = new TypeToken<HtmlDocument>() {}.getType();
        HtmlDocument newDoc = gson.fromJson(jsonString, docType);
        return newDoc;
    }
}
