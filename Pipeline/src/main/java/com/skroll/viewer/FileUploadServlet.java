package com.skroll.viewer;

import com.google.common.io.CharStreams;
import com.skroll.classifier.Classifier;
import com.skroll.classifier.ClassifierFactory;
import com.skroll.classifier.ClassifierId;
import com.skroll.document.Document;
import com.skroll.parser.Parser;
import com.skroll.parser.extractor.ParserException;
import com.skroll.pipeline.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;

/**
 * Created by saurabh on 1/24/15.
 */
//@WebServlet("/upload")
@MultipartConfig
public class FileUploadServlet extends HttpServlet {

    public static final Logger logger = LoggerFactory
            .getLogger(FileUploadServlet.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //Get all parts
        Collection<Part> parts = request.getParts();
        logger.debug("Number of Files upload {}", parts.size());
        // set response types
        response.setContentType("text/html");
        //since we are assuming that only one file will be uplaoded
        Part part = parts.iterator().next();
        if(part.getContentType() != null){
            logger.debug(part.getName());
            logger.debug(part.getContentType());
            InputStreamReader reader = new InputStreamReader(part.getInputStream());
            String content = CharStreams.toString(reader);

            try {
                //parse the document
                Document document = Parser.parseDocumentFromHtml(content);
                //create a classifier
                ClassifierFactory classifierFactory = new ClassifierFactory();
                Classifier classifier = classifierFactory.getClassifier(ClassifierId.TEN_K_DEF_CLASSIFIER);
                //test the document
                document = (Document)classifier.classify("documentid", document);
                //link the document
                response.getOutputStream().write(document.getTarget().getBytes(Constants.DEFAULT_CHARSET));
            } catch (ParserException e) {
                logger.error("Error while parsing the uploaded file", e);
            } catch (Exception e) {
                logger.error("Error while classifying", e);
            }
        } else {
            //TODO figure out error
            logger.error("Uploaded part is null");
        }
        response.getOutputStream().close();
    }

}
