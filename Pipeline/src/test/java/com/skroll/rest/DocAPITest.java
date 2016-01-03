package com.skroll.rest;

import com.google.common.base.Joiner;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.skroll.classifier.Category;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentFormat;
import com.skroll.document.JsonDeserializer;
import com.skroll.document.annotation.CategoryAnnotationHelper;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.pipeline.util.Constants;
import com.skroll.util.ObjectPersistUtil;
import com.skroll.util.UniqueIdGenerator;
import org.junit.After;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;

public class DocAPITest extends APITest {

    public DocAPITest() throws Exception {
    }

    //    @Before
//    public void setup () throws Exception {
//        try {
//            jettyServer.start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        documentId = testFileUpload();
//    }
    @After
    public void shutdown() {
        try {
            Thread.sleep(1000);
            jettyServer.stop();
            java.nio.file.Files.delete(new File(configuration.get("preEvaluatedFolder")+documentId).toPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void test_UploadFile_UpdateTerms() throws Exception, ObjectPersistUtil.ObjectPersistException {

        String preEvaluatedFolder = configuration.get("preEvaluatedFolder","/tmp/");
        Document doc = JsonDeserializer.fromJson(Files.toString(new File(preEvaluatedFolder + documentId), Constants.DEFAULT_CHARSET));
        for(CoreMap coreMap: doc.getParagraphs()){
            CategoryAnnotationHelper.setMatchedText(coreMap, "Capital Stock", Category.TOC_2);
            if(CategoryAnnotationHelper.isParagraphAnnotatedWithCategoryId(coreMap, Category.TOC_2)) {
                System.out.println("TOC_2:" + CategoryAnnotationHelper.getTokenStringsForCategory(coreMap, Category.TOC_2));
            }
        }
        //API.documentMap.put("smaller-indenture.html",doc);
        logger.debug("TOC Paragraph before calling updateTerm: {}", CategoryAnnotationHelper.getParagraphsAnnotatedWithCategory(doc, Category.TOC_2));

        testUpdateTerms();

        assert(doc.getTarget().contains("Capital Stock"));

        for (CoreMap paragraph : doc.getParagraphs()) {
                List<List<String>> definitionList = CategoryAnnotationHelper.getTokenStringsForCategory(
                        paragraph, Category.TOC_2);
                logger.debug(paragraph.getId() + " " + Joiner.on(" ").join(definitionList));
        }
    }

    @Test
    public void test_UploadFile_RemoveTerms() throws Exception, ObjectPersistUtil.ObjectPersistException {
        testRemoveTerms();
        String preEvaluatedFolder = configuration.get("preEvaluatedFolder","/tmp/");
        Document doc = JsonDeserializer.fromJson(Files.toString(new File(preEvaluatedFolder + documentId), Constants.DEFAULT_CHARSET));
        assert(doc.getTarget().contains("Capital Stock"));
        for (CoreMap paragraph : doc.getParagraphs()) {
                List<List<String>> definitionList = CategoryAnnotationHelper.getTokenStringsForCategory(
                        paragraph, Category.DEFINITION);
                logger.debug(paragraph.getId() + " " + Joiner.on(" ").join(definitionList));
        }
    }

    @Test
    public void testImportPartialPDF() throws Exception {
        String docURL = "http://www.ge.com/ar2014/assets/pdf/GE_2014_Form_10K.pdf";
        String partialParse = "true";
        String id = importTestHelper(docURL, partialParse, DocumentFormat.PDF.id());
        assert(factory.isDocumentExist(id));
        Document doc = factory.get(id);
        assert(doc.get(CoreAnnotations.DocumentFormatAnnotationInteger.class) == DocumentFormat.PDF.id());
        assert(doc.get(CoreAnnotations.IsPartiallyParsedAnnotation.class));
    }

    @Test
    public void testImportPartialHTML() throws Exception {
        String docURL = "https://www.sec.gov/Archives/edgar/data/50863/000005086315000015/a10kdocument12272014.htm";
        String partialParse = "true";
        String id = importTestHelper(docURL, partialParse, DocumentFormat.HTML.id());
        assert(!factory.isDocumentExist(id));
    }

    @Test
    public void testImportHTML() throws Exception {
        String docURL = "https://www.sec.gov/Archives/edgar/data/1288776/000119312513028362/d452134d10k.htm";
        String partialParse = "false";
        String id = importTestHelper(docURL, partialParse, DocumentFormat.HTML.id());
        assert(factory.isDocumentExist(id));
        Document doc = factory.get(id);
        assert(doc.get(CoreAnnotations.DocumentFormatAnnotationInteger.class) == DocumentFormat.HTML.id());
        assert(!doc.containsKey(CoreAnnotations.IsPartiallyParsedAnnotation.class));
    }


    public String importTestHelper(String docURL, String partialParse, int documentFormat) throws Exception {
        String TARGET_URL = "http://localhost:8888/restServices/doc/importDoc";
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(TARGET_URL).queryParam("documentId", docURL).queryParam("partialParse", partialParse);
        Response response = webTarget.request(MediaType.TEXT_HTML).get();
        response.readEntity(String.class);
        String id = UniqueIdGenerator.generateId(docURL);
        String headerId = response.getHeaderString("documentId");
        String headerFormat = response.getHeaderString("format");
        assert(response.getStatus()==(200));
        assert(id.equals(headerId));
        assert(headerFormat.equals(Integer.toString(documentFormat)));
        Document doc = factory.get(id);
        client.close();
        return id;
    }


    @Test
    public void testUpdateTerms() throws Exception {
        String TARGET_URL = "http://localhost:8888/restServices/doc/updateTerms";
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(TARGET_URL).queryParam("documentId", documentId);

        String jsonString ="[{\"paragraphId\":\"p_1238\",\"term\":\"Cash Equivalents\", \"classificationId\":0}]";
        //String jsonString ="[{\"paragraphId\":\"p_1371\",\"term\":\"Disclosure Regarding Forward-Looking Statements\", \"classificationId\":2}]";

        Response response = webTarget.request(MediaType.TEXT_HTML)
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
        WebTarget webTarget = client.target(TARGET_URL).queryParam("documentId", documentId);
        String response = webTarget.request(MediaType.APPLICATION_JSON).get(String.class);
        logger.debug("Here is the response: " + response);
        assert(response.contains(""));
        client.close();
    }

    @Test
    public void testUploadListDocs() throws Exception {
        String TARGET_URL = "http://localhost:8888/restServices/doc/listDocs";
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(TARGET_URL).queryParam("documentId", documentId);
        String response = webTarget.request().get(String.class);
        logger.info("Here is the response: " + response);
        String fileId = UniqueIdGenerator.generateId(Files.toString(new File(TEST_FILE_NAME), Charset.defaultCharset()));
        assert(response.contains(fileId));
        client.close();
    }

    @Test
    public void testGetDoc() throws Exception {
        String TARGET_URL = "http://localhost:8888/restServices/doc/getDoc";
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(TARGET_URL).queryParam("documentId", documentId);
        String response = webTarget.request().get(String.class);
        assert(response.contains("Restricted Subsidiaries"));
    }
    @Test
    public void testRemoveTerms() throws Exception {
        String TARGET_URL = "http://localhost:8888/restServices/doc/updateTerms";
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(TARGET_URL).queryParam("documentId", documentId);

        String jsonString ="[{\"paragraphId\":\"p_1238\",\"term\":\"\", \"classificationId\":1}]";

        Response response = webTarget.request(MediaType.TEXT_HTML).header("documentId", documentId)
                .post(Entity.entity(jsonString, MediaType.APPLICATION_JSON));

        //logger.debug("Here is the response: "+response.getEntity().toString());
        logger.debug("Here is the response status: " + response.getStatus());
        assert(response.getStatus()==(200));
        //String responseString = testGetTerms();
        //assert(!responseString.contains("Cash Equivalents"));
        client.close();
    }

    @Test
    public void testGetIndex() throws Exception {
        String TARGET_URL = "http://localhost:8888/restServices/doc/getIndex";
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(TARGET_URL).queryParam("documentId", documentId);
        String response = webTarget.request().get(String.class);
        //validate if response is a valid json
        Gson gson = new GsonBuilder().create();
        HashMap map = gson.fromJson(response, HashMap.class);
        assert (!map.isEmpty());
        assert (map.get("corpusTokens") != null);
    }

    @Test
    public void testGetDocType() throws Exception {
        String TARGET_URL = "http://localhost:8888/restServices/doc/getDocType";
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(TARGET_URL).queryParam("documentId", documentId);
        String response = webTarget.request().get(String.class);
        HashMap<String, Integer> map = new GsonBuilder().create().fromJson(response, new TypeToken<HashMap<String, Integer>>() {}.getType());
        assert(map.get("docTypeId").equals(101));

    }
}
