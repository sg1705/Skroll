package com.skroll.rest;

import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.io.File;

public class APITest {

    WebServer jettyServer = new WebServer(8888);
    @Before
    public void setup () {

        try {
            jettyServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @After
    public void shutdown() {
        try {
            jettyServer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testTest() throws Exception {
            Client restClient = ClientBuilder.newClient();
            WebTarget target = restClient.target("http://localhost:8888/restServices/jsonAPI");
            WebTarget resourceTarget = target.path("/test"); //change the URI without affecting a root URI
            String responseString = resourceTarget.request("text/plain").get(String.class);
            System.out.println("Here is the response: "+responseString);
            assert(responseString.equals("Test"));
        }

    @Test
    public void testJSP() throws Exception {
        Client restClient = ClientBuilder.newClient();
        WebTarget target = restClient.target("http://localhost:8888/");
        WebTarget resourceTarget = target.path("/jspForJunit.jsp"); //change the URI without affecting a root URI
        String responseString = resourceTarget.request("text/plain").get(String.class);
        System.out.println("Here is the response: "+responseString);
        assert(responseString.contains("JSP JUnit"));
    }

    @Test
    public void test_SetCookie_UploadFile_GetDefinition() throws Exception {
        testSetCookie();
        String cookie = testFileUpload();
        testGetDefinition(cookie);
    }


    public void testSetCookie() throws Exception {
        Client restClient = ClientBuilder.newClient();
        WebTarget target = restClient.target("http://localhost:8888/restServices/jsonAPI");
        WebTarget resourceTarget = target.path("/test"); //change the URI without affecting a root URI
        String responseString = resourceTarget.request("text/plain").get(String.class);
        System.out.println("Here is the response: "+responseString);
        assert(responseString.equals("Test"));
    }

    public String testFileUpload() throws Exception {
        String TARGET_URL = "http://localhost:8888/restServices/jsonAPI/upload";
        Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
        WebTarget webTarget = client.target(TARGET_URL);
        MultiPart multiPart = new MultiPart();
        multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);

        FileDataBodyPart fileDataBodyPart = new FileDataBodyPart("file",
                new File("src/test/resources/analyzer/evaluate/docclassifier/SIX FLAGS_ex4-1.html"),
                MediaType.APPLICATION_OCTET_STREAM_TYPE);
        //byte[] bytes = new byte[10];
        multiPart.
                bodyPart(fileDataBodyPart);

        Response response = webTarget.request(MediaType.TEXT_HTML)
                .post(Entity.entity(multiPart, MediaType.MULTIPART_FORM_DATA));

        System.out.println("Cookies:" + response.getCookies().get("documentId").getValue());

        //System.out.println(response.readEntity(String.class));
        return response.getCookies().get("documentId").getValue();
    }

    public void testGetDefinition(String documentId) throws Exception {
        String TARGET_URL = "http://localhost:8888/restServices/jsonAPI/getDefinition";
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(TARGET_URL);

        String responseString = webTarget.request(MediaType.APPLICATION_JSON).cookie(new  NewCookie("documentId", documentId)).get(String.class);
        System.out.println("Here is the response: "+responseString);
        assert(responseString.contains("Accredited Investor"));
    }

    @Test
    public void test_UploadFile_AddDefinition() throws Exception {
        String documentId = testFileUpload();
        testAddDefinition(documentId);
    }

    public void testAddDefinition(String documentId) throws Exception {
        String TARGET_URL = "http://localhost:8888/restServices/jsonAPI/addDefinition";
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(TARGET_URL);

        String jsonString ="[{\"paragraphId\":\"1854\",\"definedTerm\":\"Unit Test\"},{\"paragraphId\":\"1854\",\"definedTerm\":\"200 Test\"}]";

        Response response = webTarget.request(MediaType.APPLICATION_JSON).cookie(new  NewCookie("documentId", documentId))
                .post(Entity.entity(jsonString, MediaType.APPLICATION_JSON));

        System.out.println("Here is the response: "+response.getEntity().toString());
        System.out.println("Here is the response status: "+response.getStatus());
        assert(response.getStatus()==(200));
    }

    @Test
    public void test_UploadFile_DeleteDefinition() throws Exception {
        String documentId = testFileUpload();
        testDeleteDefinition(documentId);
    }

    public void testDeleteDefinition(String documentId) throws Exception {
        String TARGET_URL = "http://localhost:8888/restServices/jsonAPI/deleteDefinition";
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(TARGET_URL);

        String jsonString = "[{\"paragraphId\":\"1854\",\"definedTerm\":\"144A Global Note\"},\n" +
                "{\"paragraphId\":\"1856\",\"definedTerm\":\"Accredited Investor\"}]\n";
        Response response = webTarget.request(MediaType.APPLICATION_JSON).cookie(new  NewCookie("documentId", documentId))
                .post(Entity.entity(jsonString, MediaType.APPLICATION_JSON));

        System.out.println("Here is the response: "+response.getEntity().toString());
        System.out.println("Here is the response status: "+response.getStatus());
        assert(response.getStatus()==(200));
    }
}
