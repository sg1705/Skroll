package com.skroll.analyzer.model.hmm;

import com.skroll.document.HtmlDocument;
import com.skroll.document.Paragraph;
import com.skroll.pipeline.SyncPipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wei2learn on 12/26/2014.
 */
public class HTMLHiddenMarkovModelTestingPipe extends SyncPipe<HtmlDocument, List<double[][]>> {

    @Override
    public List<double[][]> process(HtmlDocument input) {
        HiddenMarkovModel model = (HiddenMarkovModel)config.get(0);

        List<double[][]> output = new ArrayList<double[][]>();

        List<Paragraph> paragraphs = input.getParagraphs();

        for( Paragraph paragraph : paragraphs) {
                List<String> tokens = paragraph.getTokens();
                String[] tokensArray = tokens.toArray(new String[tokens.size()]);
                output.add(model.infer(tokensArray));
        }
        return output;
    }
}
