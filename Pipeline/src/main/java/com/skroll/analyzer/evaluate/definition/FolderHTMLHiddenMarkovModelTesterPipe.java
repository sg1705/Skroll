package com.skroll.analyzer.evaluate.definition;

import com.google.common.collect.Lists;
import com.skroll.analyzer.model.hmm.HiddenMarkovModel;
import com.skroll.document.Document;
import com.skroll.document.Entity;
import com.skroll.document.HtmlDocument;
import com.skroll.document.Paragraph;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.SyncPipe;
import com.skroll.pipeline.util.Utils;

import java.io.File;
import java.util.List;

/**
 * Created by wei2learn on 12/26/2014.
 */
public class FolderHTMLHiddenMarkovModelTesterPipe extends SyncPipe<String, String> {

    @Override
    public String process(String folderName) {
        HiddenMarkovModel model = (HiddenMarkovModel)config.get(0);
        String output="";

        File folder = new File(folderName);
        File[] listOfFiles = folder.listFiles();
        for (File file:listOfFiles){
            String htmlString = null;
            try {
                htmlString = Utils.readStringFromFile(folderName + '/' + file.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            Document htmlDoc= new Document();
            htmlDoc.setSource(htmlString);

            //create a pipeline
            Pipeline<Document, Document> testingDocPipe =
                    new Pipeline.Builder()
                            .add(Pipes.PARSE_HTML_TO_DOC)
                            .add(Pipes.REMOVE_BLANK_PARAGRAPH_FROM_HTML_DOC)
                            .add(Pipes.REMOVE_NBSP_IN_HTML_DOC)
                            .add(Pipes.REPLACE_SPECIAL_QUOTE_IN_HTML_DOC)
                            .add(Pipes.FILTER_STARTS_WITH_QUOTE_IN_HTML_DOC)
                            .add(Pipes.TOKENIZE_PARAGRAPH_IN_HTML_DOC)
                            .add(Pipes.EXTRACT_DEFINITION_FROM_PARAGRAPH_IN_HTML_DOC)
                            .build();
            Document testDoc = testingDocPipe.process(htmlDoc);

            Pipeline<Document, List<double[][]>> testingPipe =
                    new Pipeline.Builder()
                            .add(Pipes.HTML_HIDDEN_MARKOV_MODEL_TESTING_PIPE,
                                    Lists.newArrayList((Object) model))
                            .build();


            List<double[][]> probabilities = testingPipe.process(testDoc);
            List<Entity> paragraphs = testDoc.getParagraphs();
            for (int i=0; i<paragraphs.size();i++){
                output +=( paragraphs.get(i).getText() + '\n');
                int k=0;
                for (int j=0; j< paragraphs.get(i).getTokens().size() && k < model.size(); j++){
                    //if (paragraphs.get(i).getWords().get(j).equals("\"")) continue; //skip quote
                    if (probabilities.get(i)[k][1]>0.1)
                        output+=String.format("    %s=%.2f ", paragraphs.get(i).getTokens().get(j), probabilities.get(i)[k++][1]);
                    else
                        output += String.format("%s=%.2f ", paragraphs.get(i).getTokens().get(j), probabilities.get(i)[k++][1]);

//                    if (probabilities.get(i)[k][1]>0.1)
//                        output+=String.format(" -+- %d %s=%.2f", j,paragraphs.get(i).getWords().get(j), probabilities.get(i)[k++][1]);
//                    else
//                        output += String.format("%d %s=%.2f ", j,paragraphs.get(i).getWords().get(j), probabilities.get(i)[k++][1]);
                }
                output +='\n';

            }



        }

        //System.out.println(document.showWordsImportance());
        return output;
    }


}
