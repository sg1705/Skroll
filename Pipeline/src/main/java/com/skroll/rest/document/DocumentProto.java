package com.skroll.rest.document;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.skroll.document.DocumentFormat;

/**
 * Created by saurabh on 12/30/15.
 */
@JsonRootName("document")
public class DocumentProto {

    private String id;
    private int typeId;
    private int format;
    private String url;
    private DocumentContentProto contentProto;
    private boolean isPartiallyParsed;

    public DocumentProto() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getFormat() {
        return format;
    }

    public void setFormat(int format) {
        this.format = format;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int type) {
        this.typeId = type;
    }

    public boolean isPartiallyParsed() {
        return isPartiallyParsed;
    }

    public void setPartiallyParsed(boolean isPartiallyParsed) {
        this.isPartiallyParsed = isPartiallyParsed;
    }

    public DocumentContentProto getContentProto() {
        return contentProto;
    }

    public void setContentProto(DocumentContentProto contentProto) {
        this.contentProto = contentProto;
    }
}
