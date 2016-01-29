package com.skroll.analyzer.model.topic;

import cc.mallet.util.Maths;
import com.google.inject.Inject;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.annotation.CoreAnnotations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by wei2l on 1/18/2016.
 */
public class RelatedParaWithinDocFinder {

    @Inject
    private SkrollTopicModel stm;

    static HashMap<String, double[][]> topicProbabilities = new HashMap();
    //double[][] paraTopicProbs;

    public RelatedParaWithinDocFinder() {
//        paraTopicProbs = stm.infer(doc);
    }

//    /**
//     * This constructor also specifies a different topic model to use
//     * @param doc
//     * @param modelPath
//     */
//    public RelatedParaWithinDocFinder(Document doc, String modelPath){
//        this.doc = doc;
//        this.stm = new SkrollTopicModel(modelPath);
//        paraTopicProbs = stm.infer(doc);
//    }


    /**
     * Computes the distance from the given para to all the paras in the doc
     * @param para
     * @return
     */
    protected Double[] computeDistances(CoreMap para, Document doc){
        double[][] paraTopicProbs = getTopicProbabilitiesForDoc(doc);
        int i = para.get(CoreAnnotations.IndexInteger.class);
        Double[] distances =
                Arrays.stream(paraTopicProbs)
                        .map(probs -> Maths.jensenShannonDivergence(probs, paraTopicProbs[i]))
                        .toArray(Double[]::new);
        return distances;
    }

    /**
     * compute the distance from the text to all the para in the doc
     * @param text
     * @param doc
     * @return
     */
    protected Double[] computeDistances(String text, Document doc){
        double[][] paraTopicProbs = getTopicProbabilitiesForDoc(doc);
        double[] textTopics = stm.infer(text);
        Double[] distances =
                Arrays.stream(paraTopicProbs)
                        .map(probs -> Maths.jensenShannonDivergence(probs, textTopics))
                        .toArray(Double[]::new);
        return distances;
    }

    /**
     * sort the paras in the doc by their closeness to the given para
     * @param para input para to compare with
     * @return the sorted list of paras
     */
    public List<CoreMap> sortParasByDistance(Document doc, CoreMap para){
        int n = 5;
        if (doc.getParagraphs().size() < n) {
            n = doc.getParagraphs().size();
        }
        Double[] distances = computeDistances(para, doc);
        return doc.getParagraphs().stream()
                .sorted((p1,p2) -> Double.compare(
                        distances[p1.get(CoreAnnotations.IndexInteger.class)],
                        distances[p2.get(CoreAnnotations.IndexInteger.class)]))
                .collect(Collectors.toList())
                .subList(0, n);
    }

    /**
     * sort the paras in the doc by their closeness to the given text
     * @param doc
     * @param text
     * @return
     */
    public List<CoreMap> sortParasByDistance(Document doc, String text){
        int n = 5;
        if (doc.getParagraphs().size() < n) {
            n = doc.getParagraphs().size();
        }
        Double[] distances = computeDistances(text, doc);
        return doc.getParagraphs().stream()
                .sorted((p1,p2) -> Double.compare(
                        distances[p1.get(CoreAnnotations.IndexInteger.class)],
                        distances[p2.get(CoreAnnotations.IndexInteger.class)]))
                .collect(Collectors.toList())
                .subList(0, n);
    }

    private double[][] getTopicProbabilitiesForDoc(Document doc) {
        double[][] topicProb = topicProbabilities.get(doc.getId());
        if (topicProb == null) {
            topicProb = stm.infer(doc);
            topicProbabilities.put(doc.getId(), topicProb);
        }
        return topicProb;
    }
}
