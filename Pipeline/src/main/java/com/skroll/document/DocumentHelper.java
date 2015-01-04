package com.skroll.document;

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
            token.setToken(word);
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
            token.setToken(word);
            tokens1.add(token);
        }
        entity.setTokens(tokens1);
        return entity;
    }

    public static boolean isDefinition(Entity entity) {
        return entity.hasChildEntity(EntityType.DEFINITIONS);
    }

    public static List<String> getDefinedTerms(Entity entity) {
        return DocumentHelper.getTokenString(entity.getChildEntity(EntityType.DEFINITIONS).getTokens());
    }

    public static void setDefinition(List<String> definitions, Entity paragraph) {
        Entity defintion = createEntityFromTokens(definitions);
        paragraph.addChildEntity(EntityType.DEFINITIONS, defintion);

    }


}
