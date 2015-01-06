package com.skroll.analyzer.train.definition;

import com.google.common.collect.Lists;
import com.skroll.analyzer.model.nb.BinaryNaiveBayesModel;
import com.skroll.analyzer.model.nb.DataTuple;
import com.skroll.analyzer.model.nb.NaiveBayes;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.SyncPipe;
import com.skroll.pipeline.util.Constants;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by wei2learn on 1/5/2015.
 * 
 * builds a NB model using data from a file
 */
public class FileNaiveBayesTrainerPipe extends SyncPipe<String, String> {


    @Override
    public String process(String fileName) {
        NaiveBayes model = (NaiveBayes)config.get(0);
        int categoryType = (Integer)config.get(1);


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
        for (List<String> line : csvStrings){
            int length= Constants.DEFINITION_CLASSIFICATION_NAIVE_BAYES_NUMBER_TOKENS;
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


            for (;indexAfterQuote<line.size() && tokenSet.size()<length; indexAfterQuote++){
                //if (line.get(i)==null || line.get(i).equals("")) continue;
                if (line.get(indexAfterQuote).equals("\""))
                    continue;

                tokenSet.add(line.get(indexAfterQuote));
            }
//            if (tokenSet.contains("\""))
//                System.out.println(line);
//            //if (tokenSet.size()<length) continue;
//            if (tokenSet.size()<3){
//                //if (tokenSet.contains(null)) {
//                if (tokenSet.size()==2) {
//                    System.out.println(line);
//                    System.out.println(tokenSet);
//                }
//
//                //}
//                continue;
//            }
            DataTuple tuple;

            features[1] = tokenSet.size();


            if (tokenSet.size()<Constants.DEFINITION_CLASSIFICATION_NAIVE_BAYES_NUMBER_TOKENS)
                tuple =  new DataTuple(Constants.CATEGORY_NEGATIVE, tokenSet.toArray(new String[tokenSet.size()]),features);
            else tuple =  new DataTuple(categoryType, tokenSet.toArray(new String[tokenSet.size()]),features);

            model.addSample(tuple);
        }

        return fileName;
    }


}