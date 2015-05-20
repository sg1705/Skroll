package com.skroll.analyzer.model;

import com.skroll.analyzer.data.DocData;
import com.skroll.analyzer.model.bn.SimpleDataTuple;
import com.skroll.analyzer.model.bn.config.NBFCConfig;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.annotation.CoreAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wei on 5/10/15.
 */
public class DocProcessor {
    static List<CoreMap> processParagraphs(List<CoreMap> paras, int numWordsToUse) {
        List<CoreMap> processedParas = new ArrayList<>();
        for (CoreMap para : paras) processedParas.add(ParaProcessor.processParagraph(para, numWordsToUse));
        return processedParas;
    }

    public static int[][] getFeaturesVals(List<RandomVariable> rvs,
                                          List<CoreMap> originalParas, List<CoreMap> processedParas) {
        int nP = originalParas.get(0).size();
        int[][] features = new int[nP][rvs.size()];
        for (int p = 0; p < nP; p++) {
            List<CoreMap> paras = Arrays.asList(originalParas.get(p), processedParas.get(p));
            for (int f = 0; f < rvs.size(); f++) {
                features[p][f] = ParaProcessor.getFeatureValue(rvs.get(f), paras);
            }
        }
        return features;
    }

    static boolean isParaObserved(CoreMap para) {
        Boolean isObserved = para.get(CoreAnnotations.IsUserObservationAnnotation.class);
        if (isObserved == null) isObserved = false;
        return isObserved;
    }


    // process the document to make data tuples stored in DocData for models to use
    static DocData getDataFromDoc(Document doc, List<CoreMap> processedParas, NBFCConfig config) {
        DocData data = new DocData(doc, config);
        List<RandomVariable> features = config.getAllParagraphFeatures();

        List<CoreMap> originalParas = doc.getParagraphs();
//        List<CoreMap> processedParas = processParagraphs(doc.getParagraphs());

        SimpleDataTuple[] tuples = new SimpleDataTuple[originalParas.size()];

        int[] docFeatureVals = generateDocumentFeatures(originalParas, processedParas, config);

        int numVals = features.size() + docFeatureVals.length + 1;
        for (int p = 0; p < originalParas.size(); p++) {
            List<CoreMap> paras = Arrays.asList(originalParas.get(p), processedParas.get(p));
            int[] vals = new int[numVals];
            int iVal = 0;
            vals[iVal++] = ParaProcessor.getFeatureValue(config.getCategoryVar(),
                    Arrays.asList(originalParas.get(p)));

            for (int i = 0; i < features.size(); i++) {
                vals[iVal++] = ParaProcessor.getFeatureValue(features.get(i), paras);
            }

            for (int i = 0; i < docFeatureVals.length; i++) vals[iVal++] = docFeatureVals[i];

            List<String[]> wordsList = new ArrayList<>();
            for (RandomVariable rv : config.getWordVarList()) {
                //RVValues.getWords(rv, processedParas.get(p)).toArray(new String[]);
                wordsList.add(RVValues.getWords(rv, processedParas.get(p)));
            }
            tuples[p] = new SimpleDataTuple(wordsList, vals);
        }
        data.setDocFeatureValues(docFeatureVals);
        data.setTuples(tuples);
        return data;
    }


    //todo: we're check both processedParagraphs and originalParas. But should probably combine the information and just check one.
    // this method is assuming all the doc features are binary
    public static int[] generateDocumentFeatures(List<CoreMap> originalParas, List<CoreMap> processedParagraphs,
                                                 NBFCConfig nbfcConfig) {

        int[] docFeatureValues = new int[nbfcConfig.getDocumentFeatureVarList().size()];

        Arrays.fill(docFeatureValues, 1);
        //for( CoreMap paragraph : processedParagraphs) {
        for (int p = 0; p < processedParagraphs.size(); p++) {
            CoreMap paragraph = processedParagraphs.get(p);
            for (int f = 0; f < docFeatureValues.length; f++) {
                if (RVValues.getValue(nbfcConfig.getCategoryVar(), originalParas.get(p)) == 1)
                    docFeatureValues[f] &= (ParaProcessor.getFeatureValue(
                            nbfcConfig.getFeatureExistsAtDocLevelVarList().get(f),
                            Arrays.asList(originalParas.get(p), paragraph)));
            }
        }
        return docFeatureValues;
    }

}
