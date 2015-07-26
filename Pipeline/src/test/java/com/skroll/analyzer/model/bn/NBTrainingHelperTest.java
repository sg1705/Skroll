package com.skroll.analyzer.model.bn;

import com.skroll.analyzer.model.applicationModel.DefModelRVSetting;
import com.skroll.analyzer.model.applicationModel.ModelRVSetting;
import com.skroll.analyzer.model.bn.config.NBMNConfig;

import com.skroll.classifier.Classifiers;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wei on 4/28/15.
 */
public class NBTrainingHelperTest {
    ModelRVSetting setting = new DefModelRVSetting(Classifiers.DEF_CLASSIFIER_ID,Classifiers.defClassifierProto.getCategoryIds());
    NBMNConfig config = setting.getNbmnConfig();
    NaiveBayesWithMultiNodes nbmn = NBTrainingHelper.createTrainingNBMN(config);
    List<String[]> wordsList;
    NBMNTuple tuple;

    @Before
    public void setup() {
        List<String[]> wordsList = new ArrayList<>();
        wordsList.add(new String[]{"test", "para"});
        tuple = new NBMNTuple(wordsList,
                1,
                new int[]{1},
                new int[]{1, 0, 0, 0, 0},
                new int[][]{{0, 1}, {1, 1}, {1, 0}, {0, 0}, {0, 1}});

    }

    @Test
    public void testCreateTrainingNB() throws Exception {

    }

    @Test
    public void testCreateFeatureNodes() throws Exception {

    }

    @Test
    public void testCreateWordNodes() throws Exception {

    }

    @Test
    public void testAddSample() throws Exception {
        NBTrainingHelper.addSample(nbmn, tuple);
        System.out.println(nbmn);
        System.out.println(Arrays.toString(nbmn.documentFeatureNodes.get(2).get(1).getParameters()));
        assert (Arrays.equals(nbmn.documentFeatureNodes.get(0).get(0).getParameters(), new double[]{0.1, 0.1}));
        assert (Arrays.equals(nbmn.documentFeatureNodes.get(0).get(1).getParameters(), new double[]{0.1, 1.1}));
        assert (Arrays.equals(nbmn.documentFeatureNodes.get(2).get(1).getParameters(), new double[]{1.1, 0.1}));
        System.out.println(Arrays.toString(nbmn.getMultiNodes().get(0).getNodes()[1].getParameters()));
        assert (Arrays.equals(nbmn.getMultiNodes().get(0).getNodes()[1].getParameters(), new double[]{0.1, 0.1, 0.1, 1.1}));
        assert (Arrays.equals(nbmn.getMultiNodes().get(0).getNodes()[0].getParameters(), new double[]{0.1, 0.1, 0.1, 0.1}));

        assert (Arrays.equals(nbmn.getFeatureNodes().get(0).getParameters(), new double[]{
                0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 1.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1}));


    }

    @Test
    public void testAddSampleWithWeight() throws Exception {
        NBTrainingHelper.addSample(nbmn, tuple, 9);
        System.out.println(nbmn);
        System.out.println(Arrays.toString(nbmn.documentFeatureNodes.get(2).get(1).getParameters()));
        assert (Arrays.equals(nbmn.documentFeatureNodes.get(0).get(0).getParameters(), new double[]{0.1, 0.1}));
        assert (Arrays.equals(nbmn.documentFeatureNodes.get(0).get(1).getParameters(), new double[]{0.1, 9.1}));
        assert (Arrays.equals(nbmn.documentFeatureNodes.get(2).get(1).getParameters(), new double[]{9.1, 0.1}));
        System.out.println(Arrays.toString(nbmn.getMultiNodes().get(0).getNodes()[1].getParameters()));
        assert (Arrays.equals(nbmn.getMultiNodes().get(0).getNodes()[1].getParameters(), new double[]{0.1, 0.1, 0.1, 9.1}));
        assert (Arrays.equals(nbmn.getMultiNodes().get(0).getNodes()[0].getParameters(), new double[]{0.1, 0.1, 0.1, 0.1}));

        assert (Arrays.equals(nbmn.getFeatureNodes().get(0).getParameters(), new double[]{
                0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 9.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1}));


    }

    @Test
    public void testCreateTrainingNBWithFeatureConditioning() throws Exception {

    }

    @Test
    public void testCreateDocFeatureNodes() throws Exception {

    }

    @Test
    public void testCreateFeatureExistAtDoclevelNodes() throws Exception {

    }
}