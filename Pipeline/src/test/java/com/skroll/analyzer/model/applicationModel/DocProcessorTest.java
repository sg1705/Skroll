package com.skroll.analyzer.model.applicationModel;

import com.skroll.analyzer.data.DocData;
import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.applicationModel.randomVariables.NumberTokensComputer;
import com.skroll.analyzer.model.applicationModel.randomVariables.RVCreater;
import com.skroll.analyzer.model.applicationModel.randomVariables.RVValues;
import com.skroll.analyzer.model.applicationModel.randomVariables.UniqueWordsComputer;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.annotation.CoreAnnotations;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wei on 5/10/15.
 */
public class DocProcessorTest {

    int maxNumWords = 20;
    static final RandomVariable DEFAULT_PARA_IS_DEF =
            RVCreater.createRVFromAnnotation(CoreAnnotations.IsDefinitionAnnotation.class);

    static final List<RandomVariable> DEFAULT_PARA_FEATURE_VARS = Arrays.asList(
            RVCreater.createDiscreteRVWithComputer(new NumberTokensComputer(), "numTokens")
    );
    static final List<RandomVariable> DEFAULT_PARA_DOC_FEATURE_VARS = Arrays.asList(
            RVCreater.createParagraphStartsWithRV(CoreAnnotations.InQuotesAnnotation.class)
    );

    static final List<RandomVariable> DEFAULT_WORD_VARS = Arrays.asList(
            RVCreater.createWordsRVWithComputer(new UniqueWordsComputer(), "uniqueWords")
    );

    Document doc = new Document();
    ModelRVSetting setting = new ModelRVSetting(
            DefModelRVSetting.WORD_IS_DEF, DefModelRVSetting.DEFAULT_WORD_FEATURES,
            DEFAULT_PARA_IS_DEF,
            DEFAULT_PARA_FEATURE_VARS, DEFAULT_PARA_DOC_FEATURE_VARS, DEFAULT_WORD_VARS
    );

    static String trainingFileName = "src/test/resources/analyzer/definedTermExtractionTraining/AMC Networks CA.html";

    @Before
    public void setUp() throws Exception {

        doc = TestHelper.setUpTestDoc();

        // for testing with real file
//        doc = TestHelper.makeTrainingDoc(new File(trainingFileName));
//        setting = new DefModelRVSetting();

    }

    @Test
    public void testProcessParagraphs() throws Exception {
        List<CoreMap> processedParas = DocProcessor.processParagraphs(doc.getParagraphs(), maxNumWords);
        for (CoreMap para : processedParas) {
            ParaProcessor.print(para);
        }


    }

    @Test
    public void testGetDataFromDoc() throws Exception {
        List<CoreMap> processedParas = DocProcessor.processParagraphs(doc.getParagraphs(), maxNumWords);
        DocData data = DocProcessor.getDataFromDoc(doc, processedParas, setting.getNbfcConfig());
        System.out.print(data);

    }

    @Test
    public void testGetFeatureValue() throws Exception {
        List<CoreMap> processedParas = DocProcessor.processParagraphs(doc.getParagraphs(), maxNumWords);
        for (int i = 0; i < processedParas.size(); i++) {
            System.out.print("paragraph " + i + ": \n");
            for (RandomVariable rv : setting.getNbfcConfig().getAllParagraphFeatures()) {
                int v = ParaProcessor.getFeatureValue(rv, Arrays.asList(
                        doc.getParagraphs().get(i), processedParas.get(i)));
                System.out.print(rv.getName() + "=" + v + " ");
            }
            for (RandomVariable rv : setting.getNbfcConfig().getWordVarList()) {
                System.out.print(Arrays.toString(RVValues.getWords(rv, processedParas.get(i))));
            }
            System.out.println();
        }
        assert (ParaProcessor.getFeatureValue(setting.getNbfcConfig().getAllParagraphFeatures().get(0),
                Arrays.asList(doc.getParagraphs().get(0), processedParas.get(0))) == 5);

        assert (ParaProcessor.getFeatureValue(setting.getNbfcConfig().getAllParagraphFeatures().get(1),
                Arrays.asList(doc.getParagraphs().get(0), processedParas.get(0))) == 1);
    }

    @Test
    public void testGenerateDocumentFeatures() throws Exception {
        List<CoreMap> processedParas = DocProcessor.processParagraphs(doc.getParagraphs(), maxNumWords);
        int[] docFeatureVals =
                DocProcessor.generateDocumentFeatures(doc.getParagraphs(), processedParas, setting.getNbfcConfig());
        System.out.println(Arrays.toString(docFeatureVals));
    }


    @Test
    public void testGetFeaturesVals() throws Exception {

    }

    @Test
    public void testIsParaObserved() throws Exception {

    }


    @Test
    public void testGenerateDocumentFeatures1() throws Exception {

    }
}