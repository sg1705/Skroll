package com.skroll.rest.mail;

import com.google.common.net.UrlEscapers;
import com.google.gson.GsonBuilder;
import com.skroll.document.DocumentHelper;
import com.skroll.search.QueryProcessor;
import com.skroll.services.mail.MailService;
import com.skroll.services.mail.SendGridMailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * API involved with search on landing page.
 */

@Path("/mail")
public class MailAPI {

    private static final Logger logger = LoggerFactory.getLogger(MailAPI.class);

    @Inject
    @SendGridMailService
    private MailService mailService;

    @POST
    @Path("/sendFeedback")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    /**
     * Sends feedback email
     */
    public Response sendFeedback(String json) throws Exception {
        Feedback feedback = new GsonBuilder().create().fromJson(json, Feedback.class);

        mailService.sendMail("no-reply+feedback@skroll.io",
                "skrollioteamsep2015@gmail.com", "Feedback from user", feedback.toString());
        Response r = Response.ok().status(Response.Status.OK).entity("").build();
        return r;
    }

}
