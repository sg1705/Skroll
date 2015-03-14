package com.skroll.rest;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.skroll.classifier.DefinitionClassifier;
import com.skroll.document.*;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.document.annotation.TrainingWeightAnnotationHelper;
import com.skroll.parser.Parser;
import com.skroll.parser.extractor.ParserException;
import com.skroll.pipeline.util.Constants;
import com.skroll.util.Configuration;
import com.skroll.util.ObjectPersistUtil;
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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Path("/jsonAPI")
public class API {

    public static final Logger logger = LoggerFactory
            .getLogger(API.class);

    // documentMap is defined as concurrent hashmap
    // as we would like to share this hashmap between multiple requests from multiple clients
    // It provides the construct to synchronize only block of map not the whole hashmap.
    private static Map<String,Document> documentMap = new ConcurrentHashMap<String,Document>();
    private static DefinitionClassifier definitionClassifier = new DefinitionClassifier();
    private static Configuration configuration = new Configuration();
    private static String  preEvaluatedFolder = configuration.get("preEvaluatedFolder","/tmp/");
    private static ObjectPersistUtil docPersistUtil = new ObjectPersistUtil(preEvaluatedFolder);
    private static Type type = new TypeToken<Document>() {}.getType();
    // by default,
    private float userWeight = 95;

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
        logger.debug("headerParams:" +headerParams);
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
                reader.close();

