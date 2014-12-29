package com.skroll.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;

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

    public static HtmlDocument tokenizeModel(String html) {
        HtmlDocument htmlDoc = new HtmlDocument();
        htmlDoc.setSourceHtml(html);
        //create a pipeline
        Pipeline<HtmlDocument, HtmlDocument> pipeline =
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

}
