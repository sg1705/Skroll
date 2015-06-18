package com.skroll.trainer;

import com.skroll.util.Configuration;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

public class BenchmarkModelTest {

    BenchmarkModel benchmark = null;
    @Before
    public void setup() {
        Configuration configuration = new Configuration("src/test/resources/skroll-test.properties");
        benchmark = new BenchmarkModel(configuration);
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
}