package com.skroll.util;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PersistObjectTest {

    @Test
    public void testPersistObject()  {
           Model model = new Model();
           model.wordCounts = new HashMap[1];
           model.wordCounts[0] = new HashMap<String, Integer>();
           model.wordCounts[0].put("awesome", 100000);
        try {
            PersistObject.persistObject(model,"com.skroll.util.Model.CoolModel");
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
        if ( obj instanceof Model) {
            Model readModel = (Model)obj;
           assertEquals("It worked.",readModel.wordCounts[0].get("awesome").intValue(),100000);
        }
    }

}