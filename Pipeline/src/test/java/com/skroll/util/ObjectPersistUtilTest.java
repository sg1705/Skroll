package com.skroll.util;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ObjectPersistUtilTest {
    public static final Logger logger = LoggerFactory
            .getLogger(Configuration.class);
    @Test
    public void testPersistReadObject() throws Exception {

        PersistModelTestClass persistModelTestClass = new PersistModelTestClass();
        persistModelTestClass.wordCounts = new HashMap[1];
        persistModelTestClass.wordCounts[0] = new HashMap<String, Integer>();
        persistModelTestClass.wordCounts[0].put("awesome", 100000);


        ObjectPersistUtil objectPersistUtil = new ObjectPersistUtil<PersistModelTestClass>();

        try {
            objectPersistUtil.persistObject(persistModelTestClass, "com.skroll.util.PersistModelTestClass.persistModelTestClass");
        } catch (ObjectPersistUtil.ObjectPersistException e) {
            e.printStackTrace();
            fail("failed persist Object");
        }
        Object obj = null;
        try {
            obj = objectPersistUtil.readObject("com.skroll.util.PersistModelTestClass.persistModelTestClass");
        } catch (ObjectPersistUtil.ObjectPersistException e) {
            e.printStackTrace();
            fail("failed readObject");
        }
        if ( obj instanceof PersistModelTestClass) {
            PersistModelTestClass readPersistModelTestClass = (PersistModelTestClass)obj;
            logger.info("Read Back:" + readPersistModelTestClass.wordCounts[0].get("awesome").intValue());
            assertEquals(readPersistModelTestClass.wordCounts[0].get("awesome").intValue(), 100000);
        }
    }
    }