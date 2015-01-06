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
        for (int i = 0; i < csvStrings.size(); i++) {
            List<String> line = csvStrings.get(i);
            int length = Math.min(line.size(), Constants.DEFINITION_CLASSIFICATION_NAIVE_BAYES_NUMBER_TOKENS);

            Set<String> tokenSet = new HashSet<String>();


            // skip words inside the quotes for training
            int indexAfterQuote=0;
            int [] features = new int[2];
            if (line.get(0).equals("\"")) {
                features[0]=1;
                indexAfterQuote=1;
                while (indexAfterQuote<line.size() && !line.get(indexAfterQuote).equals("\""))
                    indexAfterQuote++;
                indexAfterQuote ++;
            }
            else features[0]=0;



            for (; indexAfterQuote<line.size() && tokenSet.size()<length; indexAfterQuote++){
                //if (line.get(j)==null || line.get(j).equals("")) continue;
                tokenSet.add(line.get(indexAfterQuote));
            }
            if (tokenSet.size()==0) continue;

            features[1] = tokenSet.size();

            DataTuple tuple = new DataTuple(-1, tokenSet.toArray(new String[length]), features);
            output += String.format("%d %.2f %s\n", model.mostLikelyCategory(tuple) ,
                    model.inferCategoryProbabilityMoreStable(1, tuple.getTokens(), tuple.getFeatures()),
                    fileStrings.get(i));
            //output += model.mostLikelyCategory(tuple) +" "+model.inferCategoryProbabilityMoreStable(1, tuple.getTokens(), tuple.getFeatures())+" "+ fileStrings.get(i) + '\n';
        }
        return output;
    }
}
