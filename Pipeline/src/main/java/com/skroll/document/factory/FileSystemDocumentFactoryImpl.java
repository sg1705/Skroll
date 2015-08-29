package com.skroll.document.factory;

import com.google.common.cache.CacheLoader;
import com.google.common.collect.FluentIterable;
import com.google.common.io.Files;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.document.JsonDeserializer;
import com.skroll.parser.Parser;
import com.skroll.parser.extractor.ParserException;
import com.skroll.util.Configuration;
import org.eclipse.jetty.util.ConcurrentHashSet;
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
public abstract class FileSystemDocumentFactoryImpl implements DocumentFactory, CacheHandler<Document> {

    public static final Logger logger = LoggerFactory.getLogger(FileSystemDocumentFactoryImpl.class);
    protected Configuration configuration;
    protected String folder;
    protected int cacheSize;
    public Document load(String documentId) throws Exception {
            Document doc = null;
            String jsonString;
            try {
                logger.info("Fetching [{}] from filesystem [{}]", documentId, folder);
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

    @Override
    public void onRemoval(String key, Document value) {
        if(getDocumentCache().getLoadingCache().size()>=cacheSize){
            if(getSaveLaterDocumentId().contains(key)) {
                try {
                    saveDocument(value);
                } catch (Exception e) {
                    logger.error("Error in saving Document", e);
                }
                getSaveLaterDocumentId().remove(key);
            }
        }
    }

    /**
     * abstract method to getDocumentsCache
     * @return
     */
    protected abstract  CacheService<Document>  getDocumentCache();

    /**
     * abstract method to getDocumentsCache
     * @return
     */
    protected abstract ConcurrentHashSet<String> getSaveLaterDocumentId();


    /**
     * When the document evict from the cache, the removelListener will save that document in file
     */


    @Override
    public boolean isDocumentExist(String documentId) throws Exception {
        if (getDocumentCache().getLoadingCache().asMap().keySet().contains(documentId)) {
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
        Document doc = null;
        try {
            doc = getDocumentCache().getLoadingCache().get(documentId);
        } catch (CacheLoader.InvalidCacheLoadException e){
            logger.error("doc is not Found in cache: {}", e.getMessage());
            return null;
        }
        logger.info("document map size: {}", getDocumentCache().getLoadingCache().size());
        return doc;
    }

    @Override
    public void putDocument(Document document) throws Exception {
        if (document.getId() == null) {
            throw new Exception("Cannot put document with [null] id");
        }
        logger.info("putDocument: {}", document.getId());
        getSaveLaterDocumentId().add(document.getId());
        getDocumentCache().getLoadingCache().put(document.getId(),document);
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
            this.putDocument(document);
            this.saveDocument(document);
        } catch (ParserException e) {
            logger.error("Cannot reparse document {}", document.getId());
        }

        return document;
    }

    @Override
    public void saveDocument(Document document) throws Exception {
        // call explicitly to cleanup the guava cache
        getDocumentCache().getLoadingCache().cleanUp();
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
        List<String> docLists = new ArrayList<String>();
        docLists.addAll(getDocumentCache().getLoadingCache().asMap().keySet());
        FluentIterable<File> iterable = Files.fileTreeTraverser().breadthFirstTraversal(new File(this.folder));
        for (File f : iterable) {
            if (f.isFile()) {
                docLists.add(f.getName());
            }
        }
        return docLists;
    }


}