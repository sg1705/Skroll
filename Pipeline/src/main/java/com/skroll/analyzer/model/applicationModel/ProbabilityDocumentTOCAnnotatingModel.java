package com.skroll.analyzer.model.applicationModel;

import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.applicationModel.randomVariables.RVValues;
import com.skroll.analyzer.model.bn.NBInferenceHelper;
import com.skroll.analyzer.model.bn.NaiveBayesWithMultiNodes;
import com.skroll.analyzer.model.bn.config.NBMNConfig;
import com.skroll.analyzer.model.bn.inference.BNInference;
import com.skroll.analyzer.model.hmm.HiddenMarkovModel;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.util.Visualizer;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Probability model for annotating the Document headings.
 * todo: should probably make it more general to handle lower level TOC model recursively. But it may not worth the effort needed at this point.
 * Created by wei2learn on 2/16/2015.
 */
public class ProbabilityDocumentTOCAnnotatingModel extends ProbabilityTextAnnotatingModel {

    static final int SEC_NUM_ITERATION = 40;
    static double[] SEC_ANNOTATING_THRESHOLD = {0, 0.8};
    Document doc;
    NaiveBayesWithMultiNodes secNbmn = null;
    HiddenMarkovModel secHmm = null;
    ProbabilityTextAnnotatingModel secModel = null;


    public ProbabilityDocumentTOCAnnotatingModel(int id,
                                                 NaiveBayesWithMultiNodes tnbm,
                                                 HiddenMarkovModel hmm,
                                                 NaiveBayesWithMultiNodes secTnbm,
                                                 HiddenMarkovModel secHmm,
                                                 Document doc, TOCModelRVSetting setting) {
        this(id,
                tnbm,
                hmm,
                secTnbm,
                secHmm,
                doc,
                setting,
                setting.getWordType(),
                setting.getWordFeatures(),
                setting.getNbmnConfig()
        );

        // quick way to disable features without retraining.
        setting.disableParaDocFeature(1); //para starts with number
        setting.disableParaDocFeature(2); //para ends with number
//        setting.disableParaDocFeature(3); //para is number
    }

    public ProbabilityDocumentTOCAnnotatingModel(int id,
                                                 NaiveBayesWithMultiNodes tnbm,
                                                 HiddenMarkovModel hmm,
                                                 NaiveBayesWithMultiNodes secTnbm,
                                                 HiddenMarkovModel secHmm,
                                                 Document doc,
                                                 TOCModelRVSetting setting,
                                                 RandomVariable wordType,
                                                 List<RandomVariable> wordFeatures,
                                                 NBMNConfig nbmnConfig
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
        List<Integer> lowerCatIds = setting.getLowLevelCategoryIds();
        if (lowerCatIds != null) {
            this.secNbmn = NBInferenceHelper.createLogProbNBMN(secTnbm);
            this.secHmm = secHmm;
            secHmm.updateProbabilities();

            ModelRVSetting lowerTOCSetting = new TOCModelRVSetting(
                    modelRVSetting.wordFeatures,
                    nbmnConfig.getFeatureVarList(),
                    nbmnConfig.getFeatureExistsAtDocLevelVarList(),
                    nbmnConfig.getWordVarList(),
                    lowerCatIds, null);

            lowerTOCSetting.disableParaDocFeature(1);
            lowerTOCSetting.disableParaDocFeature(2);

            this.secModel = new ProbabilityTextAnnotatingModel(
                    secNbmn,
                    secHmm,
                    null, // paragraphs will be set later once the sections are determined.
                    null,
                    data,
                    lowerTOCSetting
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
        annotateParaProbs(CoreAnnotations.TOCParaProbsDocLevel.class, processedParagraphs, paragraphCategoryBelief);

        List<Integer> lowerCatIds = ((TOCModelRVSetting) modelRVSetting).getLowLevelCategoryIds();
        if (lowerCatIds == null) return;

        List<List<List<CoreMap>>> sectionsList = DocProcessor.createSections(paragraphs, processedParagraphs, getParaCategory());
        List<List<CoreMap>> sections = sectionsList.get(0);
        List<List<CoreMap>> processedSections = sectionsList.get(1);

        for (int i = 0; i < sections.size(); i++) {
            secModel.setParagraphs(sections.get(i));
            secModel.setProcessedParagraphs(processedSections.get(i));
            secModel.setNumIterations(SEC_NUM_ITERATION);
            secModel.setAnnotatingThreshold(SEC_ANNOTATING_THRESHOLD);
            secModel.initialize();
            secModel.annotateParagraphs();
            annotateParaProbs(CoreAnnotations.TOCParaProbsSecLevel.class, processedSections.get(i), secModel.getParagraphCategoryBelief());
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


