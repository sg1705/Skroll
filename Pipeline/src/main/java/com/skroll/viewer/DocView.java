package com.skroll.viewer;

import com.aliasi.util.Files;
import com.skroll.document.Document;
import com.skroll.document.factory.CorpusFSDocumentFactory;
import com.skroll.document.factory.DocumentFactory;
import com.skroll.pipeline.util.Constants;
import com.skroll.rest.WebServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;

/**
 * Created by Shwetambar on 9/14/15.
 */
@Path("/")
public class DocView {
    public static final Logger logger = LoggerFactory.getLogger(DocView.class);
    private static String DOC_VIEW_HTML_1 = "";
    private static String DOC_VIEW_HTML_2 = "";

    @Inject
    @CorpusFSDocumentFactory DocumentFactory documentFactory;

    public DocView() {
        //read the html file
        File file = new File(WebServer.BASE_URI + "/docview.html");
        try {
            String docViewHtml = Files.readFromFile(file, "UTF8");
            //divide html into two parts
            String[] splitHtml = docViewHtml.split("<!-- Insert Content -->");
            DOC_VIEW_HTML_1 = splitHtml[0];
            DOC_VIEW_HTML_2 = splitHtml[1];

        } catch (IOException e) {
            logger.error("Cannot read DocView.html from webapp root", e);
        }
    }

    @GET
    @Path("{docId}")
    @Produces(MediaType.TEXT_HTML)
    public Response getDoc(@PathParam("docId") String documentId, @Context HttpHeaders hh) {
        logger.info("Opening [{}]", documentId);
        Document document = null;
        try {
            document = documentFactory.get(documentId);
        } catch (Exception e) {
            return404(e.getMessage());
        }
        if (document == null) {
            logger.debug("Not found in documentMap, fetching from corpus: {}", documentId.toString());
            return return404("Failed to read/deserialize document from Pre Evaluated Folder");
        }
        StringBuffer output = new StringBuffer();
        output
                .append(DOC_VIEW_HTML_1)
                .append(document.getTarget())
                .append(DOC_VIEW_HTML_2);

        return Response.status(Response.Status.ACCEPTED)
                .entity(output.toString())
                .build();
    }

    private Response return404(String message) {
        logger.error("{} : {}", message);
        return Response.status(Response.Status.NOT_FOUND).entity(message).build();
    }
}