package com.skroll.rest.document;

import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Created by saurabh on 12/30/15.
 */
@JsonRootName("document")
public class DocumentProto {

    private String id;
    private String type;
    private String format;
    private String url;

    public DocumentProto() {

    }

    public DocumentProto(String id, String format) {
        this.id = id;
        this.format = format;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }




}
