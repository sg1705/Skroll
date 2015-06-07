package com.skroll.trainer;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.skroll.classifier.Category;
import com.skroll.classifier.Classifier;
import com.skroll.classifier.ClassifierFactory;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentFactory;
import com.skroll.document.Token;
import com.skroll.document.annotation.CategoryAnnotationHelper;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.document.annotation.TrainingWeightAnnotationHelper;
import com.skroll.parser.Parser;
import com.skroll.parser.extractor.ParserException;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Constants;
import com.skroll.pipeline.util.Utils;
import com.skroll.util.Configuration;
import com.skroll.util.ObjectPersistUtil;
import com.skroll.util.SkrollGuiceModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Created by saurabhagarwal on 1/19/15.
 */


/* current arguments for testing:
--trainWithOverride src/main/resources/trainingDocuments/indentures
--classify src/test/resources/analyzer/definedTermExtractionTesting/random-indenture.html
*/

public class SkrollTrainer {
    //The following line needs to be added to enable log4j
    public static final Logger logger = LoggerFactory
            .getLogger(SkrollTrainer.class);
    Injector injector = Guice.createInjector(new SkrollGuiceModule());
    ClassifierFactory classifierFactory = injector.getInstance(ClassifierFactory.class);
    Configuration configuration = new Configuration();
    DocumentFactory documentFactory = new DocumentFactory(configuration);
    private static Classifier documentClassifier = null;
    private static Classifier tocExperimentClassifier = null;


    public static void main(String[] args) throws IOException, ObjectPersistUtil.ObjectPersistException {

        SkrollTrainer skrollTrainer = new SkrollTrainer();

        QC qc = skrollTrainer.qcDocument("build/resources/main/preEvaluated/mini-indenture.html", "build/resources/main/preEvaluated/smaller-indenture.html");
        System.out.println("QC:" +qc.stats);
        //ToDO: use the apache common commandline
        /*
        if (args[0].equals("--generateHRFs")) {
            skrollTrainer.generateHRFs(args[1]);
        }
        if (args[0].equals("--trainWithOverride")) {
            logger.debug("folder Name :" + args[1]);
            skrollTrainer.trainWithOverride(args[1]);
        }
        if (args[0].equals("--classify")){
            skrollTrainer.classify(args[1]);
        }
        if (args[0].equals("--trainWithWeight")) {
            logger.debug("folder Name :" + args[1]);
            skrollTrainer.trainFolderUsingTrainingWeight(args[1]);
        }
*/
    }

    public void trainFolderUsingTrainingWeight (String preEvaluatedFolder)  {
        FluentIterable<File> iterable = Files.fileTreeTraverser().breadthFirstTraversal(new File(preEvaluatedFolder));
        List<String> docLists = new ArrayList<String>();
        for (File f : iterable) {
            if (f.isFile()) {
                trainFileUsingTrainingWeight(f.getPath());
            }
        }

    }