                return Response.status(Response.Status.ACCEPTED).cookie(documentIdCookie).entity(document.getTarget().getBytes(Constants.DEFAULT_CHARSET)).type(MediaType.TEXT_HTML).build();

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
    @Path("/overwriteAnnotation")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response overwriteAnnotation(String definedTermParagraphList, @Context HttpHeaders hh) {

        logger.debug("changeAnnotation- DefinedTermParagraphList:{}", definedTermParagraphList);
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

        logger.info("overwriteAnnotation:{} for doc id: {}", definitionJson,documentId);

    } catch(Exception ex) {
           logger.error("Failed to parse the json document: {}", ex);
        return Response.status(Response.Status.BAD_REQUEST).entity("Failed to parse the json document" ).type(MediaType.APPLICATION_JSON).build();
        }
        for (Paragraph modifiedParagraph: definitionJson) {
            for (CoreMap paragraph : doc.getParagraphs()) {
                 if(paragraph.getId().equals(modifiedParagraph.getParagraphId())) {
                     paragraph.set(CoreAnnotations.IsUserObservationAnnotation.class, true);
                     paragraph.set(CoreAnnotations.IsTrainerFeedbackAnnotation.class,true);
                     TrainingWeightAnnotationHelper.updateTrainingWeight(paragraph, TrainingWeightAnnotationHelper.DEFINITION, userWeight);
                     // log the existing definitions

                     if (paragraph.containsKey(CoreAnnotations.IsDefinitionAnnotation.class)) {
                         List<List<String>> definitionList = DocumentHelper.getDefinedTermLists(
                                 paragraph);
                         for (List<String> definition : definitionList) {
                             logger.debug(paragraph.getId() + "\t" + "existing definition:" + "\t" + definition);
                         }
                     }
                     //remove any existing annotations - definedTermList
                     paragraph.set(CoreAnnotations.DefinedTermListAnnotation.class, null);
                     paragraph.set(CoreAnnotations.IsDefinitionAnnotation.class, false);

                     // add annotations that received from client - definedTermList
                     List<String> addedDefinition = Lists.newArrayList( Splitter.on(" ").split(modifiedParagraph.getDefinedTerm()));
                     if (addedDefinition!=null && !addedDefinition.isEmpty()) {
                         List<Token> tokens = DocumentHelper.getTokens(addedDefinition);
                         DocumentHelper.addDefinedTermTokensInParagraph(tokens, paragraph);
                         paragraph.set(CoreAnnotations.IsDefinitionAnnotation.class, true);
                     }
                     // log the updated definitions
                     List<List<String>> definitionList = DocumentHelper.getDefinedTermLists(
                             paragraph);
                     for (List<String> definition : definitionList) {
                         logger.debug(paragraph.getId() + "\t" + "changed annotation:" + "\t" + definition);

                     }
                     logger.debug("TrainingWeightAnnotation:" + paragraph.get(CoreAnnotations.TrainingWeightAnnotation.class).toString());
                 }
            }
        }
        // persist the document using document id. Let's use the file name
        try {

            Files.write(ModelHelper.getJson(doc), new File(preEvaluatedFolder + documentId), Charset.defaultCharset());
        } catch (Exception e) {
            logger.error("Failed to persist the document object: {}", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to persist the document object" ).type(MediaType.APPLICATION_JSON).build();
        }
        logger.debug("updated document is stored in {}", preEvaluatedFolder + documentId);

        return Response.ok().status(Response.Status.OK).entity("").type(MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/updateModel")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateModel( @Context HttpHeaders hh) {

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

        definitionClassifier.train(doc);

        logger.debug("train the model using document is stored in {}", preEvaluatedFolder + documentId);

        return Response.ok().status(Response.Status.OK).entity("ok").type(MediaType.APPLICATION_JSON).build();
    }


    @GET
    @Path("/updateBNI")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateBNI( @Context HttpHeaders hh) {

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
        // persist the document using document id. Let's use the file name

        try {
            doc = (Document) definitionClassifier.classify(doc);
        } catch (Exception e) {
            logger.error("Failed to classify the document object: {}", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to classify the document object" ).type(MediaType.APPLICATION_JSON).build();
        }

        logger.debug("classify the model using document is stored in {}", preEvaluatedFolder + documentId);

        documentMap.put(documentId, doc);

        logger.debug("Added document into the documentMap with a generated hash key:"+ documentMap.keySet());

        NewCookie documentIdCookie = new NewCookie("documentId", documentId);

        return Response.status(Response.Status.ACCEPTED).cookie(documentIdCookie).entity(doc.getTarget().getBytes(Constants.DEFAULT_CHARSET)).type(MediaType.TEXT_HTML_TYPE).build();

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
    @Path("/getSimilarPara")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSimilarParagraph(@QueryParam("paragraphId") String paragraphId, @Context HttpHeaders hh) {

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

        //find paragraph annotation
        CoreMap para = null;
        for (CoreMap paragraph : doc.getParagraphs()) {
            if (paragraph.getId().equals(paragraphId)) {
                //found it
                para = paragraph;
            }
        }

        if (para == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("Failed to find the document in Map")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        //find similar para now
        boolean isBold = para.containsKey(CoreAnnotations.IsBoldAnnotation.class);
        boolean isItalic = para.containsKey(CoreAnnotations.IsItalicAnnotation.class);
        boolean isUnderline = para.containsKey(CoreAnnotations.IsUnderlineAnnotation.class);
        boolean isUpperCase = para.containsKey(CoreAnnotations.IsUpperCaseAnnotation.class);
        boolean isCenterAligned = para.containsKey(CoreAnnotations.IsCenterAlignedAnnotation.class);
        String fontSize = para.get(CoreAnnotations.FontSizeAnnotation.class);

        List<CoreMap> similarPara = new ArrayList<>();
        for (CoreMap paragraph : doc.getParagraphs()) {
            if (!paragraph.getId().equals(paragraphId)) {
                //check to see if it matches
                boolean isParaBold = paragraph.containsKey(CoreAnnotations.IsBoldAnnotation.class);
                boolean isParaItalic = paragraph.containsKey(CoreAnnotations.IsItalicAnnotation.class);
                boolean isParaUnderline = paragraph.containsKey(CoreAnnotations.IsUnderlineAnnotation.class);
                boolean isParaUpperCase = paragraph.containsKey(CoreAnnotations.IsUpperCaseAnnotation.class);
                boolean isParaCenterAligned = paragraph.containsKey(CoreAnnotations.IsCenterAlignedAnnotation.class);
                String paraFontSize = paragraph.get(CoreAnnotations.FontSizeAnnotation.class);


                if ((isBold == isParaBold)
                        && (isItalic == isParaItalic)
                        && (isUnderline == isParaUnderline)
                        && (isUpperCase == isParaUpperCase)
                        && (isCenterAligned == isParaCenterAligned)
                        && (fontSize.equals(paraFontSize))) {
                    //everything is similar
                    similarPara.add(paragraph);
                }
            }
        }

        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(similarPara);
        return Response.ok().status(Response.Status.OK).entity(json).build();
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
}