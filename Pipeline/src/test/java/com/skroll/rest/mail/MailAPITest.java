package com.skroll.rest.mail;

import com.google.gson.GsonBuilder;
import com.skroll.rest.WebServer;
import com.skroll.util.SkrollTestGuiceModule;
import org.glassfish.jersey.client.ClientResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


public class MailAPITest{

    WebServer jettyServer = new WebServer(8888, new SkrollTestGuiceModule());


    @Before
    public void setup () throws Exception {
        try {
            jettyServer.start();
        } catch(Exception e) {
            e.printStackTrace();
        }
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
    public void testSendFeedback() throws Exception {
        Feedback feedback = new Feedback("test@test.com", "feedbackMessage", "test", "test.com","test browser");
        String feedbackJson = new GsonBuilder().create().toJson(feedback);
        String TARGET_URL = "http://localhost:8888/restServices/mail/sendFeedback";
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(TARGET_URL);
        WebTarget webTargetWithQueryParam =
                webTarget.queryParam("documentId", "aaa");
        Response r = webTargetWithQueryParam.request(MediaType.APPLICATION_JSON).post(Entity.json(feedbackJson));
        assert(r.readEntity(String.class).contains("ok"));
        client.close();
    }
}