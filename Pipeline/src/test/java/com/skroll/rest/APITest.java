package com.skroll.rest;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.skroll.document.*;
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
import java.util.ArrayList;
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
        String TARGET_URL = "http://localhost:8888/restServices/jsonAPI/getTerms";
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(TARGET_URL);

        String responseString = webTarget.request(MediaType.APPLICATION_JSON).cookie(new  NewCookie("documentId", documentId)).get(String.class);
        System.out.println("Here is the response: "+responseString);
        return responseString;
    }

    @Test
    public void test_UploadFile_UpdateTerms() throws Exception, ObjectPersistUtil.ObjectPersistException {
        String documentId = testFileUpload();
        Configuration configuration = new Configuration();
        String preEvaluatedFolder = configuration.get("preEvaluatedFolder","/tmp/");
        Document doc = JsonDeserializer.fromJson(Files.toString(new File(preEvaluatedFolder + documentId), Constants.DEFAULT_CHARSET));
        for(CoreMap coreMap: doc.getParagraphs()){
            DocumentHelper.setMatchedText(coreMap, DocumentHelper.createTokens(Lists.newArrayList("Capital", "Stock")), Paragraph.TOC_CLASSIFICATION);
            if(coreMap.get(CoreAnnotations.IsTOCAnnotation.class)) {
                System.out.println(DocumentHelper.getTOCLists(coreMap));
                assert(Joiner.on(" ").join(DocumentHelper.getTOCLists(coreMap)).equals("Capital Stock"));
            }
        }
        API.documentMap.put("smaller-indenture.html",doc);
        logger.debug("TOC Paragraph before calling updateTerm: {}", DocumentHelper.getTOCParagraphs((doc)));

        testUpdateTerms(documentId);

        logger.trace("Doc.target():" +doc.getTarget());

        assert(doc.getTarget().contains("Capital Stock"));

        for (CoreMap paragraph : doc.getParagraphs()) {
            if (paragraph.containsKey(CoreAnnotations.IsDefinitionAnnotation.class)) {
                List<List<String>> definitionList = DocumentHelper.getDefinedTermLists(
                        paragraph);
                logger.debug(paragraph.getId() + " " + Joiner.on(" ").join(definitionList));

            }
            if(paragraph.containsKey(CoreAnnotations.IsTrainerFeedbackAnnotation.class)) {
                logger.debug("TrainingWeight:" +paragraph.get(CoreAnnotations.TrainingWeightAnnotationFloat.class));
            }
        }
    }

    @Test
    public void test_UploadFile_RemoveTerms() throws Exception, ObjectPersistUtil.ObjectPersistException {
        String documentId = testFileUpload();
        testRemoveTerms(documentId);
        Configuration configuration = new Configuration();
        String preEvaluatedFolder = configuration.get("preEvaluatedFolder","/tmp/");
        Document doc = JsonDeserializer.fromJson(Files.toString(new File(preEvaluatedFolder + documentId), Constants.DEFAULT_CHARSET));
        assert(doc.getTarget().contains("Capital Stock"));
        for (CoreMap paragraph : doc.getParagraphs()) {
            if (paragraph.containsKey(CoreAnnotations.IsDefinitionAnnotation.class)) {
                List<List<String>> definitionList = DocumentHelper.getDefinedTermLists(
                        paragraph);
                logger.debug(paragraph.getId() + " " + Joiner.on(" ").join(definitionList));

            }
            if(paragraph.containsKey(CoreAnnotations.IsTrainerFeedbackAnnotation.class)) {
                logger.debug("TrainingWeight:" +paragraph.get(CoreAnnotations.TrainingWeightAnnotationFloat.class));
            }
        }
    }
    public void testUpdateTerms(String documentId) throws Exception {
        String TARGET_URL = "http://localhost:8888/restServices/jsonAPI/updateTerms";
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(TARGET_URL);

        String jsonString ="[{\"paragraphId\":\"p_1253\",\"term\":\"Base Terms\", \"classificationId\":2}]";
        //String jsonString ="[{\"paragraphId\":\"p_1371\",\"term\":\"Disclosure Regarding Forward-Looking Statements\", \"classificationId\":2}]";

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
        String documentId = "smaller-indenture.html";
        WebTarget webTargetWithQueryParam =
                webTarget.queryParam("documentId", documentId);
        String response = webTargetWithQueryParam.request().get(String.class);
        assert(response.contains("Restricted Subsidiaries"));
    }

    public void testRemoveTerms(String documentId) throws Exception {
        String TARGET_URL = "http://localhost:8888/restServices/jsonAPI/updateTerms";
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(TARGET_URL);

        String jsonString ="[{\"paragraphId\":\"p_1236\",\"term\":\"\", \"classificationId\":1}]";

        Response response = webTarget.request(MediaType.TEXT_HTML).cookie(new  NewCookie("documentId", documentId))
                .post(Entity.entity(jsonString, MediaType.APPLICATION_JSON));

        //logger.debug("Here is the response: "+response.getEntity().toString());
        logger.debug("Here is the response status: " + response.getStatus());
        assert(response.getStatus()==(200));
        client.close();
    }

    @Test
    public void testUpdateTermsEx() throws Exception {
        String documentId = "100";
        Document doc = createDoc();
        API.documentMap.put(documentId, doc);
        testGetTerms(documentId);
        //testUpdateTerms(documentId);
        String responseString = testGetTerms(documentId);
        assert(responseString.contains("jack susan"));
    }

    @Test
    public void testRemoveTermsEx() throws Exception {
        String documentId = "100";
        Document doc = createDoc();
        API.documentMap.put(documentId, doc);
        testGetTerms(documentId);
        //testRemoveTerms(documentId);
        String responseString = testGetTerms(documentId);
        assert(responseString.contains("jack susan"));
    }

    private Document createDoc() {
        Document doc = new Document();
        doc.setTarget(" ");
        doc.setSource(" ");
        List<CoreMap> paralist = new ArrayList<>();
        CoreMap paragraph =new CoreMap("1253", "para");

        List<String> addedDefinition = Lists.newArrayList("jack", "susan");
        List<Token> tokens = DocumentHelper.getTokens(addedDefinition);
        DocumentHelper.addDefinedTermTokensInParagraph(tokens, paragraph);
        paragraph.set(CoreAnnotations.IsDefinitionAnnotation.class, true);
        paragraph.set(CoreAnnotations.ParagraphIdAnnotation.class, "1");
        paragraph.set(CoreAnnotations.IsUserObservationAnnotation.class, true);
        paragraph.set(CoreAnnotations.IsTrainerFeedbackAnnotation.class, true);
        paragraph.set(CoreAnnotations.IsUserObservationAnnotation.class, true);
        paralist.add(paragraph);
        List<List<String>> definitionList = DocumentHelper.getDefinedTermLists(
                paragraph);
        for (List<String> definition : definitionList) {
            logger.debug(paragraph.getId() + "\t" + " annotation:" + "\t" + definition);

        }


        CoreMap paragraph1 =new CoreMap("1253", "para");

        List<String> addedDefinition1 = Lists.newArrayList("sam", "adam");
        List<Token> tokens1 = DocumentHelper.getTokens(addedDefinition1);
        DocumentHelper.addTOCsInParagraph(tokens1, paragraph1);
        paragraph1.set(CoreAnnotations.IsTOCAnnotation.class, true);
        paragraph1.set(CoreAnnotations.ParagraphIdAnnotation.class, "1");
        paragraph1.set(CoreAnnotations.IsUserObservationAnnotation.class, true);
        paragraph1.set(CoreAnnotations.IsTrainerFeedbackAnnotation.class, true);
        paralist.add(paragraph1);

        List<List<String>> definitionList1 = DocumentHelper.getDefinedTermLists(
                paragraph1);
        for (List<String> definition : definitionList1) {
            logger.debug(paragraph.getId() + "\t" + " annotation:" + "\t" + definition);

        }

        doc.setParagraphs(paralist);
        return doc;
    }
}
