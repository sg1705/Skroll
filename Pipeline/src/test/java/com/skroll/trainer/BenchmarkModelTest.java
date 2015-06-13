package com.skroll.trainer;

import com.skroll.util.Configuration;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.fail;

public class BenchmarkModelTest {

    Benchmark benchmark = null;
    @Before
    public void setup() {
        Configuration configuration = new Configuration();
        benchmark = new Benchmark(configuration);
    }
    @Test
    public void TestBenchmark(){

        try {
            System.out.println(benchmark.runQCOnBenchmarkFile("d452134d10k.htm"));
        } catch (Exception e) {
            e.printStackTrace();
            fail(" failed to create overwrite files");
        }
    }

    @Test
    public void TestQCOnBenchmarkFolder(){
        try {
            System.out.println(benchmark.runQCOnBenchmarkFolder());
        } catch (Exception e) {
            e.printStackTrace();
            fail(" failed to create overwrite files");
        }
    }
}