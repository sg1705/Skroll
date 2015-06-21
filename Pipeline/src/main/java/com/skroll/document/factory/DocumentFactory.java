package com.skroll.document.factory;

import com.skroll.document.Document;

import java.util.List;

/**
 * Operations performed on a document factory.
 * Factories can be of many types - FileSystem (FS) or a Service based
 *
 * Created by saurabh on 6/14/15.
 */
public interface DocumentFactory {

    /**
     * Returns a document with the given id
     * @param documentId
     * @return
     * @throws Exception
     */
    Document get(String documentId) throws Exception;

    /**
     * Puts the document in cache
     * @param documentId
     * @param document
     * @throws Exception
     */
    void putDocument(String documentId, Document document) throws Exception;

    /**
     * Saves the document in corpus.
     * @param document
     * @throws Exception
     */
    void saveDocument(Document document) throws Exception;

    /**
     * return the list of document names in corpus.
     * @throws Exception
     */
    List<String> getDocumentIds() throws Exception;
}
