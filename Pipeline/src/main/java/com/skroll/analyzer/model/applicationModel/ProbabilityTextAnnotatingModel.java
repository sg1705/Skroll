package com.skroll.analyzer.model.applicationModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.skroll.analyzer.data.NBMNData;
import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.applicationModel.randomVariables.RVValues;
import com.skroll.analyzer.model.bn.NaiveBayesWithMultiNodes;
import com.skroll.analyzer.model.bn.config.NBMNConfig;
import com.skroll.analyzer.model.bn.inference.BNInference;
import com.skroll.analyzer.model.bn.node.DiscreteNode;
import com.skroll.analyzer.model.bn.node.MultiplexNode;
import com.skroll.analyzer.model.bn.node.NodeInferenceHelper;
import com.skroll.analyzer.model.bn.node.WordNode;
import com.skroll.analyzer.model.hmm.HiddenMarkovModel;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.document.annotation.TypesafeMap;
import com.skroll.util.Visualizer;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by wei2learn on 2/16/2015.
 */
public class ProbabilityTextAnnotatingModel extends DocumentAnnotatingModel {

    private static final int DEFAULT_NUM_ITERATIONS = 1;
    private static final double[] DEFAULT_ANNOTATING_THRESHOLD = new double[]{0, .999999, 0.99999};
    List<CoreMap> paragraphs;
    // todo: should probably store paragraphs, otherwise, need to recreate it everytime when model has new observations
    List<CoreMap> processedParagraphs = new ArrayList<>();
    NBMNData data;

    private int numIterations = DEFAULT_NUM_ITERATIONS;
    private double[] annotatingThreshold = DEFAULT_ANNOTATING_THRESHOLD;


    // indexed by feature number, paragraph number, category number
    double[][][] messagesToParagraphCategory; //From feature ij to paragraph i category
    // paragraph number, feature number, category number

    double[][][][] messagesToDocumentFeature; //From feature ij to documentFeature j
    // Paragraph number, feature number, category number, false/true

    double[][] paragraphCategoryBelief; // paragraph number, category number
    double[][][] documentFeatureBelief; // feature number, category number, false/true


    ProbabilityTextAnnotatingModel() {

    }

    public ProbabilityTextAnnotatingModel(NaiveBayesWithMultiNodes nbmn,
                                          HiddenMarkovModel hmm,
                                          Document doc,
                                          ModelRVSetting setting) {
        this(nbmn, hmm, doc.getParagraphs(),
                DocProcessor.processParas(doc), // both processed Paras and data should be already stored in cache, so computation here should be fast.
                DocProcessor.getParaDataFromDoc(doc, setting.getNbmnConfig()),
                setting);
        this.initialize();
    }

    // constructor used by lower TOC model.
    // todo: It might be better to have a separate class for lower TOC model, or make TOCModel more general to handle lower TOC model.
    // The only difficulty with making TOCModel more general is that probability annotation also need to be made more general,
    // which may create more complexity not really worth the effort needed at this point.
    public ProbabilityTextAnnotatingModel(NaiveBayesWithMultiNodes nbmn,
                                          HiddenMarkovModel hmm,
                                          List<CoreMap> paragraphs,
                                          List<CoreMap> processedParagraphs,
                                          NBMNData data,
                                          ModelRVSetting setting
    ) {
        super.nbmnConfig = setting.getNbmnConfig();
        super.wordType = setting.getWordType();
        super.wordFeatures = setting.getWordFeatures();
        super.modelRVSetting = setting;
        this.paragraphs = paragraphs;
        this.processedParagraphs = processedParagraphs;
        this.data = data;
        this.nbmnModel = nbmn;
        this.hmm = hmm;
        if (hmm != null) hmm.updateProbabilities();

//        this.initialize();
    }
    /**
     * Set belief based on the observed paragraphs.
     *
     * @param observedParagraphs The paragraphs that are observed.
     */

