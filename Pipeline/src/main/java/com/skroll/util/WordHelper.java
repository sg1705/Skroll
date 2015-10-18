package com.skroll.util;

/**
 * Created by wei2learn on 1/25/2015.
 */
public class WordHelper {
    public static boolean isQuote(String s){
        String s2 = WordEquivalence.getRepresentative(s);
        return s2!=null && s2.equals("\"");
    }

    public static boolean isAlphanumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c < 0x30 || (c >= 0x3a && c <= 0x40) || (c > 0x5a && c <= 0x60) || c > 0x7a)
                return false;
        }
        return true;
    }

    public static boolean isInt(String str) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c < 0x30 || c > 0x39)
                return false;
        }
        return true;
    }

}
