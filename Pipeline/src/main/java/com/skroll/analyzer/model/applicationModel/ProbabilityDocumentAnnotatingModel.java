package com.skroll.analyzer.model.applicationModel;

import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.bn.NBInferenceHelper;
import com.skroll.analyzer.model.bn.NaiveBayesWithMultiNodes;
import com.skroll.analyzer.model.bn.config.NBMNConfig;
import com.skroll.analyzer.model.hmm.HiddenMarkovModel;
import com.skroll.classifier.ClassifierFactory;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;

import java.util.*;

/**
 * Created by wei2learn on 2/16/2015.
 */
public class ProbabilityDocumentAnnotatingModel extends ProbabilityTextAnnotatingModel {

    Document doc;
    NaiveBayesWithMultiNodes secNbmn = null;
    HiddenMarkovModel secHmm = null;





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
        if (secNbmnConfig != null) {
            this.secNbmn = NBInferenceHelper.createLogProbNBMN(secTnbm);
            this.secHmm = secHmm;
        }
        hmm.updateProbabilities();
        secHmm.updateProbabilities();

        preprocessData();
        super.computeInitalBeliefs();
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
        List<List<List<CoreMap>>> sectionsList = DocProcessor.createSections(paragraphs, processedParagraphs, getParaCategory());
        List<List<CoreMap>> sections = sectionsList.get(0);
        List<List<CoreMap>> processedSections = sectionsList.get(1);

        ModelRVSetting lowerTOCSetting = new TOCModelRVSetting(ClassifierFactory.LOWER_TOC_CATEGORY_IDS, null);
        for (int i = 0; i < sections.size(); i++) {
            ProbabilityTextAnnotatingModel secModel = new ProbabilityTextAnnotatingModel(
                    secNbmn,
                    secHmm,
                    sections.get(i),
                    processedSections.get(i),
                    data,
                    lowerTOCSetting,
                    wordType,
                    wordFeatures,
                    lowerTOCSetting.getNbmnConfig()
            );
            secModel.setNumIterations(5);
            secModel.annotateParagraphs();
        }
    }

}


