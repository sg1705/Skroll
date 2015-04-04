package com.skroll.util;

import java.util.HashMap;

/**
 * Created by saurabh on 4/3/15.
 */
public class Flags {
    private static final HashMap<String, Boolean> flags = new HashMap();
    public static final String ENABLE_UPDATE_BNI = "ENABLE_UPDATE_BNI";

    public static boolean get(String key) {
        if (flags.containsValue(key)) {
            return flags.get(key);
        }
        return false;
    }

    public static void put(String key, boolean value) {
        flags.put(key, value);
    }
}
