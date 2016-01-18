package com.skroll.analyzer.model.topic;

import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.util.TestHelper;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wei2l on 1/18/2016.
 */
public class RelatedParaWithinDocFinderTest {

    static final String TEST_MODEL = "src/test/resources/analyzer/topics/testModelForInfer";
    Document doc = TestHelper.setUpTestDocForTopicModeling();
    RelatedParaWithinDocFinder finder = new RelatedParaWithinDocFinder(doc, TEST_MODEL);

    @Test
    public void testComputeDistance() throws Exception {
        Double[] distances = finder.computeDistances(doc.getParagraphs().get(0));
        System.out.println(Arrays.toString(distances));
        assert((int)(distances[1]*100) == 70);
    }

    @Test
    public void testSortParasByDistance() throws Exception {
        List<CoreMap> rankedParas = finder.sortParasByDistance(doc.getParagraphs().get(0));
        rankedParas.stream().forEach(p -> System.out.println(p.getText()));
    }
}