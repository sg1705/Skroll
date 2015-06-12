package com.skroll.document;

import com.google.common.io.Files;
import com.skroll.parser.Parser;
import com.skroll.parser.extractor.ParserException;
import com.skroll.util.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;

/**
 * Created by saurabh on 4/16/15.
 * The objective of document factory is to manage the interface with corpus.
 * How to get and save the document of different types.
 * You will be able to define the document of different type and they can have different mechanism to
 * retrieve those documents. Some are from data stores, some are from URL, and some are from files.
 * It is all abstract from the caller code.
 */
public class DocumentFactory {

    public static final Logger logger = LoggerFactory.getLogger(DocumentFactory.class);
    private static HashMap<String, Document> documents = new HashMap();
    private Configuration configuration;
    public static final String PRE_EVALUATED_FOLDER = "preEvaluatedFolder";
    public static final String BENCHMARK = "benchmark";
    public static final String TEST ="test";

    @Inject
    public DocumentFactory(Configuration configuration ) {
        this.configuration = configuration;
        //PRE_EVALUATED_FOLDER = configuration.get("preEvaluatedFolder", "/tmp/");

    }


    public Document get(String docType, String documentId) {
        Document doc;
        String docFolder = configuration.get(docType, "/tmp/");
        String docPath = docFolder + documentId;
        if (documents.containsKey(docPath)) {
            doc = documents.get(docPath);
        } else {
            //document does not exist in map
            //let's fetch it from the filesystem
            String jsonString;
            try {
                logger.debug("Fetching [{}] from filesystem", docPath);
                jsonString = Files.toString(new File(docPath), Charset.defaultCharset());
            } catch (IOException e) {
                logger.info("[{}] cannot be found", docPath);
                return null;
            }
            try {
                doc = JsonDeserializer.fromJson(jsonString);
            } catch (Exception e) {
                logger.error("[{}] cannot be parsed", documentId);
                return null;
            }
        }
        doc = this.getLatestParsed(docType,doc);
        return doc;
    }

    public void putDocument(String docType, String documentId, Document document) {
        String docFolder = configuration.get(docType, "/tmp/");
        String docPath = docFolder + documentId;
        if (document.getId() == null) {
            document.setId(documentId);
        }
        documents.put(docPath, document);
    }

    private Document getLatestParsed(String docType,Document document) {
        if (DocumentHelper.isLatestParser(document)) {
            //latest doc
            return document;
        }
        //doc is not the latest
        //now need to parse and return the latest
        try {
            document = Parser.reParse(document);
            //save it back since it is reparsed
            this.putDocument(docType, document.getId(), document);
            this.saveDocument(docType, document);
        } catch (ParserException e) {
            logger.error("Cannot reparse document {}", document.getId());
        }

        return document;
    }

    public void saveDocument(String docType,Document document) {
        String docFolder = configuration.get(docType, "/tmp/");
        String docPath = docFolder + document.getId();
        try {
            Files.write(
                    JsonDeserializer.getJson(document),
                    new File(docPath),
                    Charset.defaultCharset());
        } catch (IOException e) {
            logger.error("Error when saving file {}", docPath, e);
        }

    }

}
