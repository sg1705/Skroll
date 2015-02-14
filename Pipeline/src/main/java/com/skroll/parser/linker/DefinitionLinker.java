package com.skroll.parser.linker;

import com.google.common.base.Joiner;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.pipeline.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by saurabh on 1/19/15.
 */
public class DefinitionLinker {

    //example of regex
    //term1(ignore1|ignore2|\W|&nbsp)+term2(ignore1|ignore2|\W|&nbsp)+term3
    //Agent's Asset
    // Agent ' s Asset
    private static final String REGEX_TOKEN_SEPARATOR = "(&nbsp;| |\n)*";

    private static final String LINK_TAG = "<a href=\"#%s\">%s</a>";

    public static final Logger logger = LoggerFactory
            .getLogger(DefinitionLinker.class);

    /**
     * Algorithm to link definitions in the document.
     *
     * 1. Check if paragraph is a definition
     * 2. If paragraph is a definition then obtain the defined terms
     * 3. For each defined term, find the term in the targetHtml of the document.
     *    Strategies
     *     a) Search text of the defined term and replace with a HTML link
     *
     * @param document
     * @return
     */
    public Document linkDefinition(Document document) {
        //iterate over each paragraph
        for(CoreMap paragraph : document.getParagraphs()) {
            //check if paragraph is a definition
            if (!paragraph.containsKey(CoreAnnotations.IsDefinitionAnnotation.class)) {
                continue;
            }
            //get all the defined terms
            List<List<Token>> tokensList = paragraph.get(CoreAnnotations.DefinedTermListAnnotation.class);

            if (tokensList.size() == 0) {
                Marker linkMarker = MarkerFactory.getMarker(Constants.LINKER_MARKER);
                logger.error(linkMarker, "Inconsistency in linker. " +
                        "Found a definition paragraph but didn't find defined terms");
                continue;
            }
            for (List<Token> tokens : tokensList) {
                //if tokens.size is 0 then there is a problem
                if (tokens.size() == 0) {
                    Marker linkMarker = MarkerFactory.getMarker(Constants.LINKER_MARKER);
                    logger.error(linkMarker, "Inconsistency in linker. " +
                            "Found a definition paragraph but didn't find defined terms");
                    continue;
                }
                //if only one token and its size is 1 then ignore
                if ((tokens.size() == 1) & (tokens.get(0).getText().length() == 1)) {
                    continue;
                }
                //combine tokens into a regex
                String regex = Joiner.on(REGEX_TOKEN_SEPARATOR).join(tokens);
                //search and replace
                String text = searchAndReplace(document.getTarget(), regex, paragraph.getId());
                document.setTarget(text);
            }
        }
        return document;
    }

    protected String searchAndReplace(String text, String regex, String paragraphId) {
        //compile regex
        Pattern pattern = Pattern.compile(regex);
        //match
        Matcher matcher = pattern.matcher(text);
        //create a buffer for output string
        StringBuffer outputBuffer = new StringBuffer();
        //loop over all the matches
        while (matcher.find()) {
            String replacement = String.format(LINK_TAG, paragraphId, matcher.group());
            //append the replaced string into the buffer
            matcher.appendReplacement(outputBuffer, replacement);
        }
        //add tail
        matcher.appendTail(outputBuffer);
        return outputBuffer.toString();
    }


}
