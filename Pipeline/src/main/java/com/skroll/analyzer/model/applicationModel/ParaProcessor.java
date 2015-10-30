package com.skroll.analyzer.model.applicationModel;

import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.applicationModel.randomVariables.RVValues;
import com.skroll.document.CoreMap;
import com.skroll.document.DocumentHelper;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.util.WordHelper;

import java.util.*;

/**
 * Created by wei on 5/10/15.
 */
public class ParaProcessor {
    // create a copy of paragraph and annotate it further for training
    public static CoreMap processParagraph(CoreMap paragraph) {
        return processParagraph(paragraph, ModelRVSetting.NUM_WORDS_TO_USE_PER_PARAGRAPH);
    }

    // check through the CoreMaps(usually paragraphs) for feature value.
    // take the maximum of the values from different CoreMap, because -1 or 0 indicates no value present in the Coremap
    // todo: this may not be a good approach
    static int getFeatureValue(RandomVariable v, List<CoreMap> mList) {

        return RVValues.getValue(v, mList);
//
//        int result = -1; // -1 means unobserved/missing
//
//        for (CoreMap m : mList) {
//            int value = RVValues.getValue(v, m);
//            if (value > result) result = value;
//        }
//        return result;
    }

    /**
     * Gets the feature value of a given token. For example, if a token is Bold then it returns false
     * @param v
     * @param token
     * @param mList
     * @return
     */
    static int getWordFeatureValue(RandomVariable v, Token token, List<CoreMap> mList) {
        int result = 0; // return 0 in case of null.
        for (CoreMap m : mList) {
            int value = RVValues.getWordLevelRVValue(v, token, mList);
            if (value > result) result = value;
        }
        return result;
    }

    /**
     * Returns the feature values of the paragraph
     * @param rvs
     * @param m Two paragraphs, where first one is original and the second is processed
     * @return
     */
    public static int[] getFeatureVals(List<RandomVariable> rvs, List<CoreMap> m) {
        int[] vals = new int[rvs.size()];
        for (int f = 0; f < rvs.size(); f++) {
            vals[f] = getFeatureValue(rvs.get(f), m);
        }
        return vals;
    }

    public static List<String[]> getWordsList(List<RandomVariable> rvs, CoreMap para) {
        List<String[]> wordsListOfOnePara = new ArrayList<>();
        for (int w = 0; w < rvs.size(); w++) {
            wordsListOfOnePara.add(RVValues.getWords(rvs.get(w), para));
        }

        return wordsListOfOnePara;

    }

    // set inquote annotation and make word sets
    public static CoreMap processParagraph(CoreMap paragraph, int numWordsToUse) {
        CoreMap trainingParagraph = new CoreMap();
        List<Token> tokens = paragraph.getTokens();
        List<Token> newTokens = new ArrayList<>();

        if (tokens.size() > 0 && WordHelper.isQuote(tokens.get(0).getText()))
            trainingParagraph.set(CoreAnnotations.StartsWithQuote.class, true);

        boolean inQuotes = false; // flag for annotating if a token is in quotes or not
        int i = 0;
        for (Token token : tokens) {
            if (i == numWordsToUse) break;
            if (WordHelper.isQuote(token.getText())) {
                inQuotes = !inQuotes;
                continue;
            }
            if (inQuotes) {
                token.set(CoreAnnotations.InQuotesAnnotation.class, true);
            }
            token.set(CoreAnnotations.IndexInteger.class, i++);
            newTokens.add(token);
        }

        trainingParagraph.set(CoreAnnotations.TokenAnnotation.class, newTokens);

        return trainingParagraph;
    }


    // print processedPara for testing purpose
    static void print(CoreMap processedPara) {
        Set<String> wordSet = processedPara.get(CoreAnnotations.WordSetForTrainingAnnotation.class);
        System.out.println((wordSet));
        List<Token> processedTokens = processedPara.get(CoreAnnotations.TokenAnnotation.class);
        List<String> strings = DocumentHelper.getTokenString(processedTokens);
        System.out.println(strings);

        for (Token token : processedTokens) {
            System.out.print(token.get(CoreAnnotations.InQuotesAnnotation.class) + " ");
        }
        System.out.println();

    }

    static boolean isParaObserved(CoreMap para){
        Boolean isObserved = para.get(CoreAnnotations.IsUserObservationAnnotation.class);
        if (isObserved==null) isObserved = false;
        return  isObserved;
    }


}
