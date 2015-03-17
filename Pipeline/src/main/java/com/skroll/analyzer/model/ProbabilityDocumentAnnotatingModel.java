package com.skroll.analyzer.model;

import com.skroll.analyzer.model.bn.ProbabilityNaiveBayesWithFeatureConditions;
import com.skroll.analyzer.model.bn.SimpleDataTuple;
import com.skroll.analyzer.model.bn.TrainingNaiveBayesWithFeatureConditions;
import com.skroll.analyzer.model.bn.node.ProbabilityDiscreteNode;
import com.skroll.analyzer.model.bn.node.ProbabilityWordNode;
import com.skroll.analyzer.model.hmm.HiddenMarkovModel;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wei2learn on 2/16/2015.
 */
public class ProbabilityDocumentAnnotatingModel extends DocumentAnnotatingModel{

    ProbabilityNaiveBayesWithFeatureConditions pnbfModel;

    int [][] paraFeatureValsExistAtDocLevel;
//    int[] docFeatureValues;

    double[][][] messagesToParagraphCategory; //From feature ij to paragraph i category
    double[][][] messagesToDocumentFeature; //From feature ij to documentFeature j
    double[][] paragraphCategoryBelief;
    double[][] documentFeatureBelief;

    public ProbabilityDocumentAnnotatingModel(TrainingNaiveBayesWithFeatureConditions tnbf, Document doc) {

        super();
        pnbfModel = new ProbabilityNaiveBayesWithFeatureConditions(tnbf);

        int[] wordFeatureSizes = new int[WORD_FEATURES.size()]; // include state at the feature index 0.
        for (int i = 0; i < wordFeatureSizes.length; i++)
            wordFeatureSizes[i] = WORD_FEATURES.get(i).getFeatureSize();
        hmm = new HiddenMarkovModel(HMM_MODEL_LENGTH,
                RandomVariableType.WORD_IS_DEFINED_TERM.getFeatureSize(), wordFeatureSizes);

         initialize(doc);
    }

    void initialize(Document doc){
        List<CoreMap> paragraphs = new ArrayList<>();

        // process raw input paragraph to be used for model
        for( CoreMap paragraph : doc.getParagraphs())
            paragraphs.add(DocumentAnnotatingHelper.processParagraph(paragraph));

//        docFeatureValues = DocumentAnnotatingHelper.generateDocumentFeatures(paragraphs,DOCUMENT_FEATURES,
//                PARAGRAPH_FEATURES_EXIST_AT_DOC_LEVEL);

        // store feature values for later probability updates
        paraFeatureValsExistAtDocLevel = new int[paragraphs.size()][PARAGRAPH_FEATURES_EXIST_AT_DOC_LEVEL.size()];
        for (int p=0; p<paragraphs.size();p++){
            for (int f=0; f<PARAGRAPH_FEATURES_EXIST_AT_DOC_LEVEL.size(); f++) {
                paraFeatureValsExistAtDocLevel[p][f] = DocumentAnnotatingHelper.getParagraphFeature(
                        paragraphs.get(p), PARAGRAPH_FEATURES_EXIST_AT_DOC_LEVEL.get(f));
            }
        }

        computeInitalBelieves(paragraphs);
    }

