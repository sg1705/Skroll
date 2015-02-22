package com.skroll.analyzer.model;

import com.skroll.analyzer.model.nb.DataTuple;
import com.skroll.document.CoreMap;
import com.skroll.document.DocumentHelper;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.util.WordHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by wei2learn on 1/24/2015.
 */
public class DefinedTermExtractionHelper {


    //PARAGRAPH_NUMBER_TOKENS can be from 0 to PFS_TOKENS_NUMBER_FEATURE_MAX,
    //so number of possibilities is one more than PFS_TOKENS_NUMBER_FEATURE_MAX
    public static final int PFS_TOKENS_NUMBER_FEATURE_MAX =
            RandomVariableType.PARAGRAPH_NUMBER_TOKENS.getFeatureSize()-1;

    // create a copy of paragraph and annotate it further for training
    static CoreMap makeTrainingParagraph(CoreMap paragraph){
        CoreMap trainingParagraph = new CoreMap();
        List<Token> tokens = paragraph.getTokens();
        List<Token> newTokens = new ArrayList<>();

        Set<String> wordSet = new HashSet<>();
        if (tokens.size()>0 && WordHelper.isQuote(tokens.get(0).getText()))
            trainingParagraph.set(CoreAnnotations.StartsWithQuote.class, true);

        boolean inQuotes=false; // flag for annotating if a token is in quotes or not
        int i=0;
        for (Token token: tokens){
            if (WordHelper.isQuote(token.getText())) {
                inQuotes = !inQuotes;
                continue;
            }
            if (inQuotes){
                token.set(CoreAnnotations.InQuotesAnnotation.class, true);
            }
            token.set(CoreAnnotations.Index.class, i++);
            wordSet.add(token.getText());
            newTokens.add(token);
        }

        trainingParagraph.set(CoreAnnotations.WordSetForTrainingAnnotation.class, wordSet);
        trainingParagraph.set(CoreAnnotations.TokenAnnotation.class, newTokens);

        // put defined terms from paragraph in trainingParagraph
        // todo: may remove this later if trainer creates a training paragraph and put defined terms there directly
        List<List<Token>> definedTokens = paragraph.get(CoreAnnotations.DefinedTermListAnnotation.class);
        if (definedTokens != null && definedTokens.size()>0) {
            trainingParagraph.set(CoreAnnotations.IsDefinitionAnnotation.class, true);
        }
        trainingParagraph.set(CoreAnnotations.DefinedTermListAnnotation.class,
                paragraph.get(CoreAnnotations.DefinedTermListAnnotation.class));

        return trainingParagraph;
    }
//    static DataTuple makeNBDataTuple(CoreMap paragraph){
//        int category = (DocumentHelper.isDefinition(paragraph)) ? 1:0;
//        String []tokens = getNBWords(paragraph);
//
//        RandomVariableType[] features = DefinedTermExtractionModel.PARAGRAPH_FEATURES;
//        int [] featureValues = new int[features.length];
//        for (int i=0; i<featureValues.length;i++){
//            featureValues[i] = getParagraphFeature(paragraph, features[i]);
//        }
//
//        return new DataTuple(category,tokens,featureValues);
//    }

    static DataTuple makeNBDataTuple(CoreMap paragraph, RandomVariableType[] features){
        int category = (DocumentHelper.isDefinition(paragraph)) ? 1:0;
        String []tokens = getNBWords(paragraph);

        //RandomVariableType[] features = DefinedTermExtractionModel.PARAGRAPH_FEATURES;
        int [] featureValues = new int[features.length];
        for (int i=0; i<featureValues.length;i++){
            featureValues[i] = getParagraphFeature(paragraph, features[i]);
        }

        return new DataTuple(category,tokens,featureValues);
    }

    static DataTuple makeNBDataTuple(CoreMap paragraph, List<RandomVariableType> features){
        int category = (DocumentHelper.isDefinition(paragraph)) ? 1:0;
        String []tokens = getNBWords(paragraph);

        //RandomVariableType[] features = DefinedTermExtractionModel.PARAGRAPH_FEATURES;
        int [] featureValues = new int[features.size()];
        for (int i=0; i<featureValues.length;i++){
            featureValues[i] = getParagraphFeature(paragraph, features.get(i));
        }

        return new DataTuple(category,tokens,featureValues);
    }
    // remove quotes and duplicate words
    static String[] getNBWords(CoreMap paragraph){
        Set<String> wordSet = paragraph.get(CoreAnnotations.WordSetForTrainingAnnotation.class);
        return wordSet.toArray(new String[wordSet.size()]);
    }

