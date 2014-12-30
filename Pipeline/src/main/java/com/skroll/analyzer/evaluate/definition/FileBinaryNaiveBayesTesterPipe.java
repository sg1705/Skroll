package com.skroll.analyzer.evaluate.definition;

import com.google.common.collect.Lists;
import com.skroll.analyzer.model.nb.BinaryNaiveBayesModel;
import com.skroll.document.Document;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.SyncPipe;

import java.util.List;

/**
 *
 * Created by saurabh on 12/21/14.
 */
public class FileBinaryNaiveBayesTesterPipe extends SyncPipe<String, String> {

    @Override
    public String process(String fileName) {
        BinaryNaiveBayesModel model = (BinaryNaiveBayesModel)config.get(0);
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


        Pipeline<List<List<String>>,List<Double>> analyzer =
                new Pipeline.Builder<List<List<String>>, List<Double>>()
                        .add(Pipes.BINARY_NAIVE_BAYES_TESTING,
                                Lists.newArrayList((Object)model))
                        .build();

        List<Double> probabilities = analyzer.process(csvStrings);
        String output="";
        for (int i=0; i < probabilities.size(); i++){
            output += probabilities.get(i) + fileStrings.get(i) + '\n';
        }
        //System.out.println(document.toString());
        return output;
    }


}
