package com.skroll.analyzer.model;

import com.skroll.analyzer.data.DocData;
import com.skroll.analyzer.model.bn.config.NBFCConfig;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wei on 5/10/15.
 */
public class DocProcessor {
    static List<CoreMap> processParagraphs(List<CoreMap> paras) {
        List<CoreMap> processedParas = new ArrayList<>();
        for (CoreMap para : paras) processedParas.add(ParaProcessor.processParagraph(para));
        return processedParas;
    }

    static DocData getDataFromDoc(Document doc, NBFCConfig config) {
        DocData data = new DocData();

        return data;
    }


    // check through the CoreMaps(usually paragraphs) for feature value.
    // take the maximum of the values from different CoreMap, because -1 or 0 indicates no value present in the Coremap
    static int getFeatureValue(RandomVariable v, List<CoreMap> mList) {
        int result = -1;
        for (CoreMap m : mList) {
            int value = RVValues.getValue(v, m);
            if (value > result) result = value;
        }
        return result;
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
                    docFeatureValues[f] &= (getFeatureValue(nbfcConfig.getFeatureExistsAtDocLevelVarList().get(f),
                            Arrays.asList(originalParas.get(p), paragraph)));
            }
        }
        return docFeatureValues;
    }

}
