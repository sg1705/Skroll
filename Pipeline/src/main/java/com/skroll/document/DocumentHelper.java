package com.skroll.document;

import com.skroll.document.annotation.CoreAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by saurabh on 12/29/14.
 */
public class DocumentHelper {

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

    public static boolean isDefinition(CoreMap coreMap) {
        if (coreMap.containsKey(CoreAnnotations.IsDefinitionAnnotation.class)) {
            return coreMap.get(CoreAnnotations.IsDefinitionAnnotation.class);
        }
        return false;
    }

    public static boolean startsWithQuote(CoreMap coreMap) {
        if (coreMap.containsKey(CoreAnnotations.StartsWithQuote.class)) {
            return coreMap.get(CoreAnnotations.StartsWithQuote.class);
        }
        return false;
    }

    public static List<String> getDefinedTerms(CoreMap coreMap) {
        List<Token> tokens = coreMap.get(CoreAnnotations.DefinedTermsAnnotation.class);
        List<String> strings = new ArrayList<>();
        if (tokens!=null) strings =DocumentHelper.getTokenString(tokens);
        return strings;
    }

    public static void setDefinition(List<String> definitions, CoreMap paragraph) {
        List<Token> tokens1 = DocumentHelper.createTokens(definitions);
        paragraph.set(CoreAnnotations.DefinedTermsAnnotation.class, tokens1);
        if (tokens1.size() > 0) {
            paragraph.set(CoreAnnotations.IsDefinitionAnnotation.class, true);
        }

    }

    public static void setDefinedTermTokensInParagraph(List<Token> definitions, CoreMap paragraph) {
        paragraph.set(CoreAnnotations.DefinedTermsAnnotation.class, definitions);
        if (definitions.size() > 0) {
            paragraph.set(CoreAnnotations.IsDefinitionAnnotation.class, true);
        }
    }

    public static List<Token> getDefinedTermTokensInParagraph(CoreMap paragraph) {
        return paragraph.get(CoreAnnotations.DefinedTermsAnnotation.class);
    }

    public static List<Token> createTokens(List<String> strings) {
        List<Token> tokens = new ArrayList<Token>();
        for(String str: strings) {
            Token token = new Token(str);
            tokens.add(token);
        }
        return tokens;
    }

    public static List<CoreMap> getDefinitionParagraphs(Document doc){
        List<CoreMap> definitionParagraphs= new ArrayList<>();
        for (CoreMap paragraph: doc.getParagraphs()){
            if (isDefinition(paragraph)) definitionParagraphs.add(paragraph);
        }
        return definitionParagraphs;
    }

}
