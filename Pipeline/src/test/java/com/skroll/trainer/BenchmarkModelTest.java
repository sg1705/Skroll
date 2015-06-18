package com.skroll.trainer;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.skroll.classifier.ClassifierFactory;
import com.skroll.document.factory.BenchmarkFSDocumentFactoryImpl;
import com.skroll.document.factory.DocumentFactory;
import com.skroll.rest.APITestGuiceModule;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

public class BenchmarkModelTest {

    BenchmarkModel benchmark = null;
    @Before
    public void setup() throws Exception {
        //Configuration configuration = new Configuration("src/test/resources/skroll-test.properties");
        Injector injector = Guice.createInjector(new APITestGuiceModule());
        ClassifierFactory classifierFactory = injector.getInstance(ClassifierFactory.class);
        DocumentFactory documentFactory = injector.getInstance(BenchmarkFSDocumentFactoryImpl.class);
        benchmark = new BenchmarkModel(documentFactory,classifierFactory.getClassifier());
    }
    @Test
    public void TestBenchmark(){

        try {

            QC qc = benchmark.runQCOnBenchmarkFile("d629534d10k.htm");
            System.out.println("QC output:"+ qc);
            assertNotEquals(qc.stats.get(0).overallOccurance,0);
            assertNotEquals(qc.stats.get(1).overallOccurance,0);
        } catch (Exception e) {
            e.printStackTrace();
            fail(" failed to run benchmark");
        }
    }
    @Test
    public void TestBenchmarkFolder(){

        try {
            QC qc = benchmark.runQCOnBenchmarkFolder();
            System.out.println("QC output:"+ qc);
            assertNotEquals(qc.stats.get(0).overallOccurance,0);
            assertNotEquals(qc.stats.get(1).overallOccurance,0);
        } catch (Exception e) {
            e.printStackTrace();
            fail(" failed to run benchmark");
        }
    }
}