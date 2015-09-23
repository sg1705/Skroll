package com.skroll.rest;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.skroll.classifier.Category;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.document.JsonDeserializer;
import com.skroll.document.annotation.CategoryAnnotationHelper;
import com.skroll.pipeline.util.Constants;
import com.skroll.util.ObjectPersistUtil;
import com.skroll.util.UniqueIdGenerator;
import org.junit.After;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

public class SearchAPITest extends APITest {

    public SearchAPITest() throws Exception {
    }

    @After
    public void shutdown() {
        try {
            Thread.sleep(1000);
            jettyServer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSearchSec() throws Exception {
        String TARGET_URL = "http://localhost:8888/restServices/search/searchSec";
        Client client = ClientBuilder.newClient();
        String query = "&text=google&start=1&count=80&first=2015&last=2015";
        WebTarget webTarget = client.target(TARGET_URL).queryParam("text", query);

        String response = webTarget.request().get(String.class);
        System.out.println(response);
        client.close();
    }
}
