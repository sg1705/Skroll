package com.skroll.analyzer.model.applicationModel;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.skroll.analyzer.data.NBMNData;
import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.applicationModel.randomVariables.LowerCaseWordsComputer;
import com.skroll.analyzer.model.applicationModel.randomVariables.NumberTokensComputer;
import com.skroll.analyzer.model.applicationModel.randomVariables.RVCreater;
import com.skroll.analyzer.model.applicationModel.randomVariables.RVValues;
//import com.skroll.analyzer.model.applicationModel.randomVariables.UniqueWordsComputer;
import com.skroll.classifier.Category;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.JsonDeserializer;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.util.TestHelper;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wei on 5/10/15.
 */
public class DocProcessorTest {

    int maxNumWords = 20;
    //static final RandomVariable DEFAULT_PARA_IS_DEF =
    //        RVCreater.createRVFromAnnotation(CoreAnnotations.IsDefinitionAnnotation.class);

    static final List<Integer> TEST_DEFINITION_CATEGORY_IDS =  new ArrayList<>(Arrays.asList(Category.NONE, Category.DEFINITION));
    static final List<RandomVariable> DEFAULT_PARA_FEATURE_VARS = Arrays.asList(
            RVCreater.createDiscreteRVWithComputer(new NumberTokensComputer(), "numTokens")
    );
    static final List<RandomVariable> DEFAULT_PARA_DOC_FEATURE_VARS = Arrays.asList(
            RVCreater.createParagraphStartsWithRV(CoreAnnotations.InQuotesAnnotation.class)
    );

    static final List<RandomVariable> DEFAULT_WORD_VARS = Arrays.asList(
            RVCreater.createWordsRVWithComputer(new LowerCaseWordsComputer(), "lowerCaseWords")
    );

    Document doc = new Document();
    ModelRVSetting setting = new ModelRVSetting(
             DefModelRVSetting.DEFAULT_WORD_FEATURES,
            DEFAULT_PARA_FEATURE_VARS, DEFAULT_PARA_DOC_FEATURE_VARS, DEFAULT_WORD_VARS,
            TEST_DEFINITION_CATEGORY_IDS);


    static String trainingFileName = "src/test/resources/analyzer/definedTermExtractionTraining/AMC Networks CA.html";

    @Before
    public void setUp() throws Exception {

        doc = TestHelper.setUpTestDoc();


    }

    @Test
    public void testProcessParagraphs() throws Exception {
        List<CoreMap> processedParas = DocProcessor.processParas(doc);
        assert (processedParas.get(0).get(CoreAnnotations.StartsWithQuote.class));
        assert (processedParas.get(0).getTokens().get(1).get(CoreAnnotations.IndexInteger.class) == 1);
    }

    @Test
    public void testParaGetDataFromDoc() throws Exception {
        List<CoreMap> processedParas = DocProcessor.processParas(doc);
        NBMNData data = DocProcessor.getParaDataFromDoc(doc, setting.getNbmnConfig());
        System.out.print(data);
        assert (Arrays.deepToString(data.getParaFeatures()).equals("[[3], [24], [3]]"));
        assert (Arrays.deepToString(data.getParaDocFeatures()).equals("[[1], [1], [0]]"));
        assert (Arrays.toString(data.getWordsLists()[0].get(0)).equals("[in, out, out]"));
        assert (Arrays.toString(data.getWordsLists()[1].get(0)).equals(
                "[in, out, out, out, out, out, out, out, out, out, out, out, out, out, out, out, out, out, out, out, out, out, out, out, out, out, out, out, out, out, out, out, out, out, out]"
        ));
    }

    @Test
    public void testGetFeatureValue() throws Exception {
        List<CoreMap> processedParas = DocProcessor.processParas(doc);
        for (int i = 0; i < processedParas.size(); i++) {
            System.out.print("paragraph " + i + ": \n");
            for (RandomVariable rv : setting.getNbmnConfig().getAllParagraphFeatures()) {
                int v = ParaProcessor.getFeatureValue(rv, Arrays.asList(
                        doc.getParagraphs().get(i), processedParas.get(i)));
                System.out.print(rv.getName() + "=" + v + " ");
            }
            for (RandomVariable rv : setting.getNbmnConfig().getWordVarList()) {
                System.out.print(Arrays.toString(RVValues.getWords(rv, processedParas.get(i))));
            }
            System.out.println();
        }
        assert (ParaProcessor.getFeatureValue(setting.getNbmnConfig().getAllParagraphFeatures().get(0),
                Arrays.asList(doc.getParagraphs().get(0), processedParas.get(0))) == 3);

        assert (ParaProcessor.getFeatureValue(setting.getNbmnConfig().getAllParagraphFeatures().get(1),
                Arrays.asList(doc.getParagraphs().get(0), processedParas.get(0))) == 1);
    }


    @Test
    public void testGetFeaturesVals() throws Exception {
        List<CoreMap> processedParas = DocProcessor.processParas(doc);
        List<RandomVariable> rv = Lists.newArrayList(
                RVCreater.createRVFromAnnotation(CoreAnnotations.IsBoldAnnotation.class),
                RVCreater.createRVFromAnnotation(CoreAnnotations.StartsWithQuote.class));
        int[][] featureVals = DocProcessor.getFeaturesVals(rv, doc.getParagraphs(), processedParas);
        assert(featureVals[0][0] == 0);
        assert(featureVals[0][1] == 1);

        assert(featureVals[1][0] == 0);
        assert(featureVals[1][1] == 1);

    }


    @Test
    public void testIsParaObserved() throws Exception {
        assert(!DocProcessor.isParaObserved(doc.getParagraphs().get(0)));
    }


    @Test
    public void testCreateSections() throws Exception {

        String folder = "src/test/resources/analyzer/test10kDoc/";
        String documentId = "bb1b0ae115c0bca4262b5e1483346fca";

        String jsonString = Files.toString(new File(folder + documentId), Charset.defaultCharset());
        doc = JsonDeserializer.fromJson(jsonString);

        ModelRVSetting tocRVSetting = new TOCModelRVSetting(Arrays.asList(Category.NONE, Category.TOC_1, Category.TOC_2), null);
        List<List<List<CoreMap>>> sectionsList = DocProcessor.createSections(doc.getParagraphs(),
                doc.getParagraphs(), tocRVSetting.getNbmnConfig().getCategoryVar());
        List<List<CoreMap>> sections = sectionsList.get(0);

        for (int si = 0; si < sections.size(); si++) {
            System.out.println();
            System.out.println();
            System.out.println("Section " + si);
            System.out.println("________________________");
            for (CoreMap p : sections.get(si)) {
                System.out.println(p.get(CoreAnnotations.IndexInteger.class) + " " + p.getText());
            }
        }

        assert (sections.size() == 24);


    }
}