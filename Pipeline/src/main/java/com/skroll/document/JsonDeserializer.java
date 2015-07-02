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
        String target=null;
        String source=null;
        String id=null;
        Document document=null;
        int counter =0;
        while (iterator.hasNext()) {
            Map.Entry<String, JsonElement> entry = iterator.next();
            JsonElement element = entry.getValue();
            String key = entry.getKey();
            if(key.equals("target")){
                target=element.getAsString();
                counter++;
            }
            if (key.equals("map")) {
                //it is a coremap
                //let's start
                CoreMap coreMap = processObject("documentLevelKey", element);
                document = new Document(coreMap);
                counter++;
            }
            if(counter==4){
                break;
            }
        }
        if(document==null){
            return null;
        }
        document.setTarget(target);
        return document;
    }

    /**
     * process CoreMap in Array
     * @param mapKey
     * @param elmt
     * @return CoreMap
     * @throws Exception
     */
    private static CoreMap processMapInArray(String mapKey, JsonElement elmt) throws Exception {
        Set<Map.Entry<String, JsonElement>> set = elmt.getAsJsonObject().entrySet();
        Iterator<Map.Entry<String, JsonElement>> iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, JsonElement> entry = iterator.next();
            JsonElement element = entry.getValue();
            String key = entry.getKey();
            if (key.equals("map")) {
                //check to see if the key contains Token
                CoreMap coreMap = processObject(mapKey, element);
                return coreMap;

            }
        }
        return null;
    }


    /**
     * Handle process Object
     * @param key
     * @param element
     * @return
     * @throws Exception
     */
    private static CoreMap processObject(String key, JsonElement element) throws Exception {
        CoreMap coreMap = new CoreMap();
        if (key.contains("Token")) {
            coreMap = new Token();
        }
        Set<Map.Entry<String, JsonElement>> set = element.getAsJsonObject().entrySet();
        logger.trace("EntrySet:{}", set);
        for(Map.Entry<String, JsonElement> entry : set) {
            JsonElement elmt = entry.getValue();
            if (elmt.isJsonPrimitive()) {
                // add in the core map
                coreMap.set(entry.getKey(), processPrimitives(entry.getKey(), elmt));
            } else if (entry.getKey().equals("TermTokensAnnotation")) {
                coreMap.set(entry.getKey(), processDefinedTerm(entry.getKey(), elmt));
            } else if (entry.getKey().equals("CategoryAnnotations")) {
                coreMap.set(entry.getKey(), processCategoryId(entry.getKey(), elmt));
            } else if (elmt.isJsonArray()) {
                //infer type
                logger.trace("processing key {} as Array", entry.getKey());
                coreMap.set(entry.getKey(), processArray(entry.getKey(), elmt));
            } else if (elmt.isJsonObject()) {
                logger.trace("processing key {} as Object:", entry.getKey());
                coreMap.set(entry.getKey(), processObject(entry.getKey(),elmt));
            }
        }
        return coreMap;
    }

    /**
     * Handle json Array type
     * @param key
     * @param element
     * @return
     * @throws Exception
     */
    private static List<Object> processArray(String key, JsonElement element) throws Exception {
        //if it an array again other wise
        List<Object> ObjectList = new ArrayList<Object>();
        Iterator<JsonElement> elements = element.getAsJsonArray().iterator();
        while (elements.hasNext()) {
            JsonElement elmt = elements.next();
            //assume that it is a list of coreMap. Hence, each element has to be a object
            if (elmt.isJsonObject()) {
                CoreMap map = processMapInArray(key, elmt);
                ObjectList.add(map);
            } else {
                if (elmt.isJsonPrimitive()) {
                    ObjectList.add(processPrimitives(key, elmt));
                }
            }
        }
        return ObjectList;
    }

    /**
     * Handle  the defined Term  which is list of list.
     * @param key
     * @param element
     * @return
     * @throws Exception
     */
    private static List<List<Object>> processDefinedTerm(String key, JsonElement element) throws Exception {
        //if it an array again other wise

        List<List<Object>> coreMapList = new ArrayList<List<Object>>();
        Iterator<JsonElement> elements = element.getAsJsonArray().iterator();
        while (elements.hasNext()) {
            JsonElement elmt = elements.next();
            if (elmt.isJsonArray()) {
                //infer type
                coreMapList.add(processArray(key, elmt));
            } else {
                System.out.println("Failed to deserialize the DefinedTerm");
                throw new Exception("Deserialzation failed because DefinedTerm is not a List<List<CoreMap>>");
            }
        }
        return coreMapList;
    }


    private static HashMap processCategoryId(String key, JsonElement element) throws Exception {
        HashMap map = new HashMap();
        Set<Map.Entry<String, JsonElement>> absorbSet = element.getAsJsonObject().entrySet();
        for (Map.Entry<String, JsonElement> mapEntry : absorbSet) {
            int mapKey = Integer.parseInt(mapEntry.getKey());
            JsonElement jElement = mapEntry.getValue();
            Set<Map.Entry<String, JsonElement>> set = jElement.getAsJsonObject().entrySet();
            for (Map.Entry<String, JsonElement> entry : set) {
                JsonElement elmt = entry.getValue();
                //since this is a map
                map.put(mapKey, processObject(entry.getKey(), elmt));
            }
        }
        return map;
    }

    /**
     * Handler for json primitives. Important things to remember..
     *
     * All boolean start with Is
     * Float,Integer need to have in it
     *
     * @param key
     * @param element
     * @return
     */
    private static Object processPrimitives(String key, JsonElement element) {
        if (key.startsWith("Is")) {
            logger.trace("processing key {} as boolean",key);
            return new Boolean(element.getAsBoolean());
        } else if (key.contains("Float")) {
            logger.trace("processing key {} as Float",key);
            return new Float(element.getAsFloat());
        } else if (key.contains("Integer")) {
            logger.trace("processing key {} as Integer",key);
            return new Integer(element.getAsInt());
        }
        //consider it as a string
        return new String(element.getAsString());
    }
}
