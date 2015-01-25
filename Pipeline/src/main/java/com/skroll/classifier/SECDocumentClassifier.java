package com.skroll.classifier;

import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;
import com.skroll.analyzer.model.nb.DataTuple;
import com.skroll.analyzer.model.nb.NaiveBayes;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.util.ObjectPersistUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by saurabhagarwal on 1/5/15.
 */
public class SECDocumentClassifier extends ClassifierImpl {


    public static final Logger logger = LoggerFactory
            .getLogger(SECDocumentClassifier.class);

    // Initialize the list of category this classifier support.
    private NaiveBayes nbModelForDoc;
    private String modelName = "com.skroll.analyzer.model.nb.NaiveBayes.secDocumentNB";
    private Type type = null;

    public SECDocumentClassifier() {
        categories.add(new Category(0, "Indenture"));
        categories.add(new Category(1, "CreditAgreements"));
        categories.add(new Category(2, "Prospectus"));
        try {
            type = new TypeToken<NaiveBayes>() {}.getType();
            nbModelForDoc = (NaiveBayes) objectPersistUtil.readObject(type,modelName); //"com.skroll.analyzer.model.nb.NaiveBayes.secDocumentNB.1");
            logger.debug("nbModelForDoc" + nbModelForDoc);

        } catch (Throwable e) {
            e.printStackTrace();
            nbModelForDoc=null;
        }
        if (nbModelForDoc==null) {

            nbModelForDoc = new NaiveBayes(categories.size(), new int[0]);
        }

    }

    @Override
    public void persistModel() throws ObjectPersistUtil.ObjectPersistException {
        objectPersistUtil.persistObject(type,nbModelForDoc,modelName);
    }

    @Override
    public void train(Category category, Document doc) {

    }

    @Override
    public void train(Category category, String fileName, int numOfLines) {

        Pipeline<String, List<String>> fileIntoString =
                new Pipeline.Builder<String, List<String>>()
                        .add(Pipes.FILE_INTO_LIST_OF_STRING)
                        .build();


        Pipeline<List<String>, List<List<String>>> csvSplitPipeline =
                new Pipeline.Builder<List<String>, List<List<String>>>()
                        .add(Pipes.CSV_SPLIT_INTO_LIST_OF_STRING)
                        .build();


        List<String> fileStrings = fileIntoString.process(fileName);
        List<String> partitionFileStrings  =(List<String>) Lists.partition(new ArrayList(fileStrings), fileStrings.size()<numOfLines?fileStrings.size():numOfLines).get(0);
        List<List<String>> csvStrings = csvSplitPipeline.process(partitionFileStrings);

        for (List<String> line : csvStrings) {

            // to remove the duplication for a line, let put all words into Set
            Set<String> tokenSet = new HashSet<>(line);
            DataTuple tuple = new DataTuple(category.getId(), tokenSet.toArray(new String[tokenSet.size()]), new int[0]);
            //DataTuple tuple = stringsToTrainingTuple.process(line);
            nbModelForDoc.addSample(tuple);

        }
        //System.out.println("nbModelForDoc:" + nbModelForDoc);
    }

    @Override
    public Object classify(String fileName, int numOfLines ) {

        Pipeline<String, List<String>> fileIntoString =
                new Pipeline.Builder<String, List<String>>()
                        .add(Pipes.FILE_INTO_LIST_OF_STRING)
                        .build();


        Pipeline<List<String>, List<List<String>>> csvSplitPipeline =
                new Pipeline.Builder<List<String>, List<List<String>>>()
                        .add(Pipes.CSV_SPLIT_INTO_LIST_OF_STRING)
                        .build();


        List<String> fileStrings = fileIntoString.process(fileName);
        List<String> partitionFileStrings  =(List<String>) Lists.partition(new ArrayList(fileStrings), fileStrings.size()<numOfLines?fileStrings.size():numOfLines).get(0);
        List<List<String>> csvStrings = csvSplitPipeline.process(partitionFileStrings);
        Set<String> words = new HashSet<String>();

        for (List<String> line : csvStrings) {
            // to remove the duplication for a line, let put all words into Set
            words.addAll(line);
        }

        DataTuple tuple = new DataTuple(-1, words.toArray(new String[words.size()]), new int[0]);
        Integer output = nbModelForDoc.mostLikelyCategory(tuple);

        Map<Category, Double> probableCategory = new HashMap<Category, Double>();

        for ( Category category: categories){
            double prob =nbModelForDoc.inferCategoryProbabilityMoreStable(category.getId(),words.toArray(new String[words.size()]),new int[0]);
            probableCategory.put(category,prob);
        }
        logger.info("probableCategory:" + probableCategory);

        return output;

    }

    @Override
    public void train(Document doc) {

    }

    @Override
    public Object classify(Document document) {
        return classify(document,-1);
    }


    @Override
    public SortedMap<Category, Double> classifyDetailed(Document doc, int numOfTokens) {
        Map<Category, Double> probableCategory = new HashMap<Category, Double>();
        System.out.println("nbModelForDoc:" + nbModelForDoc);
        List<String> tokens = extractTokenFromDoc(doc);
        List<String> partitionTokens = (List<String>) Lists.partition(new ArrayList(tokens), tokens.size()<numOfTokens?tokens.size():numOfTokens).get(0);
        DataTuple tuple = new DataTuple(-1, partitionTokens.toArray(new String[partitionTokens.size()]), new int[0]);


        for ( Category category: categories){
            double prob =nbModelForDoc.inferCategoryProbabilityMoreStable(category.getId(),partitionTokens.toArray(new String[partitionTokens.size()]),new int[0]);
            probableCategory.put(category,prob);
        }
        logger.info("probableCategory:" + probableCategory);
        return null;
    }

    @Override
    public Object classify(Document doc, int numOfTokens) {
        List<String> tokens = extractTokenFromDoc(doc);
        List<String> partitionTokens =null;
        if (numOfTokens==-1) {
            partitionTokens = tokens;
         } else {
            partitionTokens= (List<String>) Lists.partition(new ArrayList(tokens), tokens.size()<numOfTokens?tokens.size():numOfTokens).get(0);
         }

        DataTuple tuple = new DataTuple(-1, partitionTokens.toArray(new String[partitionTokens.size()]), new int[0]);
        Integer output = nbModelForDoc.mostLikelyCategory(tuple);


        Map<Category, Double> probableCategory = new HashMap<Category, Double>();

        for ( Category category: categories){
            double prob =nbModelForDoc.inferCategoryProbabilityMoreStable(category.getId(),partitionTokens.toArray(new String[partitionTokens.size()]),new int[0]);
            probableCategory.put(category,prob);
        }
        logger.info("probableCategory:" + probableCategory);

        return output;

    }


    public List<String> extractTokenFromDoc(Document doc) {
        List<CoreMap> paragraphs = doc.getParagraphs();
        Set<String> words = new HashSet<String>();
        for (CoreMap paragraph : paragraphs) {
            List<Token> tokens = paragraph.get(CoreAnnotations.TokenAnnotation.class);
            for (Token token : tokens) {
                words.add(token.getText());
            }
        }
        return new ArrayList<String>(words);
    }
}
