package com.skroll.analyzer.model;

import com.skroll.analyzer.model.bn.ProbabilityNaiveBayesWithFeatureConditions;
import com.skroll.analyzer.model.bn.SimpleDataTuple;
import com.skroll.analyzer.model.bn.TrainingNaiveBayesWithFeatureConditions;
import com.skroll.analyzer.model.bn.node.ProbabilityDiscreteNode;
import com.skroll.analyzer.model.hmm.HiddenMarkovModel;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wei2learn on 2/16/2015.
 */
public class ProbabilityDocumentAnnotatingModel extends DocumentAnnotatingModel{

    ProbabilityNaiveBayesWithFeatureConditions pnbfModel;

    double[][][] messagesToParagraphCategory; //From feature ij to paragraph i category
    double[][][] messagesToDocumentFeature; //From feature ij to documentFeature j
    double[][] paragraphCategoryBelief;
    double[][] documentFeatureBelief;

    public ProbabilityDocumentAnnotatingModel(TrainingNaiveBayesWithFeatureConditions tnbf, Document doc) {

        pnbfModel = new ProbabilityNaiveBayesWithFeatureConditions(tnbf);

        int[] wordFeatureSizes = new int[WORD_FEATURES.size()]; // include state at the feature index 0.
        for (int i = 0; i < wordFeatureSizes.length; i++)
            wordFeatureSizes[i] = WORD_FEATURES.get(i).getFeatureSize();
        hmm = new HiddenMarkovModel(HMM_MODEL_LENGTH,
                RandomVariableType.WORD_IS_DEFINED_TERM.getFeatureSize(), wordFeatureSizes);

        this.doc = doc;
        int numParagraphs = doc.getParagraphs().size();
        messagesToDocumentFeature = new double[numParagraphs][DOCUMENT_FEATURES.size()][2];
        messagesToParagraphCategory = new double[numParagraphs][PARAGRAPH_FEATURES_EXIST_AT_DOC_LEVEL.size()][2];
        paragraphCategoryBelief = new double[numParagraphs][2];
        documentFeatureBelief = new double[DOCUMENT_FEATURES.size()][2];

        initialize();
        ProbabilityDiscreteNode[] documentFeatureNodeArray =
                (ProbabilityDiscreteNode[]) pnbfModel.getDocumentFeatureNodeArray();
        Arrays.fill(messagesToDocumentFeature,1);
        Arrays.fill(messagesToParagraphCategory,1);
        for (int f=0; f<PARAGRAPH_FEATURES_EXIST_AT_DOC_LEVEL.size(); f++)
            documentFeatureBelief[f] = documentFeatureNodeArray[f].getParameters().clone();

        ProbabilityDiscreteNode[] fna = (ProbabilityDiscreteNode[]) pnbfModel.getFeatureNodeArray();
        ProbabilityDiscreteNode categoryNode = (ProbabilityDiscreteNode)pnbfModel.getCategoryNode();
        int numCategories = categoryNode.getVariable().getFeatureSize();
        double[][] messagesFromFeatures = new double[fna.length][numCategories];

        List<CoreMap> paraList = doc.getParagraphs();
        for (int p=0; p<paraList.size(); p++){
            SimpleDataTuple tuple =
                    DocumentAnnotatingHelper.makeDataTuple(paraList.get(p), allParagraphFeatures, docFeatureValues);
            pnbfModel.setObservation(tuple);
            paragraphCategoryBelief[p] = categoryNode.getProbabilities().clone();
            for (int i=0; i<fna.length; i++){
                double[] message = fna[i].sumOutOtherNodesWithObservation( categoryNode);
                for (int j=0; j<message.length; j++)
                    paragraphCategoryBelief[p][j] *= message[j];
            }
        }

    }

