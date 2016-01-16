package com.skroll.rest;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.net.UrlEscapers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.skroll.document.DocumentHelper;
import com.skroll.document.TermProto;
import com.skroll.search.QueryProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private static List<String> filing = Lists.newArrayList("Financial","Prospectus","Registration","Proxy","News");
    private static List<String> exhibit = Lists.newArrayList("Underwriting Agreement",
                "Plans of Reorganization, Merger or Acquisition", "Articles of Incorporation and bylaw",
                "Indenture",
                "Legal Opinion",
                "Tax Opinion",
                "Voting Agreement",
                "Material Contract",
                "Credit Agreement");
    private static Map<String, String> filingToFormType = ImmutableMap.<String, String>builder()
            .put("Financial","10-K* or%2010-Q* or% 10-D*")
            .put("Prospectus", "424* or%20FWP* or 144* or%20425")
            .put("Registration","S-1* or S-4* or S-8* or 15-15* or 15-12* or D or D/A or S-3* or POS*")
            .put("Proxy","DEF* or PX*")
            .put("News","8-K*")
            .build();
    private static Map<String, String> exhibitToFormType = ImmutableMap.<String, String>builder()
            .put("Underwriting Agreement","\"EX\" NOT (\"EX-99.1\" OR \"EX-99.2\" OR \"EX-99.3\" OR \"EX-31.1\" OR \"EX-31.2\" OR \"EX-32.1\" OR \"EX-32.2\")")
            .put("Plans of Reorganization, Merger or Acquisition", "\"EX\" NOT (\"EX-99.1\" OR \"EX-99.2\" OR \"EX-99.3\" OR \"EX-31.1\" OR \"EX-31.2\" OR \"EX-32.1\" OR \"EX-32.2\")")
            .put("Articles of Incorporation and bylaw", "\"EX\" NOT (\"EX-99.1\" OR \"EX-99.2\" OR \"EX-99.3\" OR \"EX-31.1\" OR \"EX-31.2\" OR \"EX-32.1\" OR \"EX-32.2\")")
            .put("Indenture", "\"EX\" NOT (\"EX-99.1\" OR \"EX-99.2\" OR \"EX-99.3\" OR \"EX-31.1\" OR \"EX-31.2\" OR \"EX-32.1\" OR \"EX-32.2\")")
            .put("Legal Opinion","\"EX\" NOT (\"EX-99.1\" OR \"EX-99.2\" OR \"EX-99.3\" OR \"EX-31.1\" OR \"EX-31.2\" OR \"EX-32.1\" OR \"EX-32.2\")")
            .put("Tax Opinion","\"EX\" NOT (\"EX-99.1\" OR \"EX-99.2\" OR \"EX-99.3\" OR \"EX-31.1\" OR \"EX-31.2\" OR \"EX-32.1\" OR \"EX-32.2\")")
            .put("Material Contract","\"EX\" NOT (\"EX-99.1\" OR \"EX-99.2\" OR \"EX-99.3\" OR \"EX-31.1\" OR \"EX-31.2\" OR \"EX-32.1\" OR \"EX-32.2\")" )
            .put("Credit Agreement","\"EX\" NOT (\"EX-99.1\" OR \"EX-99.2\" OR \"EX-99.3\" OR \"EX-31.1\" OR \"EX-31.2\" OR \"EX-32.1\" OR \"EX-32.2\")")
            .build();

    @GET
    @Path("/searchSec")
    @Produces(MediaType.TEXT_HTML)
    /**
     * Returns results for landing page search
     */
    public Response searchSec(@QueryParam("text") String searchText) throws Exception {

        logger.info("Search for {}", searchText);
        LandingPageQueryProto landingPageQueryProto = null;
        try {
            landingPageQueryProto = new GsonBuilder().create().fromJson(searchText, new TypeToken<LandingPageQueryProto>() {
            }.getType());
            QueryProcessor.process(landingPageQueryProto, landingPageQueryProto.searchText);
        } catch (Exception ex) {
            logger.warn("No chips found in landingPageQueryProto");
            landingPageQueryProto = new LandingPageQueryProto();
            QueryProcessor.process(landingPageQueryProto, searchText);
        }

        logger.info("After running queryProcessor: landingPageQueryProto: {}", landingPageQueryProto);

        List<LandingPageQueryProto.SelectedChip> companySelectedChip = landingPageQueryProto.selectedChips.stream().filter(p -> p.type.equals("company")).collect(Collectors.toList());
        List<LandingPageQueryProto.SelectedChip> categorySelectedChip = landingPageQueryProto.selectedChips.stream().filter(p -> p.type.equals("category")).collect(Collectors.toList());
        List<LandingPageQueryProto.SelectedChip> formtypeSelectedChip = landingPageQueryProto.selectedChips.stream().filter(p -> p.type.equals("formtype")).collect(Collectors.toList());
        List<LandingPageQueryProto.SelectedChip> startyearSelectedChip = landingPageQueryProto.selectedChips.stream().filter(p -> p.type.equals("startyear")).collect(Collectors.toList());
        List<LandingPageQueryProto.SelectedChip> endyearSelectedChip = landingPageQueryProto.selectedChips.stream().filter(p -> p.type.equals("endyear")).collect(Collectors.toList());

        String rssUrl = null;
        // if more than one company
        if (companySelectedChip.isEmpty() || companySelectedChip.size() > 1) {
            rssUrl = "ERROR: Use only one company to search";
            logger.warn(rssUrl);
        } else {
            if (categorySelectedChip.isEmpty()){
                if(formtypeSelectedChip.isEmpty()){
                    rssUrl = edgerBooleanSearch(companySelectedChip.get(0).id,startyearSelectedChip.get(0).field1,endyearSelectedChip.get(0).field1);
                } else {
                    //Only one form type supported and for only boolean search
                    rssUrl = edgerBooleanSearch(companySelectedChip.get(0).id,formtypeSelectedChip.get(0).field1,startyearSelectedChip.get(0).field1,endyearSelectedChip.get(0).field1);
                }
            } else if (categorySelectedChip.size() >1) {
                rssUrl = "ERROR: Use only one category to search";
                logger.warn(rssUrl);
            } else {
                //check whether category is filing type
                if(filing.contains(categorySelectedChip.get(0).field1)) {
                    rssUrl = edgerBooleanSearch(companySelectedChip.get(0).id,filingToFormType.get(categorySelectedChip.get(0).field1),startyearSelectedChip.get(0).field1,endyearSelectedChip.get(0).field1);
                } else if (exhibit.contains(categorySelectedChip.get(0).field1)){
                    rssUrl = edgerFullTextSearch(companySelectedChip.get(0).id, exhibitToFormType.get(categorySelectedChip.get(0).field1), startyearSelectedChip.get(0).field1, endyearSelectedChip.get(0).field1);
                } else {
                    rssUrl = "ERROR: Unknown category";
                    logger.warn(rssUrl);
                }
            }
        }

        logger.info("Search string for {}", rssUrl);
        String rssXml = DocumentHelper.fetchHtml(rssUrl);
        Response r = Response.ok().status(Response.Status.OK).entity(rssXml).build();
        return r;
    }

    private String edgerFullTextSearch(String cik, String formtype, String startYear, String endYear){
        String tenDigitCik = String.format("%010d", Integer.parseInt(cik));
        String fullTestSeachURL = "&queryCik=" + tenDigitCik + "&search_text=" + "\"" + UrlEscapers.urlFormParameterEscaper().escape(formtype) + "\"" + "&fromDate=" +startYear + "&toDate=" + endYear;
        logger.debug("fullTestSeachURL:" + fullTestSeachURL);
        return EDGER_FULL_TEXT_SEARCH_URL + fullTestSeachURL;
    }

    private String edgerBooleanSearch(String cik, String startYear, String endYear){
        return edgerBooleanSearch(cik, null, startYear, endYear);
    }
    private String edgerBooleanSearch(String cik, String formtype, String startYear, String endYear){
        String tenDigitCik = String.format("%010d", Integer.parseInt(cik));
        String booleanSearchURL = null;
        if (formtype==null) {
            booleanSearchURL = "&text=company-cik=" + tenDigitCik + "&first=" + startYear + "&last=" + endYear;
        } else {
            booleanSearchURL = "&text=company-cik=" + tenDigitCik + UrlEscapers.urlFormParameterEscaper().escape(" and form-type=" + formtype) + "&first=" + startYear + "&last=" + endYear;
        }
            logger.debug("booleanSearchURL:" + booleanSearchURL);
        return EDGER_BOOLEAN_SEARCH_URL +   booleanSearchURL;
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
