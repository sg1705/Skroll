package com.skroll.analyzer.model.bn;

import com.skroll.analyzer.model.applicationModel.DefModelRVSetting;
import com.skroll.analyzer.model.applicationModel.ModelRVSetting;
import com.skroll.analyzer.model.bn.config.NBMNConfig;
import com.skroll.analyzer.model.bn.node.NodeTrainingHelper;
import com.skroll.classifier.Category;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wei on 4/28/15.
 */
public class NBTrainingHelperTest {
    static final List<Integer> TEST_DEF_CATEGORY_IDS =  new ArrayList<>(Arrays.asList(Category.NONE, Category.DEFINITION));

    ModelRVSetting setting = new DefModelRVSetting(TEST_DEF_CATEGORY_IDS);
    NBMNConfig config = setting.getNbmnConfig();
    NaiveBayesWithMultiNodes nbmn = NBTrainingHelper.createTrainingNBMN(config);
    List<String[]> wordsList;
    double priorCount = NodeTrainingHelper.PRIOR_COUNT;
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
        assert (Arrays.equals(nbmn.documentFeatureNodes.get(0).get(0).getParameters(), new double[]{priorCount, priorCount}));
        assert (Arrays.equals(nbmn.documentFeatureNodes.get(0).get(1).getParameters(), new double[]{priorCount, 1 + priorCount}));
        assert (Arrays.equals(nbmn.documentFeatureNodes.get(2).get(1).getParameters(), new double[]{1 + priorCount, priorCount}));
        System.out.println(Arrays.toString(nbmn.getMultiNodes().get(0).getNodes()[1].getParameters()));
        assert (Arrays.equals(nbmn.getMultiNodes().get(0).getNodes()[1].getParameters(), new double[]{priorCount, priorCount, priorCount, 1 + priorCount}));
        assert (Arrays.equals(nbmn.getMultiNodes().get(0).getNodes()[0].getParameters(), new double[]{priorCount, priorCount, priorCount, priorCount}));

        assert (nbmn.getFeatureNodes().get(0).getParameters()[26] == 1 + priorCount);

    }

    @Test
    public void testAddSampleWithWeight() throws Exception {
        NBTrainingHelper.addSample(nbmn, tuple, 9);
        System.out.println(nbmn);
        System.out.println(Arrays.toString(nbmn.documentFeatureNodes.get(2).get(1).getParameters()));
        assert (Arrays.equals(nbmn.documentFeatureNodes.get(0).get(0).getParameters(), new double[]{priorCount, priorCount}));
        assert (Arrays.equals(nbmn.documentFeatureNodes.get(0).get(1).getParameters(), new double[]{priorCount, 9 + priorCount}));
        assert (Arrays.equals(nbmn.documentFeatureNodes.get(2).get(1).getParameters(), new double[]{9 + priorCount, priorCount}));
        System.out.println(Arrays.toString(nbmn.getMultiNodes().get(0).getNodes()[1].getParameters()));
        assert (Arrays.equals(nbmn.getMultiNodes().get(0).getNodes()[1].getParameters(), new double[]{priorCount, priorCount, priorCount, 9 + priorCount}));
        assert (Arrays.equals(nbmn.getMultiNodes().get(0).getNodes()[0].getParameters(), new double[]{priorCount, priorCount, priorCount, priorCount}));

        assert (nbmn.getFeatureNodes().get(0).getParameters()[26] == 9 + priorCount);

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