package com.skroll.analyzer.hmm;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.skroll.analyzer.nb.BinaryNaiveBayesModel;
import com.skroll.model.HtmlDocument;
import com.skroll.model.Paragraph;
import com.skroll.pipeline.SyncPipe;

import java.util.*;

/**
 * Created by saurabh on 12/21/14.
 */
public class HiddenMarkovModelTrainingPipe extends SyncPipe<HtmlDocument, HtmlDocument> {

    @Override
    public HtmlDocument process(HtmlDocument input) {
        HiddenMarkovModel model = (HiddenMarkovModel)config.get(0);

        List<Paragraph> paragraphs = input.getParagraphs();

        for( Paragraph paragraph : paragraphs) {
            List<String> tokens = paragraph.getWords();

            HashSet<String> definitionsSet;
            if (paragraph.getDefinitions().size()==0){
                definitionsSet= new HashSet<String>();
            } else {

                List<String> definitions = Splitter.on(' ').splitToList(paragraph.getDefinitions().get(0));
                definitionsSet = new HashSet<String>(definitions);
            }

            int[] tokenType = new int[tokens.size()];
            int ii = 0;
            for(String token : tokens) {
                if (definitionsSet.contains(token)) {
                    tokenType[ii] = 1;
                } else {
                    tokenType[ii] = 0;
                }
                ii++;
            }


            model.updateCounts(tokens
                    .toArray(new String[tokens.size()]), tokenType);
        }
        return this.target.process(input);
    }


}
