package com.skroll.rest;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.skroll.classifier.DefinitionClassifier;
import com.skroll.document.*;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.parser.Parser;
import com.skroll.parser.extractor.ParserException;
import com.skroll.pipeline.util.Constants;
import com.skroll.pipeline.util.Utils;
import com.skroll.util.Configuration;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.*;

@Path("/jsonAPI")
public class API {

    public static final Logger logger = LoggerFactory
            .getLogger(API.class);

    private static Map<String,Document> documentMap = new HashMap<String,Document>();
    private DefinitionClassifier definitionClassifier = new DefinitionClassifier();
    private Configuration configuration = new Configuration();
    String preEvaluatedFolder = configuration.get("preEvaluatedFolder","/tmp/");

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
                //test the document
                document = (Document) definitionClassifier.classify(document);
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

    @POST
    @Path("/addDefinition")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateDefinition(String definedTermParagraphList, @Context HttpHeaders hh) {

        logger.debug("updateDefinition- DefinedTermParagraphList:{}", definedTermParagraphList);
        if (definedTermParagraphList.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).entity("NO input data in post request" ).type(MediaType.APPLICATION_JSON).build();
        }
        MultivaluedMap<String, String> headerParams = hh.getRequestHeaders();
            Map<String, Cookie> pathParams = hh.getCookies();
            logger.debug("getDocumentId: Cookie: {}", pathParams);
            if ( pathParams.get("documentId")==null) {
                logger.error("documentId is missing from Cookie");
                return Response.status(Response.Status.EXPECTATION_FAILED).entity("documentId is missing from Cookie").type(MediaType.APPLICATION_JSON).build();

            }
        String documentId = pathParams.get("documentId").getValue();
        Document doc = documentMap.get(documentId);
        if (doc==null){
            return Response.status(Response.Status.NO_CONTENT).entity("document cannot be found for document id: "+ documentId).type(MediaType.APPLICATION_JSON).build();
        }
        List<Paragraph> definitionJson =null;
    try {
        Gson gson = new GsonBuilder().create();
        Type type = new TypeToken<List<Paragraph>>() {
        }.getType();
        definitionJson = gson.fromJson(definedTermParagraphList, type);

        logger.info("updateDefinition- definitionJson:{}", definitionJson);

    } catch(Exception ex) {
           logger.error("Failed to parse the json document: {}", ex);
        return Response.status(Response.Status.BAD_REQUEST).entity("Failed to parse the json document" ).type(MediaType.APPLICATION_JSON).build();
        }
        for (Paragraph modifiedParagraph: definitionJson) {
            for (CoreMap paragraph : doc.getParagraphs()) {
                 if(paragraph.getId().equals(modifiedParagraph.getParagraphId())) {

                     // log the existing definitions
                     if (paragraph.containsKey(CoreAnnotations.IsDefinitionAnnotation.class)) {
                         List<List<String>> definitionList = DocumentHelper.getDefinedTermLists(
                                 paragraph);
                         for (List<String> definition : definitionList) {
                             logger.debug(paragraph.getId() + "\t" + "existing definition:" + "\t" + definition);
                         }
                     }

                     List<String> addedDefinition = Lists.newArrayList( Splitter.on(" ").split(modifiedParagraph.getDefinedTerm()));
                     List<Token> tokens = DocumentHelper.getTokens(addedDefinition);
                     DocumentHelper.addDefinedTermTokensInParagraph(tokens, paragraph);


                     // log the updated definitions
                     List<List<String>> definitionList = DocumentHelper.getDefinedTermLists(
                             paragraph);
                     for (List<String> definition : definitionList) {
                         logger.debug(paragraph.getId() + "\t" + "updated definition:" + "\t" + definition);

                     }
                 }
            }
        }

