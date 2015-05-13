package com.skroll.analyzer.model;

import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotation;
import com.skroll.document.annotation.CoreAnnotations;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by wei on 5/10/15.
 */
public class DocProcessorTest {
    Document doc = new Document();
    ModelRVSetting setting = new DefModelRVSetting();


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

    }

    @Test
    public void testProcessParagraphs() throws Exception {
        List<CoreMap> processedParas = DocProcessor.processParagraphs(doc.getParagraphs());
        for (CoreMap para : processedParas) {
            ParaProcessor.print(para);
        }


    }

    @Test
    public void testGetDataFromDoc() throws Exception {

    }

    @Test
    public void testGetFeatureValue() throws Exception {
        List<CoreMap> processedParas = DocProcessor.processParagraphs(doc.getParagraphs());
        for (int i = 0; i < processedParas.size(); i++) {
            System.out.print("paragraph " + i + ": \n");
            for (RandomVariable rv : setting.getNbfcConfig().getAllParagraphFeatures()) {
                int v = DocProcessor.getFeatureValue(rv, Arrays.asList(
                        doc.getParagraphs().get(i), processedParas.get(i)));
                System.out.print(rv.getName() + "=" + v + " ");
            }
        }
        assert (DocProcessor.getFeatureValue(setting.getNbfcConfig().getAllParagraphFeatures().get(0),
                Arrays.asList(doc.getParagraphs().get(0), processedParas.get(0))) == 5);

        assert (DocProcessor.getFeatureValue(setting.getNbfcConfig().getAllParagraphFeatures().get(1),
                Arrays.asList(doc.getParagraphs().get(0), processedParas.get(0))) == 1);
    }

    @Test
    public void testGenerateDocumentFeatures() throws Exception {
        List<CoreMap> processedParas = DocProcessor.processParagraphs(doc.getParagraphs());
        int[] docFeatureVals =
                DocProcessor.generateDocumentFeatures(doc.getParagraphs(), processedParas, setting.getNbfcConfig());
        System.out.println(Arrays.toString(docFeatureVals));
    }

}