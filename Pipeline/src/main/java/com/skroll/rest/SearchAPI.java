package com.skroll.rest;

import com.google.common.net.UrlEscapers;
import com.skroll.document.DocumentHelper;
import com.skroll.search.QueryProcessor;
import org.apache.jasper.tagplugins.jstl.core.Url;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * API involved with search on landing page.
 * <p>
 * Created by saurabh on 9/21/15.
 */

@Path("/search")
public class SearchAPI {

    private static final Logger logger = LoggerFactory.getLogger(DocAPI.class);
    private static final String SEARCH_URL = "http://www.sec.gov/cgi-bin/srch-edgar?&output=atom";
    private static final String FETCH_INDEX_URL = "http://www.sec.gov";


    @GET
    @Path("/searchSec")
    @Produces(MediaType.APPLICATION_ATOM_XML)
    /**
     * Returns results for landing page search
     */
    public Response searchSec(@QueryParam("text") String searchText) throws Exception {

        List<String> queryList = QueryProcessor.process(searchText);

        searchText = UrlEscapers.urlFormParameterEscaper().escape(queryList.get(0));

        String rssUrl = SEARCH_URL + "&text=" + searchText + "&first=" + queryList.get(1) + "&last=" + queryList.get(2);
        logger.info("Search string for {}", rssUrl);
        String rssXml = DocumentHelper.fetchHtml(rssUrl);
        Response r = Response.ok().status(Response.Status.OK).entity(rssXml).build();
        return r;
    }

    @GET
    @Path("/fetchIndex")
    @Produces(MediaType.TEXT_HTML)
    /**
     * Returns the HTML of the page clicked on landing page (from sec.gov)
     */
    public Response fetchIndex(@QueryParam("url") String docURL) throws Exception {

        String rssUrl = FETCH_INDEX_URL + docURL;
        logger.info("Search string for {}", docURL);
        String rssXml = DocumentHelper.fetchHtml(rssUrl);
        Response r = Response.ok().status(Response.Status.OK).entity(rssXml).build();
        return r;
    }

}
