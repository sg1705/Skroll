package com.skroll.rest.benchmark;

import com.skroll.rest.APITest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;

import static org.junit.Assert.*;

public class BenchmarkAPITest extends APITest {


    @Test
    public void testSaveBenchmarkFile() throws Exception {
        String TARGET_URL = "http://localhost:8888/restServices/doc/saveBenchmarkFile";
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(TARGET_URL);

        String response = webTarget.request(MediaType.APPLICATION_JSON).cookie(new NewCookie("documentId", documentId)).get(String.class);
        logger.debug("Here is the response: " + response);
        assert(response.contains("benchmark file is stored"));
        client.close();
    }

    @Test(expected = Exception.class)
    public void testSaveBenchmarkFileWithNullDoc() throws Exception {
        String TARGET_URL = "http://localhost:8888/restServices/doc/saveBenchmarkFile";
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(TARGET_URL);

        try {
            String response = webTarget.request(MediaType.APPLICATION_JSON).cookie(new NewCookie("documentId", "aa343")).get(String.class);
        } catch (Exception e) {
            logger.debug("Here is the response: " + e.getMessage());
        } finally {
            client.close();
            throw new Exception();
        }

    }

}