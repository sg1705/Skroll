package com.skroll.services.mail;

/**
 * Created by saurabh on 12/5/15.
 */
public interface MailService {
    void sendMail(String from, String to, String subject, String body) throws Exception;
}
