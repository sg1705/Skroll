package com.skroll.document;

import com.skroll.document.annotation.CoreAnnotations;
import junit.framework.TestCase;

public class CoreMapTest extends TestCase {

    public void testKeyName() throws Exception {
        CoreMap map = new CoreMap();
        System.out.println(map.keyName(CoreAnnotations.TextAnnotation.class));
        System.out.println(CoreAnnotations.TextAnnotation.class.getSimpleName());
        System.out.println(CoreAnnotations.TextAnnotation.class.getEnclosingClass().getSimpleName());
        assert ( map.keyName(CoreAnnotations.TextAnnotation.class)
                .equals("com.skroll.document.annotation.CoreAnnotations.TextAnnotation"));
    }
}