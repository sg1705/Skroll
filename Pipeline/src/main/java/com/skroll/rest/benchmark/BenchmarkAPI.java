package com.skroll.rest.benchmark;

import com.google.gson.GsonBuilder;
import com.skroll.document.Document;
import com.skroll.trainer.BenchmarkModel;
import com.skroll.trainer.QC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/doc")
public class BenchmarkAPI {

    public static final Logger logger = LoggerFactory.getLogger(BenchmarkAPI.class);

    // documentMap is defined as concurrent hashmap
    // as we would like to share this hashmap between multiple requests from multiple clients
    // It provides the construct to synchronize only block of map not the whole hashmap.

    private Response logErrorResponse(String message, Exception e) {
        logger.error("{} : {}", message, e);
        if (e != null) e.printStackTrace();
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();

    }

    private Response logErrorResponse(String message) {
        return logErrorResponse(message, null);

    }

    @GET
    @Path("/saveBenchmarkFile")
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveBenchmarkFile(@Context HttpHeaders hh, @BeanParam BenchmarkRequestBean request) {

        String documentId = request.getDocumentId();
        Document doc = request.getDocument();
        if (doc == null) {
            return logErrorResponse("document cannot be found for document id: " + documentId);
        }
        try {
            request.getDocumentFactory().saveDocument(doc);
        } catch (Exception e) {
            logErrorResponse("Failed to store the benchmark file: {}", e);
        }
        logger.debug("benchmark file {} is stored..", documentId);
        //return Response.ok().status(Response.Status.OK).entity("{'ok': 'benchmark file is stored'}").build();
        return Response.ok().status(Response.Status.OK).entity("").build();
    }

    @GET
    @Path("/getBenchmarkScore")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBenchmarkScore(@BeanParam BenchmarkRequestBean request) {
        BenchmarkModel benchmarkModel = new BenchmarkModel(request.getDocumentFactory(), request.getClassifiers());
        QC qc = null;
        try {
            qc = benchmarkModel.runQCOnBenchmarkFolder();
        } catch (Exception e) {
            e.printStackTrace();
            return logErrorResponse("getBenchmarkScore failed: +" + e);
        }
        String qcJson = new GsonBuilder().create().toJson(qc);
        return Response.ok().status(Response.Status.OK).entity(qcJson).build();
    }

}