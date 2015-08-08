package com.skroll.document;

import com.google.common.io.Resources;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by saurabh on 12/29/14.
 */
public class DocumentHelper {
    public static final Logger logger = LoggerFactory
            .getLogger(DocumentHelper.class);
    public static List<Token> getTokens(List<String> words) {
        List<Token> tokens = new ArrayList<Token>();
        for(String word: words) {
            Token token = new Token();
            token.setText(word);
            tokens.add(token);
        }
        return tokens;
    }

    public static List<String> getTokenString(List<Token> tokens) {
        if (tokens==null) return null;
        List<String> words = new ArrayList<String>();
        for(Token token : tokens) {
            words.add(token.getText());
        }
        return words;
    }

    public static CoreMap createEntityFromTokens(List<String> tokens) {
        CoreMap coreMap = new CoreMap();
        List<Token> tokens1 = new ArrayList<Token>();
        for(String word : tokens) {
            Token token = new Token();
            token.setText(word);
            tokens1.add(token);
        }
        coreMap.set(CoreAnnotations.TokenAnnotation.class, tokens1);
        return coreMap;
    }
    public static boolean isObserved(CoreMap coreMap) {
        if (coreMap.containsKey(CoreAnnotations.IsUserObservationAnnotation.class)) {
            return coreMap.get(CoreAnnotations.IsUserObservationAnnotation.class);
        }
        return false;
    }
    public static boolean isObserved(Document document) {
        if(document==null) {
            logger.info("Document is not found. returning null");
            return false;
        }
        for (CoreMap para : document.getParagraphs()) {
            if (DocumentHelper.isObserved(para)) {
                return true;
            }
        }
        return false;
    }

    public static boolean startsWithQuote(CoreMap coreMap) {
        if (coreMap.containsKey(CoreAnnotations.StartsWithQuote.class)) {
            return coreMap.get(CoreAnnotations.StartsWithQuote.class);
        }
        return false;
    }

    public static List<Token> createTokens(List<String> strings) {
        List<Token> tokens = new ArrayList<Token>();
        for(String str: strings) {
            Token token = new Token(str);
            tokens.add(token);
        }
        return tokens;
    }

    public static List<CoreMap> getObservedParagraphs(List<CoreMap> paras) {
        List<CoreMap> observedParagraphs = new ArrayList<>();
        for (CoreMap paragraph : paras) {
            if (isObserved(paragraph)) observedParagraphs.add(paragraph);
        }
        return observedParagraphs;

    }


    public static void clearObservedParagraphs(Document doc) {
        for (CoreMap paragraph : doc.getParagraphs()) {
            if (isObserved(paragraph)) {
                paragraph.set(CoreAnnotations.IsUserObservationAnnotation.class, false);
            }
        }
    }

    public static List<Token> getTokensOfADoc(Document doc){
        List<Token> tokens= new ArrayList<>();
        for (CoreMap paragraph: doc.getParagraphs()){
            for (Token token: paragraph.getTokens()){
                tokens.add(token);
            }
        }
        return tokens;

    }

    public static List<Token> getDocumentTokens(Document document) {
        //get paragraph
        List<CoreMap> paragraphs = document.getParagraphs();
        List<Token> tokens = new ArrayList();
        int count = 0;
        for(CoreMap paragraph: paragraphs) {
            count = count + paragraph.getTokens().size();
            tokens.addAll(paragraph.getTokens());
        }
        return tokens;
    }

    /**
     * Returns true if the document is parsed with the latest version
     * of the parser (see Parser.VERSION)
     * @param doc
     * @return
     */
    public static boolean isLatestParser(Document doc) {
        Integer version = doc.get(CoreAnnotations.ParserVersionAnnotationInteger.class);
        if (version != null) {
            if (version != Parser.VERSION) {
                return false;
            }
            return true;
        }
        return false;
    }

    public static boolean areDocumentsEqual(Document doc1, Document doc2) {
        //compare paragraph sizes
        if (doc1.getParagraphs().size() != doc2.getParagraphs().size()) {
            return false;
        }

        //compare doc id
        if (!(doc1.getId().equals(doc2.getId()))) {
            return false;
        }

        //compare source html
        if (!doc1.getSource().equals(doc2.getSource())) {
            return false;
        }

        //compare each paragraph
        int count = doc1.getParagraphs().size();
        for(int ii = 0; ii < count; ii++) {
            CoreMap map1 = doc1.getParagraphs().get(ii);
            CoreMap map2 = doc2.getParagraphs().get(ii);
            //check if text is the same
            if (!map1.getText().equals(map2.getText())) {
                return false;
            }
            boolean isEqual = map1.equals(map2);
            if (!isEqual) {
                return false;
            }
        }
        //looks good
        return true;
    }

    public static String fetchHtml(String url) throws Exception {
        long startTime = System.currentTimeMillis();
        //fetch the document
        String content = Resources.asCharSource(new URL(url), Charset.forName("UTF-8")).read();
        logger.info("[{}]ms to fetch document", (System.currentTimeMillis() - startTime));
        return content;
    }

}
