package com.skroll.document;

import org.junit.Test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TermProtoTest {

    @Test
    public void testSortingByParagraphId(){
        List<TermProto> termProtoList = new LinkedList<>();
        termProtoList.add(new TermProto("90","aa",1, false));
        termProtoList.add(new TermProto("10","aa",1, false));
        termProtoList.add(new TermProto("1000", "aa", 1, false));
        System.out.println("Before Sort:" + termProtoList);
        Collections.sort(termProtoList, new TermProto.ParagraphComparator());
        System.out.println("After Sort:" + termProtoList);
    }
}

