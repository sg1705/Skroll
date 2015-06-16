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
 */
public class DocumentFactory implements com.skroll.document.factory.IDocumentFactory {

    public static final Logger logger = LoggerFactory.getLogger(DocumentFactory.class);
    private static HashMap<String, Document> documents = new HashMap();
    private Configuration configuration;
    private static String PRE_EVALUATED_FOLDER;
    public static enum DocType {
        DEFAULT, BENCHMARK
    };

    @Inject
    public DocumentFactory(Configuration configuration) {
        this.configuration = configuration;
        PRE_EVALUATED_FOLDER = configuration.get("preEvaluatedFolder", "/tmp/");
    }


    @Override
    public Document get(String documentId) {
        Document doc;
        if (documents.containsKey(documentId)) {
            doc = documents.get(documentId);
        } else {
            //document does not exist in map
            //let's fetch it from the filesystem
            String jsonString;
            try {
                logger.debug("Fetching [{}] from filesystem [{}]", documentId, PRE_EVALUATED_FOLDER);
                jsonString = Files.toString(new File(PRE_EVALUATED_FOLDER + documentId), Charset.defaultCharset());
            } catch (IOException e) {
                logger.info("[{}] cannot be found", documentId);
                return null;
            }
            try {
                doc = JsonDeserializer.fromJson(jsonString);
            } catch (Exception e) {
                logger.error("[{}] cannot be parsed", documentId);
                return null;
            }
        }
        doc = this.getLatestParsed(doc);
        return doc;
    }

    @Override
    public void putDocument(String documentId, Document document) {
        if (document.getId() == null) {
            document.setId(documentId);
        }
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
            this.saveDocument(document);
        } catch (ParserException e) {
            logger.error("Cannot reparse document {}", document.getId());
        }

        return document;
    }

    @Override
    public void saveDocument(Document document) {
        saveDocument(DocType.DEFAULT, document);
    }

    public void saveDocument(DocType docType, Document document) {
        String folder = null;
        if (docType == DocType.DEFAULT) {
            folder = PRE_EVALUATED_FOLDER;
        } else if (docType == DocType.BENCHMARK) {
            folder = configuration.get("benchmarkFolder", "/tmp/");
        } else {
            logger.error("Error Invalid DocType {}", docType);
            return;
        }
        try {
            Files.write(
                    JsonDeserializer.getJson(document),
                    new File(folder + document.getId()),
                    Charset.defaultCharset());
        } catch (IOException e) {
            logger.error("Error when saving file {}", document.getId(), e);
        }

    }

}