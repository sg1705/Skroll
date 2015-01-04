package com.skroll.analyzer.model.hmm;

import com.skroll.document.*;

import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.pipeline.SyncPipe;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wei2learn on 12/26/2014.
 */
public class HTMLHiddenMarkovModelStateSequenceTestingPipe extends SyncPipe<Document, List<int[]>> {

    @Override
    public List<int[]> process(Document input) {
        HiddenMarkovModel model = (HiddenMarkovModel)config.get(0);

        List<int[]> output = new ArrayList<int[]>();

        List<CoreMap> paragraphs = input.getParagraphs();

        for( CoreMap paragraph : paragraphs) {
                List<String> tokens = DocumentHelper.getTokenString(paragraph.get(CoreAnnotations.TokenAnnotation.class));
                String[] tokensArray = tokens.toArray(new String[tokens.size()]);
                output.add(model.mostLikelyStateSequence(tokensArray));
        }
        return output;
    }
}
