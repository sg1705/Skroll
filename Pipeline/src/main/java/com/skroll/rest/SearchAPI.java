package com.skroll.rest;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.net.UrlEscapers;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.skroll.document.DocumentHelper;
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
    private static final String EDGAR_BOOLEAN_SEARCH_URL = "http://www.sec.gov/cgi-bin/srch-edgar?&output=atom&count=100";
    private static final String EDGAR_FULL_TEXT_SEARCH_URL = "https://searchwww.sec.gov/EDGARFSClient/jsp/EDGAR_MainAccess.jsp?" +
            "&sort=Date&formType=1&isAdv=true&numResults=100";
    private static final String EDGAR_COMPANY_SEARCH_URL = "http://www.sec.gov/cgi-bin/browse-edgar?action=getcompany&type=&dateb=&owner=exclude&start=0&count=100&output=atom&CIK=";

    private static final String FETCH_INDEX_URL = "http://www.sec.gov/";
    private static final List<String> SEC_FILING_CATAGORIES = Lists.newArrayList("Financials","Prospectuses","Proxies","News");
    private static final List<String> SEC_EXHIBIT_CATAGORIES = Lists.newArrayList("Underwriting Agreements",
                "Plans of Reorganization, Merger or Acquisitions", "Articles of Incorporation and Bylaws",
                "Indentures",
                "Legal Opinions",
                "Tax Opinions",
                "Voting Agreements",
                "Material Contracts",
                "Credit Agreements");
    private static final Map<String, String> FILING_TO_FORM_TYPE = ImmutableMap.<String, String>builder()
            .put("Financials", "10-K* or form-type=10-Q* or form-type=10-D*")
            .put("Prospectuses", "424* or form-type=FWP* or form-type=144* or form-type=425 or form-type=S-1* or form-type=S-4* or form-type=S-8* or form-type=15-15* or form-type=15-12* or form-type=D or form-type=D/A or form-type=S-3* or form-type=POS*")
            .put("Proxies","DEF* or form-type=PX*")
            .put("News","8-K*")
            .build();
    private static final String EXHIBIT_TO_FORM_TYPE = "\"EX\" NOT (\"EX-99.1\" OR \"EX-99.2\" OR \"EX-99.3\" OR \"EX-31.1\" OR \"EX-31.2\" OR \"EX-32.1\" OR \"EX-32.2\")";

    private static final String DEFAULT_FORM_TYPE_BOOLEAN_SEARCH = " and not (form-type=4 or form-type=3 or form-type=5 or form-type=3/a or form-type=4/a or form-type=5/a or form-type=\"SC 13D\" or form-type=\"SC 13D/a\" or form-type=\"SC 13G\" or form-type=\"SC 13G/a\" )";


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

        String rssUrl = "";
        // if more than one company
        if (companySelectedChip.isEmpty()) {
            logger.warn("ERROR: Use atleast one company to search");
        } else {
            String companyCIKs = companySelectedChip
                    .stream()
                    .map(p -> p.id)
                    .collect(Collectors.joining(" or company-cik= "));
            if (categorySelectedChip.isEmpty()){
                if(formtypeSelectedChip.isEmpty()){
                    rssUrl = edgarBooleanSearch(companyCIKs, startyearSelectedChip.get(0).field1, endyearSelectedChip.get(0).field1);
                } else {
                    //Only one form type supported and for only boolean search
                    String formtypes = formtypeSelectedChip
                            .stream()
                            .map(p -> p.field1)
                            .collect(Collectors.joining(" or form-type= "));

                    rssUrl = edgarBooleanSearch(companyCIKs, formtypes, startyearSelectedChip.get(0).field1, endyearSelectedChip.get(0).field1);
                }
            } else if (categorySelectedChip.size() > 1) {
                logger.warn("ERROR: Use only one category to search");
            } else {
                //check whether category is SEC_FILING_CATAGORIES type
                if(SEC_FILING_CATAGORIES.contains(categorySelectedChip.get(0).field1)) {
                    rssUrl = edgarBooleanSearch(companyCIKs, FILING_TO_FORM_TYPE.get(categorySelectedChip.get(0).field1), startyearSelectedChip.get(0).field1, endyearSelectedChip.get(0).field1);
                } else if (SEC_EXHIBIT_CATAGORIES.contains(categorySelectedChip.get(0).field1)){
                    rssUrl = edgarFullTextSearch(companySelectedChip.get(0).id, EXHIBIT_TO_FORM_TYPE, startyearSelectedChip.get(0).field1, endyearSelectedChip.get(0).field1);
                } else {
                    logger.warn("ERROR: Unknown category");
                }
            }
        }

        logger.info("Search string for {}", rssUrl);
        String rssXml = "";
        if (!rssUrl.isEmpty()) {
            rssXml = DocumentHelper.fetchHtml(rssUrl);
        }
        Response r = Response.ok().status(Response.Status.OK).entity(rssXml).build();
        return r;
    }

    /**
     * Form and return a URL for Edger full text search.
     * @param cik
     * @param formtype
     * @param startYear
     * @param endYear
     * @return
     */
    private String edgarFullTextSearch(String cik, String formtype, String startYear, String endYear){
        String tenDigitCik = String.format("%010d", Integer.parseInt(cik));
        String fullTestSeachURL = "&queryCik=" + tenDigitCik + "&search_text=" + UrlEscapers.urlFormParameterEscaper().escape(formtype) + "&fromDate=01/01/" +startYear + "&toDate=12/31/" + endYear;
        logger.debug("fullTestSeachURL:" + fullTestSeachURL);
        return EDGAR_FULL_TEXT_SEARCH_URL + fullTestSeachURL;
    }

    /**
     * Form and return a URL for boolean Search using the input parameters
     * @param cik
     * @param startYear
     * @param endYear
     * @return
     */
    private String edgarBooleanSearch(String cik, String startYear, String endYear){
        return edgarBooleanSearch(cik, null, startYear, endYear);
    }

    /**
     * Form and return a URL for boolean Search using the input parameters
     * @param cik
     * @param formtype
     * @param startYear
     * @param endYear
     * @return
     */
    private String edgarBooleanSearch(String cik, String formtype, String startYear, String endYear){
        String booleanSearchURL = null;
        if (formtype==null) {
            booleanSearchURL = "&text=company-cik=" +  UrlEscapers.urlFormParameterEscaper().escape(cik + DEFAULT_FORM_TYPE_BOOLEAN_SEARCH) + "&first=" + startYear + "&last=" + endYear;
        } else {
            booleanSearchURL = "&text=company-cik=" + UrlEscapers.urlFormParameterEscaper().escape(cik + " and ( form-type=" + formtype +")" ) + "&first=" + startYear + "&last=" + endYear;
        }
            logger.debug("booleanSearchURL:" + booleanSearchURL);
        return EDGAR_BOOLEAN_SEARCH_URL +   booleanSearchURL;
    }

    /**
     * Form and edgarCompanySearch.
     * @param cik
     * @return
     */
    private String edgarCompanySearch(String cik){
        String tenDigitCik = String.format("%010d", Integer.parseInt(cik));
        return EDGAR_COMPANY_SEARCH_URL + tenDigitCik;
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
