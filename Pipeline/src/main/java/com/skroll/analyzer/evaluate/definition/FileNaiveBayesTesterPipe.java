package com.skroll.analyzer.evaluate.definition;

import com.google.common.collect.Lists;
import com.skroll.analyzer.model.nb.BinaryNaiveBayesModel;
import com.skroll.analyzer.model.nb.DataTuple;
import com.skroll.analyzer.model.nb.NaiveBayes;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.SyncPipe;
import com.skroll.pipeline.util.Constants;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by wei2learn on 1/5/2015.
 */
public class FileNaiveBayesTesterPipe extends SyncPipe<String, String> {

    @Override
    public String process(String fileName) {
        NaiveBayes model = (NaiveBayes)config.get(0);
        //int categoryType = (Integer)config.get(1);


        Pipeline<String, List<String>> fileIntoString =
                new Pipeline.Builder<String, List<String>>()
                        .add(Pipes.FILE_INTO_LIST_OF_STRING)
                        .build();


        Pipeline<List<String>, List<List<String>>> csvSplitPipeline =
                new Pipeline.Builder<List<String>, List<List<String>>>()
                        .add(Pipes.CSV_SPLIT_INTO_LIST_OF_STRING)
                        .build();

        List<String> fileStrings = fileIntoString.process(fileName);
        List<List<String>> csvStrings = csvSplitPipeline.process(fileStrings);

        String output="";
        output +="-----------------------------\n";
        output +=fileName+"\n";
        output +="-----------------------------\n";

        Pipeline<List<String>, DataTuple> stringsToTrainingTuple =
                new Pipeline.Builder<List<String>, DataTuple>()
                        .add(Pipes.STRINGS_TO_NAIVE_BAYES_DATA_TUPLE, Lists.newArrayList(model, -1))
                        .build();

        for (List<String> line : csvStrings){
            DataTuple tuple = stringsToTrainingTuple.process(line);
            output += String.format("%d %.2f %s\n", model.mostLikelyCategory(tuple) ,
                    model.inferCategoryProbabilityMoreStable(1, tuple.getTokens(), tuple.getFeatures()),
                    line);
        }

        return output;
    }
}
