package com.skroll.util;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PersistObjectTest {

    @Test
    public void testPersistObject()  {
           PersistModelTestClass persistModelTestClass = new PersistModelTestClass();
           persistModelTestClass.wordCounts = new HashMap[1];
           persistModelTestClass.wordCounts[0] = new HashMap<String, Integer>();
           persistModelTestClass.wordCounts[0].put("awesome", 100000);
        try {
            PersistObject.persistObject(persistModelTestClass,"com.skroll.util.Model.CoolModel");
        } catch (PersistObject.ObjectPersistException e) {
            e.printStackTrace();
            fail("failed persist Object");
        }
        Object obj = null;
        try {
            obj = PersistObject.readObject("com.skroll.util.Model.CoolModel");
        } catch (PersistObject.ObjectPersistException e) {
            e.printStackTrace();
            fail("failed readObject");
        }
        if ( obj instanceof PersistModelTestClass) {
            PersistModelTestClass readPersistModelTestClass = (PersistModelTestClass)obj;
           assertEquals("It worked.", readPersistModelTestClass.wordCounts[0].get("awesome").intValue(),100000);
        }
    }

}