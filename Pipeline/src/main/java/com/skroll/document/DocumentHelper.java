package com.skroll.document;

import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public static List<CoreMap> getObservedParagraphs(Document doc) {
        List<CoreMap> observedParagraphs = new ArrayList<>();
        for (CoreMap paragraph : doc.getParagraphs()) {
            if (isObserved(paragraph)) observedParagraphs.add(paragraph);
        }
        return observedParagraphs;

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
}
