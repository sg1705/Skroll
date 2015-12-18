package com.skroll.rest;

import com.google.common.io.Files;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.skroll.document.factory.CorpusFSDocumentFactoryImpl;
import com.skroll.document.factory.DocumentFactory;
import com.skroll.util.Configuration;
import com.skroll.util.SkrollTestGuiceModule;
import com.skroll.util.TestConfiguration;
import com.skroll.util.UniqueIdGenerator;
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
import java.nio.charset.Charset;

/**
 * Created by saurabh on 5/10/15.
 */
public class APITest {
    public static final Logger logger = LoggerFactory.getLogger(DocAPITest.class);

    WebServer jettyServer = new WebServer(8888, new SkrollTestGuiceModule());
    protected String documentId = null;

    protected DocumentFactory factory;
    protected Configuration configuration;
    protected final String TEST_FILE_NAME = "src/test/resources/classifier/smaller-indenture.html";

    public APITest() throws Exception {

    }


    @Before
    public void setup () throws Exception {
        try {
            Injector injector = Guice.createInjector(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(DocumentFactory.class)
                            .to(CorpusFSDocumentFactoryImpl.class);
                    bind(Configuration.class).to(TestConfiguration.class);
                }
            });
            factory = injector.getInstance(DocumentFactory.class);
            configuration = injector.getInstance(Configuration.class);

            jettyServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        testFileUpload();
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
    public void testFileUpload() throws Exception {
        String TARGET_URL = "http://localhost:8888/restServices/doc/upload";
        Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
        WebTarget webTarget = client.target(TARGET_URL);
        MultiPart multiPart = new MultiPart();
        multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);

        FileDataBodyPart fileDataBodyPart = new FileDataBodyPart("file",
                new File(TEST_FILE_NAME),
                MediaType.APPLICATION_OCTET_STREAM_TYPE);
        multiPart.bodyPart(fileDataBodyPart);
        Response response =null;
        documentId = UniqueIdGenerator.generateId(Files.toString(new File(TEST_FILE_NAME), Charset.defaultCharset()));
        try {
            WebTarget webTargetWithQueryParam =
                    webTarget.queryParam("documentId", documentId);

            response = webTargetWithQueryParam.request(MediaType.TEXT_HTML)
                    .post(Entity.entity(multiPart, MediaType.MULTIPART_FORM_DATA));
        } catch(Throwable ex) {
            logger.error("SEVERE: An I/O error has occurred while writing a response message entity to the container output stream.");
        }
    }

    public String testGetTerms(String documentId) throws Exception {
        String TARGET_URL = "http://localhost:8888/restServices/doc/getTerms";
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(TARGET_URL);

        String responseString = webTarget.request(MediaType.APPLICATION_JSON).cookie(new NewCookie("documentId", documentId)).get(String.class);
        System.out.println("Here is the response: "+responseString);
        return responseString;
    }



}
