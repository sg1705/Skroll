package com.skroll.index;


import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.skroll.document.Document;
import com.skroll.document.JsonDeserializer;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.parser.Parser;
import com.skroll.pipeline.util.Constants;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;


public class IndexCreatorTest {

    @Test
    public void testIndexCreator() throws Exception {
        String random10k = "./src/test/resources/document/random10k.html";
        String html = Files.toString(new File(random10k), Constants.DEFAULT_CHARSET);
        String fileName = "aa";
        Document doc = Parser.parseDocumentFromHtml(html);
        doc.setId(fileName);
        String json = JsonDeserializer.getJson(doc);
        //save it to a file in temp
        Files.write(json.getBytes(Constants.DEFAULT_CHARSET), new File("/tmp/indexcreatortest.json"));
        IndexCreator creator = new IndexCreator();
        doc = creator.process(doc, "/tmp/indexcreatortest.json");
        new File("/tmp/indexcreatortest.json").delete();
        assert (doc.get(CoreAnnotations.SearchIndexAnnotation.class) != null);
        //convert to an object to see if valid json
        Gson gson = new GsonBuilder().create();
        HashMap map = gson.fromJson(doc.get(CoreAnnotations.SearchIndexAnnotation.class), HashMap.class);
        assert(!map.isEmpty());
    }
}