    public void updateBeliefWithObservation(List<CoreMap> observedParagraphs) {
        initialize();
    }

    // index of paragraph in the document
    int getParaIndex(int i) {
        return paragraphs.get(i).get(CoreAnnotations.IndexInteger.class);
    }

    //todo: should probably set inital belief based on observations if a document is reopened by the trainer or the same user again.
    void initialize() {

        int[][] allParaFeatures = data.getParaFeatures();

        List<List<RandomVariable>> docFeatures = nbmnConfig.getDocumentFeatureVarList();
        List<RandomVariable> paraDocFeatures = nbmnConfig.getFeatureExistsAtDocLevelVarList();
        RandomVariable paraCategory = nbmnConfig.getCategoryVar();

        int numParagraphs = processedParagraphs.size();
        // todo: assuming the values are binary sized. need to make this more general.
        messagesToDocumentFeature =
                new double[numParagraphs][docFeatures.size()][getParaCategory().getFeatureSize()][2];
        messagesToParagraphCategory = new double[numParagraphs][paraDocFeatures.size()][getParaCategory().getFeatureSize()];
        paragraphCategoryBelief = new double[numParagraphs][getParaCategory().getFeatureSize()];
        documentFeatureBelief = new double[docFeatures.size()][getParaCategory().getFeatureSize()][2];

        // compute initial beliefs
        List<List<DiscreteNode>> docFeatureNodes = nbmnModel.getDocumentFeatureNodes();
        for (int f = 0; f < paraDocFeatures.size(); f++)
            for (int c = 0; c < getParaCategory().getFeatureSize(); c++)
                documentFeatureBelief[f][c] = docFeatureNodes.get(f).get(c).getParameters().clone();

        List<DiscreteNode> fnl = nbmnModel.getFeatureNodes();
        DiscreteNode categoryNode = nbmnModel.getCategoryNode();

        // observation is only stored in the orignal paragraphs.
        // May consider to use a separate method to get a list of observed paragrpahs.
        for (int p = 0; p < processedParagraphs.size(); p++) {
            if (DocProcessor.isParaObserved(paragraphs.get(p))) {
                int observedVal = RVValues.getValue(paraCategory, paragraphs.get(p));

                for (int i = 0; i < paraCategory.getFeatureSize(); i++) {
                    if (i == observedVal) paragraphCategoryBelief[p][i] = 0;
                    else paragraphCategoryBelief[p][i] = Double.NEGATIVE_INFINITY;
                }
                continue;
            }

            int[] paraFeatures = allParaFeatures[getParaIndex(p)];

            nbmnModel.setWordsObservation(ParaProcessor.getWordsList(
                    nbmnConfig.getWordVarList(), processedParagraphs.get(p)));
            nbmnModel.setParaFeatureObservation(paraFeatures);
            paragraphCategoryBelief[p] = categoryNode.getParameters().clone();
            for (int i = 0; i < fnl.size(); i++) {
                if (paraFeatures[i] == -1) continue;

                double[] message = NodeInferenceHelper.sumOutOtherNodesWithObservation(fnl.get(i), categoryNode);
                for (int j = 0; j < message.length; j++)
                    paragraphCategoryBelief[p][j] += message[j];
            }

            // incorporate word information
            List<WordNode> wordNodes = nbmnModel.getWordNodes();
            for (WordNode node : wordNodes) {
                double[] message = NodeInferenceHelper.sumOutWordsWithObservation(node);
                for (int j = 0; j < message.length; j++)
                    paragraphCategoryBelief[p][j] += message[j];
            }

            BNInference.normalizeLog(paragraphCategoryBelief[p]);
        }

    }


