package com.skroll.rest;

import com.google.common.base.Joiner;
import com.google.common.io.Files;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.document.ModelHelper;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.pipeline.util.Constants;
import com.skroll.util.Configuration;
import com.skroll.util.ObjectPersistUtil;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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
import java.util.List;

public class APITest {
    public static final Logger logger = LoggerFactory
            .getLogger(APITest.class);
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
            Thread.sleep(1000);
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
    public void test_UploadFile_GetDefinition() throws Exception {
        String cookie = testFileUpload();
        testGetTerms(cookie);
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

    public void testGetTerms(String documentId) throws Exception {
        String TARGET_URL = "http://localhost:8888/restServices/jsonAPI/getTerms";
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(TARGET_URL);

        String responseString = webTarget.request(MediaType.APPLICATION_JSON).cookie(new  NewCookie("documentId", documentId)).get(String.class);
        System.out.println("Here is the response: "+responseString);
        assert(responseString.contains("Accredited Investor"));
    }

    @Test
    public void test_UploadFile_UpdateTerms() throws Exception, ObjectPersistUtil.ObjectPersistException {
        String documentId = testFileUpload();
        testUpdateTerms(documentId);
        Configuration configuration = new Configuration();
        String preEvaluatedFolder = configuration.get("preEvaluatedFolder","/tmp/");
        Document doc = ModelHelper.getModel(Files.toString(new File(preEvaluatedFolder + documentId), Constants.DEFAULT_CHARSET));
        assert(doc.getTarget().contains("Accredited Investor"));
        for (CoreMap paragraph : doc.getParagraphs()) {
            if (paragraph.containsKey(CoreAnnotations.IsDefinitionAnnotation.class)) {
                List<List<String>> definitionList = DocumentHelper.getDefinedTermLists(
                        paragraph);
                logger.debug("definitionList:" + Joiner.on(" ").join(definitionList));
                assert((Joiner.on(" ").join(definitionList).contains("Accredited")));
            }
            List<Float> trainingWeight = paragraph.get(CoreAnnotations.TrainingWeightAnnotationFloat.class);
            logger.debug("trainingWeight:" +trainingWeight);
        }
    }

    public void testUpdateTerms(String documentId) throws Exception {
        String TARGET_URL = "http://localhost:8888/restServices/jsonAPI/updateTerms";
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(TARGET_URL);

        String jsonString ="[{\"paragraphId\":\"1854\",\"definedTerm\":\"Unit Test\"},{\"paragraphId\":\"1854\",\"definedTerm\":\"200 Test\"}]";

        Response response = webTarget.request(MediaType.TEXT_HTML).cookie(new  NewCookie("documentId", documentId))
                .post(Entity.entity(jsonString, MediaType.APPLICATION_JSON));

        //logger.debug("Here is the response: "+response.getEntity().toString());
        logger.debug("Here is the response status: " + response.getStatus());
        assert(response.getStatus()==(200));
        client.close();
    }

    public void testUpdateModel(String documentId) throws Exception {
        String TARGET_URL = "http://localhost:8888/restServices/jsonAPI/updateModel";
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(TARGET_URL);

        String response = webTarget.request(MediaType.APPLICATION_JSON).cookie(new  NewCookie("documentId", documentId)).get(String.class);
        logger.debug("Here is the response: " + response);
        assert(response.contains("ok"));
        client.close();
    }

    public void testUpdateBNI(String documentId) throws Exception {
        String TARGET_URL = "http://localhost:8888/restServices/jsonAPI/updateBNI";
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(TARGET_URL);

        String response = webTarget.request(MediaType.APPLICATION_JSON).cookie(new  NewCookie("documentId", documentId)).get(String.class);
        assert(response.contains("ok"));
        client.close();
    }

    @Test
    public void testListDocs() throws Exception {
        String TARGET_URL = "http://localhost:8888/restServices/jsonAPI/listDocs";
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(TARGET_URL);
        String response = webTarget.request(MediaType.APPLICATION_JSON).get(String.class);
        logger.debug("Here is the response: "+response);
        //assert(response.contains(""));
        client.close();
    }

    @Test
    public void testGetDoc() throws Exception {
        String TARGET_URL = "http://localhost:8888/restServices/jsonAPI/getDoc";
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(TARGET_URL);
        String documentId = "SIX FLAGS_ex4-1.html";
        WebTarget webTargetWithQueryParam =
                webTarget.queryParam("documentId", documentId);
        Response response = webTargetWithQueryParam.request(MediaType.APPLICATION_JSON).get();
        logger.debug("Here is the response: "+response.getEntity().toString());
        assert(response.getStatus()==(200));
    }
}
