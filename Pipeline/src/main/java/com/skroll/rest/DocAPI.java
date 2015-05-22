package com.skroll.rest;

import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.skroll.classifier.Category;
import com.skroll.classifier.Classifier;
import com.skroll.classifier.ClassifierFactory;
import com.skroll.document.*;
import com.skroll.document.annotation.CategoryAnnotationHelper;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.document.annotation.TrainingWeightAnnotationHelper;
import com.skroll.parser.Parser;
import com.skroll.parser.extractor.ParserException;
import com.skroll.pipeline.util.Constants;
import com.skroll.util.Configuration;
import com.skroll.util.ObjectPersistUtil;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Path("/doc")
public class DocAPI {

    public static final Logger logger = LoggerFactory
            .getLogger(DocAPI.class);

    // documentMap is defined as concurrent hashmap
    // as we would like to share this hashmap between multiple requests from multiple clients
    // It provides the construct to synchronize only block of map not the whole hashmap.

    @Inject
    private ClassifierFactory classifierFactory;
    private static Configuration configuration = new Configuration();
    private static String preEvaluatedFolder = configuration.get("preEvaluatedFolder", "/tmp/");

    // by default,
    private float userWeight = 95;


    private Response logErrorResponse(String message, Exception e) {
        logger.error("{} : {}", message, e);
        if (e != null) e.printStackTrace();
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();

    }

