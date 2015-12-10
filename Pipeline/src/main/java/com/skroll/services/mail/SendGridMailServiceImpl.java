package com.skroll.services.mail;

import com.sendgrid.SendGrid;
import com.skroll.util.Configuration;

import javax.inject.Inject;

/**
 * Created by saurabh on 12/5/15.
 */
public class SendGridMailServiceImpl implements MailService {

    private String apiKey;

    @Inject
    public SendGridMailServiceImpl(Configuration configuration) {
        apiKey = configuration.get("sendGridAPIKey");
    }

    @Override
    public void sendMail(String from, String to, String subject, String body) throws Exception {
        SendGrid sendgrid = new SendGrid(apiKey);
        SendGrid.Email email = new SendGrid.Email();
        email.addTo(to);
        email.setFrom(from);
        email.setSubject(subject);
        email.setText(body);
        sendgrid.send(email);

    }
}
