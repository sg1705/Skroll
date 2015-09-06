package com.skroll.rest;

import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.skroll.classifier.Category;
import com.skroll.classifier.Classifier;
import com.skroll.classifier.ClassifierFactory;
import com.skroll.document.*;
import com.skroll.document.annotation.CategoryAnnotationHelper;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.document.annotation.DocTypeAnnotationHelper;
import com.skroll.document.factory.DocumentFactory;
import com.skroll.parser.Parser;
import com.skroll.parser.extractor.ParserException;
import com.skroll.pipeline.util.Constants;
import com.skroll.util.Configuration;
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

    @Inject
    private Configuration configuration;
    //private String preEvaluatedFolder = configuration.get("preEvaluatedFolder", "/tmp/");
    //private String preEvaluatedFolder = null;

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

    /**
     *
     * @param multiPart
     * @param hh
     * @param request
     * @return
     */
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
            String documentId = null;
            //parse, classify and store the document
            try {
                content = CharStreams.toString(reader);
                //TODO: Add back the UniqueIdGenerator code after viewer integration
                documentId = fileName; //UniqueIdGenerator.generateId(content);
                List<Classifier> classifiers = request.getClassifiers();
                document = fetchOrSaveDocument(documentId, content, request.getDocumentFactory(), classifiers);
                reader.close();
            } catch (Exception e) {
                return logErrorResponse("Failed to classify", e);
            }
            return Response.status(Response.Status.ACCEPTED)
                    .header("documentId", documentId)
                    .entity(document.getTarget().getBytes(Constants.DEFAULT_CHARSET))
                    .build();
        }
        return logErrorResponse("Failed to process attachments. Reason ");
    }

    @GET
    @Path("/importDoc")
    @Produces(MediaType.TEXT_HTML)
    public Response importDoc(@QueryParam("documentId") String documentId, @QueryParam("partialParse") String partialParse, @Context HttpHeaders hh, @BeanParam RequestBean request) throws Exception {
        Document document = null;
        String fileName = new URL(documentId).getPath();
        String[] strs = fileName.split("/");
        if (partialParse == null)
            partialParse = "false";
        fileName = strs[strs.length - 1];
        String uniqueDocumentId = null;
        try {
            List<Classifier> classifiers = request.getClassifiers();
            //TODO: Add back the UniqueIdGenerator code after viewer integration
            //uniqueDocumentId = UniqueIdGenerator.generateId(content);
            //fetchOrSaveDocument(uniqueDocumentId, content, request.getDocumentFactory(), classifiers);

            if (partialParse.equals("true")) {
                document = Parser.parsePartialDocumentFromUrl(documentId);
            } else {
                document = Parser.parseDocumentFromUrl(documentId);
                //Streams require final objects
                String fName = fileName;
                Document fDoc = document;
                request.getClassifiers().forEach(c -> c.classify(fName, fDoc));
                document.setId(fileName);
                request.getDocumentFactory().putDocument(document);
                request.getDocumentFactory().saveDocument(document);
                logger.debug("Added document into the documentMap with a generated hash key:{}" ,fileName);
            }

        } catch (ParserException e) {
            return logErrorResponse("Failed to parse the uploaded file", e);
        } catch (Exception e) {
            return logErrorResponse("Failed to classify", e);
        }
        logger.info(fileName);
        return Response.status(Response.Status.OK)
                .header("documentId", fileName)
                .entity(document.getTarget().getBytes(Constants.DEFAULT_CHARSET))
                .type(MediaType.TEXT_HTML).build();
    }

    private Document fetchOrSaveDocument(String documentId, String content, DocumentFactory documentFactory, List<Classifier> classifiers) throws Exception {
        Document document;

        if (documentFactory.isDocumentExist(documentId)) {
            document = documentFactory.get(documentId);
            logger.debug("Fetched the existing document: {}", documentId);
        } else {
            document = Parser.parseDocumentFromHtml(content);
            document.setId(documentId);
            for (Classifier classifier : classifiers) {
                document = (Document) classifier.classify(documentId, document);
            }
            documentFactory.putDocument(document);
            documentFactory.saveDocument(document);
            logger.debug("Added document into the documentMap with a generated hash key: {}", documentId);
        }
        return document;
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
        String preEvaluatedFolder = configuration.get("preEvaluatedFolder", "/tmp/");
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

        logger.info("Opening [{}]", documentId);
        Document doc = request.getDocument();
        if (doc == null) {
            logger.debug("Not found in documentMap, fetching from corpus: {}", documentId.toString());
            return logErrorResponse("Failed to read/deserialize document from Pre Evaluated Folder");
        }
        //Streams require final objects
        final Document finalDoc = doc;
        try {
            request.getClassifiers().forEach(c -> c.classify(documentId, finalDoc));
            request.getDocumentFactory().putDocument(doc);
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
    public Response getTerm(@Context HttpHeaders hh, @BeanParam RequestBean request) {

        Document doc = request.getDocument();
        if (doc == null) return logErrorResponse("Failed to find the document in Map");

        List<TermProto> termList = CategoryAnnotationHelper.getParagraphsAnnotatedWithAnyCategory(doc);

        String term = new GsonBuilder().create().toJson(termList);
        if (logger.isTraceEnabled()) logger.trace("term" + "\t" + term);
        Response r = Response.ok().status(Response.Status.OK).entity(term).build();
        return r;
    }

    @POST
    @Path("/updateTerms")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Produces(MediaType.TEXT_HTML)
    public Response updateTerms(String termsList, @Context HttpHeaders hh, @BeanParam RequestBean request) {

        logger.debug("updateTerms- TermList:{}", termsList);
        if (termsList.isEmpty()) return logErrorResponse("NO input data in post request");
        String documentId = request.getDocumentId();
        Document doc = request.getDocument();
        if (doc == null) return logErrorResponse("document cannot be found for document id: " + documentId);

        List<TermProto> termsproto = null;
        try {
            termsproto = new GsonBuilder().create().fromJson(termsList, new TypeToken<List<TermProto>>() {
            }.getType());
            logger.info("updateTerms:{} for doc id: {}", termsproto, documentId);

        } catch (Exception ex) {
            return logErrorResponse("Failed to parse the json document: {}", ex);
        }
        long startTime = System.currentTimeMillis();
        int updateCategoryId =Category.NONE;
        Map<TermProto, List<String>> termProtoMap = TermProto.combineTerms(termsproto);
        logger.debug("combineTerms:{}", termProtoMap);

        List<CoreMap> parasForUpdateBNI = new ArrayList<>();

        for (TermProto termProto : termProtoMap.keySet()) {
            for (CoreMap paragraph : doc.getParagraphs()) {
                if (paragraph.getId().equals(termProto.getParagraphId())) {

                    paragraph.set(CoreAnnotations.IsUserObservationAnnotation.class, true);
                    paragraph.set(CoreAnnotations.IsTrainerFeedbackAnnotation.class, true);

                    // log the existing definitions
                    CategoryAnnotationHelper.displayParagraphsAnnotatedWithAnyCategory(paragraph);

                    List<List<Token>> addedTerms = new ArrayList<>();
                    if (!(termProto.getClassificationId() == Category.NONE)) {

                        for (String modifiedTerm : termProtoMap.get(termProto)) {
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
                    updateCategoryId = termProto.getClassificationId();
                    // check whether the term is "" ro empty or not that received from client
                    //remove any existing annotations
                    CategoryAnnotationHelper.clearCategoryAnnotations(paragraph);
                    // add annotations that received from client - definedTermList
                    if (addedTerms.isEmpty()) {
                        CategoryAnnotationHelper.annotateCategoryWeight(paragraph, Category.NONE, userWeight);
                    } else {
                        for (List<Token> addedTerm : addedTerms) {
                            if (addedTerm == null || addedTerm.isEmpty()) {
                                CategoryAnnotationHelper.annotateCategoryWeight(paragraph, Category.NONE, userWeight);
                            } else {
                                if (Joiner.on("").join(addedTerm).equals("")) {
                                    CategoryAnnotationHelper.annotateCategoryWeight(paragraph, Category.NONE, userWeight);
                                } else {
                                    if (CategoryAnnotationHelper.setMatchedText(paragraph, addedTerm, termProto.getClassificationId())) {
                                        CategoryAnnotationHelper.annotateCategoryWeight(paragraph, termProto.getClassificationId(), userWeight);

                                    } else {
                                        CategoryAnnotationHelper.annotateCategoryWeight(paragraph, Category.NONE, userWeight);
                                    }
                                }
                            }
                        }
                    }
                    // Add the userObserved paragraphs
                    parasForUpdateBNI.add(paragraph);
                    logger.debug("userObserved paragraphs:\t {}", paragraph.getId());
                    CategoryAnnotationHelper.displayParagraphsAnnotatedWithAnyCategory(paragraph);
                    break;
                }
            }
        }
        logger.info("Total time taken to process the updateTerm without updateBNI: {} msec", System.currentTimeMillis() - startTime);
        // persist the document using document id. Let's use the file name
        try {
            if (!parasForUpdateBNI.isEmpty()) {
                for (Classifier classifier : request.getClassifiers()) {
                    logger.debug("updateCategoryId: {}", updateCategoryId);
                    //if(classifier.getModelRVSetting().getCategoryId() == updateCategoryId) {
                        doc = (Document) classifier.updateBNI(documentId, doc, parasForUpdateBNI);
                    //}
                }
                request.getDocumentFactory().putDocument(doc);
            }
        } catch (Exception e) {
            logger.error("Failed to update updateBNI:", e);
        }

        //logger.debug("updated document is stored in {} {}", preEvaluatedFolder, documentId);
        return Response.status(Response.Status.OK).entity("").type(MediaType.TEXT_HTML).build();
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
            try {
                classifier.persistModel();
            } catch (Exception e) {
                return logErrorResponse("Failed to persist the model:" + classifier.toString());
            }
        }
        for (CoreMap paragraph : doc.getParagraphs()) {
            if (paragraph.containsKey(CoreAnnotations.IsUserObservationAnnotation.class)) {
                CategoryAnnotationHelper.copyCurrentCategoryWeightsToPrior(paragraph);
            }
        }

        try {
            request.getDocumentFactory().putDocument(doc);
            request.getDocumentFactory().saveDocument(doc);
//            Files.write(JsonDeserializer.getJson(doc), new File(preEvaluatedFolder + documentId), Charset.defaultCharset());
        } catch (Exception e) {
            logErrorResponse("Failed to persist the document object: {}", e);
        }
//        logger.debug("train the model using document is stored in {} {}", preEvaluatedFolder, documentId);
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
                if (CategoryAnnotationHelper.isParagraphAnnotatedWithCategoryId(paragraph, categoryId)) {
                    CategoryAnnotationHelper.annotateCategoryWeight(paragraph, categoryId, userWeight);
                    IsNoCategoryExist = false;
                }
            }
            if (IsNoCategoryExist) {
                CategoryAnnotationHelper.clearCategoryAnnotations(paragraph);
                CategoryAnnotationHelper.annotateCategoryWeight(paragraph, Category.NONE, userWeight);
            }
        }
        try {
            request.getDocumentFactory().putDocument(doc);
            request.getDocumentFactory().saveDocument(doc);
        } catch (Exception e) {
            logErrorResponse("Failed to persist the document object: {}", e);
        }
//        Files.write(JsonDeserializer.getJson(doc), new File(preEvaluatedFolder + documentId), Charset.defaultCharset());
        return Response.ok().status(Response.Status.OK).entity("").build();
    }


    @POST
    @Path("/unObserve")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Produces(MediaType.TEXT_HTML)
    public Response unObserve(String observationsJson, @Context HttpHeaders hh, @BeanParam RequestBean request) {

        logger.debug("unObserve- DefinedTermParagraphList:{}", observationsJson);
        if (observationsJson.isEmpty()) return logErrorResponse("NO input data in post request");
        String documentId = request.getDocumentId();
        Document doc = request.getDocument();
        if (doc == null)
            return logErrorResponse("document cannot be found for document id: " + documentId);

        List<TermProto> observations;
        try {
            observations = new GsonBuilder()
                    .create()
                    .fromJson(observationsJson, new TypeToken<List<TermProto>>() {
                    }.getType());
            logger.info("updateTerms:{} for doc id: {}", observations, documentId);

        } catch (Exception ex) {
            return logErrorResponse("Failed to parse the json document: {}", ex);
        }
        Map<TermProto, List<String>> paraMap = TermProto.combineTerms(observations);
        List<CoreMap> parasForUpdateBNI = new ArrayList<>();
        for (TermProto observation : paraMap.keySet()) {
            doc.getParagraphs()
                    .stream()
                    .filter( p -> p.getId().equals(observation.getParagraphId()))
                    .forEach( p -> {
                        logger.debug("Unobserved - {}", p.getId());
                        //un observe this paragraph
                        CategoryAnnotationHelper.clearObservations(p);
                        // Add the userObserved paragraphs
                        parasForUpdateBNI.add(p);
                    });
        }
        try {
            if (!parasForUpdateBNI.isEmpty()) {
                for (Classifier classifier : request.getClassifiers()) {
                    doc = (Document) classifier.updateBNI(documentId, doc, parasForUpdateBNI);
                }
                request.getDocumentFactory().putDocument(doc);
            }
        } catch (Exception e) {
            logger.error("Failed to update updateBNI:", e);
        }

        return Response.status(Response.Status.OK).entity("").type(MediaType.TEXT_HTML).build();
    }

    @GET
    @Path("/updateDocType")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateDocType(@Context HttpHeaders hh, @BeanParam DocTypeRequestBean request) {

        String documentId = request.getDocumentId();
        Document doc = request.getDocument();
        int docType = request.getDocType();

        if (doc == null) {
            return logErrorResponse("document cannot be found for document id: " + documentId);
        }

        DocTypeAnnotationHelper.annotateDocTypeWithWeightAndUserObservation(doc,docType,userWeight);

        logger.debug("updateDocType using document id {}", documentId);
        return Response.ok().status(Response.Status.OK).entity("DocType has been updated").build();
    }

}