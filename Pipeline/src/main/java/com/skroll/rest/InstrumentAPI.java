package com.skroll.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.skroll.classifier.Classifier;
import com.skroll.classifier.ClassifierFactory;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.document.annotation.DocTypeAnnotationHelper;
import com.skroll.util.Configuration;
import com.skroll.util.Flags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/instrument")
public class InstrumentAPI {

    public static final Logger logger = LoggerFactory
            .getLogger(InstrumentAPI.class);

    // documentMap is defined as concurrent hashmap
    // as we would like to share this hashmap between multiple requests from multiple clients
    // It provides the construct to synchronize only block of map not the whole hashmap.

    @Inject private ClassifierFactory classifierFactory;
    private static Configuration configuration = new Configuration();
    private static String preEvaluatedFolder = configuration.get("preEvaluatedFolder", "/tmp/");


    private Response logErrorResponse( String message, Exception e){
        logger.error("{} : {}",message, e);
        if(e!=null) e.printStackTrace();
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();

    }
    private Response logErrorResponse(String message){
        return logErrorResponse(message, null);

    }

    @GET
    @Path("/getParagraphJson")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getParagraphJson(@QueryParam("paragraphId") String paragraphId, @Context HttpHeaders hh,@BeanParam RequestBean request) {

        String documentId = request.getDocumentId();
        Document document = request.getDocument();
        if (document == null) {
            return logErrorResponse("document cannot be found for document id: " + documentId);
        }
        //For debug purpose, display top 20 paragraph to know the company name of the document.
        document.getParagraphs().stream().limit(20).forEach(para -> logger.debug("{}: {}", para.getId(), para.getText()));
        Gson gson = new GsonBuilder().create();
        StringBuffer buf = new StringBuffer();
        String annotationJson = "";
        String probabilityJson;
        buf.append("[");
        int paraIndex = 0;
        probabilityJson = gson.toJson("DocType Annotation : ");
        buf.append(probabilityJson);
        buf.append(",");
        probabilityJson = gson.toJson(DocTypeAnnotationHelper.getDocLevelCategoryAnnotation(document));
        buf.append(probabilityJson);
        buf.append(",");

        for (CoreMap paragraph : document.getParagraphs()) {
            if (paragraph.getId().equals(paragraphId)) {
                //found it
                annotationJson = gson.toJson(paragraph);
                break;
            }
            paraIndex++;
        }

        for (CoreMap table : document.getTables()) {
            if (table.get(CoreAnnotations.ParagraphIdAnnotation.class).equals(paragraphId)) {
                //found a matching table
                annotationJson = annotationJson + "," + gson.toJson(table);
            }
        }

        // get the json from BNI
        try {
            /*
            HashMap<String, HashMap<String, HashMap<String, Double>>> map = classifierFactory.getClassifier(ClassifierFactory.TOC_CLASSIFIER_ID).getBNIVisualMap(doc, paraIndex);
            HashMap<String, HashMap<String, HashMap<String, Double>>> modelMap = classifierFactory.getClassifier(ClassifierFactory.TOC_CLASSIFIER_ID).getModelVisualMap();
            probabilityJson = gson.toJson(map);
            buf.append(probabilityJson);
            buf.append(",");
            probabilityJson = gson.toJson(modelMap);
            buf.append(probabilityJson);
            buf.append(",");
            */
            for (Classifier classifier : request.getClassifiersForClassify(document)) {
                probabilityJson = gson.toJson("ClassifierId:" + classifier.getId());
                buf.append(probabilityJson);
                buf.append(",");
                HashMap<String, HashMap<String, HashMap<String, Double>>> map = classifier.getBNIVisualMap(document, paraIndex);
                probabilityJson = gson.toJson(map);
                buf.append(probabilityJson);
                buf.append(",");
            }

            for (Classifier classifier : request.getClassifiersForClassify(document)) {
                probabilityJson = gson.toJson("ClassifierId:" + classifier.getId());
                buf.append(probabilityJson);
                buf.append(",");
                HashMap<String, HashMap<String, HashMap<String, Double>>> modelMap = classifier.getModelVisualMap();
                buf.append(gson.toJson(modelMap));
                buf.append(",");
            }

            buf.append(annotationJson);
            buf.append("]");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Response.ok().status(Response.Status.OK).entity(buf.toString()).build();
    }


    @GET
    @Path("/getSimilarPara")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSimilarParagraph(@QueryParam("paragraphId") String paragraphId, @Context HttpHeaders hh, @BeanParam RequestBean request) {

        String documentId = request.getDocumentId();
        Document doc = request.getDocument();
        if (doc == null) return logErrorResponse("document cannot be found for document id: " + documentId);

        if (doc == null) {
            logger.warn("document cannot be found for document id: " + documentId);
            return Response.status(Response.Status.NOT_FOUND).entity("Failed to find the document in Map").type(MediaType.TEXT_PLAIN).build();
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
    @Path("/getProbabilityDump")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProbabilityDump(@Context HttpHeaders hh, @BeanParam RequestBean request) throws IOException {

        String documentId = request.getDocumentId();
        Document document = request.getDocument();
        if (document == null) return logErrorResponse("document cannot be found for document id: " + documentId);
        if (document == null) {
            logger.warn("document cannot be found for document id: " + documentId);
            return Response.status(Response.Status.NOT_FOUND).entity("Failed to find the document in Map").type(MediaType.TEXT_PLAIN).build();
        }
        // get probabilities
        try {
            Map<String, Double> allPs = new HashMap();
            for (Classifier classifier : request.getClassifiersForClassify(document)) {
                if (classifier.getId() == 1) {
                    allPs = classifier.getProbabilityDataForDoc(document);
                }
            }
            Gson gson = new GsonBuilder().create();
            String json = gson.toJson(allPs);

            return Response.ok().status(Response.Status.OK).entity(json).build();
        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.EXPECTATION_FAILED).entity("documentId is missing").type(MediaType.TEXT_HTML).build();

        }
    }

}