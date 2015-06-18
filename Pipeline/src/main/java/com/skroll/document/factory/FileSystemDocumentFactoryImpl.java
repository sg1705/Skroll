package com.skroll.document.factory;

import com.google.common.collect.FluentIterable;
import com.google.common.io.Files;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.document.JsonDeserializer;
import com.skroll.parser.Parser;
import com.skroll.parser.extractor.ParserException;
import com.skroll.util.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by saurabh on 4/16/15.
 */
public abstract class FileSystemDocumentFactoryImpl implements DocumentFactory {

    public static final Logger logger = LoggerFactory.getLogger(FileSystemDocumentFactoryImpl.class);
    private static HashMap<String, Document> documents = new HashMap();
    protected Configuration configuration;
    protected String folder;


    @Override
    public Document get(String documentId) throws Exception {
        Document doc;
        if (documents.containsKey(documentId)) {
            doc = documents.get(documentId);
        } else {
            //document does not exist in map
            //let's fetch it from the filesystem
            String jsonString;
            try {
                logger.debug("Fetching [{}] from filesystem [{}]", documentId, folder);
                jsonString = Files.toString(new File(folder + documentId), Charset.defaultCharset());
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
    public void putDocument(String documentId, Document document) throws Exception {
        if (documentId == null) {
            throw new Exception("Cannot put document with [null] id");
        }
        if (document.getId() == null) {
            document.setId(documentId);
        }
        documents.put(documentId, document);
    }

    private Document getLatestParsed(Document document) throws Exception {
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
    public void saveDocument(Document document) throws Exception {
        try {
            if (document.getId() == null) {
                throw new Exception("Cannot save a document with [null] documentId");
            }
            Files.write(
                    JsonDeserializer.getJson(document),
                    new File(folder + document.getId()),
                    Charset.defaultCharset());
        } catch (IOException e) {
            logger.error("Error when saving file {}", document.getId(), e);
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }

    }
    @Override
    public List<String> getDocLists() throws Exception {
        FluentIterable<File> iterable = Files.fileTreeTraverser().breadthFirstTraversal(new File(this.folder));
        List<String> docLists = new ArrayList<String>();
        for (File f : iterable) {
            if (f.isFile()) {
                docLists.add(f.getName());
            }
        }
        return docLists;
    }

}