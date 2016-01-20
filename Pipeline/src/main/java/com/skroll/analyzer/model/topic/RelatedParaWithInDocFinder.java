package com.skroll.analyzer.model.topic;

import cc.mallet.util.Maths;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.annotation.CoreAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by wei2l on 1/18/2016.
 */
public class RelatedParaWithinDocFinder {

    static SkrollTopicModel stm;
    static{
        stm = new SkrollTopicModel();
    }
    Document doc;
    double[][] paraTopicProbs;

    public RelatedParaWithinDocFinder(Document doc){
        this.doc = doc;
//        this.stm = new SkrollTopicModel();
        paraTopicProbs = stm.infer(doc);
    }

    /**
     * This constructor also specifies a different topic model to use
     * @param doc
     * @param modelPath
     */
    public RelatedParaWithinDocFinder(Document doc, String modelPath){
        this.doc = doc;
        this.stm = new SkrollTopicModel(modelPath);
        paraTopicProbs = stm.infer(doc);
    }


    /**
     * Computes the distance from the given para to all the paras in the doc
     * @param para
     * @return
     */
    Double[] computeDistances(CoreMap para){
        int i = para.get(CoreAnnotations.IndexInteger.class);
        Double[] distances =
                Arrays.stream(paraTopicProbs)
                        .map(probs -> Maths.jensenShannonDivergence(probs, paraTopicProbs[i]))
                        .toArray(Double[]::new);
        return distances;
    }

    /**
     * sort the paras in the doc by their closeness to the given para
     * @param para input para to compare with
     * @return the sorted list of paras
     */
    public List<CoreMap> sortParasByDistance(CoreMap para){
        Double[] distances = computeDistances(para);
        return doc.getParagraphs().stream()
                .sorted((p1,p2) -> Double.compare(
                        distances[p1.get(CoreAnnotations.IndexInteger.class)],
                        distances[p2.get(CoreAnnotations.IndexInteger.class)]))
                .collect(Collectors.toList());
    }
}
