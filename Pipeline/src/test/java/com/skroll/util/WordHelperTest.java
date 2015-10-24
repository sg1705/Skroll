package com.skroll.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by wei on 10/18/15.
 */
public class WordHelperTest {

    @Test
    public void testIsQuote() throws Exception {
        assert (WordHelper.isQuote("\""));

    }

    @Test
    public void testIsAlphanumeric() throws Exception {
        assert (WordHelper.isAlphanumeric("aB2"));
        assert (!WordHelper.isAlphanumeric("!@#"));

    }

    @Test
    public void testIsInt() throws Exception {

        assert (WordHelper.isInt("78"));
        assert (!WordHelper.isInt("8.6"));
        assert (!WordHelper.isInt("xy"));
    }
}