    private Response logErrorResponse(String message) {
        return logErrorResponse(message, null);

    }

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_HTML)
    public Response upload(MultiPart multiPart, @Context HttpHeaders hh, @BeanParam RequestBean request) {

        List<BodyPart> bodyParts = multiPart.getBodyParts();
        logger.debug("BodyParts:" + bodyParts);
        // get the second part which is the project logo
        String message = null;
        MultivaluedMap<String, String> headerParams = hh.getRequestHeaders();
        logger.debug("headerParams:" + headerParams);
        Map<String, Cookie> pathParams = hh.getCookies();
        logger.debug("pathParams:" + pathParams);

        for (BodyPart bodyPart : bodyParts) {
            String fileName = ((FormDataBodyPart) bodyPart).getContentDisposition().getFileName();
            logger.debug("FileName:" + fileName);
            BodyPartEntity bpe = (BodyPartEntity) bodyPart.getEntity();
            InputStreamReader reader = new InputStreamReader(bpe.getInputStream());
            String content = null;
            Document document = null;
            //parse, classify and store the document
            try {
                content = CharStreams.toString(reader);
                document = Parser.parseDocumentFromHtml(content);
                for (Classifier classifier : request.getClassifiers()) {
                    document = (Document) classifier.classify(fileName, document);
                }
                request.getDocumentFactory().putDocument(fileName, document);
                logger.debug("Added document into the documentMap with a generated hash key:" + fileName);
                reader.close();
            } catch (ParserException e) {
                return logErrorResponse("Failed to parse the uploaded file", e);
            } catch (Exception e) {
                return logErrorResponse("Failed to classify", e);
            }
            // persist the document using document id. Let's use the file name
            try {
                Files.createParentDirs(new File(preEvaluatedFolder + fileName));
                Files.write(JsonDeserializer.getJson(document), new File(preEvaluatedFolder + fileName), Charset.defaultCharset());
            } catch (Exception e) {
                return logErrorResponse("Failed to persist the document object", e);
            }
            return Response.status(Response.Status.ACCEPTED).cookie(new NewCookie("documentId", fileName)).entity(document.getTarget().getBytes(Constants.DEFAULT_CHARSET)).build();
        }
        return logErrorResponse("Failed to process attachments. Reason ");
    }

    @GET
    @Path("/importDoc")
    @Produces(MediaType.APPLICATION_JSON)
    public Response importDoc(@QueryParam("documentId") String documentId, @Context HttpHeaders hh, @BeanParam RequestBean request) throws Exception {
        Document document = null;
        //fetch the document
        String content = Resources.asCharSource(new URL(documentId), Charset.forName("UTF-8")).read();
        String fileName = new URL(documentId).getPath();
        String[] strs = fileName.split("/");
        int lastIndexOfSlash = documentId.lastIndexOf('/');
        String url = documentId.substring(0, lastIndexOfSlash);

        fileName = strs[strs.length - 1];
        try {
            document = Parser.parseDocumentFromHtml(content, url);
            //Streams require final objects
            String fName = fileName;
            Document fDoc = document;
            request.getClassifiers().parallelStream().forEach(c -> c.classify(fName, fDoc));
            request.getDocumentFactory().putDocument(fileName, document);
            logger.debug("Added document into the documentMap with a generated hash key:{}" ,fileName);

        } catch (ParserException e) {
            return logErrorResponse("Failed to parse the uploaded file", e);
        } catch (Exception e) {
            return logErrorResponse("Failed to classify", e);
        }
        // persist the document using document id. Let's use the file name
        try {
            Files.createParentDirs(new File(preEvaluatedFolder + fileName));
            Files.write(JsonDeserializer.getJson(document), new File(preEvaluatedFolder + fileName), Charset.defaultCharset());
        } catch (Exception e) {
            return logErrorResponse("Failed to persist the document object", e);
        }
        logger.info(fileName);
        return Response.status(Response.Status.OK).cookie(new NewCookie("documentId", fileName)).entity("").type(MediaType.APPLICATION_JSON).build();
    }

    /**
     * list the docs under the pre Evaluated Folder
     *
     * @param hh
     * @return
     */
    @GET
    @Path("/listDocs")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listDocs(@Context HttpHeaders hh) {
        FluentIterable<File> iterable = Files.fileTreeTraverser().breadthFirstTraversal(new File(preEvaluatedFolder));
        List<String> docLists = new ArrayList<>();
        for (File f : iterable) {
            if (f.isFile()) {
                docLists.add(f.getName());

            }
        }
        String docsJson = new GsonBuilder().create().toJson(docLists);
        return Response.status(Response.Status.OK).entity(docsJson).build();
    }

    @GET
    @Path("/getDoc")
    @Produces(MediaType.TEXT_HTML)
    public Response getDoc(@QueryParam("documentId") String documentId, @Context HttpHeaders hh, @BeanParam RequestBean request) {

        Document doc = request.getDocument();
        String jsonString = null;
        if (doc == null) {
            logger.debug("Not found in documentMap, fetching from corpus: {}", documentId.toString());
            try {
                jsonString = Files.toString(new File(preEvaluatedFolder + documentId), Charset.defaultCharset());
                doc = JsonDeserializer.fromJson(jsonString);
            } catch (Exception e) {
                return logErrorResponse("Failed to read/deserialize document from Pre Evaluated Folder", e);
            }
        }
        //Streams require final objects
        final Document finalDoc = doc;
        try {
            request.getClassifiers().parallelStream().forEach(c -> c.classify(documentId, finalDoc));
            request.getDocumentFactory().putDocument(documentId, doc);
        } catch (Exception e) {
            return logErrorResponse("Failed to classify/store document", e);
        }
        return Response.status(Response.Status.OK).cookie(new NewCookie("documentId", documentId)).entity(doc.getTarget().getBytes(Constants.DEFAULT_CHARSET)).build();
    }

    @GET
    @Path("/getDocumentId")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDocumentId(@Context HttpHeaders hh, @BeanParam RequestBean request) {
        return Response.status(Response.Status.OK).entity(request.getDocumentId()).build();
    }

    @GET
    @Path("/getTerms")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTerm(@QueryParam("documentId") String documentId, @Context HttpHeaders hh, @BeanParam RequestBean request) {

        Document doc = request.getDocument();
        if (doc == null) return logErrorResponse("Failed to find the document in Map");

        List<Paragraph> termList = CategoryAnnotationHelper.getTerm(doc);

        String definitionJson = new GsonBuilder().create().toJson(termList);
        if (logger.isTraceEnabled()) logger.trace("definitionJson" + "\t" + definitionJson);
        Response r = Response.ok().status(Response.Status.OK).entity(definitionJson).build();
        return r;
    }

    @POST
    @Path("/updateTerms")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Produces(MediaType.TEXT_HTML)
    public Response updateTerms(String definedTermParagraphList, @Context HttpHeaders hh, @BeanParam RequestBean request) {

        logger.debug("updateTerms- DefinedTermParagraphList:{}", definedTermParagraphList);
        if (definedTermParagraphList.isEmpty()) return logErrorResponse("NO input data in post request");
        String documentId = request.getDocumentId();
        Document doc = request.getDocument();
        if (doc == null) return logErrorResponse("document cannot be found for document id: " + documentId);

        List<Paragraph> definitionJson = null;
        try {
            definitionJson = new GsonBuilder().create().fromJson(definedTermParagraphList, new TypeToken<List<Paragraph>>() {
            }.getType());
            logger.info("updateTerms:{} for doc id: {}", definitionJson, documentId);

        } catch (Exception ex) {
            return logErrorResponse("Failed to parse the json document: {}", ex);
        }
        long startTime = System.currentTimeMillis();

        Map<Paragraph, List<String>> paraMap = Paragraph.combineTerms(definitionJson);
        logger.debug("combineTerms:{}", paraMap);

        List<CoreMap> parasForUpdateBNI = new ArrayList<>();

        for (Paragraph modifiedParagraph : paraMap.keySet()) {
            for (CoreMap paragraph : doc.getParagraphs()) {
                if (paragraph.getId().equals(modifiedParagraph.getParagraphId())) {

                    paragraph.set(CoreAnnotations.IsUserObservationAnnotation.class, true);
                    //TODO: currently assuming that the trainer is always active
                    paragraph.set(CoreAnnotations.IsTrainerFeedbackAnnotation.class, true);
                    // log the existing definitions

                    CategoryAnnotationHelper.displayTerm(paragraph);

                    List<List<Token>> addedTerms = new ArrayList<>();
                    if (!(modifiedParagraph.getClassificationId() == Category.NONE)) {

                        for (String modifiedTerm : paraMap.get(modifiedParagraph)) {
                            List<Token> tokens = null;
                            try {
                                Document tempDoc = Parser.parseDocumentFromHtml(modifiedTerm);
                                tokens = DocumentHelper.getTokensOfADoc(tempDoc);
                            } catch (ParserException e) {
                                e.printStackTrace();
                            }
                            addedTerms.add(tokens);
                        }
                    }

                    // check whether the term is "" ro empty or not that received from client
                    //remove any existing annotations
                    CategoryAnnotationHelper.clearAnnotations(paragraph);
                    // add annotations that received from client - definedTermList
                    if (addedTerms.isEmpty()) {
                        TrainingWeightAnnotationHelper.setTrainingWeight(paragraph, Category.NONE, userWeight);
                    } else {
                        for (List<Token> addedTerm : addedTerms) {
                            if (addedTerm == null || addedTerm.isEmpty()) {
                                TrainingWeightAnnotationHelper.setTrainingWeight(paragraph, Category.NONE, userWeight);
                            } else {
                                if (Joiner.on("").join(addedTerm).equals("")) {
                                    TrainingWeightAnnotationHelper.setTrainingWeight(paragraph, Category.NONE, userWeight);
                                } else {
                                    if (CategoryAnnotationHelper.setMatchedText(paragraph, addedTerm, modifiedParagraph.getClassificationId())) {
                                        TrainingWeightAnnotationHelper.setTrainingWeight(paragraph, modifiedParagraph.getClassificationId(), userWeight);
                                    } else {
                                        TrainingWeightAnnotationHelper.setTrainingWeight(paragraph, Category.NONE, userWeight);
                                    }
                                }
                            }
                        }
                    }
                    // Add the userObserved paragraphs
                    parasForUpdateBNI.add(paragraph);
                    logger.debug("userObserved paragraphs:\t {}", paragraph.getId());
                    CategoryAnnotationHelper.displayTerm(paragraph);
                    logger.debug("TrainingWeightAnnotation: {}", paragraph.get(CoreAnnotations.TrainingWeightAnnotationFloat.class));
                    break;
                }
            }
        }
        logger.info("Total time taken to process the updateTerm without updateBNI: {} msec", System.currentTimeMillis() - startTime);
        // persist the document using document id. Let's use the file name
        try {
            if (!parasForUpdateBNI.isEmpty()) {

                for (Classifier classifier : request.getClassifiers()) {
                    doc = (Document) classifier.updateBNI(documentId, doc, parasForUpdateBNI);
                }
                request.getDocumentFactory().putDocument(documentId, doc);
            }
        } catch (Exception e) {
            logger.error("Failed to update updateBNI, using existing document : {}", e);
        }

        logger.debug("updated document is stored in {} {}", preEvaluatedFolder, documentId);
        return Response.status(Response.Status.OK).entity(doc.getTarget().getBytes(Constants.DEFAULT_CHARSET)).type(MediaType.TEXT_HTML).build();
    }

    @GET
    @Path("/updateModel")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateModel(@Context HttpHeaders hh, @BeanParam RequestBean request) {

        String documentId = request.getDocumentId();
        Document doc = request.getDocument();
        if (doc == null) {
            return logErrorResponse("document cannot be found for document id: " + documentId);
        }

        for (Classifier classifier : request.getClassifiers()) {
            classifier.trainWithWeight(doc);
            for (CoreMap paragraph : doc.getParagraphs()) {
                if (paragraph.containsKey(CoreAnnotations.IsTrainerFeedbackAnnotation.class)) {
                    TrainingWeightAnnotationHelper.updateTrainingWeight(paragraph, classifier.getCategory().getId(), userWeight);
                }
            }
            try {
                classifier.persistModel();
            } catch (ObjectPersistUtil.ObjectPersistException e) {
                return logErrorResponse("Failed to persist the model:" + classifier.toString());
            }
        }
        request.getDocumentFactory().putDocument(documentId, doc);

        try {
            Files.write(JsonDeserializer.getJson(doc), new File(preEvaluatedFolder + documentId), Charset.defaultCharset());
        } catch (Exception e) {
            logErrorResponse("Failed to persist the document object: {}", e);
        }
        logger.debug("train the model using document is stored in {} {}", preEvaluatedFolder, documentId);
        return Response.ok().status(Response.Status.OK).entity("model has been updated").build();
    }

    @GET
    @Path("/observeNone")
    @Produces(MediaType.TEXT_PLAIN)
    public Response observeNone(@Context HttpHeaders hh, @BeanParam RequestBean request) throws IOException {

        String documentId = request.getDocumentId();
        Document doc = request.getDocument();
        if (doc == null) return logErrorResponse("document cannot be found for document id: " + documentId);

        //iterate over each paragraph
        for (CoreMap paragraph : doc.getParagraphs()) {
            boolean IsNoCategoryExist = true;
            paragraph.set(CoreAnnotations.IsUserObservationAnnotation.class, true);
            paragraph.set(CoreAnnotations.IsTrainerFeedbackAnnotation.class, true);
            for (int categoryId : Category.getCategories()) {
                TrainingWeightAnnotationHelper.setTrainingWeight(paragraph, categoryId, userWeight);
                IsNoCategoryExist = false;
            }
            if (IsNoCategoryExist) {
                CategoryAnnotationHelper.clearAnnotations(paragraph);
                TrainingWeightAnnotationHelper.setTrainingWeight(paragraph, Category.NONE, userWeight);
            }
        }
        Files.write(JsonDeserializer.getJson(doc), new File(preEvaluatedFolder + documentId), Charset.defaultCharset());
        return Response.ok().status(Response.Status.OK).entity("").build();
    }

}