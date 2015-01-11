package com.skroll.document;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;

import java.lang.reflect.Type;

/**
 * Created by saurabh on 12/22/14.
 */
public class ModelHelper {

    public static String getJson(Document doc) {
        Gson gson = getGson();
        String jsonString = gson.toJson(doc);
        return jsonString;
    }

    public static Document getModel(String jsonString) {
        Gson gson = getGson();
        Type docType = new TypeToken<Document>() {}.getType();
        Document newDoc = gson.fromJson(jsonString, docType);
        return newDoc;
    }

    public static Document tokenizeModel(String html) {
        Document htmlDoc = new Document();
        htmlDoc.setSource(html);
        //create a pipeline
        Pipeline<Document, Document> pipeline =
                new Pipeline.Builder()
                        .add(Pipes.PARSE_HTML_TO_DOC)
                        .add(Pipes.REMOVE_BLANK_PARAGRAPH_FROM_HTML_DOC)
                        .add(Pipes.REMOVE_NBSP_IN_HTML_DOC)
                        .add(Pipes.REPLACE_SPECIAL_QUOTE_IN_HTML_DOC)
                        .add(Pipes.TOKENIZE_PARAGRAPH_IN_HTML_DOC)
                        .build();
        htmlDoc = pipeline.process(htmlDoc);
        return htmlDoc;
    }

    public static Gson getGson() {
        Gson gson = new GsonBuilder().create();
        return gson;
    }


}
