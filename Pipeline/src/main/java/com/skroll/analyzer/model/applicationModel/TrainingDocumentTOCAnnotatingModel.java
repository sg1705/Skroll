package com.skroll.analyzer.model.applicationModel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skroll.analyzer.data.NBMNData;
import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.bn.NBTrainingHelper;
import com.skroll.analyzer.model.bn.NaiveBayesWithMultiNodes;
import com.skroll.analyzer.model.bn.config.NBMNConfig;
import com.skroll.analyzer.model.hmm.HiddenMarkovModel;
import com.skroll.classifier.ClassifierFactory;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Base class for training document model.
 * Contains most of the high level functionalities for training doc models.
 *
 * Created by wei2learn on 2/16/2015.
 *
 */

public class TrainingDocumentTOCAnnotatingModel extends TrainingTextAnnotatingModel {

    public static final Logger logger = LoggerFactory.getLogger(TrainingDocumentTOCAnnotatingModel.class);
    @JsonProperty("secModel")
    TrainingTextAnnotatingModel secModel = null;

    @JsonProperty("secNbmnModel")
    NaiveBayesWithMultiNodes secNbmnModel = null;

    @JsonProperty("secHmm")
    HiddenMarkovModel secHmm = null;


    public TrainingDocumentTOCAnnotatingModel(int id, TOCModelRVSetting setting) {
        this(id, setting.getWordType(), setting.getWordFeatures(), setting.getNbmnConfig(), setting);
        this.id =id;
        modelRVSetting = setting;
    }

    private TrainingDocumentTOCAnnotatingModel(int id, RandomVariable wordType,
                                               List<RandomVariable> wordFeatures,
                                               NBMNConfig nbmnConfig,
                                               TOCModelRVSetting modelRVSetting
    ) {

        this(id, NBTrainingHelper.createTrainingNBMN(nbmnConfig),
                NBTrainingHelper.createTrainingNBMN(modelRVSetting.getLowLevelNbmnConfig()),
                wordType, wordFeatures, nbmnConfig, modelRVSetting);
        if (modelRVSetting.getLowLevelNbmnConfig() == null) return;

        List<Integer> lowerCatIds = modelRVSetting.getLowLevelCategoryIds();
        if (lowerCatIds != null) {
//        ModelRVSetting lowerTOCSetting = new TOCModelRVSetting(ClassifierFactory.LOWER_TOC_CATEGORY_IDS, null);
            ModelRVSetting lowerTOCSetting = new TOCModelRVSetting(
                    modelRVSetting.wordFeatures,
                    nbmnConfig.getFeatureVarList(),
                    nbmnConfig.getFeatureExistsAtDocLevelVarList(),
                    nbmnConfig.getWordVarList(),
                    lowerCatIds, null);
            secModel = new TrainingTextAnnotatingModel(0, secNbmnModel, wordType, wordFeatures, modelRVSetting.getLowLevelNbmnConfig(), lowerTOCSetting);
        }

    }

    @JsonCreator
    public TrainingDocumentTOCAnnotatingModel(
            @JsonProperty("id") int id,
            @JsonProperty("nbmnModel") NaiveBayesWithMultiNodes nbmnModel,
            @JsonProperty("secNbmnModel") NaiveBayesWithMultiNodes secNbmnModel,
            @JsonProperty("wordType") RandomVariable wordType,
            @JsonProperty("wordFeatures") List<RandomVariable> wordFeatures,
            @JsonProperty("nbmnConfig") NBMNConfig nbmnConfig,
            @JsonProperty("modelRVSetting") TOCModelRVSetting modelRVSetting) {
        this.id = id;
        this.nbmnConfig = nbmnConfig;
        this.nbmnModel = nbmnModel;
        this.wordType = wordType;
        this.wordFeatures = wordFeatures;
        this.modelRVSetting = modelRVSetting;
        int []wordFeatureSizes = new int[wordFeatures.size()]; // include state at the feature index 0.
        for (int i=0; i<wordFeatureSizes.length;i++)
            wordFeatureSizes[i] =  wordFeatures.get(i).getFeatureSize();
        this.secNbmnModel = secNbmnModel;
        hmm = new HiddenMarkovModel(HMM_MODEL_LENGTH,
                wordType.getFeatureSize(), wordFeatureSizes);
        if (modelRVSetting.getLowLevelNbmnConfig() == null) return;
        secHmm = new HiddenMarkovModel(HMM_MODEL_LENGTH,
                wordType.getFeatureSize(), wordFeatureSizes);
    }

    public NaiveBayesWithMultiNodes getSecNbmnModel() {
        return secNbmnModel;
    }

    public HiddenMarkovModel getSecHmm() {
        return secHmm;
    }

    /**
     * todo: to reduce memory usage at the cost of more computation, can process training paragraph one by one later instead of process all and store them now
     * training involves updating Fij for each paragraph i and feature j.
     */
    public void updateWithDocumentAndWeight(Document doc) {
        logger.info("starting traing doc " + doc.getId());
        List<CoreMap> originalParas = doc.getParagraphs();

        // todo: the following two lines can cause a lot of inefficiency with the current approach of
        // updating training model with the whole doc each time user makes an observation.
        List<CoreMap> processedParas = DocProcessor.processParas(doc);
        modelRVSetting.postProcessFunctions
                .stream()
                .forEach(f -> f.apply(doc.getParagraphs(), processedParas));


        // in NBMNData, para features can be preprocessed for the whole doc,
        // but doc features depends on the set of the observed paras and cannot be preprocessed just once.
        NBMNData data = DocProcessor.getParaDataFromDoc(doc, nbmnConfig);

        updateWithProcessedParasAndWeight(originalParas, processedParas, data);

        if (secModel == null) return;

        List<List<List<CoreMap>>> sectionsList = DocProcessor.createSections(doc.getParagraphs(), processedParas, getParaCategory());
        List<List<CoreMap>> sections = sectionsList.get(0);
        List<List<CoreMap>> processedSections = sectionsList.get(1);

        for (int i = 0; i < sections.size(); i++) {
            secModel.updateWithProcessedParasAndWeight(sections.get(i), processedSections.get(i), data);
        }

        logger.info("done traing doc " + doc.getId());

    }

    /**
     * the old method for training with doc. Does not use weight and go through all paragraphs, not just the observed.
     *
     * @param doc
     */
    public void updateWithDocument(Document doc) {


        List<CoreMap> processedParas = DocProcessor.processParas(doc);
        modelRVSetting.postProcessFunctions
                .stream()
                .forEach(f -> f.apply(doc.getParagraphs(), processedParas));

        NBMNData data = DocProcessor.getParaDataFromDoc(doc, nbmnConfig);
        updateWithDocument(doc.getParagraphs(), processedParas, data);


        List<List<List<CoreMap>>> sectionsList = DocProcessor.createSections(doc.getParagraphs(), processedParas, getParaCategory());
        List<List<CoreMap>> sections = sectionsList.get(0);
        List<List<CoreMap>> processedSections = sectionsList.get(1);

        for (int i = 0; i < sections.size(); i++) {
            secModel.updateWithDocument(sections.get(i), processedSections.get(i), data);
        }


    }

}


