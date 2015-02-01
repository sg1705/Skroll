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
                jettyServer.waitForInterrupt();
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
    public void startServer() throws Exception {
        Client restClient = ClientBuilder.newClient();
        WebTarget target = restClient.target("http://localhost:8888/restServices/jsonAPI");
        WebTarget resourceTarget = target.path("/test"); //change the URI without affecting a root URI
        String responseString = resourceTarget.request("text/plain").get(String.class);
        System.out.println("Here is the response: "+responseString);
        assert(responseString.equals("Test"));
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
        testFileUpload();
        testGetDefinition();
    }


    public void testSetCookie() throws Exception {
        Client restClient = ClientBuilder.newClient();
        WebTarget target = restClient.target("http://localhost:8888/restServices/jsonAPI");
        WebTarget resourceTarget = target.path("/test"); //change the URI without affecting a root URI
        String responseString = resourceTarget.request("text/plain").get(String.class);
        System.out.println("Here is the response: "+responseString);
        assert(responseString.equals("Test"));
    }

    public void testFileUpload() throws Exception {
        String TARGET_URL = "http://localhost:8888/restServices/jsonAPI/upload";
        Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
        WebTarget webTarget = client.target(TARGET_URL);
        MultiPart multiPart = new MultiPart();
        multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);

        FileDataBodyPart fileDataBodyPart = new FileDataBodyPart("file",
                new File("/Users/saurabhagarwal/hackathon/_Indenture/SIX FLAGS_ex4-1.html"),
                MediaType.APPLICATION_OCTET_STREAM_TYPE);
        //byte[] bytes = new byte[10];
        multiPart.
                bodyPart(fileDataBodyPart);

        Response response = webTarget.request(MediaType.TEXT_HTML).cookie(new NewCookie("documentId", "101"))
                .post(Entity.entity(multiPart, MediaType.MULTIPART_FORM_DATA));

        System.out.println(response.readEntity(String.class));
    }

    @Test
    public void testGetDefinition() throws Exception {
        String TARGET_URL = "http://localhost:8888/restServices/jsonAPI/getDefinition?documentId=101";
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(TARGET_URL);

        String responseString = webTarget.request(MediaType.APPLICATION_JSON).get(String.class);
        System.out.println("Here is the response: "+responseString);
    }
}