        Utils.writeToFile(preEvaluatedFolder + documentId, doc.getTarget());
        logger.debug("updated document is stored in {}", preEvaluatedFolder + documentId);
        // train the model
        /*
        definitionClassifier.train(doc);

        try {
            // persist the model
            definitionClassifier.persistModel();
        } catch (ObjectPersistUtil.ObjectPersistException e) {
            logger.error("Failed to persist the model");
            e.printStackTrace();
        }
        // classify again against the updated model
        try {
            doc = (Document)definitionClassifier.classify(doc);
        } catch (Exception e) {
            logger.error("Failed to classify the doc");
            e.printStackTrace();
        }
        logger.debug ("Number fo Paragraphs returned: " + doc.getParagraphs().size());

        return Response.ok().status(Response.Status.OK).entity(doc.getTarget()).type(MediaType.APPLICATION_JSON).build();
        */
        return Response.ok().status(Response.Status.OK).entity("").type(MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Path("/deleteDefinition")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteDefinition(String definedTermParagraphList, @Context HttpHeaders hh) {

        logger.debug("updateDefinition- DefinedTermParagraphList:{}", definedTermParagraphList);
        if (definedTermParagraphList.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).entity("NO input data in post request" ).type(MediaType.APPLICATION_JSON).build();
        }
        MultivaluedMap<String, String> headerParams = hh.getRequestHeaders();
        Map<String, Cookie> pathParams = hh.getCookies();
        logger.debug("getDocumentId: Cookie: {}", pathParams);
        if ( pathParams.get("documentId")==null) {
            logger.error("documentId is missing from Cookie");
            return Response.status(Response.Status.EXPECTATION_FAILED).entity("documentId is missing from Cookie").type(MediaType.APPLICATION_JSON).build();

        }
        String documentId = pathParams.get("documentId").getValue();
        Document doc = documentMap.get(documentId);
        if (doc==null){
            return Response.status(Response.Status.NO_CONTENT).entity("document cannot be found for document id: "+ documentId).type(MediaType.APPLICATION_JSON).build();
        }
        List<Paragraph> definitionJson =null;
        try {
            Gson gson = new GsonBuilder().create();
            Type type = new TypeToken<List<Paragraph>>() {
            }.getType();
            definitionJson = gson.fromJson(definedTermParagraphList, type);

            logger.info("updateDefinition- definitionJson:{}", definitionJson);

        } catch(Exception ex) {
            logger.error("Failed to parse the json document: {}", ex);
            return Response.status(Response.Status.BAD_REQUEST).entity("Failed to parse the json document" ).type(MediaType.APPLICATION_JSON).build();
        }
        for (Paragraph modifiedParagraph: definitionJson) {
            for (CoreMap paragraph : doc.getParagraphs()) {
                if(paragraph.getId().equals(modifiedParagraph.getParagraphId())) {

                    // log the existing definitions
                    if (paragraph.containsKey(CoreAnnotations.IsDefinitionAnnotation.class)) {
                        List<List<String>> definitionList = DocumentHelper.getDefinedTermLists(
                                paragraph);
                        if (definitionList.size()==1){
                            paragraph.set(CoreAnnotations.DefinedTermListAnnotation.class, null);
                            paragraph.set(CoreAnnotations.IsDefinitionAnnotation.class, false);
                            logger.debug("removed DefinedTermListAnnotation for paragraph :{}", modifiedParagraph);
                        } else {
                            List<String> defTermParagraphList = new ArrayList<>();
                            List<List<String>> defList = DocumentHelper.getDefinedTermLists(
                                    paragraph);
                            for (List<String> definition: definitionList) {
                                logger.debug(paragraph.getId() + "\t" + "existing definition" + "\t" + definition);
                                if (definition.isEmpty())
                                    continue;
                                if (Joiner.on(" ").join(definition).equals(modifiedParagraph.getDefinedTerm())){
                                    defList.remove(definition);
                                }
                            }
                        }
                    }

                    // log the updated definitions
                    List<List<String>> definitionList = DocumentHelper.getDefinedTermLists(
                            paragraph);
                    for (List<String> definition : definitionList) {
                        logger.debug(paragraph.getId() + "\t" + "updated definition:" + "\t" + definition);

                    }
                }
            }
        }

        Utils.writeToFile(preEvaluatedFolder + documentId, doc.getTarget());
        logger.debug("updated document is stored in {}",preEvaluatedFolder + documentId);

        return Response.ok().status(Response.Status.OK).entity("").type(MediaType.APPLICATION_JSON).build();
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