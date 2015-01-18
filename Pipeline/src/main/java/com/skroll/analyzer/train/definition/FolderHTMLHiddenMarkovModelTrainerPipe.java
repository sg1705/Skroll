package com.skroll.analyzer.train.definition;

import com.google.common.collect.Lists;
import com.skroll.analyzer.model.hmm.HiddenMarkovModel;
import com.skroll.document.Document;
import com.skroll.parser.Parser;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.SyncPipe;
import com.skroll.pipeline.util.Utils;

import java.io.File;

/**
 *
 * Trains a HMM document with all the html files in a given folder
 *
 * Created by wei2learn on 12/26/2014.
 */
public class FolderHTMLHiddenMarkovModelTrainerPipe extends SyncPipe<String, String> {

    @Override
    public String process(String folderName) {
        HiddenMarkovModel model = (HiddenMarkovModel)config.get(0);


        File folder = new File(folderName);
        if (folder.isDirectory()) {
            File[] listOfFiles = folder.listFiles();
            for (File file:listOfFiles) {
                processFile(file, model);
            }
        } else {
            processFile(folder, model);
        }

        return this.target.process(folderName);
    }


    private void processFile(File file, HiddenMarkovModel model) {
        String htmlString = null;
        try {
            htmlString = Utils.readStringFromFile(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Document htmlDoc = new Document();
        htmlDoc = Parser.parseDocumentFromHtml(htmlString);
        //create a pipeline
        Pipeline<Document, Document> pipeline =
                new Pipeline.Builder()
//                        .add(Pipes.PARSE_HTML_TO_DOC)
//                        .add(Pipes.REMOVE_BLANK_PARAGRAPH_FROM_HTML_DOC)
//                        .add(Pipes.REMOVE_NBSP_IN_HTML_DOC)
//                        .add(Pipes.REPLACE_SPECIAL_QUOTE_IN_HTML_DOC)
                        .add(Pipes.FILTER_STARTS_WITH_QUOTE_IN_HTML_DOC)
//                        .add(Pipes.TOKENIZE_PARAGRAPH_IN_HTML_DOC)
                        .add(Pipes.EXTRACT_DEFINITION_FROM_PARAGRAPH_IN_HTML_DOC)
                        .add(Pipes.HTML_HIDDEN_MARKOV_MODEL_TRAINING_PIPE,
                                Lists.newArrayList((Object) model))
                        .build();
        Document doc = pipeline.process(htmlDoc);
    }

}
