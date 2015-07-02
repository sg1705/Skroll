package com.skroll.rest.benchmark;

import com.skroll.rest.APITest;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;

public class BenchmarkAPITest extends APITest {


    @Test
    public void testSaveBenchmarkFile() throws Exception {
        String TARGET_URL = "http://localhost:8888/restServices/doc/saveBenchmarkFile";
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(TARGET_URL);

        String response = webTarget.request(MediaType.APPLICATION_JSON).cookie(new NewCookie("documentId", documentId)).get(String.class);
        logger.debug("Here is the response: " + response);
        assert(response.contains("benchmark file"));
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

    @Test
    public void testGetBenchmarkScore() throws Exception {
        String TARGET_URL = "http://localhost:8888/restServices/doc/getBenchmarkScore";
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(TARGET_URL);
        String response = webTarget.request().cookie(new NewCookie("documentId", "smaller-indenture.html")).get(String.class);
        logger.info("Here is the response: " + response);
        assert(response.contains("{\"stats\":[{\"categoyId\""));
        // test setup upload
        assert(response.contains("{\"isFileBenchmarked\":true"));
    }
}