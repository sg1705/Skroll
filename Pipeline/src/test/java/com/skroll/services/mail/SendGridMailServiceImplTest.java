package com.skroll.services.mail;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.skroll.util.SkrollTestGuiceModule;
import org.junit.Before;
import org.junit.Test;

public class SendGridMailServiceImplTest {

    MailService sendGridMailService;

    @Before
    public void setUp() throws Exception {
        try {
            Injector injector = Guice.createInjector(new SkrollTestGuiceModule());
            sendGridMailService = injector.getInstance(Key.get(MailService.class, SendGridMailService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testSendMail() throws Exception {
        sendGridMailService.sendMail("no-reply+test@skroll.io",
                "skrollioteamsep2015@gmail.com", "test subject", "test body");
    }
}