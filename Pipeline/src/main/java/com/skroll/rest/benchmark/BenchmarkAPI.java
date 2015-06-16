package com.skroll.rest.benchmark;

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
import com.skroll.rest.RequestBean;
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
public class BenchmarkAPI {

    public static final Logger logger = LoggerFactory
            .getLogger(BenchmarkAPI.class);

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
        return Response.ok().status(Response.Status.OK).entity("benchmark file is stored").build();
    }

}