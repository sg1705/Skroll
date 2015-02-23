package com.skroll.rest;

import com.google.common.base.Joiner;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.skroll.classifier.DefinitionClassifier;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.document.Paragraph;
import com.skroll.parser.Parser;
import com.skroll.parser.extractor.ParserException;
import com.skroll.pipeline.util.Constants;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.File;
import java.io.InputStreamReader;
import java.util.*;

@Path("/jsonAPI")
public class API {

    public static final Logger logger = LoggerFactory
            .getLogger(API.class);

    public static Map<String,Document> documentMap = new HashMap<String,Document>();


    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_HTML)
    public Response upload(MultiPart multiPart, @Context HttpHeaders hh) {

        List<BodyPart> bodyParts = multiPart.getBodyParts();
        // get the second part which is the project logo
        boolean isProcessed = false;
        String message = null;
        MultivaluedMap<String, String> headerParams = hh.getRequestHeaders();
        Map<String, Cookie> pathParams = hh.getCookies();
        logger.debug("pathParams:" +pathParams);

        for (BodyPart bodyPart : bodyParts) {
            BodyPartEntity bpe = (BodyPartEntity) bodyPart.getEntity();
            InputStreamReader reader = new InputStreamReader(bpe.getInputStream());
            String content = null;
            try {
                content = CharStreams.toString(reader);
                String documentId = String.valueOf(content.hashCode());

                //parse the document
                Document document = Parser.parseDocumentFromHtml(content);
                //create a classifier
                DefinitionClassifier classifier = new DefinitionClassifier();
                //test the document
                document = (Document) classifier.classify(document);
                //logger.debug("document:" + document.getTarget());
                //link the document
                documentMap.put(documentId, document);

                logger.debug("Added document into the documentMap with a generated hash key:"+ documentMap.keySet());

                NewCookie documentIdCookie = new NewCookie("documentId", documentId);

                return Response.status(Response.Status.ACCEPTED).cookie(documentIdCookie).entity(document.getTarget().getBytes(Constants.DEFAULT_CHARSET)).type(MediaType.TEXT_HTML_TYPE).build();

            } catch (ParserException e) {
                logger.error("Error while parsing the uploaded file", e);
            } catch (Exception e) {
                logger.error("Error while classifying", e);
            }
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("Failed to process attachments. Reason : " + message).type(MediaType.TEXT_HTML).build();

    }

    @GET
    @Path("/getDocumentId")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDocumentId(@Context HttpHeaders hh) {
        MultivaluedMap<String, String> headerParams = hh.getRequestHeaders();
        Map<String, Cookie> pathParams = hh.getCookies();
        logger.debug("getDocumentId: Cookie: {}", pathParams);
        if ( pathParams.get("documentId")==null) {
            return Response.status(Response.Status.EXPECTATION_FAILED).entity("documentId is missing from Cookie").type(MediaType.TEXT_HTML).build();

        }
        String documentId = pathParams.get("documentId").getValue();
        return Response.status(Response.Status.OK).entity(documentId).type(MediaType.APPLICATION_JSON).build();

    }

    @GET
    @Path("/getDefinition")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDefinition(@QueryParam("documentId") String documentId, @Context HttpHeaders hh) {

        if(documentId==null) {
            MultivaluedMap<String, String> headerParams = hh.getRequestHeaders();
            Map<String, Cookie> pathParams = hh.getCookies();
            logger.debug("getDocumentId: Cookie: {}", pathParams);
            if ( pathParams.get("documentId")==null) {
                return Response.status(Response.Status.EXPECTATION_FAILED).entity("documentId is missing from Cookie").type(MediaType.TEXT_HTML).build();

            }
             documentId = pathParams.get("documentId").getValue();
        }
        logger.info("getDefinition- DocumentId:" + documentId.toString());

        Document doc = documentMap.get(documentId);
        if (doc==null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Failed to find the document in Map" ).type(MediaType.TEXT_PLAIN).build();
        }
        List<Paragraph> definedTermParagraphList = new ArrayList<>();

        for (CoreMap paragraph : doc.getParagraphs()) {
            if (paragraph.containsKey(CoreAnnotations.IsDefinitionAnnotation.class)) {
                List<List<String>> definitionList = DocumentHelper.getDefinedTermLists(
                        paragraph);
                for (List<String> definition: definitionList) {
                    logger.debug(paragraph.getId() + "\t" + "DEFINITION" + "\t" + definition);
                    if (definition.isEmpty())
                        continue;
                    definedTermParagraphList.add(new Paragraph(paragraph.getId(), Joiner.on(" ").join(definition)));
                }
            }
        }
        if (definedTermParagraphList.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).entity("Failed to find the document in Map" ).type(MediaType.TEXT_PLAIN).build();
        }
        Gson gson = new GsonBuilder().create();
        String definitionJson = gson.toJson(definedTermParagraphList);
        logger.debug("definitionJson" + "\t" + definitionJson);
        Response r = Response.ok().status(Response.Status.OK).entity(definitionJson).build();
        return r;
    }


    @GET
    @Path("/getParagraphJson")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getParagraphJson(@QueryParam("paragraphId") String paragraphId, @Context HttpHeaders hh) {

        MultivaluedMap<String, String> headerParams = hh.getRequestHeaders();
        Map<String, Cookie> pathParams = hh.getCookies();
        logger.debug("getDocumentId: Cookie: {}", pathParams);
        if ( pathParams.get("documentId")==null) {
            return Response.status(Response.Status.EXPECTATION_FAILED).entity("documentId is missing from Cookie").type(MediaType.TEXT_HTML).build();

        }
        String documentId = pathParams.get("documentId").getValue();

        Document doc = documentMap.get(documentId);
        if (doc==null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Failed to find the document in Map" ).type(MediaType.TEXT_PLAIN).build();
        }

        for (CoreMap paragraph : doc.getParagraphs()) {
            if (paragraph.getId().equals(paragraphId)) {
                //found it
                Gson gson = new GsonBuilder().create();
                String json = gson.toJson(paragraph);
                return Response.ok().status(Response.Status.OK).entity(json).build();
            }
        }
        return Response.ok().status(Response.Status.OK).entity("").build();
    }



    @GET
    @Path("/test")
    @Produces(MediaType.TEXT_PLAIN)
    public Response test() {
        NewCookie cookie = new NewCookie("documentId", "101");
        String output = "Test";
        Response r = Response.ok().cookie(cookie).status(200).entity(output).build();
        return r;
    }

    @POST
    @Path("/uploadFile")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadFile(MultiPart multiPart) {

        List<BodyPart> bodyParts = multiPart.getBodyParts();
        // get the second part which is the project logo
        boolean isProcessed = false;
        String message = null;
        for (BodyPart bodyPart : bodyParts) {
            BodyPartEntity bpe = (BodyPartEntity) bodyPart.getEntity();
            String id = UUID.randomUUID().toString();

            try {
                byte[] content =ByteStreams.toByteArray(bpe.getInputStream());
                File file = new File("/tmp/" + id + ".out");
                Files.write(content, file);
                isProcessed = true;

            } catch (Exception e) {
                message = e.getMessage();
            }
        }
        if (isProcessed) {
            return Response.status(Response.Status.ACCEPTED).entity("Attachements processed successfully.").type(MediaType.APPLICATION_JSON_TYPE).build();
        }

        return Response.status(Response.Status.BAD_REQUEST).entity("Failed to process attachments. Reason : " + message).type(MediaType.APPLICATION_JSON_TYPE).build();
    }
}