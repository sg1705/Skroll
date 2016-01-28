package com.skroll.rest.document;

import com.skroll.rest.APITest;
import org.junit.After;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class DocumentAPITest extends APITest {

    public DocumentAPITest() throws Exception {
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


    public void testImportPartial() throws Exception {
        String TARGET_URL = "http://localhost:8888/restServices/document/importFromUrl";
        DocumentProto docProto = new DocumentProto();
        docProto.setUrl("abc");
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(TARGET_URL).queryParam("url", "test.html");

//        Response response = webTarget.request(MediaType.APPLICATION_JSON).post(Entity.json(docProto));
        Response response = webTarget.request(MediaType.APPLICATION_JSON).get();

        DocumentProto responseProto = response.readEntity(DocumentProto.class);
        assert(responseProto.getId().equals("eac0a7ec83537763d3ba7671828d0989"));
    }

    @Test
    public void testFindRelatedDocument() throws Exception {
        String TARGET_URL = "http://localhost:8888/restServices/document/" + documentId + "/related/p/p_1254";
        DocumentProto docProto = new DocumentProto();
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(TARGET_URL);
        Response response = webTarget.request().post(Entity.json(docProto));
        assert (response.getStatus() == 200);
        List<ParaProto> paraProtos = response.readEntity(new GenericType<List<ParaProto>>(){});
        System.out.println(paraProtos.size());
        assert (paraProtos.size() == 5);
    }
}
