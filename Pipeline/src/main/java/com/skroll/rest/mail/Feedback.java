package com.skroll.rest.mail;

/**
 * Created by saurabh on 12/5/15.
 */
public class Feedback {
    String address;
    String feedbackMessage;


    public Feedback(String address, String feedbackMessage) {
        this.address = address;
        this.feedbackMessage = feedbackMessage;
    }

    public String getAddress() {
        return address;
    }

    public String getFeedbackMessage() {
        return feedbackMessage;
    }

    public String toString() {
        StringBuilder str = new StringBuilder().append("From:").append(this.address);
        str.append("\n").append("Feedback:").append(this.feedbackMessage);
        return str.toString();
    }

}
