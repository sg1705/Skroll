package com.skroll.parser.linker;

import com.skroll.document.Document;

/**
 * Created by saurabh on 1/19/15.
 */
public class DefinitionLinker {

    /**
     * Algorithm to link definitions in the document.
     *
     * 1. Check if paragraph is a definition
     * 2. If paragraph is a definition then obtain the defined terms
     * 3. For each defined term, find the term in the targetHtml of the document.
     *    Strategies
     *     a) Search text of the defined term and replace with a HTML link
     *     b) Use jQuery to find the text of defined terms; and then link those
     *
     *
     * @param document
     * @return
     */
    public Document linkDefinition(Document document) {


        return null;
    }
}
