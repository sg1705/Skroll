package com.skroll.analyzer.model;

import com.google.common.base.Splitter;
import com.skroll.analyzer.model.hmm.HiddenMarkovModel;
import com.skroll.analyzer.model.nb.DataTuple;
import com.skroll.analyzer.model.nb.NaiveBayes;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotations;


import java.util.*;

/**
 * DefinitionExtractionModel has both a NB and a HMM substructures,
 * and has a link from the class node of NB to the first state of HMM.
 *
 * Created by wei2learn on 1/18/2015.
 */


public class DefinedTermExtractionModel {

    //static final int PFS_TOKENS_NUMBER_FEATURE_MAX = 11;
    static final int HMM_MODEL_LENGTH = 12;
    static final int WFS_NUMBER_TOKENS_USED = 10;
    //static final PARAGRAPH_FEATURES;

    HiddenMarkovModel hmm;
    NaiveBayes nb;

//    // random variables at the paragraph level
//    RandomVariableOld paragraphHasDefinition = new RandomVariableOld("paragraphHasDefinition",2);
//    RandomVariableOld paragraphHasQuote = new RandomVariableOld("paragraphHasQuote",2);
//    RandomVariableOld paragraphNumberTokens = new RandomVariableOld("paragraphNumberTokens",PFS_TOKENS_NUMBER_FEATURE_MAX);
//
//    // randome variables at the word level
//    RandomVariableOld wordIsDefinedTerm = new RandomVariableOld("wordIsDefinedTerm",2);
//    RandomVariableOld wordIsInQuotes = new RandomVariableOld("wordIsInQuotes",2);
//    RandomVariableOld wordIndexInParagraph = new RandomVariableOld("wordIndexInParagraph",HMM_MODEL_LENGTH);
//
//
//
//    // features used at the paragraph
//    EnumMap<ParagraphFeatures, RandomVariableOld> features=
//            new EnumMap<ParagraphFeatures, RandomVariableOld>(ParagraphFeatures.class){{
//                put(ParagraphFeatures.HAS_DEFINITION, paragraphHasDefinition);
//                put(ParagraphFeatures.HAS_QUOTE, paragraphHasQuote);
//                put(ParagraphFeatures.NUMBER_TOKENS, paragraphNumberTokens);
//    }};


//    Set<RandomVariable> PARAGRAPH_FEATURES = new HashSet<RandomVariable>(Arrays.asList(
//            paragraphHasQuote, paragraphNumberTokens
//    ));
//
//    // features used at the word level
//    Set<RandomVariable> WORD_FEATURES = new HashSet<RandomVariable>(Arrays.asList(
//            wordIsInQuote, wordIndexInParagraph
//    ));

//    EnumMap<WordFeatures, RandomVariableOld> features=
//            new EnumMap<WordFeatures, RandomVariableOld>(WordFeatures.class){{
//                put(WordFeatures.IS_DEFINED_TERM, wordIsDefinedTerm);
//                put(WordFeatures.IN_QUOTES, wordIsInQuotes);
//                put(WordFeatures.INDEX, paragraphNumberTokens);
//            }};
//

    // the link between paragraph category to the state of the first word in the paragraph
    // the links from paragraph category to the remaining states should not be significant,
    //      and makes the model more complicated and expensive.

    static final RandomVariableType[] PARAGRAPH_FEATURES = {
            RandomVariableType.PARAGRAPH_STARTS_WITH_QUOTE, RandomVariableType.PARAGRAPH_NUMBER_TOKENS};
    static final RandomVariableType[] WORD_FEATURES = {
            RandomVariableType.WORD_IN_QUOTES, RandomVariableType.WORD_INDEX};

    int[][] nbCategoryToHmmState1;

//    public DefinedTermExtractionModel(){
//
//    }

    public DefinedTermExtractionModel(int[] nbFeatureSizes, int[] hmmFeatureSizes, int hmmModelLength){
        //nbCategoryToHmmState1 = new int[];
    }

    void updateWithParagraph(CoreMap paragraph) {
        updateNBWithParagraph(paragraph);
        updateHMMWithParagraphOld(paragraph);
        //int paraType = DefinedTermExtractionHelper.getParagraphFeature(paragraph, RandomVariableType.PARAGRAPH_HAS_DEFINITION);
    }

    void updateNBWithParagraph(CoreMap paragraph){
        DataTuple nbDataTuple = DefinedTermExtractionHelper.makeNBDataTuple(paragraph);
        nb.addSample(nbDataTuple);

    }

    void updateHMMWithParagraphOld(CoreMap paragraph){
            List<Token> tokens = paragraph.get(CoreAnnotations.TokenAnnotation.class);

            HashSet<String> definitionsSet;
            if (!paragraph.containsKey(CoreAnnotations.IsDefinitionAnnotation.class)) {
                definitionsSet= new HashSet<String>();
            } else {
                List<Token> defTokens = paragraph.get(CoreAnnotations.DefinedTermsAnnotation.class);
                List<String> definitions = Splitter.on(' ').splitToList(DocumentHelper.getTokenString(defTokens).get(0));
                definitionsSet = new HashSet<String>(definitions);
            }

            int[] tokenType = new int[tokens.size()];
            int ii = 0;
            for(Token token : tokens) {
                if (definitionsSet.contains(token.getText())) {
                    tokenType[ii] = 1;
                } else {
                    tokenType[ii] = 0;
                }
                ii++;
            }


            hmm.updateCounts(
                    DocumentHelper.getTokenString(tokens).toArray(new String[tokens.size()]),
                    tokenType);

    }
    void updateWithDocument(Document doc){

    }
    void updateWithDocuments(List<Document> docs){

    }
}