    static int getParagraphFeature(CoreMap paragraph, RandomVariableType feature){
        if (paragraph==null) return 0;
        List<Token> tokens = paragraph.getTokens();
        if (tokens==null || tokens.size()==0) return 0;
        switch (feature){
            case PARAGRAPH_HAS_DEFINITION: return (DocumentHelper.isDefinition(paragraph)) ?1:0;
            case PARAGRAPH_NUMBER_TOKENS:
                Set<String> words = paragraph.get(CoreAnnotations.WordSetForTrainingAnnotation.class);
                int num=0;
                if (words!=null) num = words.size();
                return Math.min(num, PFS_TOKENS_NUMBER_FEATURE_MAX);
            case PARAGRAPH_STARTS_WITH_SPECIAL_FORMAT:
                return booleanToInt(tokens.get(0).get(CoreAnnotations.InQuotesAnnotation.class )) |
                        booleanToInt(tokens.get(0).get(CoreAnnotations.IsUnderlineAnnotation.class ))|
                        booleanToInt(tokens.get(0).get(CoreAnnotations.IsBoldAnnotation.class ))|
                        booleanToInt(tokens.get(0).get(CoreAnnotations.IsItalicAnnotation.class ));
            case PARAGRAPH_STARTS_WITH_QUOTE:
                return (DocumentHelper.startsWithQuote(paragraph)) ?1:0;
            case PARAGRAPH_STARTS_WITH_BOLD:
                return booleanToInt(tokens.get(0).get(CoreAnnotations.IsBoldAnnotation.class ));
            case PARAGRAPH_STARTS_WITH_UNDERLINE:
                return booleanToInt(tokens.get(0).get(CoreAnnotations.IsUnderlineAnnotation.class ));
            case PARAGRAPH_STARTS_WITH_ITALIC:
                return booleanToInt(tokens.get(0).get(CoreAnnotations.IsItalicAnnotation.class ));
        }
        return -1;
    }

    /**
     * returns 0 if input parameter false, 1 if true
     * @param b
     * @return
     */
    static int booleanToInt(Boolean b){
        return (b!=null && b) ? 1:0;
    }
    /**
     * todo: consider removing paragraph parameter, and store info in just word tokens.
     * @param paragraph
     * @param word
     * @param feature
     * @return
     */
    static int getWordFeature(CoreMap paragraph, Token word, RandomVariableType feature){
        switch (feature){
            case WORD_IS_DEFINED_TERM:
                List<List<Token>> tokens =  DocumentHelper.getDefinedTermTokensInParagraph(paragraph);
                if (tokens==null) return 0;
                for (List<Token> list: tokens)
                    for (Token t:list) // matching string instead of matching token reference is a hack here.
                        if (t.getText().equals(word.getText())) return 1;
                    //if (list.contains(word)) return 1;
                return 0;
            case WORD_IN_QUOTES:
//                Boolean inQuotes = word.get(CoreAnnotations.InQuotesAnnotation.class );
//                return (inQuotes!=null && inQuotes==true) ?1:0;
                return booleanToInt(word.get(CoreAnnotations.InQuotesAnnotation.class ));
            case WORD_IS_BOLD:
                return booleanToInt(word.get(CoreAnnotations.IsBoldAnnotation.class ));
            case WORD_IS_UNDERLINED:
                return booleanToInt(word.get(CoreAnnotations.IsUnderlineAnnotation.class ));
            case WORD_IS_ITALIC:
                return booleanToInt(word.get(CoreAnnotations.IsItalicAnnotation.class ));
            case WORD_HAS_SPECIAL_FORMAT:
                return booleanToInt(word.get(CoreAnnotations.InQuotesAnnotation.class )) |
                        booleanToInt(word.get(CoreAnnotations.IsUnderlineAnnotation.class ))|
                        booleanToInt(word.get(CoreAnnotations.IsBoldAnnotation.class ))|
                        booleanToInt(word.get(CoreAnnotations.IsItalicAnnotation.class ));
            case WORD_INDEX:  return word.get(CoreAnnotations.Index.class );
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
