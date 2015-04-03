package com.skroll.rest;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.skroll.classifier.Classifier;
import com.skroll.classifier.DefinitionClassifier;
import com.skroll.classifier.TOCExperimentClassifier;
import com.skroll.document.*;
import com.skroll.document.annotation.CoreAnnotation;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.document.annotation.TrainingWeightAnnotationHelper;
import com.skroll.parser.Parser;
import com.skroll.parser.extractor.ParserException;
import com.skroll.pipeline.util.Constants;
import com.skroll.util.Configuration;
import com.skroll.util.Flags;
import com.skroll.util.ObjectPersistUtil;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
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

    protected static Map<String,Document> documentMap = new ConcurrentHashMap<String,Document>();
    private static DefinitionClassifier definitionClassifier = new DefinitionClassifier();
    private static Classifier tocClassifier = new TOCExperimentClassifier();
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
        logger.debug("BodyParts:"+ bodyParts);
        // get the second part which is the project logo
        boolean isProcessed = false;
        String message = null;
        MultivaluedMap<String, String> headerParams = hh.getRequestHeaders();
        logger.debug("headerParams:" +headerParams);
        Map<String, Cookie> pathParams = hh.getCookies();
        logger.debug("pathParams:" +pathParams);

        for (BodyPart bodyPart : bodyParts) {
            String fileName = ((FormDataBodyPart)bodyPart).getContentDisposition().getFileName();
            logger.debug("FileName:" +fileName);
            BodyPartEntity bpe = (BodyPartEntity) bodyPart.getEntity();
            InputStreamReader reader = new InputStreamReader(bpe.getInputStream());
            String content = null;
            try {
                content = CharStreams.toString(reader);
                //String documentId = String.valueOf(content.hashCode());

                //parse the document
                Document document = Parser.parseDocumentFromHtml(content);
                //test the document
                document = (Document) definitionClassifier.classify(fileName, document);
                document = (Document) tocClassifier.classify(fileName, document);
                //logger.debug("document:" + document.getTarget());
                //link the document
                documentMap.put(fileName, document);

                logger.debug("Added document into the documentMap with a generated hash key:"+ documentMap.keySet());

                NewCookie documentIdCookie = new NewCookie("documentId", fileName);
                reader.close();
                // persist the document using document id. Let's use the file name
                try {
                    Files.createParentDirs(new File(preEvaluatedFolder + fileName));
                    Files.write(JsonDeserializer.getJson(document), new File(preEvaluatedFolder + fileName), Charset.defaultCharset());
                } catch (Exception e) {
                    logger.error("Failed to persist the document object: {}", e);
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to persist the document object" ).type(MediaType.APPLICATION_JSON).build();
                }
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
    @Path("/listDocs")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listDocs(@Context HttpHeaders hh) {
        MultivaluedMap<String, String> headerParams = hh.getRequestHeaders();
        FluentIterable<File> iterable = Files.fileTreeTraverser().breadthFirstTraversal(new File(preEvaluatedFolder));
        List<String> docLists = new ArrayList<String>();
        for (File f : iterable) {
            if (f.isFile()) {
                docLists.add(f.getName());

            }
        }
        Gson gson = new GsonBuilder().create();
        String docsJson = gson.toJson(docLists);
        return Response.status(Response.Status.OK).entity(docsJson).type(MediaType.APPLICATION_JSON).build();

    }
    @GET
    @Path("/getDoc")
    @Produces(MediaType.TEXT_HTML)
    public Response getDoc(@QueryParam("documentId") String documentId, @Context HttpHeaders hh) {

        if(documentId==null) {
            MultivaluedMap<String, String> headerParams = hh.getRequestHeaders();
            Map<String, Cookie> pathParams = hh.getCookies();
            logger.debug("getDocumentId: Cookie: {}", pathParams);
            if ( pathParams.get("documentId")==null) {
                return Response.status(Response.Status.EXPECTATION_FAILED).entity("documentId is missing from Cookie").type(MediaType.TEXT_HTML).build();

            }
            documentId = pathParams.get("documentId").getValue();
        }
        logger.info("getDoc- DocumentId:" + documentId.toString());

        Document doc = documentMap.get(documentId);

        String jsonString = null;
        if (doc==null) {
            logger.debug("Not found in documentMap, fetching from corpus:" + documentId.toString());
            try {
                jsonString = Files.toString(new File(preEvaluatedFolder + documentId), Charset.defaultCharset());
            } catch (Exception e) {
                logger.error("Failed to read document from Corpus:" + e.toString());
                e.printStackTrace();
                return Response.status(Response.Status.EXPECTATION_FAILED).entity("Failed to read document from Corpus").type(MediaType.TEXT_HTML).build();

            }
            try {
                doc = JsonDeserializer.fromJson(jsonString);
            } catch (Exception e) {
                logger.error("Failed to deserialize the message:" + e.toString());
                e.printStackTrace();
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to parse document").type(MediaType.TEXT_HTML).build();

            }
            try {
                doc = (Document) definitionClassifier.classify(documentId, doc);
                doc = (Document) tocClassifier.classify(documentId, doc);
            } catch (Exception e) {
                e.printStackTrace();
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to classify the document").type(MediaType.TEXT_HTML).build();
            }
            documentMap.put(documentId,doc);
        }
        NewCookie documentIdCookie = new NewCookie("documentId", documentId);
        return Response.status(Response.Status.OK).cookie(documentIdCookie).entity(doc.getTarget().getBytes(Constants.DEFAULT_CHARSET)).type(MediaType.TEXT_HTML).build();
    }

    @GET
    @Path("/getDocumentId")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDocumentId(@Context HttpHeaders hh) {
        MultivaluedMap<String, String> headerParams = hh.getRequestHeaders();
        Map<String, Cookie> pathParams = hh.getCookies();
        logger.debug("getDocumentId: Cookie: {}", pathParams);
        if ( pathParams.get("documentId")==null) {
            logger.warn("documentId is missing from Cookie");
            return Response.status(Response.Status.EXPECTATION_FAILED).entity("documentId is missing from Cookie").type(MediaType.TEXT_HTML).build();

        }
        String documentId = pathParams.get("documentId").getValue();
        return Response.status(Response.Status.OK).entity(documentId).type(MediaType.APPLICATION_JSON).build();

    }

    @GET
    @Path("/getTerms")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTerm(@QueryParam("documentId") String documentId, @Context HttpHeaders hh) {

        if(documentId==null) {
            MultivaluedMap<String, String> headerParams = hh.getRequestHeaders();
            Map<String, Cookie> pathParams = hh.getCookies();
            logger.debug("getDocumentId: Cookie: {}", pathParams);
            if ( pathParams.get("documentId")==null) {
                logger.warn("documentId is missing from Cookie");
                return Response.status(Response.Status.EXPECTATION_FAILED).entity("documentId is missing from Cookie").type(MediaType.TEXT_HTML).build();

            }
             documentId = pathParams.get("documentId").getValue();
        }
        logger.info("getDefinition- DocumentId:" + documentId.toString());

        Document doc = documentMap.get(documentId);
        if (doc==null) {
            logger.error("Failed to find the document in Map");
            return Response.status(Response.Status.NOT_FOUND).entity("Failed to find the document in Map" ).type(MediaType.TEXT_PLAIN).build();
        }
        List<Paragraph> definedTermParagraphList = new ArrayList<>();

        for (CoreMap paragraph : doc.getParagraphs()) {
            if (paragraph.containsKey(CoreAnnotations.IsDefinitionAnnotation.class)) {
                List<List<String>> definitionList = DocumentHelper.getDefinedTermLists(
                        paragraph);
                for (List<String> definition: definitionList) {
                    logger.debug(paragraph.getId() + "\t" + "DEFINITION" + "\t" + definition);
                    if (!definition.isEmpty()) {
                        if(!(Joiner.on(" ").join(definition).equals(""))) {
                            definedTermParagraphList.add(new Paragraph(paragraph.getId(), Joiner.on(" ").join(definition), Paragraph.DEFINITION_CLASSIFICATION));

                        }
                    }
                }
            }
            if (paragraph.containsKey(CoreAnnotations.IsTOCAnnotation.class)) {
                List<String> tocList = DocumentHelper.getTOCLists(
                        paragraph);
                    if (!tocList.isEmpty()) {
                        if(!(Joiner.on(" ").join(tocList).equals(""))) {
                            logger.debug(paragraph.getId() + "\t" + "TOC" + "\t" + tocList);
                            definedTermParagraphList.add(new Paragraph(paragraph.getId(), Joiner.on(" ").join(tocList), Paragraph.TOC_CLASSIFICATION));
                        }
                    }
            }
        }

        Gson gson = new GsonBuilder().create();
        String definitionJson = gson.toJson(definedTermParagraphList);
        logger.debug("definitionJson" + "\t" + definitionJson);
        Response r = Response.ok().status(Response.Status.OK).entity(definitionJson).build();
        return r;
    }

    @POST
    @Path("/updateTerms")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_HTML)
    public Response updateTerms(String definedTermParagraphList, @Context HttpHeaders hh) {

        logger.debug("updateTerms- DefinedTermParagraphList:{}", definedTermParagraphList);
        if (definedTermParagraphList.isEmpty()) {
            logger.warn("NO input data in post request");
            return Response.status(Response.Status.NO_CONTENT).entity("NO input data in post request" ).type(MediaType.APPLICATION_JSON).build();
        }
        MultivaluedMap<String, String> headerParams = hh.getRequestHeaders();
            Map<String, Cookie> pathParams = hh.getCookies();
            logger.debug("getDocumentId: Cookie: {}", pathParams);
            if ( pathParams.get("documentId")==null) {
                logger.warn("documentId is missing from Cookie");
                return Response.status(Response.Status.EXPECTATION_FAILED).entity("documentId is missing from Cookie").type(MediaType.APPLICATION_JSON).build();

            }
        String documentId = pathParams.get("documentId").getValue();
        Document doc = documentMap.get(documentId);
        if (doc==null){
            logger.warn("document cannot be found for document id: "+ documentId);
            return Response.status(Response.Status.NO_CONTENT).entity("document cannot be found for document id: "+ documentId).type(MediaType.APPLICATION_JSON).build();
        }
        List<Paragraph> definitionJson =null;
        try {
        Gson gson = new GsonBuilder().create();
        Type type = new TypeToken<List<Paragraph>>() {
        }.getType();
        definitionJson = gson.fromJson(definedTermParagraphList, type);

        logger.info("updateTerms:{} for doc id: {}", definitionJson,documentId);

        } catch(Exception ex) {
           logger.error("Failed to parse the json document: {}", ex);
        return Response.status(Response.Status.BAD_REQUEST).entity("Failed to parse the json document" ).type(MediaType.APPLICATION_JSON).build();
        }
        long startTime = System.currentTimeMillis();
        Map<Paragraph, List<String>> paraMap = Paragraph.combineTerms(definitionJson);
        logger.debug("combineTerms:" + paraMap);

        List<CoreMap> parasForUpdateBNI = new ArrayList<>();
            for (Paragraph  modifiedParagraph: paraMap.keySet()) {
                for (CoreMap paragraph : doc.getParagraphs()) {
                    if (paragraph.getId().equals(modifiedParagraph.getParagraphId())) {
                        paragraph.set(CoreAnnotations.IsUserObservationAnnotation.class, true);
                        //TODO: currently assuming that the trainer is always active
                        paragraph.set(CoreAnnotations.IsTrainerFeedbackAnnotation.class, true);
                        //TrainingWeightAnnotationHelper.updateTrainingWeight(paragraph, TrainingWeightAnnotationHelper.DEFINITION, userWeight);
                        // log the existing definitions
                        if (paragraph.containsKey(CoreAnnotations.IsDefinitionAnnotation.class)) {
                            List<List<String>> definitionList = DocumentHelper.getDefinedTermLists(paragraph);
                            logger.debug(paragraph.getId() + "\t" + "existing definition:" + "\t" + Joiner.on(" , ").join(definitionList));
                        }
                        if (paragraph.containsKey(CoreAnnotations.IsTOCAnnotation.class)) {
                            logger.debug(paragraph.getId() + "\t" + "existing TOCs:" + "\t" + DocumentHelper.getTOCLists(paragraph));
                        }

                        List<List<String>> addedTerms = new ArrayList<> ();
                        for(String modifiedTerm : paraMap.get(modifiedParagraph)) {
                            addedTerms.add(Lists.newArrayList(Splitter.on(" ").split(modifiedTerm)));
                        }

                        if (modifiedParagraph.getClassificationId() == Paragraph.DEFINITION_CLASSIFICATION) {
                            TrainingWeightAnnotationHelper.setTrainingWeight(paragraph, TrainingWeightAnnotationHelper.DEFINITION, userWeight);

                            //remove any existing annotations - definedTermList
                            paragraph.set(CoreAnnotations.DefinedTermTokensAnnotation.class, null);
                            paragraph.set(CoreAnnotations.IsDefinitionAnnotation.class, false);

                            // add annotations that received from client - definedTermList
                            if (!addedTerms.isEmpty()) {
                                for(List<String> addedTerm :addedTerms) {
                                    if (addedTerm != null && !addedTerm.isEmpty()) {
                                        if (!Joiner.on("").join(addedTerm).equals("")){
                                            DocumentHelper.setMatchedTokens(paragraph, addedTerm, Paragraph.DEFINITION_CLASSIFICATION);
                                        }
                                    }
                                }
                            }
                            // Add the userObserved paragraphs
                            parasForUpdateBNI.add(paragraph);
                            logger.debug("userObserved paragraphs:"+ "\t" + paragraph.getId());
                            if (paragraph.containsKey(CoreAnnotations.IsDefinitionAnnotation.class)) {
                                List<List<String>> definitionList = DocumentHelper.getDefinedTermLists(paragraph);
                                logger.debug(paragraph.getId() + "\t" + "userObserved paragraphs:" + "\t" + Joiner.on(" , ").join(definitionList));
                            }

                        }
                        if (modifiedParagraph.getClassificationId() == Paragraph.TOC_CLASSIFICATION) {
                            TrainingWeightAnnotationHelper.setTrainingWeight(paragraph, TrainingWeightAnnotationHelper.TOC, userWeight);

                            //remove any existing annotations - TOCList
                            paragraph.set(CoreAnnotations.TOCTokensAnnotation.class, null);
                            paragraph.set(CoreAnnotations.IsTOCAnnotation.class, false);

                            // add annotations that received from client - TOCList
                            if (addedTerms != null && !addedTerms.isEmpty()) {
                                for(List<String> addedTerm :addedTerms) {
                                    if (addedTerm != null && !addedTerm.isEmpty()) {
                                        if (!Joiner.on("").join(addedTerm).equals("")) {
                                            DocumentHelper.setMatchedTokens(paragraph, addedTerm, Paragraph.TOC_CLASSIFICATION);

                                        }
                                    }
                                }

                            }
                            // Add the userObserved paragraphs
                            parasForUpdateBNI.add(paragraph);
                            logger.debug("userObserved paragraphs for TOC:"+ "\t" + paragraph.getId());
                            // log the userObserved TOC
                            if (paragraph.containsKey(CoreAnnotations.IsTOCAnnotation.class)) {
                                logger.debug(paragraph.getId() + "\t" + "updated TOCs:" + "\t" + DocumentHelper.getTOCLists(paragraph));
                            }
                        } else if (modifiedParagraph.getClassificationId() == Paragraph.NONE_CLASSIFICATION) {
                            TrainingWeightAnnotationHelper.setTrainingWeight(paragraph, TrainingWeightAnnotationHelper.NONE, userWeight);
                            //remove any existing annotations - definedTermList
                            paragraph.set(CoreAnnotations.DefinedTermTokensAnnotation.class, null);
                            paragraph.set(CoreAnnotations.IsDefinitionAnnotation.class, false);
                            //remove any existing annotations - TOCList
                            paragraph.set(CoreAnnotations.TOCTokensAnnotation.class, null);
                            paragraph.set(CoreAnnotations.IsTOCAnnotation.class, false);
                            // Add the userObserved paragraphs
                            parasForUpdateBNI.add(paragraph);
                            logger.debug("userObserved paragraphs for NONE:"+ "\t" + paragraph.getId());
                        }
                        logger.debug("TrainingWeightAnnotation:" + paragraph.get(CoreAnnotations.TrainingWeightAnnotationFloat.class).toString());
                        break;
                    }
                }
            }
        logger.debug("Total time taken to process the updateTerm without updateBNI: {}", System.currentTimeMillis() - startTime);
        // persist the document using document id. Let's use the file name
        try {
            logger.debug("Number of Definition Paragraph before update BNI: {}",DocumentHelper.getDefinitionParagraphs(doc).size());
            logger.debug("Number of TOCs Paragraph before update BNI: {}",DocumentHelper.getTOCParagraphs(doc).size());
            if (Flags.get(Flags.ENABLE_UPDATE_BNI)) {
                doc = (Document) definitionClassifier.updateBNI(documentId, doc, parasForUpdateBNI);
                doc = (Document) tocClassifier.updateBNI(documentId, doc, parasForUpdateBNI);
            } else {
                logger.debug("No BNIIIIIIIIIIIIIIIIIIIIIIIIIII");
            }
            logger.debug("Number of Definition Paragraph After update BNI: {}",DocumentHelper.getDefinitionParagraphs(doc).size());
            logger.debug("Number of TOCs Paragraph after update BNI: {}",DocumentHelper.getTOCParagraphs(doc).size());
        } catch (Exception e) {
            logger.error("Failed to update updateBNI, using existing document : {}", e);
        }
        try{
            //clear userObservation from the documents before saving the document.
            /*
            for (CoreMap paragraph : doc.getParagraphs()) {
                if (paragraph.containsKey(CoreAnnotations.IsUserObservationAnnotation.class)) {
                    paragraph.set(CoreAnnotations.IsUserObservationAnnotation.class, null);
                }
            }
            */
            documentMap.put(documentId,doc);
            Files.write(JsonDeserializer.getJson(doc), new File(preEvaluatedFolder + documentId), Charset.defaultCharset());
        } catch (Exception e) {
            logger.error("Failed to persist the document object: {}", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to persist the document object" ).type(MediaType.APPLICATION_JSON).build();
        }


        logger.debug("updated document is stored in {}", preEvaluatedFolder + documentId);
        return Response.status(Response.Status.OK).entity(doc.getTarget().getBytes(Constants.DEFAULT_CHARSET)).type(MediaType.TEXT_HTML).build();
    }

    @GET
    @Path("/updateModel")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateModel( @Context HttpHeaders hh) {

        MultivaluedMap<String, String> headerParams = hh.getRequestHeaders();
        Map<String, Cookie> pathParams = hh.getCookies();
        logger.debug("getDocumentId: Cookie: {}", pathParams);
        if ( pathParams.get("documentId")==null) {
            logger.warn("documentId is missing from Cookie");
            return Response.status(Response.Status.EXPECTATION_FAILED).entity("documentId is missing from Cookie").type(MediaType.APPLICATION_JSON).build();
        }
        String documentId = pathParams.get("documentId").getValue();
        Document doc = documentMap.get(documentId);
        if (doc==null){
            logger.warn("document cannot be found for document id: "+ documentId);
            return Response.status(Response.Status.NO_CONTENT).entity("document cannot be found for document id: "+ documentId).type(MediaType.APPLICATION_JSON).build();
        }

        definitionClassifier.trainWithWeight(doc);
        try {
            definitionClassifier.persistModel();
        } catch (ObjectPersistUtil.ObjectPersistException e) {
            e.printStackTrace();
        }
        //tocClassifier.trainWithWeight(doc);
        for (CoreMap paragraph : doc.getParagraphs()) {
            if (paragraph.containsKey(CoreAnnotations.IsTrainerFeedbackAnnotation.class)) {
                TrainingWeightAnnotationHelper.updateTrainingWeight(paragraph, TrainingWeightAnnotationHelper.TOC, userWeight);
            }
        }

        try {
            tocClassifier.persistModel();
        } catch (ObjectPersistUtil.ObjectPersistException e) {
            e.printStackTrace();
        }
        try{
            documentMap.put(documentId,doc);
            Files.write(JsonDeserializer.getJson(doc), new File(preEvaluatedFolder + documentId), Charset.defaultCharset());
        } catch (Exception e) {
            logger.error("Failed to persist the document object: {}", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to persist the document object" ).type(MediaType.APPLICATION_JSON).build();
        }
        logger.debug("train the model using document is stored in {}", preEvaluatedFolder + documentId);

        return Response.ok().status(Response.Status.OK).entity("").type(MediaType.APPLICATION_JSON).build();
    }


    @GET
    @Path("/updateBNI")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateBNI( @Context HttpHeaders hh) {

        MultivaluedMap<String, String> headerParams = hh.getRequestHeaders();
        Map<String, Cookie> pathParams = hh.getCookies();
        logger.debug("getDocumentId: Cookie: {}", pathParams);
        if ( pathParams.get("documentId")==null) {
            logger.warn("documentId is missing from Cookie");
            return Response.status(Response.Status.EXPECTATION_FAILED).entity("documentId is missing from Cookie").type(MediaType.APPLICATION_JSON).build();

        }
        String documentId = pathParams.get("documentId").getValue();
        Document doc = documentMap.get(documentId);
        if (doc==null){
            logger.warn("document cannot be found for document id: "+ documentId);
            return Response.status(Response.Status.NO_CONTENT).entity("document cannot be found for document id: "+ documentId).type(MediaType.APPLICATION_JSON).build();
        }
        // persist the document using document id. Let's use the file name

        try {
            doc = (Document) definitionClassifier.classify(documentId,doc);
            doc = (Document) tocClassifier.classify(documentId,doc);
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
            logger.warn("document cannot be found for document id: "+ documentId);
            return Response.status(Response.Status.NOT_FOUND).entity("Failed to find the document in Map" ).type(MediaType.TEXT_PLAIN).build();
        }

        Gson gson = new GsonBuilder().create();
        StringBuffer buf = new StringBuffer();
        String annotationJson = "";
        String probabilityJson;
        buf.append("[");
        int paraIndex = 0;
        for (CoreMap paragraph : doc.getParagraphs()) {
            if (paragraph.getId().equals(paragraphId)) {
                //found it
                annotationJson = gson.toJson(paragraph);
                break;
            }
            paraIndex++;
        }

        // get the json from BNI
        logger.debug("ParaIndex: " + paraIndex);
        HashMap<String, HashMap<String, Double>> map = definitionClassifier.getBNIVisualMap(documentId, paraIndex);
        HashMap<String, HashMap<String, HashMap<String, Double>>> modelMap = definitionClassifier.getModelVisualMap(documentId);
        probabilityJson = gson.toJson(map);
        buf.append(probabilityJson);
        buf.append(",");
        probabilityJson = gson.toJson(modelMap);
        buf.append(probabilityJson);
        buf.append(",");
        map = tocClassifier.getBNIVisualMap(documentId, paraIndex);
        probabilityJson = gson.toJson(map);
        buf.append(probabilityJson);
        buf.append(",");
        modelMap = tocClassifier.getModelVisualMap(documentId);
        buf.append(gson.toJson(modelMap));
        buf.append(",");
        buf.append(annotationJson);
        buf.append("]");

        return Response.ok().status(Response.Status.OK).entity(buf.toString()).build();
    }


    @GET
    @Path("/getSimilarPara")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSimilarParagraph(@QueryParam("paragraphId") String paragraphId, @Context HttpHeaders hh) {

        MultivaluedMap<String, String> headerParams = hh.getRequestHeaders();
        Map<String, Cookie> pathParams = hh.getCookies();
        logger.debug("getDocumentId: Cookie: {}", pathParams);
        if ( pathParams.get("documentId")==null) {
            logger.warn("documentId is missing from Cookie");
            return Response.status(Response.Status.EXPECTATION_FAILED).entity("documentId is missing from Cookie").type(MediaType.TEXT_HTML).build();

        }
        String documentId = pathParams.get("documentId").getValue();

        Document doc = documentMap.get(documentId);
        if (doc==null) {
            logger.warn("document cannot be found for document id: "+ documentId);
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

    @GET
    @Path("/setFlags")
    @Produces(MediaType.TEXT_PLAIN)
    public Response setFlags(@QueryParam("flagName") String flagName,
                             @QueryParam("flagValue") String flagValue,
                             @Context HttpHeaders hh) {
        logger.debug("FlagName=" + flagName);
        logger.debug("FlagValue=" + flagValue);
        Flags.put(flagName, new Boolean(flagValue));
        return Response.ok().status(Response.Status.OK).entity("").build();
    }


    @GET
    @Path("/observeNone")
    @Produces(MediaType.TEXT_PLAIN)
    public Response observeNone(@Context HttpHeaders hh) throws IOException {

        MultivaluedMap<String, String> headerParams = hh.getRequestHeaders();
        Map<String, Cookie> pathParams = hh.getCookies();
        logger.debug("getDocumentId: Cookie: {}", pathParams);
        if ( pathParams.get("documentId")==null) {
            logger.warn("documentId is missing from Cookie");
            return Response.status(Response.Status.EXPECTATION_FAILED).entity("documentId is missing from Cookie").type(MediaType.TEXT_HTML).build();

        }
        String documentId = pathParams.get("documentId").getValue();

        Document doc = documentMap.get(documentId);
        if (doc==null) {
            logger.warn("document cannot be found for document id: "+ documentId);
            return Response.status(Response.Status.NOT_FOUND).entity("Failed to find the document in Map" ).type(MediaType.TEXT_PLAIN).build();
        }

        //iterate over each paragraph
        for(CoreMap paragraph : doc.getParagraphs()) {
            if (( paragraph.get(CoreAnnotations.IsDefinitionAnnotation.class) ||
                    paragraph.get(CoreAnnotations.IsTOCAnnotation.class))) {

            } else {
                paragraph.set(CoreAnnotations.IsUserObservationAnnotation.class, true);
                paragraph.set(CoreAnnotations.IsTrainerFeedbackAnnotation.class, true);
            }
        }
        Files.write(JsonDeserializer.getJson(doc), new File(preEvaluatedFolder + documentId), Charset.defaultCharset());
        return Response.ok().status(Response.Status.OK).entity("").build();
    }





}