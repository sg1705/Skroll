package com.skroll.document;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by saurabh on 12/22/14.
 */
public class JsonDeserializer {
    public static final Logger logger = LoggerFactory
            .getLogger(Paragraph.class);

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


    public static Document fromJson(String json) throws Exception {

        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(json);

        //assume that the first element is a map
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> set = jsonObject.entrySet();
        Iterator<Map.Entry<String, JsonElement>> iterator = set.iterator();
        //process each element
        while (iterator.hasNext()) {
            Map.Entry<String, JsonElement> entry = iterator.next();
            JsonElement element = entry.getValue();
            String key = entry.getKey();
            if (key.equals("map")) {
                //it is a coremap
                //let's start
                CoreMap coreMap = processObject(element);
                Document document = new Document(coreMap);
                return document;

            }
        }
        return null;
    }

    //find out how many entries does it have
    //find if any entry is a map
    //find if any entry is a type array
    //then it is a list of what?
    private Class inferType(JsonElement element) {
        // if element has a child and it is only one and a map
        Set<Map.Entry<String, JsonElement>> set = element.getAsJsonObject().entrySet();
        if (set.size() == 1) {
            //check for child element name
            Map.Entry<String, JsonElement> child = set.iterator().next();
            if (child.getKey().equals("map")) {
                return CoreMap.class;
            }

        }
        return null;
    }


    private static CoreMap processObject(JsonElement element) throws Exception {
        CoreMap coreMap = new CoreMap();
        Set<Map.Entry<String, JsonElement>> set = element.getAsJsonObject().entrySet();
        logger.debug("EntrySet:"+set);
        for(Map.Entry<String, JsonElement> entry : set) {
            JsonElement elmt = entry.getValue();
            if (elmt.isJsonPrimitive()) {
                logger.debug("elmt.isJsonPrimitive():"+entry.getKey());
                if (entry.getKey().startsWith("Is")) {
                    //boolean
                    coreMap.set(entry.getKey(), new Boolean(elmt.getAsBoolean()));
                } else {
                    // add in the core map
                    coreMap.set(entry.getKey(), elmt.getAsString());
                }
            } else if (entry.getKey().equals("DefinedTermListAnnotation")){
                coreMap.set(entry.getKey(), processDefinedTerm(elmt));
            } else if (elmt.isJsonArray()) {
                //infer type
                coreMap.set(entry.getKey(), processArray(elmt));
            } else if (elmt.isJsonObject()) {
                logger.debug("elmt.isJsonObject():"+elmt);
                coreMap.set(entry.getKey(), processObject(elmt));
            }
        }
        return coreMap;
    }




    private static List<CoreMap> processArray(JsonElement element) throws Exception {
        //if it an array again other wise

        List<CoreMap> coreMapList = new ArrayList<CoreMap>();
        List<Object> ObjectList = new ArrayList<Object>();
        Iterator<JsonElement> elements = element.getAsJsonArray().iterator();
        while (elements.hasNext()) {
            JsonElement elmt = elements.next();
            //assume that it is a list of coreMap. Hence, each element has to be a object
            if (elmt.isJsonObject()) {
                logger.debug("elmt.isJsonObject():"+elmt);
                CoreMap map = processObject(elmt);
                coreMapList.add(map);
            } else {
                if (elmt.isJsonPrimitive()) {
                    ObjectList.add(elmt.getAsFloat());
                }
                logger.debug("Not elmt.isJsonObject():"+elmt);
                System.out.println("Failed to deserialize the Array");
            //    throw new Exception("Deserialzation failed because array is not a List<CoreMap>");
            }
        }
        if(!ObjectList.isEmpty()){

        }
        return coreMapList;
    }
    private static List<List<CoreMap>> processDefinedTerm(JsonElement element) throws Exception {
        //if it an array again other wise

        List<List<CoreMap>> coreMapList = new ArrayList<List<CoreMap>>();
        Iterator<JsonElement> elements = element.getAsJsonArray().iterator();
        while (elements.hasNext()) {
            JsonElement elmt = elements.next();
            if (elmt.isJsonArray()) {
                //infer type
                coreMapList.add(processArray(elmt));
            } else {
                System.out.println("Failed to deserialize the DefinedTerm");
                throw new Exception("Deserialzation failed because DefinedTerm is not a List<List<CoreMap>>");
            }
        }
        return coreMapList;
    }


}