    void passMessagesToParagraphCategories() {
        List<MultiplexNode> fedna = nbmnModel.getMultiNodes();

        int[][] paraFeatureValsExistAtDocLevel = data.getParaDocFeatures();
        for (int p = 0; p < paragraphCategoryBelief.length; p++) {

            int pi = getParaIndex(p);
            nbmnModel.setMultiNodesObservation(paraFeatureValsExistAtDocLevel[pi]);

            for (int f = 0; f < nbmnConfig.getFeatureExistsAtDocLevelVarList().size(); f++) {
                if (paraFeatureValsExistAtDocLevel[pi][f] == -1) continue;

                double[][] messageFromDocFeature = new double[documentFeatureBelief[f].length][];
                for (int i = 0; i < messageFromDocFeature.length; i++)
                    messageFromDocFeature[i] = documentFeatureBelief[f][i].clone();


//                for (int i = 0; i < messageFromDocFeature.length; i++) {
                for (int c = 0; c < getParaCategory().getFeatureSize(); c++) { //skip none at index 0

                    // should normalize messageFromDocFeature here if we normalize documentFeatureBelief.
//                    BNInference.normalizeLog(messagesToDocumentFeature[p][f][c]);

                    for (int b = 0; b <= 1; b++)
                        messageFromDocFeature[c][b] -= messagesToDocumentFeature[p][f][c][b];
//                    }
                }


//                BNInference.normalizeLog(messageFromDocFeature); //normalize to prevent overflow
//                // This normalization is independent of the normalization for other paras,
//                // which should be fine as long as the message contributions are normalized fairly for the para.

                for (double[] message : messageFromDocFeature) {
                    BNInference.normalizeLogProb(message);
                }

                messagesToParagraphCategory[p][f] = NodeInferenceHelper.updateMessageToSelectingNode(
                        fedna.get(f), messageFromDocFeature);

//                BNInference.normalizeLog(messagesToParagraphCategory[p][f]); //normalize to prevent overflow
                // This normalization is independent of the normalization for other paras,
                // which should be fine as long as the message contributions are normalized fairly for the para.

                for (int i = 0; i < paragraphCategoryBelief[p].length; i++) {
                    paragraphCategoryBelief[p][i] += messagesToParagraphCategory[p][f][i];
                }

//                BNInference.normalizeLog(messagesToParagraphCategory[p][f]);
            }
        }

//        BNInference.normalizeLog(paragraphCategoryBelief);
        for (double[] belief : paragraphCategoryBelief) {
            BNInference.normalizeLog(belief);
        }

    }

    void passMessageToDocumentFeatures() {
        int[][] allParaDocFeatures = data.getParaDocFeatures();
        List<List<DiscreteNode>> dfna = nbmnModel.getDocumentFeatureNodes();
        List<MultiplexNode> multiplexNodes = nbmnModel.getMultiNodes();
//        LogProbabilityDiscreteNode[] dfna = (LogProbabilityDiscreteNode[]) nbmnModel.getDocumentFeatureNodeArray();
//        LogProbabilityDiscreteNode[] fedna = (LogProbabilityDiscreteNode[]) nbmnModel.getFeatureExistAtDocLevelArray();

        // normalize to prevent overflow
        // normalize here instead of at the end of the method, since messageToDocumentFeature is linked with documentFeatureBelief
        //  and is used in the method passMessageToParagraphCategories next. If documentFeatureBelief is normalized
        //  at the end of the method, the probabilities would become inconsistent and cause incorrect calculations.
        for (double[][] beliefs : documentFeatureBelief) {
            for (double[] belief : beliefs) {
                BNInference.normalizeLog(belief);
            }
        }


        for (int p = 0; p < paragraphCategoryBelief.length; p++) {

            int pi = getParaIndex(p);
            nbmnModel.setMultiNodesObservation(allParaDocFeatures[pi]);

            for (int f = 0; f < nbmnConfig.getFeatureExistsAtDocLevelVarList().size(); f++) {
                if (allParaDocFeatures[pi][f] == -1) continue;
                double[] messageFromParaCategory = paragraphCategoryBelief[p].clone();
                for (int i = 0; i < messageFromParaCategory.length; i++)
                    messageFromParaCategory[i] -= messagesToParagraphCategory[p][f][i];

//                BNInference.normalizeLog(messageFromParaCategory); //normalize to prevent overflow
                messagesToDocumentFeature[p][f] = NodeInferenceHelper.updateMessagesFromSelectingNode(
                        multiplexNodes.get(f), messageFromParaCategory);
//                BNInference.normalizeLog(messagesToDocumentFeature[p][f]); //normalize to prevent overflow

//                messagesToDocumentFeature[p][f] =  NodeInferenceHelper.sumOutOtherNodesWithObservationAndMessage(
//                        multiplexNodes.get(f), nbmnModel.getCategoryNode(), messageFromParaCategory, dfna.get(f));
//                        fedna[f].sumOutOtherNodesWithObservationAndMessage(
//                                (LogProbabilityDiscreteNode) nbmnModel.getCategoryNode(),
//                                messageFromParaCategory, dfna[f]);
                for (int c = 0; c < getParaCategory().getFeatureSize(); c++) {
                    for (int i = 0; i < 2; i++) {
                        documentFeatureBelief[f][c][i] += messagesToDocumentFeature[p][f][c][i];
                    }
                }
            }
        }

//        BNInference.normalizeLog(documentFeatureBelief);

//        for (double[][] beliefs : documentFeatureBelief) {
//            for (double[] belief : beliefs) {
//                BNInference.normalizeLog(belief);
//            }
//        }
    }

