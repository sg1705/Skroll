package com.skroll.benchmark;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.skroll.classifier.*;
import com.skroll.document.factory.BenchmarkFSDocumentFactoryImpl;
import com.skroll.document.factory.DocumentFactory;
import com.skroll.util.SkrollTestGuiceModule;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

public class BenchmarkTest {

    Benchmark benchmark = null;
    @Before
    public void setup() throws Exception {
        Injector injector = Guice.createInjector(new SkrollTestGuiceModule());
        ClassifierFactory classifierFactory = injector.getInstance(ClassifierFactory.class);
        ClassifierFactoryStrategy classifierFactoryStrategyForClassify = injector.getInstance(Key.get(ClassifierFactoryStrategy.class, ClassifierFactoryStrategyForClassify.class));
        DocumentFactory documentFactory = injector.getInstance(BenchmarkFSDocumentFactoryImpl.class);
        benchmark = new Benchmark(documentFactory,classifierFactory,classifierFactoryStrategyForClassify);
    }
    @Test
    public void testBenchmark(){

        try {

            QC qc = benchmark.runQCOnBenchmarkFile("5b6682f16aab7d74d423f0952f196481");
            System.out.println("QC output:"+ qc);
            assertNotEquals(qc.stats.get(Category.TOC_1).overallOccurance,0);
            assertNotEquals(qc.stats.get(Category.TOC_2).overallOccurance,0);
        } catch (Throwable e) {
            e.printStackTrace();
            fail(" failed to run benchmark");
        }
    }
    @Test
    public void testBenchmarkFolder(){

        try {
            QC qc = benchmark.runQCOnBenchmarkFolder();
            System.out.println("QC output:"+ qc);
            assertNotEquals(qc.stats.get(Category.TOC_1).overallOccurance,0);
            assertNotEquals(qc.stats.get(Category.TOC_2).overallOccurance,0);
        } catch (Throwable e) {
            e.printStackTrace();
            fail(" failed to run benchmark");
        }
    }
}