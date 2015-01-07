package com.skroll.analyzer.train.definition;

import com.google.common.collect.Lists;
import com.skroll.analyzer.model.nb.BinaryNaiveBayesModel;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.SyncPipe;

import java.util.List;

/**
 *
 * Created by saurabh on 12/21/14.
 */
public class FilesBinaryNaiveBayesTrainerPipe extends SyncPipe<List<String>, List<String>> {


    private static final int MAX_SENTENCE_LENGTH = 12;

    @Override
    public List<String> process(List<String> fileNames) {
        BinaryNaiveBayesModel model = (BinaryNaiveBayesModel)config.get(0);
        int categoryType = (Integer)config.get(1);

        for (String name: fileNames){
            Pipeline<String, List<String>> fileAnalyzer =
                    new Pipeline.Builder<String, String>()
                            .add(Pipes.FILE_BINARY_NAIVE_BAYES_TRAINER,
                                    Lists.newArrayList(model, categoryType))
                            .build();
            fileAnalyzer.process(name);
        }

        //System.out.println(document.toString());
        return fileNames;
    }


}
