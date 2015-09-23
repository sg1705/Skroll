package com.skroll.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.skroll.document.DocumentHelper;
import com.skroll.util.rss.Feed;
import com.skroll.util.rss.FeedMessage;
import com.skroll.util.rss.RssReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by saurabh on 9/21/15.
 */

@Path("/search")
public class SearchAPI {

    private static final Logger logger = LoggerFactory.getLogger(DocAPI.class);
    private static final String SEARCH_URL = "http://www.sec.gov/cgi-bin/srch-edgar?&output=atom";



    @GET
    @Path("/searchSec")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchSec(@QueryParam("text") String docURL, @QueryParam("partialParse") String partialParse) throws Exception {

        String rssUrl = SEARCH_URL + docURL;
        String rssXml = DocumentHelper.fetchHtml(rssUrl);
        SAXBuilder jdomBuilder = new SAXBuilder();
        Document jdomDocument = jdomBuilder.build(xmlSource);
        Element rss = jdomDocument.getRootElement();

        RssReader reader = new RssReader(rssUrl);
        Feed feed = reader.readFeed();
        List<FeedMessage> messages = feed.getMessages();
        Gson gson = new GsonBuilder().create();
        String rssJson = gson.toJson(messages);
        Response r = Response.ok().status(Response.Status.OK).entity(rssJson).build();
        return r;
    }


}
