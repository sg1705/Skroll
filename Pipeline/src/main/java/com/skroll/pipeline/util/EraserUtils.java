package com.skroll.pipeline.util;

/**
 * Created by saurabh on 1/3/15.
 */
public class EraserUtils {

    /**
     *  Casts an Object to a T
     * @param <T>
     */
    @SuppressWarnings("unchecked")
    public static <T> T uncheckedCast(Object o) {
        return (T)o;
    }

}
