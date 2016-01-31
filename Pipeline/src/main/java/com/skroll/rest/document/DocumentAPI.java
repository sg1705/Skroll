package com.skroll.rest.document;

import com.google.inject.Inject;
import com.skroll.analyzer.model.topic.RelatedParaWithinDocFinder;
import com.skroll.document.*;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.document.factory.CorpusFSDocumentFactory;
import com.skroll.document.factory.DocumentFactory;
import com.skroll.pipeline.util.Constants;
import com.skroll.rest.DocAPI;
import com.skroll.rest.RequestBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by saurabh on 12/30/15.
 */
@Path("/document")
public class DocumentAPI {

    public static final Logger logger = LoggerFactory.getLogger(DocumentAPI.class);

    // by default,
    private float userWeight = 95;

    @Inject
    private RelatedParaWithinDocFinder relatedFinder;

    @Inject
    @CorpusFSDocumentFactory
    private DocumentFactory documentFactory;

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

    @POST
    @Path("/{documentId}/related/p/{paraId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<ParaProto> findRelated(@PathParam("documentId")String documentId, @PathParam("paraId")String paraId) throws Exception {
        Document doc = documentFactory.get(documentId);
        CoreMap para = doc.getParagraphs().stream().filter( p -> p.getId().equals(paraId)).findFirst().get();
        List<CoreMap> relatedParas = relatedFinder.sortParasByDistance(doc, para);
        //create paraProto
        List<ParaProto> paraProtos = relatedParas.stream().map(p -> new ParaProto(documentId, p.getId())).collect(Collectors.toList());
        return paraProtos;
    }

}
