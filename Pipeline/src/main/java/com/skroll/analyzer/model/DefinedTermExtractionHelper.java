package com.skroll.analyzer.model;

import com.skroll.analyzer.model.nb.DataTuple;
import com.skroll.document.CoreMap;
import com.skroll.document.DocumentHelper;
import com.skroll.document.Token;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by wei2learn on 1/24/2015.
 */
public class DefinedTermExtractionHelper {

    static final int PFS_TOKENS_NUMBER_FEATURE_MAX = 10;


    static DataTuple makeNBDataTuple(CoreMap paragraph){
        int category = (DocumentHelper.isDefinition(paragraph)) ? 1:0;
        String []tokens = getNBWords(paragraph);

        RandomVariableType[] features = DefinedTermExtractionModel.PARAGRAPH_FEATURES;
        int [] featureValues = new int[features.length];
        for (int i=0; i<featureValues.length;i++){
            featureValues[i] = getParagraphFeature(paragraph, features[i], tokens);
        }

        return new DataTuple(category,tokens,featureValues);
    }

    // remove quotes and duplicate words
    static String[] getNBWords(CoreMap paragraph){
        List<String> wordList = DocumentHelper.getTokenString(paragraph.getTokens());
        Set<String> wordSet = new HashSet<>();
        int maxLength = PFS_TOKENS_NUMBER_FEATURE_MAX;
        for (int i=0;i<wordList.size() && wordSet.size()<=maxLength; i++){
            if (wordList.get(i).equals("\""))
                continue;
            wordSet.add(wordList.get(i));
        }
        return  wordSet.toArray(new String[wordSet.size()]);
    }

    static int getParagraphFeature(CoreMap paragraph, RandomVariableType feature, String[] words){
        switch (feature){
            case PARAGRAPH_HAS_DEFINITION: return (DocumentHelper.isDefinition(paragraph)) ?1:0;
           // case PARAGRAPH_STARTS_WITH_QUOTE: return (DocumentHelper.startsWithQuote(paragraph)) ?1:0;
            case PARAGRAPH_NUMBER_TOKENS: return Math.min(words.length, PFS_TOKENS_NUMBER_FEATURE_MAX);
        }
        return -1;
    }

    static List<Token> getTokensWithoutQuotes(CoreMap paragraph){
        int length = DefinedTermExtractionModel.HMM_MODEL_LENGTH;
        List<Token> tokens = paragraph.getTokens();
        List<Token> tokensWithoutQuotes = new ArrayList<>();
        for (int i=0; i<tokens.size() && tokensWithoutQuotes.size() <= length; i++){
            tokensWithoutQuotes.add(tokens.get(i));
        }
        return tokensWithoutQuotes;
    }

    int getWordFeature(Token word, RandomVariableType feature){
        switch (feature){
            //case WORD_IS_DEFINED_TERM: return DocumentHelper.isDefinedTerm(word) ?1:0;
        }
        return -1;
    }
}
