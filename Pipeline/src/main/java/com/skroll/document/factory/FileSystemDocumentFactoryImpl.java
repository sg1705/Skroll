package com.skroll.document.factory;

import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
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
import java.util.List;

/**
 * Created by saurabh on 4/16/15.
 */
public abstract class FileSystemDocumentFactoryImpl implements DocumentFactory {

    public static final Logger logger = LoggerFactory.getLogger(FileSystemDocumentFactoryImpl.class);
    protected Configuration configuration;
    protected String folder;
    CacheLoader<String, Document> loader = new CacheLoader<String, Document>() {
        @Override
        public Document load(String documentId) throws Exception {
            Document doc = null;
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
                doc.setId(documentId);
            } catch (Exception e) {
                logger.error("[{}] cannot be parsed", documentId);
                return null;
            }
            doc = getLatestParsed(doc);
            return doc;
        }
    };

    LoadingCache<String, Document> documents = null;

    @Override
    public boolean isDocumentExist(String documentId) throws Exception {
        if (documents.get(documentId)!=null) {
            return true;
        } else {
            File file = new File(folder + documentId);
            if(file.exists()){
                return true;
            }
        }
        return false;
    }

    @Override
    public Document get(String documentId) throws Exception {
        Document doc = documents.get(documentId);
        logger.debug("document map size: {}",documents.size());
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

    protected Document getLatestParsed(Document document) throws Exception {
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
            //check to see if folder exists
            File file = new File(folder + document.getId());
            Files.createParentDirs(file);
            Files.write(
                    JsonDeserializer.getJson(document),
                    new File(folder + document.getId()),
                    Charset.defaultCharset());
            logger.info("[{}] saved in [{}]", document.getId(), this.folder);
        } catch (IOException e) {
            logger.error("Error when saving file {}", document.getId(), e);
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }

    }
    @Override
    public List<String> getDocumentIds() throws Exception {
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