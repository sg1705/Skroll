package com.skroll.analyzer.model.hmm;

import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.pipeline.SyncPipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wei2learn on 12/26/2014.
 */
public class HTMLHiddenMarkovModelTestingPipe extends SyncPipe<Document, List<double[][]>> {

    @Override
    public List<double[][]> process(Document input) {
        HiddenMarkovModel model = (HiddenMarkovModel)config.get(0);

        List<double[][]> output = new ArrayList<double[][]>();

        List<CoreMap> paragraphs = input.getParagraphs();

        for( CoreMap paragraph : paragraphs) {
                List<String> tokens = DocumentHelper.getTokenString(paragraph.get(CoreAnnotations.TokenAnnotation.class));
                String[] tokensArray = tokens.toArray(new String[tokens.size()]);
                output.add(model.infer(tokensArray));
        }
        return output;
    }
}
