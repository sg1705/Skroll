package com.skroll.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Test for equivalence of two word strings based on equivalence classes defined
 * todo: make a dictionary file, put each set of equivalent words on a line in a csv file and read this information in
 *
 * Created by wei2learn on 1/25/2015.
 */
public class WordEquivalence {
    static Map<String, String> dict = new HashMap<String, String>(){{
        put("\u201c", "\"");
        put("\u201d", "\"");
        put("\"", "\"");
    }};

    public static boolean equivalent(String s1, String s2){
        String r1 =  dict.get(s1);
        String r2 =  dict.get(s2);
        return (r1 != null && r2 != null && r1.equals(r2));
    }

    public static String getRepresentative(String s){
        return dict.get(s);
    }

}
