package com.skroll.trainer;

import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.parser.Parser;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Utils;
import com.skroll.util.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by saurabhagarwal on 1/19/15.
 */
public class TrainingDataGenerator {


    public static void generateFilesForOverride(String folderName) throws IOException {

       // folderName = "src/main/resources/trainingDocuments/";

        FluentIterable<File> iterable = Files.fileTreeTraverser().breadthFirstTraversal(new File(folderName));
        for (File f : iterable) {
            if (f.isFile()) {
                String fileName = f.getPath();
                TrainingDataGenerator.generateFileForOverride(fileName);
            }
        }
    }

    public static void generateFileForOverride(String fileName) throws  IOException {

        Configuration configuration = new Configuration();
        String targetFolder = configuration.get("hrfFolder","hrf/");
        try {
            System.out.println(fileName);
            //read the file
            String htmlText = Utils.readStringFromFile(fileName);

            //parse the file into document
            Document doc = Parser.parseDocumentFromHtml(htmlText);

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
}
