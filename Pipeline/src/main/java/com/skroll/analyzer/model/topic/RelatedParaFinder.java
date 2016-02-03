package com.skroll.analyzer.model.topic;

import cc.mallet.util.Maths;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotations;
import com.sun.research.ws.wadl.Doc;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Created by wei2l on 1/18/2016.
 */
public class RelatedParaFinder {

    @Inject
    private SkrollTopicModel stm;

    static HashMap<String, double[][]> topicProbabilities = new HashMap();
    //double[][] paraTopicProbs;

    // cache stroing para topic distributions for doc represented by doc ID
   static Cache<String, double[][]> paraTopicProbCache = CacheBuilder.newBuilder()
            .maximumSize(2)
            .build();

    public RelatedParaFinder() {
//        paraTopicProbs = stm.infer(doc);
    }

//    /**
//     * This constructor also specifies a different topic model to use
//     * @param doc
//     * @param modelPath
//     */
//    public RelatedParaFinder(Document doc, String modelPath){
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

    /**
     * sort paras by their closeness, and return the sorted paras, and the distances to the given text.
     * The distances to the given para are in the order of the original unsorted para.
     * @param text
     * @return
     */
    public Map.Entry<List<CoreMap>, Double[]> closeParasAndDistances(Document doc, String text){
        Double[] distances = computeDistances(text, doc);
        return new AbstractMap.SimpleEntry<List<CoreMap>, Double[]> (
                doc.getParagraphs().stream()
                .sorted((p1,p2) -> Double.compare(
                        distances[p1.get(CoreAnnotations.IndexInteger.class)],
                        distances[p2.get(CoreAnnotations.IndexInteger.class)]))
                .collect(Collectors.toList()),
                distances);
    }

    /**
     * sort paras by their closeness, and return the top n paras, and the word distances to the given para.
     * The distances to the given para are in the order of the original unsorted para.
     * @param inputPara
     * @return
     */
    public Map.Entry<List<CoreMap>, List<Double[]>> closeParasWithWordDistances(Document doc, CoreMap inputPara, int n){
        n = Math.min(n, doc.getParagraphs().size());
        List<CoreMap> resultParas = sortParasByDistance(doc, inputPara).subList(0,n);
        List<Double[]> distances = resultParas.stream()
                .map(para -> computeDistances(inputPara, para, doc))
                .collect(Collectors.toList());
        return new AbstractMap.SimpleEntry(resultParas, distances);
    }

//    private double[][] getTopicProbabilitiesForDoc(Document doc) {
//        double[][] topicProb = topicProbabilities.get(doc.getId());
//        if (topicProb == null) {
//            topicProb = stm.infer(doc);
//            topicProbabilities.put(doc.getId(), topicProb);
//        }
//        return topicProb;
//    }

    private double[][] getTopicProbabilitiesForDoc(Document doc){
        try {
            return paraTopicProbCache.get(doc.getId(), new Callable<double[][]>(){
                @Override
                public double[][] call() throws Exception {
                    return stm.infer(doc);
                }
            });
        } catch (ExecutionException e){
            e.printStackTrace(System.err);
        }
        return null;
    }
}
