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

    public static Entity createEntityFromTokens(List<String> tokens) {
        Entity entity = new Entity();
        List<Token> tokens1 = new ArrayList<Token>();
        for(String word : tokens) {
            Token token = new Token();
            token.setText(word);
            tokens1.add(token);
        }
        entity.set(CoreAnnotations.TokenAnnotation.class, tokens1);
        return entity;
    }

    public static boolean isDefinition(Entity entity) {
        if (entity.containsKey(CoreAnnotations.IsDefinitionAnnotation.class)) {
            return entity.get(CoreAnnotations.IsDefinitionAnnotation.class);
        }
        return false;
    }

    public static List<String> getDefinedTerms(Entity entity) {
        return DocumentHelper.getTokenString(
                entity.get(CoreAnnotations.DefinedTermsAnnotation.class));
    }

    public static void setDefinition(List<String> definitions, Entity paragraph) {
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
