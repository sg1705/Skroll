package com.skroll.rest.document;

import com.skroll.util.UniqueIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by saurabh on 12/30/15.
 */
@Path("/document")
public class DocumentAPI {

    public static final Logger logger = LoggerFactory.getLogger(DocumentAPI.class);


    @GET
    @Path("/importFromUrl")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    //TODO - this is stubbed out method but eventually we want to use it.
    public DocumentProto importFromUrl(@QueryParam("url") String url, DocumentProto documentProto) throws Exception {
        if (documentProto == null) {
            documentProto = new DocumentProto();
        }
        String documentId = UniqueIdGenerator.generateId(url);
        documentProto.setId(documentId);
        return documentProto;
    }


}
