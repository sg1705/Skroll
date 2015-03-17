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

    private static Gson getGson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
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
                if (entry.getKey().startsWith("Is")) {
                    //boolean
                    coreMap.set(entry.getKey(), new Boolean(elmt.getAsBoolean()));
                } else {
                    // add in the core map
                    coreMap.set(entry.getKey(), elmt.getAsString());
                }
            } else if (elmt.isJsonArray()) {
                coreMap.set(entry.getKey(), processArray(elmt));
            } else if (elmt.isJsonObject()) {
                coreMap.set(entry.getKey(), processObject(elmt));
            }
        }
        return coreMap;
    }

}
