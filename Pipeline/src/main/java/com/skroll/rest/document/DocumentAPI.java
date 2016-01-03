package com.skroll.rest.document;

import com.skroll.classifier.Category;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.document.annotation.DocTypeAnnotationHelper;
import com.skroll.parser.Parser;
import com.skroll.parser.extractor.ParserException;
import com.skroll.pipeline.util.Constants;
import com.skroll.rest.RequestBean;
import com.skroll.util.UniqueIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by saurabh on 12/30/15.
 */
@Path("/document")
public class DocumentAPI {

    public static final Logger logger = LoggerFactory.getLogger(DocumentAPI.class);

    // by default,
    private float userWeight = 95;


    @POST
    @Path("/importPartialFromUrl")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_HTML)
    public Response importFromUrl(@QueryParam("docType")String docType, @QueryParam("url") String url, @QueryParam("partialParse") String partialParse, @BeanParam RequestBean request) throws Exception {
        Document document = null;
        String documentId = null;
        boolean inCache = false;
        try {
            documentId = UniqueIdGenerator.generateId(url);
            if (request.getDocumentFactory().isDocumentExist(documentId)) {
                document = request.getDocumentFactory().get(documentId);
                inCache = true;
                logger.debug("Fetched the existing document: {}", documentId);
            } else {
                if (partialParse.equals("true")) {
                    document = Parser.parsePartialDocumentFromUrl(url);
                } else {
                    document = Parser.parseDocumentFromUrl(url);
                    String fDocumentId = documentId;
                    document.setId(documentId);
                    Document fDoc = document;
                    DocTypeAnnotationHelper.annotateDocTypeWithWeightAndUserObservation(document, Category.getDocTypeId(docType), userWeight );
                    logger.info("DocType:" + DocTypeAnnotationHelper.getDocType(fDoc));
                    request.getClassifiersForClassify(fDoc).forEach(c -> c.classify(fDocumentId, fDoc));
                    request.getDocumentFactory().putDocument(document);
                    request.getDocumentFactory().saveDocument(document);
                    logger.debug("Added document into the documentMap with a generated hash key:{}", documentId);
                }
            }
        } catch(ParserException e){
            //return logErrorResponse("Failed to parse the uploaded file", e);
            throw e;
        } catch(Exception e){
            throw e;
            //return logErrorResponse("Failed to classify", e);
        }

        logger.info("DocumentId:{}",documentId);
        logger.info("InCache:{}",inCache);
        if (document == null) {
            logger.debug("Issue in parsing document: {}", documentId.toString());
            throw new ParserException("Failed to parse document");
            //return logErrorResponse("Failed to read/deserialize document from Pre Evaluated Folder");
        }

        DocumentProto proto = getDocumentProto(document);
        proto.setPartiallyParsed(true);
        DocumentContentProto contentProto = new DocumentContentProto();
        contentProto.setContent(DocumentHelper.getProcessedHtml(document).getBytes(Constants.DEFAULT_CHARSET));

        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        injectDocumentProtoInHeader(proto, response);

        return response
                .entity(contentProto.getContent())
                .type(MediaType.TEXT_HTML).build();

    }

    private static DocumentProto getDocumentProto(Document document) {
        DocumentProto proto = new DocumentProto();
        proto.setId(document.getId());
        proto.setUrl(document.get(CoreAnnotations.SourceUrlAnnotation.class));
        proto.setFormat(document.get(CoreAnnotations.DocumentFormatAnnotationInteger.class));
        proto.setTypeId(DocTypeAnnotationHelper.getDocType(document));
        return proto;
    }

    private static Response.ResponseBuilder injectDocumentProtoInHeader(DocumentProto proto, Response.ResponseBuilder response) {
        response.header("id", proto.getId());
        response.header("typeId", proto.getTypeId());
        response.header("format", proto.getFormat());
        response.header("url", proto.getUrl());

        return response;
    }

}
