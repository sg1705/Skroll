package com.skroll.util;

import junit.framework.TestCase;

public class FlagsTest extends TestCase {

    public void testGet() {
        Flags.put("MyKey", true);
        assert(Flags.get("MyKey"));
    }

}