package com.skroll.analyzer.evaluate.definition;

import com.google.common.collect.Lists;
import com.skroll.analyzer.model.hmm.HiddenMarkovModel;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.parser.Parser;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.SyncPipe;
import com.skroll.pipeline.util.Utils;

import java.io.File;
import java.util.List;

/**
 * Created by wei2learn on 12/26/2014.
 */
public class FolderHTMLHiddenMarkovModelStateSequenceTesterPipe extends SyncPipe<String, String> {

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

            try {
                htmlDoc = Parser.parseDocumentFromHtml(htmlString);
            } catch(Exception e) {
                e.printStackTrace();
                System.err.println("Error reading file");
            }
            Pipeline<Document, List<int[]>> testingPipe =
                    new Pipeline.Builder()
                            .add(Pipes.HTML_HIDDEN_MARKOV_MODEL_STATE_SEQUENCE_TESTING_PIPE,
                                    Lists.newArrayList((Object) model))
                            .build();



            List<int[]> states = testingPipe.process(htmlDoc);
            List<CoreMap> paragraphs = htmlDoc.getParagraphs();
            for (int i=0; i<paragraphs.size();i++){
                output +=( paragraphs.get(i).getText() + '\n');
                List<String> tokens = DocumentHelper.getTokenString(paragraphs.get(i).getTokens());
                int k=0;
                for (int j=0; j< paragraphs.get(i).getTokens().size() && k < model.size(); j++){
                    //if (paragraphs.get(i).getWords().get(j).equals("\"")) continue; //skip quote
                    if (states.get(i)[k]==1)
                        output+=String.format("    %s=%d ", tokens.get(j), states.get(i)[k++]);
                    else
                        output += String.format("%s=%d ", tokens.get(j), states.get(i)[k++]);

//                    if (probabilities.get(i)[k][1]>0.1)
//                        output+=String.format(" -+- %d %s=%.2f", j,paragraphs.get(i).getWords().get(j), probabilities.get(i)[k++][1]);
//                    else
//                        output += String.format("%d %s=%.2f ", j,paragraphs.get(i).getWords().get(j), probabilities.get(i)[k++][1]);
                }
                output +='\n';

            }



        }

        //System.out.println(model.showWordsImportance());
        return output;
    }


}
