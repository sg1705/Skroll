package com.skroll.rest.document;

/**
 * Created by saurabh on 1/26/16.
 */
public class ParaProto {
    String documentId;
    String paraId;

    public ParaProto() {
    }

    public ParaProto(String documentId, String paraId) {
        this.documentId = documentId;
        this.paraId = paraId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public void setParaId(String paraId) {
        this.paraId = paraId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getParaId() {
        return paraId;
    }
}
