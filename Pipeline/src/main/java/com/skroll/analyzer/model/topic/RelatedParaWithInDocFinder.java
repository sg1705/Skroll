package com.skroll.analyzer.model.topic;

import cc.mallet.util.Maths;
import com.google.inject.Inject;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotations;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by wei2l on 1/18/2016.
 */
public class RelatedParaWithInDocFinder {

    @Inject
    private SkrollTopicModel stm;

    static HashMap<String, double[][]> topicProbabilities = new HashMap();
    //double[][] paraTopicProbs;

    public RelatedParaWithInDocFinder() {
//        paraTopicProbs = stm.infer(doc);
    }

//    /**
//     * This constructor also specifies a different topic model to use
//     * @param doc
//     * @param modelPath
//     */
//    public RelatedParaWithInDocFinder(Document doc, String modelPath){
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
     * Computes the distance from the given para to all the words in the second para
     * @param para
     * @return
     */
    protected Double[] computeDistances(CoreMap para, CoreMap para2, Document doc){
        double[][] paraTopicProbs = getTopicProbabilitiesForDoc(doc);
        List<Token> tokens = para2.getTokens();

        int i = para.get(CoreAnnotations.IndexInteger.class);
        Double[] distances =
                tokens.stream().map(token ->
                        Maths.jensenShannonDivergence(paraTopicProbs[i], stm.getTopicDistribution(token.getText())))
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

    private double[][] getTopicProbabilitiesForDoc(Document doc) {
        double[][] topicProb = topicProbabilities.get(doc.getId());
        if (topicProb == null) {
            topicProb = stm.infer(doc);
            topicProbabilities.put(doc.getId(), topicProb);
        }
        return topicProb;
    }


    /**
     * sort paras by their closeness, and return the sorted paras, and the distances to the given para.
     * The distances to the given para are in the order of the original unsorted para.
     * @param para
     * @return
     */
    public Map.Entry<List<CoreMap>, Double[]> closeParasAndDistances(Document doc, CoreMap para){
        Double[] distances = computeDistances(para, doc);
        return new AbstractMap.SimpleEntry<List<CoreMap>, Double[]> (
                doc.getParagraphs().stream()
                .sorted((p1,p2) -> Double.compare(
                        distances[p1.get(CoreAnnotations.IndexInteger.class)],
                        distances[p2.get(CoreAnnotations.IndexInteger.class)]))
                .collect(Collectors.toList()),
                distances);
    }
}
