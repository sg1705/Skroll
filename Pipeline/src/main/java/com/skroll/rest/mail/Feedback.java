package com.skroll.rest.mail;

/**
 * Created by saurabh on 12/5/15.
 */
public class Feedback {
    public Feedback(String address, String feedbackMessage, String documentId, String url, String browser) {
        this.address = address;
        this.feedbackMessage = feedbackMessage;
        this.documentId = documentId;
        this.url = url;
        this.browser = browser;
    }

    String address;
    String feedbackMessage;
    String documentId;
    String url;
    String browser;



    public String getAddress() {
        return address;
    }

    public String getFeedbackMessage() {
        return feedbackMessage;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getUrl() {
        return url;
    }

    public String getBrowser() {
        return browser;
    }


    public String toString() {
        StringBuilder str = new StringBuilder().append("From:").append(this.address);
        str.append("\n\n").append("Feedback:").append(this.feedbackMessage);
        str.append("\n\n").append("DocumentId:").append(this.documentId);
        str.append("\n\n").append("Url:").append(this.url);
        str.append("\n\n").append("Browser:").append(this.browser);
        return str.toString();
    }

}