    public void updateBeliefs() {
        int numIteration = 1;
        for (int i = 0; i < numIteration; i++) {
            passMessagesToParagraphCategories();
            passMessageToDocumentFeatures();
        }
        passMessagesToParagraphCategories();
    }


    void normalize(double[] probs) {
        double sum = 0;
        for (int i = 0; i < probs.length; i++) sum += probs[i];
        for (int i = 0; i < probs.length; i++) probs[i] /= sum;
    }

    void normalizeParagraphBelieves() {
        for (int i = 0; i < paragraphCategoryBelief.length; i++)
            normalize(paragraphCategoryBelief[i]);
    }

    public void setNumIterations(int numIterations) {
        this.numIterations = numIterations;
    }

    public void setParagraphs(List<CoreMap> paragraphs) {
        this.paragraphs = paragraphs;
    }

    public void setProcessedParagraphs(List<CoreMap> processedParagraphs) {
        this.processedParagraphs = processedParagraphs;
    }

    public void setAnnotatingThreshold(double[] annotatingThreshold) {
        this.annotatingThreshold = annotatingThreshold;
    }

    public void annotateParagraphs() {


        passMessagesToParagraphCategories();

        for (int i = 0; i < numIterations; i++) {
            passMessageToDocumentFeatures();
            passMessagesToParagraphCategories();
        }
        int numParagraphs = paragraphCategoryBelief.length;


        RandomVariable paraCategory = nbmnConfig.getCategoryVar();
        for (int p = 0; p < numParagraphs; p++) {
            CoreMap paragraph = paragraphs.get(p);
            if (DocProcessor.isParaObserved(paragraph)) continue; // skip observed paragraphs
            RVValues.clearValue(paraCategory, paragraph);
            if (paragraph.getTokens().size() == 0)
                continue;
            CoreMap processedPara = processedParagraphs.get(p);

            // using NB category as the prior prob to the input of HMM.
            // This means the HMM output state sequence gives the highest p(HMM observations | given NB observations)
            double[] logPrioProbs =
                    paragraphCategoryBelief[p].clone();

            // can check for NB classification to see if we want to keep checking the words.
            // check here to make it more efficient, or keep going to be more accurate.

            List<Token> tokens = processedPara.getTokens();

            //todo: a hack for TOC and DocType annotation. should implement HMM for TOC and annotate base on HMM result
            if (this.modelRVSetting instanceof TOCModelRVSetting || this.modelRVSetting instanceof DocTypeModelRVSetting) {
                annotateParagraph(paragraph, paraCategory, tokens, logPrioProbs);
                if (true) continue;
            }

            annotateTermsWithHMM(paragraph, processedPara, paraCategory, tokens, logPrioProbs);

        }

    }

