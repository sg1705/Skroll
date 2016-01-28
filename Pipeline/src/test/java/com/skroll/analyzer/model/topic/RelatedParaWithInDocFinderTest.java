package com.skroll.analyzer.model.topic;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.util.SkrollTestGuiceModule;
import com.skroll.util.TestHelper;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wei2l on 1/18/2016.
 */
public class RelatedParaWithInDocFinderTest {

    Document doc;
    RelatedParaWithInDocFinder finder;


    @Before
    public void setup() throws Exception {
        doc = TestHelper.setUpTestDocForTopicModeling();
        Injector injector = Guice.createInjector(new SkrollTestGuiceModule());
        finder = injector.getInstance(RelatedParaWithInDocFinder.class);
    }


    @Test
    public void testComputeDistance() throws Exception {
        Double[] distances = finder.computeDistances(doc.getParagraphs().get(0), doc);
        System.out.println(Arrays.toString(distances));
        assert((int)(distances[1]*100) == 70);
    }

    @Test
    public void testSortParasByDistance() throws Exception {
        List<CoreMap> rankedParas = finder.sortParasByDistance(doc, doc.getParagraphs().get(0));
        rankedParas.stream().forEach(p -> System.out.println(p.getText()));
    }
}
