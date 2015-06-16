package com.skroll.document.factory;

import com.skroll.document.Document;

/**
 * Operations performed on a document factory.
 * Factories can be of many types - FileSystem (FS) or a Service based
 *
 * Created by saurabh on 6/14/15.
 */
public interface IDocumentFactory {

    /**
     * Returns a document with the given id
     * @param documentId
     * @return
     * @throws Exception
     */
    Document get(String documentId) throws Exception;

    /**
     * Puts the document in cache and saves it
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
}
