package com.skroll.rest.document;

import com.skroll.classifier.Category;
import com.skroll.document.Document;
import com.skroll.document.DocumentFormat;
import com.skroll.document.DocumentHelper;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.document.annotation.DocTypeAnnotationHelper;
import com.skroll.parser.Parser;
import com.skroll.parser.extractor.ParserException;
import com.skroll.pipeline.util.Constants;
import com.skroll.rest.DocAPI;
import com.skroll.rest.RequestBean;
import com.skroll.util.UniqueIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;

/**
 * Created by saurabh on 12/30/15.
 */
@Path("/document")
public class DocumentAPI {

    public static final Logger logger = LoggerFactory.getLogger(DocumentAPI.class);

    // by default,
    private float userWeight = 95;


    @GET
    @Path("/{documentId}/content")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_HTML)
    public Response importFromUrl(@PathParam("documentId") String documentId,  @BeanParam RequestBean request) throws Exception {
        logger.info("Opening [{}]", documentId);
        Document document = request.getDocument();
        if (document == null) {
            logger.debug("Not found in documentMap, fetching from corpus: {}", documentId.toString());
            throw new Exception("Failed to read/deserialize document from Pre Evaluated Folder");
        }

        if (document.get(CoreAnnotations.DocumentFormatAnnotationInteger.class) == null) {
            document.set(CoreAnnotations.DocumentFormatAnnotationInteger.class, DocumentFormat.HTML.id());
        }

        //build a response
        DocumentProto proto = DocAPI.getDocumentProto(document);
        Response.ResponseBuilder response = Response.status(Response.Status.OK);
        DocAPI.injectDocumentProtoInHeader(proto, response);
        if (document.get(CoreAnnotations.DocumentFormatAnnotationInteger.class) == DocumentFormat.PDF.id()) {
            response.entity(DatatypeConverter.parseBase64Binary(document.getSource())).type(MediaType.TEXT_PLAIN);
        } else {
            response.entity(DocumentHelper.getProcessedHtml(document).getBytes(Constants.DEFAULT_CHARSET))
                    .type(MediaType.TEXT_HTML);
        }

        return response.build();
    }

}
