package com.skroll.index;


import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.skroll.document.Document;
import com.skroll.document.JsonDeserializer;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.parser.Parser;
import com.skroll.pipeline.util.Constants;
import com.skroll.util.Configuration;
import com.skroll.util.SkrollTestGuiceModule;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;


public class IndexCreatorTest {


    Configuration configuration;

    @Before
    public void setup() throws Exception {
        Injector injector = Guice.createInjector(new SkrollTestGuiceModule());
        configuration = injector.getInstance(Configuration.class);
    }


    @Test
    public void testIndexCreator() throws Exception {
        String random10k = "./src/test/resources/document/random10k.html";
        String html = Files.toString(new File(random10k), Constants.DEFAULT_CHARSET);
        String fileName = "aa";
        Document doc = Parser.parseDocumentFromHtml(html);
        doc.setId(fileName);
        String json = JsonDeserializer.getJson(doc);
        String flName = "/tmp/" + Long.toString(System.currentTimeMillis()) + ".json";
        //save it to a file in temp
        Files.write(json.getBytes(Constants.DEFAULT_CHARSET), new File(flName));
        IndexCreator creator = new IndexCreator(configuration.get("searchindex_js"));
        doc = creator.process(doc, flName);
        new File(flName).delete();
        assert (doc.get(CoreAnnotations.SearchIndexAnnotation.class) != null);
        //convert to an object to see if valid json
        Gson gson = new GsonBuilder().create();
        HashMap map = gson.fromJson(doc.get(CoreAnnotations.SearchIndexAnnotation.class), HashMap.class);
        assert (!map.isEmpty());
    }
}
