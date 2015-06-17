package com.skroll.rest;

import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.io.File;

/**
 * Created by saurabh on 5/10/15.
 */
public class APITest {
    public static final Logger logger = LoggerFactory.getLogger(DocAPITest.class);

    WebServer jettyServer = new WebServer(8888, new APITestGuiceModule());
    protected String documentId=null;

    @Before
    public void setup () throws Exception {

        try {
            jettyServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        documentId = testFileUpload();
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

    public String testFileUpload() throws Exception {
        String TARGET_URL = "http://localhost:8888/restServices/doc/upload";
        Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
        WebTarget webTarget = client.target(TARGET_URL);
        MultiPart multiPart = new MultiPart();
        multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);

        FileDataBodyPart fileDataBodyPart = new FileDataBodyPart("file",
                //new File("src/main/resources/parser/extractor/jQuery/dish-10k.html"),
                new File("src/test/resources/classifier/smaller-indenture.html"),
                MediaType.APPLICATION_OCTET_STREAM_TYPE);
        //byte[] bytes = new byte[10];
        multiPart.
                bodyPart(fileDataBodyPart);
        Response response =null;
        try {
            response = webTarget.request(MediaType.TEXT_HTML)
                    .post(Entity.entity(multiPart, MediaType.MULTIPART_FORM_DATA));
        } catch(Throwable ex) {
            logger.error("SEVERE: An I/O error has occurred while writing a response message entity to the container output stream.");
        }
        logger.debug("Cookies:" + response.getCookies().get("documentId").getValue());

        //System.out.println(response.readEntity(String.class));
        return response.getCookies().get("documentId").getValue();
    }

    public String testGetTerms(String documentId) throws Exception {
        String TARGET_URL = "http://localhost:8888/restServices/doc/getTerms";
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(TARGET_URL);

        String responseString = webTarget.request(MediaType.APPLICATION_JSON).cookie(new NewCookie("documentId", documentId)).get(String.class);
        System.out.println("Here is the response: "+responseString);
        return responseString;
    }



}
