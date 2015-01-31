package com.skroll.util;

/**
 * Created by wei2learn on 1/25/2015.
 */
public class WordHelper {
    public static boolean isQuote(String s){
        String s2 = WordEquivalence.getRepresentative(s);
        return s2!=null && s2.equals("\"");
    }
}
