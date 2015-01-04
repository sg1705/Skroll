package com.skroll.document;

import com.skroll.document.annotation.CoreAnnotations;

import java.util.ArrayList;
import java.util.List;

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

    public static List<String> getDefinedTerms(CoreMap coreMap) {
        return DocumentHelper.getTokenString(
                coreMap.get(CoreAnnotations.DefinedTermsAnnotation.class));
    }

    public static void setDefinition(List<String> definitions, CoreMap paragraph) {
        List<Token> tokens1 = DocumentHelper.createTokens(definitions);
        paragraph.set(CoreAnnotations.DefinedTermsAnnotation.class, tokens1);

    }

    public static List<Token> createTokens(List<String> strings) {
        List<Token> tokens = new ArrayList<Token>();
        for(String str: strings) {
            Token token = new Token(str);
            tokens.add(token);
        }
        return tokens;
    }

}
