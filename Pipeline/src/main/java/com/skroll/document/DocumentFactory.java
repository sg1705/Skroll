package com.skroll.document;

import java.util.HashMap;

/**
 * Created by saurabh on 4/16/15.
 */
public class DocumentFactory {

    private static HashMap<String, Document> documents = new HashMap();

    public Document get(String documentId) {
        if (documents.containsKey(documentId))
            return documents.get(documentId);
        return null;
    }

    public void putDocument(String documentId, Document document) {
        documents.put(documentId, document);
    }
}