    public ProbabilityDocumentAnnotatingModel(ProbabilityNaiveBayesWithFeatureConditions pnbfModel) {
        this.pnbfModel = pnbfModel;

        int[] wordFeatureSizes = new int[WORD_FEATURES.size()]; // include state at the feature index 0.
        for (int i = 0; i < wordFeatureSizes.length; i++)
            wordFeatureSizes[i] = WORD_FEATURES.get(i).getFeatureSize();
        hmm = new HiddenMarkovModel(HMM_MODEL_LENGTH,
                RandomVariableType.WORD_IS_DEFINED_TERM.getFeatureSize(), wordFeatureSizes);
    }


    void passMessagesToParagraphCategories(){
        List<CoreMap> paraList = doc.getParagraphs();
        ProbabilityDiscreteNode[] dfna = (ProbabilityDiscreteNode[]) pnbfModel.getDocumentFeatureNodeArray();
        ProbabilityDiscreteNode[] fedna = (ProbabilityDiscreteNode[]) pnbfModel.getFeatureExistAtDocLevelArray();

        for (int p=0; p<paraList.size(); p++){
            SimpleDataTuple tuple =
                    DocumentAnnotatingHelper.makeDataTuple(paraList.get(p), allParagraphFeatures, docFeatureValues);
            pnbfModel.setObservation(tuple );
            for (int f=0; f<PARAGRAPH_FEATURES_EXIST_AT_DOC_LEVEL.size(); f++){
                double[] messageFromDocFeature = documentFeatureBelief[f].clone();
                for (int i=0; i<messageFromDocFeature.length; i++) messageFromDocFeature[i] /= messagesToDocumentFeature[p][f][i];
                messagesToParagraphCategory[p][f] = fedna[f].sumOutOtherNodesWithObservationAndMessage(dfna[f],
                        messageFromDocFeature, (ProbabilityDiscreteNode)pnbfModel.getCategoryNode());
                for (int i=0; i<documentFeatureBelief[p].length; i++){
                    paragraphCategoryBelief[p][i] *= messagesToParagraphCategory[p][f][i];
                }
            }
        }
    }

    void passMessageToDocumentFeatures(){
        List<CoreMap> paraList = doc.getParagraphs();
        ProbabilityDiscreteNode[] dfna = (ProbabilityDiscreteNode[]) pnbfModel.getDocumentFeatureNodeArray();
        ProbabilityDiscreteNode[] fedna = (ProbabilityDiscreteNode[]) pnbfModel.getFeatureExistAtDocLevelArray();

        for (int p=0; p<paraList.size(); p++){
            SimpleDataTuple tuple =
                    DocumentAnnotatingHelper.makeDataTuple(paraList.get(p), allParagraphFeatures, docFeatureValues);
            pnbfModel.setObservation(tuple );
            for (int f=0; f<PARAGRAPH_FEATURES_EXIST_AT_DOC_LEVEL.size(); f++){
                double[] messageFromParaCategory = paragraphCategoryBelief[p].clone();
                for (int i=0; i<messageFromParaCategory.length; i++) messageFromParaCategory[i] /= messagesToParagraphCategory[p][f][i];
                messagesToDocumentFeature[p][f] = fedna[f].sumOutOtherNodesWithObservationAndMessage(
                        (ProbabilityDiscreteNode)pnbfModel.getCategoryNode(),
                        messageFromParaCategory, dfna[f]);
                for (int i=0; i<documentFeatureBelief[p].length; i++){
                    documentFeatureBelief[f][i] *= messagesToDocumentFeature[p][f][i];
                }
            }
        }
    }

    public void updateBelieves(){
        int numIteration =1;
        for (int i=0; i<numIteration; i++) {
            passMessagesToParagraphCategories();
            passMessageToDocumentFeatures();
        }
        passMessagesToParagraphCategories();
    }

    void normalize(double[] probs){
        double sum=0;
        for (int i=0; i<probs.length; i++) sum+= probs[i];
        for (int i=0; i<probs.length; i++) probs[i] /= sum;
    }

    void normalizeParagraphBelieves(){
        for (int i=0; i<doc.getParagraphs().size(); i++)
            normalize(paragraphCategoryBelief[i]);
    }

    public void annotateDocument(){
        int numParagraphs = doc.getParagraphs().size();

        for (int i=0; i<numParagraphs; i++){

        }

    }



}