    void annotateTermsWithHMM(CoreMap paragraph, CoreMap processedPara, RandomVariable paraCategory,
                              List<Token> tokens, double[] logPrioProbs) {

            List<String> words = DocumentHelper.getTokenString(tokens);

            String[] wordsArray = words.toArray(new String[words.size()]);

            int length = Math.min(hmm.size(), tokens.size());
            int[][] features = new int[length][wordFeatures.size()];
            for (int i = 0; i < length; i++) {
                for (int f = 0; f < wordFeatures.size(); f++) {
//                    features[i][f] = RVValues.getValue(wordFeatures.get(f), tokens.get(i));
//                    features[i][f] = DocumentAnnotatingHelper.getWordFeature(paragraph, tokens.get(i), wordFeatures.get(f));
                    features[i][f] = ParaProcessor.getWordFeatureValue(wordFeatures.get(f),
                            tokens.get(i), Arrays.asList(paragraph, processedPara));
                }
            }
            int[] states = hmm.mostLikelyStateSequence(wordsArray, features, logPrioProbs);

            //assume a definition paragraph always has the first word being a defined term.
            // can do this check after naive bayes to make it faster.
        if (states[0] == 0) return;

            List<Token> terms = new ArrayList<>();

            for (int i = 0; i < states.length; i++) {
                if (states[i] == 1) terms.add(tokens.get(i));
                else {
                    if (terms.size() > 0) {
                        RVValues.addTerms(paraCategory, paragraph, terms, 1);
//                        DocumentAnnotatingHelper.addParagraphTermAnnotation(paragraph, paraCategory, terms);
                        terms = new ArrayList<>();
                    }

                }
            }
            if (terms.size() > 0) {
                RVValues.addTerms(paraCategory, paragraph, terms, 1);
//                DocumentAnnotatingHelper.addParagraphTermAnnotation(paragraph, paraCategory, terms);
            }

    }

    void annotateParagraph(CoreMap paragraph, RandomVariable paraCategory,
                           List<Token> tokens, double[] logPrioProbs) {
        int maxIndex = BNInference.maxIndex(logPrioProbs);
        double[] paraProbs = logPrioProbs.clone();
        BNInference.convertLogBeliefToProb(paraProbs);
        if (paraProbs[maxIndex] > annotatingThreshold[maxIndex])
            RVValues.addTerms(paraCategory, paragraph, tokens, maxIndex);

    }


    // annotate paragraphs with probabilities for debugging/displaying purpose
    // todo: if we need to do this for more model types, should probably implement a hashmap annotating class
    public void annotateParaProbs(Class key) {
        for (int p = 0; p < processedParagraphs.size(); p++) {
            double[] paraProbs = paragraphCategoryBelief[p].clone();
            BNInference.convertLogBeliefToProb(paraProbs);
            processedParagraphs.get(p).set(key, new ArrayList(
                    Arrays.stream(paraProbs)
                            .boxed()
                            .collect(Collectors.toList())
            ));
        }
    }
    @JsonIgnore
    public double[][] getParagraphCategoryBelief() {
        return paragraphCategoryBelief;
    }

    public double[][] getParagraphCategoryProbabilities() {
        double[][] paraCatProbs = new double[paragraphCategoryBelief.length][nbmnConfig.getCategoryVar().getFeatureSize()];
        for (int i = 0; i < paraCatProbs.length; i++)
            paraCatProbs[i] = getParaCategoryProbabilities(i);
        return paraCatProbs;
    }

    public double[] getParaCategoryProbabilities(int pi) {
        double[] probs = paragraphCategoryBelief[pi].clone();
        BNInference.convertLogBeliefToProb(probs);
        return probs;
    }