    public  void trainFileUsingTrainingWeight (String preEvaluatedFile) {
        Document doc = documentFactory.get(preEvaluatedFile);
        //iterate over each paragraph
        for(CoreMap paragraph : doc.getParagraphs()) {
            if (paragraph.containsKey(CoreAnnotations.IsTrainerFeedbackAnnotation.class)) {
                TrainingWeightAnnotationHelper.clearOldTrainingWeight(paragraph);
            }
        }
        final Document finalDoc = doc;
        try {
            classifierFactory.getClassifier(doc).forEach(c -> c.trainWithWeight(finalDoc));
            classifierFactory.getClassifier(doc).forEach(c -> {
                try {
                    c.persistModel();
                } catch (ObjectPersistUtil.ObjectPersistException e) {
                    logger.error("Failed to persist classifier: %s"+ c.toString(), e);
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public QC qcDocument(String file1, String file2){
        QC qc = new QC();
        Document firstDoc = documentFactory.get(file1);
        Document secondDoc = documentFactory.get(file2);
        for(CoreMap firstDocParagraph : firstDoc.getParagraphs()) {
            for(CoreMap secondDocParagraph : secondDoc.getParagraphs()) {
                if (firstDocParagraph.getId().equalsIgnoreCase(secondDocParagraph.getId())) {
                    for (QC.Stats stats : qc.stats) {
                        if (CategoryAnnotationHelper.isCategoryId(firstDocParagraph, stats.categoyId)){
                            stats.overallOccurance++;
                        }
                        if (CategoryAnnotationHelper.isCategoryId(firstDocParagraph, stats.categoyId) &&
                            !CategoryAnnotationHelper.isCategoryId(secondDocParagraph, stats.categoyId)) {
                             stats.typeAError++;
                            } else if (!CategoryAnnotationHelper.isCategoryId(firstDocParagraph, stats.categoyId) &&
                                CategoryAnnotationHelper.isCategoryId(secondDocParagraph, stats.categoyId)) {
                                stats.typeBError++;
                        }
                    }
                    break;
                }
            }
        }
        return qc;
    }
    // Generate CSV file for override.
    public void generateHRFs(String folderName) throws IOException {
        FluentIterable<File> iterable = Files.fileTreeTraverser().breadthFirstTraversal(new File(folderName));
        for (File f : iterable) {
            if (f.isFile()) {
                String fileName = f.getPath();
                generateHRF(fileName);
            }
        }
    }

    public void generateHRF(String fileName) throws  IOException {

        Configuration configuration = new Configuration();
        String targetFolder = configuration.get("hrfFolder","hrf/");
        try {
            logger.debug(fileName);
            //parse the file into document
            Document doc = Parser.parseDocumentFromHtmlFile(fileName);

            // extract the definition from paragraph from html doc.
            Pipeline<Document, Document> pipeline =
                    new Pipeline.Builder()
                            .add(Pipes.EXTRACT_DEFINITION_FROM_PARAGRAPH_IN_HTML_DOC)
                            .build();
            doc = pipeline.process(doc);

            // Build the contents for csv file
            List<String> defList = new ArrayList<String>();
            int count = 0;
            for (CoreMap paragraph : doc.getParagraphs()) {
                if (CategoryAnnotationHelper.isCategoryId(paragraph,Category.DEFINITION)) {

                    List<List<String>> definitionList = CategoryAnnotationHelper.getDefinedTermLists(
                            paragraph, Category.DEFINITION);
                    for (List<String> definition: definitionList) {
                        String words = Joiner.on(",").join(definition);
                        defList.add(paragraph.getId() + "\t" + "DEFINITION" + "\t" + words + "\t" + paragraph.getText());
                    }
                } else {
                    defList.add(paragraph.getId() + "\t" + "NOT_DEFINITION" + "\t" + " " + "\t" + paragraph.getText());
                }

            }

            fileName = fileName.replaceAll("\\.", "_");

            String fQFileName = configuration.get("model.persist.folder") + targetFolder + fileName + "_hrf.txt";
            Files.createParentDirs(new File(fQFileName));
            Pipeline<List<String>, List<String>> pDefTerms =
                    new Pipeline.Builder()
                            .add(Pipes.LIST_TO_CSV_FILE, Lists.newArrayList(fQFileName))
                            .build();

            pDefTerms.process(defList);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Document modifyDocWithOverride(String fileName) throws  IOException {

        Configuration configuration = new Configuration();
        String hrfFolder = configuration.get("hrfFolder","hrf/");
        String overrideFolder = configuration.get("overrideFolder","override/");
        Document doc = null;
        try {
            logger.debug(fileName);
            //read the file
            doc = Parser.parseDocumentFromHtmlFile(fileName);

            // extract the definition from paragraph from html doc.
            Pipeline<Document, Document> pipeline =
                    new Pipeline.Builder()
                            .add(Pipes.EXTRACT_DEFINITION_FROM_PARAGRAPH_IN_HTML_DOC)
                            .build();
            doc = pipeline.process(doc);

            fileName = fileName.replaceAll("\\.", "_").concat("_override.txt");
            String fQFileName = configuration.get("model.persist.folder") + overrideFolder + fileName;
            logger.debug("Override File Name: "+ fQFileName);
            if (!(new File(fQFileName).exists())){
                logger.debug("No Override File Name found, return the doc without overrides");
                return doc;
            }
            List<String> lines = Files.readLines(new File(fQFileName), Constants.DEFAULT_CHARSET);
            List<String> defList = new ArrayList<String>();
// todo: replace the inner loop with hashmap lookup to speed up
            for (String line : lines) {
                final Iterator<String> splitIterator = Splitter.on('\t').split(line).iterator();
                String paragraphID = CharMatcher.DIGIT.retainFrom(splitIterator.next());

                boolean isDefinition = false;
                Iterator<String> definedTermIterator =null;
                if(splitIterator.hasNext()) {
                    String definition = CharMatcher.JAVA_LETTER.retainFrom(splitIterator.next());
                    if (definition.trim().equals("DEFINITION")) {
                        isDefinition = true;
                        String definedTerms = splitIterator.next();
                        definedTermIterator = Splitter.on(',').split(definedTerms).iterator();
                    }
                }
                logger.debug("paragraphID to override:" + paragraphID);
                for (CoreMap paragraph : doc.getParagraphs()) {
                    if (paragraph.getId().equals(paragraphID)) {
                        logger.debug("Found Paragraph");
                        if (isDefinition) {
                            //paragraph.set(CoreAnnotations.IsDefinitionAnnotation.class, true);
                            List<Token> definedTokens = new ArrayList();
                            while (definedTermIterator.hasNext()) {
                                definedTokens.add(new Token(definedTermIterator.next()));
                            }
                            //TODO: only one definition per paragraph supported right now.
                            List<List<Token>> definedTokensList = new ArrayList();
                            definedTokensList.add(definedTokens);
                            CategoryAnnotationHelper.setDInCategoryAnnotation(paragraph, definedTokensList, Category.DEFINITION);

                            defList.add(paragraph.getId() + "\t" + "DEFINITION" + "\t" + definedTokens + "\t" + paragraph.getText());
                        } else {
                            defList.add(paragraph.getId() + "\t" + "NOT_DEFINITION" + "\t" + " " + "\t" + paragraph.getText());
                        }
                    }
                }
            }
            logger.debug("Override paragraphs:"+ defList);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return doc;
    }

    public void  trainWithOverride(String folderName) throws IOException, ObjectPersistUtil.ObjectPersistException {

            FluentIterable<File> iterable = Files.fileTreeTraverser().breadthFirstTraversal(new File(folderName));
            for (File f : iterable) {
                if (f.isFile()) {
                    String fileName = f.getPath();
                    Document doc = modifyDocWithOverride(fileName);
                    documentClassifier.train(doc);

                }
            }
            documentClassifier.persistModel();
    }

    public  void classify(String testingFile) {

        Document document = null;
        try {
            document = (Document)documentClassifier.classify(testingFile,Parser.parseDocumentFromHtmlFile(testingFile));
        } catch (ParserException e) {
            e.printStackTrace();
           logger.debug("failed to parse document");
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug(" failed to find a Model");
        }
        logger.debug ("Number fo Paragraphs returned: " + document.getParagraphs().size());
        Utils.writeToFile("build/classes/test/test-linker-random.html", document.getTarget());

    }
}
