package com.skroll.document;

import org.junit.Test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ParagraphTest {

    @Test
    public void testSortingByParagraphId(){
        List<Paragraph> paragraphList = new LinkedList<>();
        paragraphList.add(new Paragraph("90","aa",1));
        paragraphList.add(new Paragraph("10","aa",1));
        paragraphList.add(new Paragraph("1000", "aa", 1));
        System.out.println("Before Sort:" + paragraphList);
        Collections.sort(paragraphList, new Paragraph.ParagraphComparator());
        System.out.println("After Sort:" + paragraphList);
    }
}

