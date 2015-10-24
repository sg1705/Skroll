package com.skroll.util;

import com.skroll.document.CoreMap;
import com.skroll.document.Token;

import java.util.List;

/**
 * Created by wei on 10/24/15.
 */
public class ParaHelper {

    public static String getLastAlphanumericWord(CoreMap para){
        if (para==null) return "";
        List<Token> tokens = para.getTokens();
        int n = tokens.size();
        for (int i = n - 1; i > 0; i--) {
            String word = tokens.get(i).getText();
            if (WordHelper.isAlphanumeric(word)) return word;
        }
        return ""; //todo: should we return null or empty string here? Probably doesn't matter.
    }

    public static String getLastAlphaWord(CoreMap para){
        if (para==null) return "";
        List<Token> tokens = para.getTokens();
        int n = tokens.size();
        for (int i = n - 1; i > 0; i--) {
            String word = tokens.get(i).getText();
            if (WordHelper.isAlpha(word)) return word;
        }
        return ""; //todo: should we return null or empty string here? Probably doesn't matter.
    }


    public static String getLastWord(CoreMap para){
        if (para==null) return "";
        List<Token> tokens = para.getTokens();
        int n = tokens.size();
        if (n>0) return tokens.get(n-1).getText();
        return ""; //todo: should we return null or empty string here? Probably doesn't matter.
    }

}
