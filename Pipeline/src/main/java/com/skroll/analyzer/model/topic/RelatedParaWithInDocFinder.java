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
public class RelatedParaWithInDocFinder {
    Document doc;
    SkrollTopicModel stm;
    double[][] paraTopicProbs;

    public RelatedParaWithInDocFinder(Document doc){
        this.doc = doc;
        this.stm = new SkrollTopicModel();
        paraTopicProbs = stm.infer(doc);
    }

    public RelatedParaWithInDocFinder(Document doc, String modelPath){
        this.doc = doc;
        this.stm = new SkrollTopicModel(modelPath);
        paraTopicProbs = stm.infer(doc);
    }


    Double[] computeDistances(CoreMap para){
        int i = para.get(CoreAnnotations.IndexInteger.class);
        Double[] distances =
                Arrays.stream(paraTopicProbs)
                        .map(probs -> Maths.jensenShannonDivergence(probs, paraTopicProbs[i]))
                        .toArray(Double[]::new);
        return distances;
    }

    public List<CoreMap> sortParasByDistance(CoreMap para){
        Double[] distances = computeDistances(para);
        return doc.getParagraphs().stream()
                .sorted((p1,p2) -> Double.compare(
                        distances[p1.get(CoreAnnotations.IndexInteger.class)],
                        distances[p2.get(CoreAnnotations.IndexInteger.class)]))
                .collect(Collectors.toList());
    }
}
