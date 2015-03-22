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

    public static boolean startsWithQuote(CoreMap coreMap) {
        if (coreMap.containsKey(CoreAnnotations.StartsWithQuote.class)) {
            return coreMap.get(CoreAnnotations.StartsWithQuote.class);
        }
        return false;
    }


    //todo: this method should be removed. but it's used at some places.

    public static List<String> getDefinedTerms(CoreMap coreMap) {
        List<List<Token>> definiitonList = coreMap.get(CoreAnnotations.DefinedTermListAnnotation.class);
        List<String> strings = new ArrayList<>();
        if (definiitonList==null || definiitonList.size()==0) return strings;
        strings = DocumentHelper.getTokenString(definiitonList.get(0));
        return strings;
    }

    public static List<List<String>> getDefinedTermLists(CoreMap coreMap) {
        List<List<Token>> definitionList = coreMap.get(CoreAnnotations.DefinedTermListAnnotation.class);
        List<List<String>> strings = new ArrayList<>();
        if (definitionList==null) return strings;
        for (List<Token> list: definitionList){
            strings.add(DocumentHelper.getTokenString(list));
        }
        return strings;
    }

    public static List<String> getTOCLists(CoreMap coreMap) {
        List<Token> tocList = coreMap.get(CoreAnnotations.TOCListAnnotation.class);
        if (tocList==null) return new ArrayList<>();
        return DocumentHelper.getTokenString(tocList);

    }


    //todo: this method should be deleted. but it's used at some places.

    public static void setDefinedTermTokensInParagraph(List<Token> definitions, CoreMap paragraph) {
        List<List<Token>> list = new ArrayList<>();
        list.add(definitions);
        paragraph.set(CoreAnnotations.DefinedTermListAnnotation.class, list);
        if (definitions.size() > 0) {
            paragraph.set(CoreAnnotations.IsDefinitionAnnotation.class, true);
        }
    }

    public static void setDefinedTermTokenListInParagraph(List<List<Token>> definitions, CoreMap paragraph) {
        paragraph.set(CoreAnnotations.DefinedTermListAnnotation.class, definitions);
        if (definitions.size() > 0) {
            paragraph.set(CoreAnnotations.IsDefinitionAnnotation.class, true);
        }
    }

    public static void addDefinedTermTokensInParagraph(List<Token> definitions, CoreMap paragraph) {
        List<List<Token>>  definitionList = paragraph.get(CoreAnnotations.DefinedTermListAnnotation.class);
        if (definitionList == null) {
            definitionList = new ArrayList<>();
            paragraph.set(CoreAnnotations.DefinedTermListAnnotation.class, definitionList);
        }
        definitionList.add(definitions);
        if (definitions.size() > 0) {
            paragraph.set(CoreAnnotations.IsDefinitionAnnotation.class, true);
        }
    }

    public static void addTOCsInParagraph(List<Token> newToc, CoreMap paragraph) {
        List<Token> toc = paragraph.get(CoreAnnotations.TOCListAnnotation.class);
        if (toc == null) {
            toc = new ArrayList<>();
            paragraph.set(CoreAnnotations.TOCListAnnotation.class, toc);
        }
        toc.addAll(newToc);
        if (toc.size() > 0) {
            paragraph.set(CoreAnnotations.IsTOCAnnotation.class, true);
        }
    }


    public static List<List<Token>> getDefinedTermTokensInParagraph(CoreMap paragraph) {
        return paragraph.get(CoreAnnotations.DefinedTermListAnnotation.class);
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
