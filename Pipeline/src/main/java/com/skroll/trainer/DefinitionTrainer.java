package com.skroll.trainer;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.skroll.classifier.Classifier;
import com.skroll.classifier.DefinitionClassifier;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.parser.Parser;
import com.skroll.parser.extractor.ParserException;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Constants;
import com.skroll.pipeline.util.Utils;
import com.skroll.util.Configuration;
import com.skroll.util.ObjectPersistUtil;
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
public class DefinitionTrainer {
    //The following line needs to be added to enable log4j
    public static final Logger logger = LoggerFactory
            .getLogger(DefinitionTrainer.class);

    public static void main(String[] args) throws IOException, ObjectPersistUtil.ObjectPersistException {

        //ToDO: use the apache common commandline
        if (args[0].equals("--generateHRFs")) {
            DefinitionTrainer.generateHRFs(args[1]);
        }
        if (args[0].equals("--trainWithOverride")) {
            DefinitionTrainer.trainWithOverride(args[1]);
        }
        if (args[0].equals("--classify")){
            DefinitionTrainer.classify(args[1]);
        }

    }
    // Generate CSV file for override.
    public static void generateHRFs(String folderName) throws IOException {

        FluentIterable<File> iterable = Files.fileTreeTraverser().breadthFirstTraversal(new File(folderName));
        for (File f : iterable) {
            if (f.isFile()) {
                String fileName = f.getPath();
                DefinitionTrainer.generateHRF(fileName);
            }
        }
    }

    public static void generateHRF(String fileName) throws  IOException {

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
                if (paragraph.containsKey(CoreAnnotations.IsDefinitionAnnotation.class)) {
                    String words = Joiner.on(",").join(DocumentHelper
                            .getTokenString(
                                    paragraph.get(CoreAnnotations.DefinedTermsAnnotation.class)));

                    defList.add(paragraph.getId() + "\t" + "DEFINITION" + "\t" + words + "\t" + paragraph.getText());

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

    public static Document modifyDocWithOverride(String fileName) throws  IOException {

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

            List<String> lines = Files.readLines(new File(fQFileName), Constants.DEFAULT_CHARSET);
            List<String> defList = new ArrayList<String>();

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
                            paragraph.set(CoreAnnotations.IsDefinitionAnnotation.class, true);
                            List<Token> definedTokens = new ArrayList();
                            while (definedTermIterator.hasNext()) {
                                definedTokens.add(new Token(definedTermIterator.next()));
                            }
                            paragraph.set(CoreAnnotations.DefinedTermsAnnotation.class, definedTokens);
                            defList.add(paragraph.getId() + "\t" + "DEFINITION" + "\t" + definedTokens + "\t" + paragraph.getText());
                        } else {
                            paragraph.set(CoreAnnotations.IsDefinitionAnnotation.class, false);
                            paragraph.set(CoreAnnotations.DefinedTermsAnnotation.class, null);
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

    public static void  trainWithOverride(String folderName) throws IOException, ObjectPersistUtil.ObjectPersistException {
            DefinitionClassifier documentClassifier = new DefinitionClassifier();
            FluentIterable<File> iterable = Files.fileTreeTraverser().breadthFirstTraversal(new File(folderName));
            for (File f : iterable) {
                if (f.isFile()) {
                    String fileName = f.getPath();
                    Document doc = DefinitionTrainer.modifyDocWithOverride(fileName);
                    documentClassifier.train(doc);

                }
            }
            documentClassifier.persistModel();
    }

    public static void classify(String testingFile) {

        Classifier documentClassifier = new DefinitionClassifier();
        // String testingFile = "src/test/resources/parser/linker/test-linker-random.html";
        //String testingFile = "src/main/resources/trainingDocuments/indentures/AMC Networks Indenture.html";

        Document document = null;
        try {
            document = (Document)documentClassifier.classify(Parser.parseDocumentFromHtmlFile(testingFile));
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
