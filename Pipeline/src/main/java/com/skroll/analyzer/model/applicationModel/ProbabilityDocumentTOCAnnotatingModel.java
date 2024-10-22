package com.skroll.analyzer.model.applicationModel;

import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.applicationModel.randomVariables.RVValues;
import com.skroll.analyzer.model.bn.NBInferenceHelper;
import com.skroll.analyzer.model.bn.NaiveBayesWithMultiNodes;
import com.skroll.analyzer.model.bn.config.NBMNConfig;
import com.skroll.analyzer.model.hmm.HiddenMarkovModel;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.util.Visualizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Probability model for annotating the Document headings.
 * todo: should probably make it more general to handle lower level TOC model recursively. But it may not worth the effort needed at this point.
 * Created by wei2learn on 2/16/2015.
 */
public class ProbabilityDocumentTOCAnnotatingModel extends ProbabilityTextAnnotatingModel {
    public static final Logger logger = LoggerFactory.getLogger(ProbabilityDocumentTOCAnnotatingModel.class);
    static final int SEC_NUM_ITERATION = 2 ;

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
                    lowerCatIds, setting.getLowerAnnotatingThreshold(), null, null);


            this.secModel = new ProbabilityTextAnnotatingModel(
                    secNbmn,
                    secHmm,
                    null, // paragraphs will be set later once the sections are determined.
                    null,
                    data,
                    lowerTOCSetting
            );
            setAnnotatingThreshold(setting.getAnnotatingThreshold());
        }
        logger.info("initializing model");
        super.initialize();
        logger.info("finished initializing");
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

        logger.info("annotating top level headings");
        super.annotateParagraphs();
        annotateParaProbs(CoreAnnotations.TOCParaProbsDocLevel.class, processedParagraphs, paragraphCategoryBelief);

        List<Integer> lowerCatIds = ((TOCModelRVSetting) modelRVSetting).getLowLevelCategoryIds();
        if (lowerCatIds == null) return;

        logger.info("creating sections");
        // remove duplicate annotations for level 1.
        DocProcessor.removeDuplicateAnnotations(paragraphs, modelRVSetting.getCategoryIds().get(1));
        List<List<List<CoreMap>>> sectionsList = DocProcessor.createSections(paragraphs, processedParagraphs, getParaCategory());
        List<List<CoreMap>> sections = sectionsList.get(0);
        List<List<CoreMap>> processedSections = sectionsList.get(1);

        logger.info("annotating section level headings");
        for (int i = 0; i < sections.size(); i++) {
            secModel.setParagraphs(sections.get(i));
            secModel.setProcessedParagraphs(processedSections.get(i));
            secModel.setNumIterations(SEC_NUM_ITERATION);
            secModel.setEnforcingDominatingFeatureForClass(1); // class index 1 represent level 2 in the section model.
//            secModel.setUseFirstParaFormat(true);
            secModel.setKeepExistingAnnotation(true);

            secModel.initialize();
            secModel.annotateParagraphs();
            // remove duplicate annotations for level 2.
            DocProcessor.removeDuplicateAnnotations(sections.get(i), lowerCatIds.get(1));
            annotateParaProbs(CoreAnnotations.TOCParaProbsSecLevel.class, processedSections.get(i), secModel.getParagraphCategoryBelief());
        }
        logger.info("done annotating section level headings");

        showDocSecProbDiff(processedParagraphs);
    }

    void showDocSecProbDiff(List<CoreMap> processedParagraphs){
        for (CoreMap para: paragraphs){
            int index = para.get(CoreAnnotations.IndexInteger.class);
            CoreMap pp = processedParagraphs.get(index);
            List<Double> docModelProbs = pp.get(CoreAnnotations.TOCParaProbsDocLevel.class);
            List<Double> secModelProbs = pp.get(CoreAnnotations.TOCParaProbsSecLevel.class);
            if (docModelProbs == null || secModelProbs == null) continue;
            double docModelProb = docModelProbs.get(2);
            double secModelProb = secModelProbs.get(1);
            double diff = Math.abs(docModelProb-secModelProb);
            if (diff > 0.9){
                System.out.println("doc sec prob diff large for paragraph " + index+ ":" + para.getText());
                System.out.println("doc model prob = "+docModelProb);
                System.out.println("sec model prob = "+secModelProb);
            }
        }
    }

    /**
     * todo: temporarily used display sec features on the right side for debugging purpose.
     * @param paraIndex
     * @return
     */
    public double[][][] getSecFeatureProbs(int paraIndex){
        List<Integer> lowerCatIds = ((TOCModelRVSetting) modelRVSetting).getLowLevelCategoryIds();
        if (lowerCatIds == null) return null;

        // remove duplicate annotations for level 1.
        DocProcessor.removeDuplicateAnnotations(paragraphs, modelRVSetting.getCategoryIds().get(1));
        List<List<List<CoreMap>>> sectionsList = DocProcessor.createSections(paragraphs, processedParagraphs, getParaCategory());
        List<List<CoreMap>> sections = sectionsList.get(0);
        List<List<CoreMap>> processedSections = sectionsList.get(1);

        int sectionNum = 0;
        for (; sectionNum < sections.size(); sectionNum++){
            List<CoreMap> paras = sections.get(sectionNum);
            if (paras.size()==0) continue;
            int lastParaIndexOfSec = paras.get(paras.size()-1).get(CoreAnnotations.IndexInteger.class);
            if (lastParaIndexOfSec >= paraIndex) break;
        }
        secModel.setParagraphs(sections.get(sectionNum));
        secModel.setProcessedParagraphs(processedSections.get(sectionNum));
        secModel.setNumIterations(SEC_NUM_ITERATION);

        logger.info("annotating section level headings for section " + sectionNum);

        secModel.initialize();
        secModel.annotateParagraphs();

        // remove duplicate annotations for level 2.
        DocProcessor.removeDuplicateAnnotations(sections.get(sectionNum), lowerCatIds.get(1));
        logger.info("done annotating section level headings");

        return secModel.getDocumentFeatureProbabilities();

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
        logger.info("paraDocFeatures for para " + paraIndex + "\n" + Arrays.toString(data.getParaDocFeatures()[paraIndex]));
        for (int ii = 0; ii < documentFeatureBelief.length; ii++) {
            applicationModelInfo.put(this.nbmnConfig.getFeatureExistsAtDocLevelVarList().get(ii).getName(),
                    Visualizer.toDoubleArrayToMap(new double[]{.0 + data.getParaDocFeatures()[paraIndex][ii]}));
            for (int jj = 0; jj < documentFeatureBelief[0].length; jj++) {
                applicationModelInfo.put(this.nbmnConfig.getDocumentFeatureVarList().get(ii).get(jj).getName(),
                        Visualizer.toDoubleArrayToMap(this.getDocumentFeatureProbabilities()[ii][jj]));
            }
        }

        double[][][] secFeatureProbs = getSecFeatureProbs(paraIndex);


        if (secFeatureProbs != null)
            for (int ii = 0; ii < secFeatureProbs.length; ii++) {
                for (int jj = 0; jj < secFeatureProbs[0].length; jj++) {
                    applicationModelInfo.put(this.secModel.getNbmnConfig().getDocumentFeatureVarList().get(ii).get(jj).getName(),
                            Visualizer.toDoubleArrayToMap(secFeatureProbs[ii][jj]));
                }
            }


        map.put("applicationModelInfo", applicationModelInfo);
        return super.toVisualMap(map);

    }

}