    public double[][][] getDocumentFeatureProbabilities() {
//        double[][][] docFeatureProbs = new double[documentFeatureBelief.length][getParaCategory().getFeatureSize()-1][2];
//        double[][][] docFeatureProbs = documentFeatureBelief.clone();

        // clone only does a shallow copy. The following nested loop does a deep copy.
        double[][][] docFeatureProbs = new double[documentFeatureBelief.length][][];
        for (int i = 0; i < docFeatureProbs.length; i++) {
            docFeatureProbs[i] = new double[documentFeatureBelief[0].length][];
            for (int j = 0; j < docFeatureProbs[0].length; j++) {
                docFeatureProbs[i][j] = documentFeatureBelief[i][j].clone();
            }
        }


//        for (int i=0; i<documentFeatureBelief.length; i++)
//                docFeatureProbs[i] = documentFeatureBelief[i].clone();
        for (int i = 0; i < documentFeatureBelief.length; i++)
            BNInference.convertLogBeliefArrayToProb(docFeatureProbs[i]);
        return docFeatureProbs;
    }

    @JsonIgnore
    public double[][][] getDocumentFeatureBelief() {
        return documentFeatureBelief;
    }

    public NaiveBayesWithMultiNodes getLpnbfModel() {
        return nbmnModel;
    }

    @Override
    public String toString() {
        int[][] paraFeatureValsExistAtDocLevel = data.getParaDocFeatures();
        return "ProbabilityDocumentAnnotatingModel{" +
                "\nlpnbfModel=\n" + nbmnModel +
                ", \nparaFeatureValsExistAtDocLevel=\n" + Arrays.deepToString(paraFeatureValsExistAtDocLevel) +
                ", \nparagraphCategoryBelief=\n" + Arrays.deepToString(paragraphCategoryBelief) +
                ", \ndocumentFeatureBelief=\n" + Arrays.deepToString(documentFeatureBelief) +
                '}';
    }

    /**
     * Returns a string representation of the BNI for viewer.
     *
     * @param paraIndex
     * @return
     */
    public HashMap<String, HashMap<String, HashMap<String, Double>>> toVisualMap(int paraIndex) {
        //covert paraCategoryBelief
        HashMap<String, HashMap<String, HashMap<String, Double>>> map = new LinkedHashMap();
        HashMap<String, HashMap<String, Double>> applicationModelInfo = new LinkedHashMap();
        applicationModelInfo.put(this.nbmnConfig.getCategoryVar().getName(),
                Visualizer.toDoubleArrayToMap(this.getParaCategoryProbabilities(paraIndex)));
        for (int ii = 0; ii < documentFeatureBelief.length; ii++) {
            for (int jj = 0; jj < documentFeatureBelief[0].length; jj++) {
                applicationModelInfo.put(this.nbmnConfig.getDocumentFeatureVarList().get(ii).get(jj).getName(),
                        Visualizer.toDoubleArrayToMap(this.getDocumentFeatureProbabilities()[ii][jj]));
            }
        }

        map.put("applicationModelInfo", applicationModelInfo);
        return super.toVisualMap(map);

    }

    public List<Double> toParaCategoryDump() {
        List<AbstractMap.SimpleImmutableEntry<Integer, Double>> probs = new LinkedList<>();

        //covert paraCategoryBelief
        List<Double> listOfP = new ArrayList();
        for (int ii = 0; ii < this.getParagraphCategoryProbabilities().length; ii++) {
            listOfP.add(this.getParagraphCategoryProbabilities()[ii][1]);
        }
        return listOfP;
    }

    public Map<String, Double> getParaProbMap() {
        Map<String, Double> paraProbMap = new HashMap<>();
        double[][] probs = this.getParagraphCategoryProbabilities();

        for (int ii = 0; ii < probs.length; ii++) {
            int maxIndex = BNInference.maxIndex(probs[ii]);
            paraProbMap.put(paragraphs.get(ii).getId(), probs[ii][maxIndex]);
        }

        return paraProbMap;
    }

}


