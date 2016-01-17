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

    private static final Logger logger = LoggerFactory.getLogger(SearchAPI.class);
    private static final String EDGER_BOOLEAN_SEARCH_URL = "http://www.sec.gov/cgi-bin/srch-edgar?&output=atom";
    private static final String EDGER_FULL_TEXT_SEARCH_URL = "https://searchwww.sec.gov/EDGARFSClient/jsp/EDGAR_MainAccess.jsp?" +
            "&sort=Date&formType=1&isAdv=true&numResults=100";
    private static final String FETCH_INDEX_URL = "http://www.sec.gov/";

    @GET
    @Path("/searchSec")
    @Produces(MediaType.TEXT_HTML)
    /**
     * Returns results for landing page search
     */
    public Response searchSec(@QueryParam("text") String searchText) throws Exception {

        List<String> queryList = QueryProcessor.process(searchText);

        searchText = UrlEscapers.urlFormParameterEscaper().escape(queryList.get(0));

        String[] searchTextArray = queryList.get(0).split(" ");
        String rssUrl = null;

        // Edger Full Text Search is used to search exhibit only. Edger boolean search is catch all and default search.
        if (searchTextArray!=null && searchTextArray.length > 1 && searchTextArray[1].toLowerCase().startsWith("ex-")) {
            // Edger Full Text Search is only used for exhibit.
            rssUrl = EDGER_FULL_TEXT_SEARCH_URL + "&queryCik=" + searchTextArray[0] + "&search_text=" + "\"" + searchTextArray[1] + "\"" + "&fromDate=" + queryList.get(1) + "&toDate=" + queryList.get(2);
        } else {
            // Edger boolean search is default search
            rssUrl = EDGER_BOOLEAN_SEARCH_URL + "&text=" + searchText + "&first=" + queryList.get(1) + "&last=" + queryList.get(2);
        }
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
