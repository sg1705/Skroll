package com.skroll.document;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;

import java.lang.reflect.Type;
import java.util.*;

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

    private static Gson getGson() {
        Gson gson = new GsonBuilder()
                        .create();
        return gson;
    }


    public Document fromJson(String json) throws Exception {

        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(json);

        //assume that the first element is a map
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> set = jsonObject.entrySet();
        JsonElement startingMap = set.iterator().next().getValue();
        //processJson(startingMap);
        CoreMap coreMap = processObject(startingMap);
        Document document = new Document(coreMap);
        return document;
    }

     private List<CoreMap> processArray(JsonElement element) throws Exception {
        List<CoreMap> coreMapList = new ArrayList<CoreMap>();
        Iterator<JsonElement> elements = element.getAsJsonArray().iterator();
        while (elements.hasNext()) {
            JsonElement elmt = elements.next();
            //assume that it is a list of coreMap. Hence, each element has to be a object
            if (elmt.isJsonObject()) {
                CoreMap map = processObject(elmt);
                coreMapList.add(map);
            } else {
                System.out.println("WHOOAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                throw new Exception("Deserialzation failed because array is not a List<CoreMap>");
            }
        }
        return coreMapList;
    }


    private CoreMap processObject(JsonElement element) throws Exception {
        CoreMap coreMap = new CoreMap();
        Set<Map.Entry<String, JsonElement>> set = element.getAsJsonObject().entrySet();
        for(Map.Entry<String, JsonElement> entry : set) {
            JsonElement elmt = entry.getValue();
            if (elmt.isJsonPrimitive()) {
                // add in the core map
                coreMap.set(entry.getKey(), elmt.getAsString());
            } else if (elmt.isJsonArray()) {
                coreMap.set(entry.getKey(), processArray(elmt));
            } else if (elmt.isJsonObject()) {
                coreMap.set(entry.getKey(), processObject(elmt));
            }
        }
        return coreMap;
    }

}
