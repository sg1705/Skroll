package com.skroll.analyzer.model;

import com.skroll.analyzer.data.DocData;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotations;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wei on 5/10/15.
 */
public class DocProcessorTest {

    int maxNumWords = 20;
    static final RandomVariable DEFAULT_PARA_IS_DEF =
            RVCreater.createRVFromAnnotation(CoreAnnotations.IsTOCAnnotation.class);

    static final List<RandomVariable> DEFAULT_PARA_FEATURE_VARS = Arrays.asList(
            RVCreater.createDiscreteRVWithComputer(new NumberTokensComputer(), "numTokens")
    );
    static final List<RandomVariable> DEFAULT_PARA_DOC_FEATURE_VARS = Arrays.asList(
            RVCreater.createParagraphStartsWithRV(CoreAnnotations.InQuotesAnnotation.class)
    );

    static final List<RandomVariable> DEFAULT_DOC_FEATURE_VARS = Arrays.asList(
            new RandomVariable(2, "tocs in quotes")
    );
    static final List<RandomVariable> DEFAULT_WORD_VARS = Arrays.asList(
            RVCreater.createWordsRVWithComputer(new UniqueWordsComputer(), "uniqueWords")
    );

    Document doc = new Document();
    ModelRVSetting setting = new ModelRVSetting(
            DefModelRVSetting.WORD_IS_DEF, DefModelRVSetting.DEFAULT_WORD_FEATURES,
            DEFAULT_PARA_IS_DEF,
            DEFAULT_PARA_FEATURE_VARS, DEFAULT_PARA_DOC_FEATURE_VARS, DEFAULT_DOC_FEATURE_VARS, DEFAULT_WORD_VARS
    );

    static String trainingFileName = "src/test/resources/analyzer/definedTermExtractionTraining/AMC Networks CA.html";

    @Before
    public void setUp() throws Exception {
        List<CoreMap> paraList = new ArrayList<>();

        CoreMap para = new CoreMap();
        List<String> strings = Arrays.asList("\"", "in", "\"", "out", "out");
        List<Token> tokens = DocumentHelper.createTokens(strings);
        para.set(CoreAnnotations.TokenAnnotation.class, tokens);
        para.set(CoreAnnotations.IsDefinitionAnnotation.class, Boolean.TRUE);

        paraList.add(para);


        para = new CoreMap();
        strings = Arrays.asList("\"", "in", "\"", "out", "out");
        tokens = DocumentHelper.createTokens(strings);
        para.set(CoreAnnotations.TokenAnnotation.class, tokens);
        para.set(CoreAnnotations.IsDefinitionAnnotation.class, Boolean.TRUE);
        paraList.add(para);

        doc.setParagraphs(paraList);

        // for testing with real file
        doc = TestHelper.makeTrainingDoc(new File(trainingFileName));
        //doc = TestHelper.makeDoc( new File(trainingFileName));
        setting = new DefModelRVSetting();

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
                System.out.print(RVValues.getWords(rv, processedParas.get(i)));
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

}