    void computeInitalBelieves(List<CoreMap> paragraphs){

        int numParagraphs = paragraphs.size();
        // todo: assuming the values are binary sized. need to make this more general.
        messagesToDocumentFeature = new double[numParagraphs][DOCUMENT_FEATURES.size()][2];
        messagesToParagraphCategory = new double[numParagraphs][PARAGRAPH_FEATURES_EXIST_AT_DOC_LEVEL.size()][2];
        paragraphCategoryBelief = new double[numParagraphs][2];
        documentFeatureBelief = new double[DOCUMENT_FEATURES.size()][2];

        // compute initial believes
        ProbabilityDiscreteNode[] documentFeatureNodeArray =
                (ProbabilityDiscreteNode[]) pnbfModel.getDocumentFeatureNodeArray();

        for (int p=0; p<numParagraphs; p++)
            for (int f=0; f<DOCUMENT_FEATURES.size(); f++){
                Arrays.fill(messagesToDocumentFeature[p][f], 1);
                Arrays.fill(messagesToParagraphCategory[p][f], 1);
            }
        for (int f=0; f<PARAGRAPH_FEATURES_EXIST_AT_DOC_LEVEL.size(); f++)
            documentFeatureBelief[f] = documentFeatureNodeArray[f].getParameters().clone();

        ProbabilityDiscreteNode[] fna = (ProbabilityDiscreteNode[]) pnbfModel.getFeatureNodeArray();
        ProbabilityDiscreteNode categoryNode = (ProbabilityDiscreteNode)pnbfModel.getCategoryNode();
        for (int p=0; p<paragraphs.size(); p++){
            SimpleDataTuple tuple = DocumentAnnotatingHelper.makeDataTupleWithOnlyFeaturesObserved(
                    paragraphs.get(p), allParagraphFeatures, DOCUMENT_FEATURES.size());
            //pnbfModel.setObservationOfFeatureNodesExistAtDocLevel(paraFeatureValsExistAtDocLevel[p]);
            pnbfModel.setObservation(tuple);
            paragraphCategoryBelief[p] = categoryNode.getProbabilities().clone();
            for (int i=0; i<fna.length; i++){
                double[] message = fna[i].sumOutOtherNodesWithObservation( categoryNode);
                for (int j=0; j<message.length; j++)
                    paragraphCategoryBelief[p][j] *= message[j];
            }

            // incorporate word information
            ProbabilityWordNode wordNode = (ProbabilityWordNode)pnbfModel.getWordNode();
            double[] message = wordNode.sumOutWordsWithObservation();
            for (int j=0; j<message.length; j++)
                paragraphCategoryBelief[p][j] *= message[j];
        }

    }


    void passMessagesToParagraphCategories(){
        ProbabilityDiscreteNode[] dfna = (ProbabilityDiscreteNode[]) pnbfModel.getDocumentFeatureNodeArray();
        ProbabilityDiscreteNode[] fedna = (ProbabilityDiscreteNode[]) pnbfModel.getFeatureExistAtDocLevelArray();

        for (int p=0; p<paragraphCategoryBelief.length; p++){
//            SimpleDataTuple tuple =
//                    DocumentAnnotatingHelper.makeDataTuple(paragraphs.get(p), allParagraphFeatures, docFeatureValues);
//            SimpleDataTuple tuple = new SimpleDataTuple(new String[0], )
//            pnbfModel.setObservation(tuple );
            pnbfModel.setObservationOfFeatureNodesExistAtDocLevel(paraFeatureValsExistAtDocLevel[p]);

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
        ProbabilityDiscreteNode[] dfna = (ProbabilityDiscreteNode[]) pnbfModel.getDocumentFeatureNodeArray();
        ProbabilityDiscreteNode[] fedna = (ProbabilityDiscreteNode[]) pnbfModel.getFeatureExistAtDocLevelArray();

        for (int p=0; p<paragraphCategoryBelief.length; p++){
//            SimpleDataTuple tuple =
//                    DocumentAnnotatingHelper.makeDataTuple(paragraphs.get(p), allParagraphFeatures, docFeatureValues);
//            pnbfModel.setObservation(tuple );
            pnbfModel.setObservationOfFeatureNodesExistAtDocLevel(paraFeatureValsExistAtDocLevel[p]);

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
        for (int i=0; i<paragraphCategoryBelief.length; i++)
            normalize(paragraphCategoryBelief[i]);
    }

    public void annotateDocument(){
        int numParagraphs = paragraphCategoryBelief.length;

        for (int i=0; i<numParagraphs; i++){

        }

    }

    public double[][] getParagraphCategoryBelief() {
        return paragraphCategoryBelief;
    }

    public double[][] getDocumentFeatureBelief() {
        return documentFeatureBelief;
    }

    @Override
    public String toString() {
        return "ProbabilityDocumentAnnotatingModel{" +
                "\npnbfModel=\n" + pnbfModel +
                ", \nparaFeatureValsExistAtDocLevel=\n" + Arrays.deepToString(paraFeatureValsExistAtDocLevel) +
                ", \nparagraphCategoryBelief=\n" + Arrays.deepToString(paragraphCategoryBelief) +
                ", \ndocumentFeatureBelief=\n" + Arrays.deepToString(documentFeatureBelief) +
                '}';
    }
}


