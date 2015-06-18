package com.skroll.rest;

import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

public class InstrumentAPITest extends APITest {

    @Test
    public void testGetBenchmarkScore() throws Exception {
        String TARGET_URL = "http://localhost:8888/restServices/doc/getBenchmarkScore";
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(TARGET_URL);
        String response = webTarget.request().get(String.class);
        logger.debug("Here is the response: "+response);
    }
}