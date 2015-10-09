package com.skroll.analyzer.model.applicationModel;

import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.bn.NBInferenceHelper;
import com.skroll.analyzer.model.bn.NaiveBayesWithMultiNodes;
import com.skroll.analyzer.model.bn.config.NBMNConfig;
import com.skroll.analyzer.model.hmm.HiddenMarkovModel;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.util.Visualizer;

import java.util.*;

/**
 * Created by wei2learn on 2/16/2015.
 */
public class ProbabilityDocumentAnnotatingModel extends ProbabilityTextAnnotatingModel {

    static final int SEC_NUM_ITERATION = 5;
    static double[] SEC_ANNOTATING_THRESHOLD = {0, .99999};
    Document doc;
    NaiveBayesWithMultiNodes secNbmn = null;
    HiddenMarkovModel secHmm = null;
    ProbabilityTextAnnotatingModel secModel = null;





    public ProbabilityDocumentAnnotatingModel(int id,
                                              NaiveBayesWithMultiNodes tnbm,
                                              HiddenMarkovModel hmm,
                                              NaiveBayesWithMultiNodes secTnbm,
                                              HiddenMarkovModel secHmm,
                                              Document doc, ModelRVSetting setting) {
        this(id,
                tnbm,
                hmm,
                secTnbm,
                secHmm,
                doc,
                setting,
                setting.getWordType(),
                setting.getWordFeatures(),
                setting.getNbmnConfig(),
                setting.getLowLevelNbmnConfig()
        );
    }

    public ProbabilityDocumentAnnotatingModel(int id,
                                              NaiveBayesWithMultiNodes tnbm,
                                              HiddenMarkovModel hmm,
                                              NaiveBayesWithMultiNodes secTnbm,
                                              HiddenMarkovModel secHmm,
                                              Document doc,
                                              ModelRVSetting setting,
                                              RandomVariable wordType,
                                              List<RandomVariable> wordFeatures,
                                              NBMNConfig nbmnConfig,
                                              NBMNConfig secNbmnConfig
    ) {
        super.nbmnConfig = nbmnConfig;
        super.wordType = wordType;
        super.wordFeatures = wordFeatures;
        super.modelRVSetting = setting;
        super.id = id;
        this.doc = doc;
        this.paragraphs = doc.getParagraphs();
        this.nbmnModel = NBInferenceHelper.createLogProbNBMN(tnbm);
        this.hmm = hmm;
        hmm.updateProbabilities();

        preprocessData();

        // create child model if needed.
        if (secNbmnConfig != null) {
            this.secNbmn = NBInferenceHelper.createLogProbNBMN(secTnbm);
            this.secHmm = secHmm;
            secHmm.updateProbabilities();
            ModelRVSetting lowerTOCSetting = new TOCModelRVSetting(modelRVSetting.getLowLevelCategoryIds(), null);
            this.secModel = new ProbabilityTextAnnotatingModel(
                    secNbmn,
                    secHmm,
                    null, // paragraphs will be set later once the sections are determined.
                    null,
                    data,
                    lowerTOCSetting,
                    wordType,
                    wordFeatures,
                    lowerTOCSetting.getNbmnConfig()
            );
        }

        super.initialize();
    }

    void preprocessData() {
        processedParagraphs = DocProcessor.processParas(doc);
        modelRVSetting.postProcessFunctions
                .stream()
                .forEach(f -> f.apply(doc.getParagraphs(), processedParagraphs));
        data = DocProcessor.getParaDataFromDoc(doc, nbmnConfig);
    }

    @Override
    public void annotateParagraphs() {

        super.annotateParagraphs();
        if (modelRVSetting.getLowLevelCategoryIds() == null) return;

        List<List<List<CoreMap>>> sectionsList = DocProcessor.createSections(paragraphs, processedParagraphs, getParaCategory());
        List<List<CoreMap>> sections = sectionsList.get(0);
        List<List<CoreMap>> processedSections = sectionsList.get(1);

        // todo: should probably call ModelRVSetting constructor to make it more general.
        ModelRVSetting lowerTOCSetting = new TOCModelRVSetting(modelRVSetting.getLowLevelCategoryIds(), null);
        for (int i = 0; i < sections.size(); i++) {
//            ProbabilityTextAnnotatingModel secModel = new ProbabilityTextAnnotatingModel(
//                    secNbmn,
//                    secHmm,
//                    sections.get(i),
//                    processedSections.get(i),
//                    data,
//                    lowerTOCSetting,
//                    wordType,
//                    wordFeatures,
//                    lowerTOCSetting.getNbmnConfig()
//            );
            secModel.setParagraphs(sections.get(i));
            secModel.setProcessedParagraphs(processedSections.get(i));
            secModel.setNumIterations(4);
            secModel.setAnnotatingThreshold(SEC_ANNOTATING_THRESHOLD);
            secModel.initialize();
            secModel.annotateParagraphs();
        }
    }

    /**
     * Returns a string representation of the BNI for viewer.
     *
     * @param paraIndex
     * @return
     */
    @Override
    public HashMap<String, HashMap<String, HashMap<String, Double>>> toVisualMap(int paraIndex) {
        //covert paraCategoryBelief
        HashMap<String, HashMap<String, HashMap<String, Double>>> map = new LinkedHashMap();
        HashMap<String, HashMap<String, Double>> applicationModelInfo = new LinkedHashMap();
        CoreMap para = processedParagraphs.get(paraIndex);
        applicationModelInfo.put("doc level model " + this.nbmnConfig.getCategoryVar().getName(),
                Visualizer.doubleListToMap(para.get(CoreAnnotations.TOCParaProbsDocLevel.class)));

        List<Double> probs = para.get(CoreAnnotations.TOCParaProbsSecLevel.class);
        if (probs != null)
            applicationModelInfo.put("sec level model " + this.nbmnConfig.getCategoryVar().getName(),
                    Visualizer.doubleListToMap(probs));
        for (int ii = 0; ii < documentFeatureBelief.length; ii++) {
            for (int jj = 0; jj < documentFeatureBelief[0].length; jj++) {
                applicationModelInfo.put(this.nbmnConfig.getDocumentFeatureVarList().get(ii).get(jj).getName(),
                        Visualizer.toDoubleArrayToMap(this.getDocumentFeatureProbabilities()[ii][jj]));
            }
        }

        map.put("applicationModelInfo", applicationModelInfo);
        return super.toVisualMap(map);

    }

}


