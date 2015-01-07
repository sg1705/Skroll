package com.skroll.analyzer.data.definition;

import com.skroll.analyzer.model.nb.DataTuple;
import com.skroll.analyzer.model.nb.NaiveBayes;
import com.skroll.pipeline.SyncPipe;
import com.skroll.pipeline.util.Constants;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by wei2learn on 1/6/2015.
 */
public class StringsToNaiveBayesDataTuplePipe extends SyncPipe<List<String>, DataTuple> {
    public static final int NUMBER_FEATURES=2;
    public static final boolean USE_QUOTE=true;

    @Override
    public DataTuple process(List<String> input) {
        NaiveBayes model = (NaiveBayes)config.get(0);
        int categoryType = (Integer)config.get(1);



        int length= Constants.DEFINITION_CLASSIFICATION_NAIVE_BAYES_NUMBER_TOKENS;
        Set<String> tokenSet = new HashSet<String>();

        // skip words inside the quotes for training
        int indexAfterQuote=0;
        int [] features = new int[NUMBER_FEATURES];
        int featureNumber=0;
        if (input.get(0).equals("\"")) {
            features[featureNumber]=1;
            indexAfterQuote=1;
            while (indexAfterQuote<input.size() && !input.get(indexAfterQuote).equals("\""))
                indexAfterQuote++;
            indexAfterQuote ++;
        }
        else features[featureNumber]=0;
        featureNumber++;

        if (!USE_QUOTE) featureNumber--;


        for (;indexAfterQuote<input.size() && tokenSet.size()<length; indexAfterQuote++){
            if (input.get(indexAfterQuote).equals("\""))
                continue;

            tokenSet.add(input.get(indexAfterQuote));
        }

        DataTuple tuple;

        //features[featureNumber] = tokenSet.size();
        features[featureNumber] = Math.min(input.size(), Constants.DEFINITION_CLASSIFICATION_NAIVE_BAYES_NUMBER_TOKENS);
        if (input.size()<Constants.DEFINITION_CLASSIFICATION_NAIVE_BAYES_NUMBER_TOKENS)
            tuple =  new DataTuple(Constants.CATEGORY_NEGATIVE, tokenSet.toArray(new String[tokenSet.size()]),features);
        else tuple =  new DataTuple(categoryType, tokenSet.toArray(new String[tokenSet.size()]),features);

        return tuple;

    }
}