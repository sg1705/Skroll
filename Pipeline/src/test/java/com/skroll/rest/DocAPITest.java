package com.skroll.rest;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.skroll.classifier.Category;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.document.JsonDeserializer;
import com.skroll.document.annotation.CategoryAnnotationHelper;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.pipeline.util.Constants;
import com.skroll.util.Configuration;
import com.skroll.util.ObjectPersistUtil;
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
import java.util.List;

public class DocAPITest extends APITest {

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
    @Test
    public void test_UploadFile_UpdateTerms() throws Exception, ObjectPersistUtil.ObjectPersistException {

        Configuration configuration = new Configuration();
        String preEvaluatedFolder = configuration.get("preEvaluatedFolder","/tmp/");
        Document doc = JsonDeserializer.fromJson(Files.toString(new File(preEvaluatedFolder + documentId), Constants.DEFAULT_CHARSET));
        for(CoreMap coreMap: doc.getParagraphs()){
            CategoryAnnotationHelper.setMatchedText(coreMap, DocumentHelper.createTokens(Lists.newArrayList("Capital", "Stock")), Category.TOC_4);
            if(CategoryAnnotationHelper.isCategoryId(coreMap, Category.TOC_4)) {
                System.out.println("TOC_4:" + CategoryAnnotationHelper.getDefinedTermLists(coreMap, Category.TOC_4));
                //assert(Joiner.on(" ").join(CategoryAnnotationHelper.getDefinedTermLists(coreMap, Category.TOC_4)).equals("CapitalStock"));
            }
        }
        //API.documentMap.put("smaller-indenture.html",doc);
        logger.debug("TOC Paragraph before calling updateTerm: {}", CategoryAnnotationHelper.getParaWithCategoryAnnotation(doc, Category.TOC_4));

        testUpdateTerms();

        assert(doc.getTarget().contains("Capital Stock"));

        for (CoreMap paragraph : doc.getParagraphs()) {
                List<List<String>> definitionList = CategoryAnnotationHelper.getDefinedTermLists(
                        paragraph, Category.TOC_4);
                logger.debug(paragraph.getId() + " " + Joiner.on(" ").join(definitionList));

            if(paragraph.containsKey(CoreAnnotations.IsTrainerFeedbackAnnotation.class)) {
                logger.debug("TrainingWeight:" +paragraph.get(CoreAnnotations.TrainingWeightAnnotationFloat.class));
            }
        }
    }

    @Test
    public void test_UploadFile_RemoveTerms() throws Exception, ObjectPersistUtil.ObjectPersistException {
        testRemoveTerms();
        Configuration configuration = new Configuration();
        String preEvaluatedFolder = configuration.get("preEvaluatedFolder","/tmp/");
        Document doc = JsonDeserializer.fromJson(Files.toString(new File(preEvaluatedFolder + documentId), Constants.DEFAULT_CHARSET));
        assert(doc.getTarget().contains("Capital Stock"));
        for (CoreMap paragraph : doc.getParagraphs()) {
                List<List<String>> definitionList = CategoryAnnotationHelper.getDefinedTermLists(
                        paragraph,Category.DEFINITION );
                logger.debug(paragraph.getId() + " " + Joiner.on(" ").join(definitionList));

            if(paragraph.containsKey(CoreAnnotations.IsTrainerFeedbackAnnotation.class)) {
                logger.debug("TrainingWeight:" +paragraph.get(CoreAnnotations.TrainingWeightAnnotationFloat.class));
            }
        }
    }

    @Test
    public void testUpdateTerms() throws Exception {
        String TARGET_URL = "http://localhost:8888/restServices/doc/updateTerms";
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(TARGET_URL);

        String jsonString ="[{\"paragraphId\":\"p_1238\",\"term\":\"Cash Equivalents\", \"classificationId\":1}]";
        //String jsonString ="[{\"paragraphId\":\"p_1371\",\"term\":\"Disclosure Regarding Forward-Looking Statements\", \"classificationId\":2}]";

        Response response = webTarget.request(MediaType.TEXT_HTML).cookie(new  NewCookie("documentId", documentId))
                .post(Entity.entity(jsonString, MediaType.APPLICATION_JSON));

        //logger.debug("Here is the response: "+response.getEntity().toString());
        logger.debug("Here is the response status: " + response.getStatus());
        assert(response.getStatus()==(200));
        client.close();
    }

    @Test
    public void testUpdateModel() throws Exception {
        testUpdateTerms();
        String TARGET_URL = "http://localhost:8888/restServices/doc/updateModel";
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(TARGET_URL);

        String response = webTarget.request(MediaType.APPLICATION_JSON).cookie(new  NewCookie("documentId", documentId)).get(String.class);
        logger.debug("Here is the response: " + response);
        assert(response.contains("model has been updated"));
        client.close();
    }

    @Test
    public void testSaveBenchmarkFile() throws Exception {
        testUpdateTerms();
        String TARGET_URL = "http://localhost:8888/restServices/doc/saveBenchmarkFile";
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(TARGET_URL);

        String response = webTarget.request(MediaType.APPLICATION_JSON).cookie(new  NewCookie("documentId", documentId)).get(String.class);
        logger.debug("Here is the response: " + response);
        assert(response.contains("benchmark file is stored"));
        client.close();
    }

    @Test
    public void testUploadListDocs() throws Exception {
        String TARGET_URL = "http://localhost:8888/restServices/doc/listDocs";
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(TARGET_URL);
        String response = webTarget.request().get(String.class);
        logger.debug("Here is the response: "+response);
        assert(response.contains("smaller-indenture.html"));
        client.close();
    }

    @Test
    public void testGetDoc() throws Exception {
        String TARGET_URL = "http://localhost:8888/restServices/doc/getDoc";
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(TARGET_URL);
        String documentId = "smaller-indenture.html";
        WebTarget webTargetWithQueryParam =
                webTarget.queryParam("documentId", documentId);
        String response = webTargetWithQueryParam.request().get(String.class);
        assert(response.contains("Restricted Subsidiaries"));
    }
    @Test
    public void testRemoveTerms() throws Exception {
        String TARGET_URL = "http://localhost:8888/restServices/doc/updateTerms";
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(TARGET_URL);

        String jsonString ="[{\"paragraphId\":\"p_1238\",\"term\":\"\", \"classificationId\":1}]";

        Response response = webTarget.request(MediaType.TEXT_HTML).cookie(new  NewCookie("documentId", documentId))
                .post(Entity.entity(jsonString, MediaType.APPLICATION_JSON));

        //logger.debug("Here is the response: "+response.getEntity().toString());
        logger.debug("Here is the response status: " + response.getStatus());
        assert(response.getStatus()==(200));
        //String responseString = testGetTerms();
        //assert(!responseString.contains("Cash Equivalents"));
        client.close();
    }
}
