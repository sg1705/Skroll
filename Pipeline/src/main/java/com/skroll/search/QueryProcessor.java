package com.skroll.search;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import com.google.common.primitives.Ints;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Processes query string and converts into a string that is
 * optimial for searching in SEC.GOV
 * <p>
 * Created by saurabh on 9/26/15.
 */
public class QueryProcessor {

    private static final Logger logger = LoggerFactory.getLogger(QueryProcessor.class);
    static HashMap companyNameMap = new HashMap();
    static HashMap<String, String> filingKeyWords = new HashMap();

    /**
     * Load all the tickers at startup time
     */
    static {
        //read files
        URL url = Resources.getResource("cik_ticker.csv");
        try {
            String text = Resources.toString(url, Charsets.UTF_8);
            Splitter.on('\n').split(text).forEach(line -> {
                List<String> tokens = Lists.newArrayList(Splitter.on('|').split(line).iterator());
                if (tokens.size() > 2) {
                    try {
                        String cik = String.format("%010d", Integer.parseInt(tokens.get(0)));
                        companyNameMap.put(tokens.get(1).toLowerCase(), cik);
                    } catch (Exception e) {
                    }
                }
            });

        } catch (Exception e) {
            logger.error("Cannot read CIK ticker file", e);
        }

        //add filing keywords
        filingKeyWords.put("10k", "10-K");
        filingKeyWords.put("10q", "10-Q");
        filingKeyWords.put("8k", "8-K");
    }


    /**
     * Returns processed query string and start and end years
     * at index=0, query string
     * at index=1, start year
     * at index=2, end year
     *
     * @param query input query string
     * @return list of tokens that include start and end years
     */
    public static List<String> process(String query) {
        //tokenize the query
        List<String> tokens = Lists.newArrayList(Splitter.on(' ').split(query));
        List<String> newTokens = new ArrayList<>();
        List<Integer> year = new ArrayList<>();
        //match each token with number
        for (String token : tokens) {
            if (isInteger(token)) {
                int tokenInt = Integer.parseInt(token);
                if ((tokenInt > 1980) && (tokenInt < 2020)) {
                    year.add(tokenInt);
                } else {
                    newTokens.add(token);
                }
            } else if (filingKeyWords.get(token.toLowerCase()) != null) {
                newTokens.add(filingKeyWords.get(token.toLowerCase()));
            } else {
                //get fuzzy match
                String fuzzyMatch = (String) companyNameMap.get(token.toLowerCase());
                if (fuzzyMatch != null) {
                    newTokens.add(fuzzyMatch);
                } else {
                    newTokens.add(token);
                }

            }
        }
        //rejoin all tokens
        String newToken = Joiner.on(' ').join(newTokens);
        String startYear = "1994";
        String endYear = "2015";
        //process year
        if (year.size() == 1) {
            startYear = year.get(0).toString();
            endYear = year.get(0).toString();
        }
        if (year.size() >= 2) {
            int minYear = Ints.min(Ints.toArray(year));
            int maxYear = Ints.max(Ints.toArray(year));
            startYear = Integer.toString(minYear);
            endYear = Integer.toString(maxYear);

        }
        List<String> result = Lists.newArrayList(newToken, startYear, endYear);

        return result;
    }

    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c <= '/' || c >= ':') {
                return false;
            }
        }
        return true;
    }
}
