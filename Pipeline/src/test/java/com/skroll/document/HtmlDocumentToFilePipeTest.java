package com.skroll.document;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Utils;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class HtmlDocumentToFilePipeTest {


    public void testProcess() throws Exception {
        String fileName = "src/test/resources/document/experiment-jsoup-node-extraction.html";
        String targetFile = "build/resources/test/generated/document/experiment-jsoup-node-extraction.html";

        Files.createParentDirs(new File(targetFile));

        String htmlText = Utils.readStringFromFile(fileName);

        Document htmlDoc = new Document(htmlText);

        //create a pipeline
        Pipeline<Document, Document> pipeline =
                new Pipeline.Builder()
                        .add(Pipes.PARSE_HTML_TO_DOC)
                        .add(Pipes.SAVE_HTML_DOCUMENT_TO_FILE, Lists.newArrayList(targetFile))
                        .build();

        Document doc = pipeline.process(htmlDoc);

        // read the file and recreate the doc
        String newJsonText = Utils.readStringFromFile(targetFile);
        Document newDoc = ModelHelper.getModel(newJsonText);

        System.out.println(doc.getParagraphs().size());

        assert (newDoc.getParagraphs().size() == doc.getParagraphs().size());
    }


    public void testReadingPersistedDoc() throws Exception {
        testProcess();
        String targetFile = "build/resources/test/generated/document/experiment-jsoup-node-extraction.html";

        String htmlText = Utils.readStringFromFile(targetFile);

        Document htmlDoc = ModelHelper.getModel(htmlText);

        //create a pipeline
        Pipeline<Document, Document> pipeline =
                new Pipeline.Builder()
                        .add(Pipes.PARSE_HTML_TO_DOC)
                        .add(Pipes.SAVE_HTML_DOCUMENT_TO_FILE, Lists.newArrayList(targetFile))
                        .build();

        htmlDoc = pipeline.process(htmlDoc);

        // read the file and recreate the doc
        String newJsonText = Utils.readStringFromFile(targetFile);
        Document newDoc = ModelHelper.getModel(newJsonText);

        //assert (newDoc.getSourceHtml().equals(htmlDoc.getSourceHtml()));
        assert (newDoc.getParagraphs().size() == htmlDoc.getParagraphs().size());
    }

    public void testAnnotationSerialization() {
        Class annotationClass = CoreAnnotations.ParagraphsAnnotation.class;
        TestAnnotationSerialization ser = new TestAnnotationSerialization();
        ser.setAnnotationClass("123");

        //serialize
        Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassTypeAdapter())
                .enableComplexMapKeySerialization()
                .create();
        String jsonString = gson.toJson(ser);


        Type docType = new TypeToken<TestAnnotationSerialization>() {}.getType();
        TestAnnotationSerialization newDoc = gson.fromJson(jsonString, docType);
        System.out.println(newDoc.toString());


    }

    public static class TestAnnotationSerialization {
        public String getValue() {
            return (String)this.map.get(CoreAnnotations.ParagraphsAnnotation.class);
        }

        public void setAnnotationClass(String value) {
            this.map.put(CoreAnnotations.ParagraphsAnnotation.class,value);
        }


        private Map map;

        public TestAnnotationSerialization() {
            map = new HashMap();
        }
    }


    public static class ClassTypeAdapter implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {

        @Override
        public JsonElement serialize(Class<?> src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getName());
        }

        @Override
        public Class<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            try {
                return Class.forName(json.getAsString());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

    }
}