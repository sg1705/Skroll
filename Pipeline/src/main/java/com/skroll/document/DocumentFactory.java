package com.skroll.document;

import com.google.common.io.Files;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.parser.Parser;
import com.skroll.parser.extractor.ParserException;
import com.skroll.util.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;

/**
 * Created by saurabh on 4/16/15.
 */
public class DocumentFactory {

    public static final Logger logger = LoggerFactory.getLogger(DocumentFactory.class);
    private static HashMap<String, Document> documents = new HashMap();
    //private Configuration configuration;
    private static String PRE_EVALUATED_FOLDER;

    @Inject
    public DocumentFactory(Configuration configuration ) {
        //this.configuration = configuration;
        PRE_EVALUATED_FOLDER = configuration.get("preEvaluatedFolder", "/tmp/");
    }


    public Document get(String documentId) {
        Document doc;
        if (documents.containsKey(documentId)) {
            doc = documents.get(documentId);
        } else {
            //document does not exist in map
            //let's fetch it from the filesystem
            try {
                logger.debug("Fetching [{}] from filesystem", documentId);
                String jsonString = Files.toString(new File(PRE_EVALUATED_FOLDER + documentId), Charset.defaultCharset());
                doc = JsonDeserializer.fromJson(jsonString);
            } catch (Exception e) {
                logger.error("Document [{}] cannot be parsed", documentId);
                return null;
            }
        }
        doc = this.getLatestParsed(doc);
        return doc;
    }

    public void putDocument(String documentId, Document document) {
        documents.put(documentId, document);
    }

    private Document getLatestParsed(Document document) {
        if (DocumentHelper.isLatestParser(document)) {
            //latest doc
            return document;
        }
        //doc is not the latest
        //now need to parse and return the latest
        try {
            document = Parser.reParse(document);
            //save it back since it is reparsed
            this.putDocument(document.getId(), document);
            saveFile(document);
        } catch (ParserException e) {
            logger.error("Cannot reparse document {}", document.getId());
        }

        return document;
    }

    private void saveFile(Document document) {
        try {
            Files.write(
                    JsonDeserializer.getJson(document),
                    new File(PRE_EVALUATED_FOLDER + document.getId()),
                    Charset.defaultCharset());
        } catch (IOException e) {
            logger.error("Error when saving file {}", document.getId(), e);
        }

    }

}
