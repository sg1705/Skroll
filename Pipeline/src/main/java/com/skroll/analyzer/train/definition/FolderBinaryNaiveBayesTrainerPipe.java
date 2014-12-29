package com.skroll.analyzer.train.definition;

import com.google.common.collect.Lists;
import com.skroll.analyzer.model.nb.BinaryNaiveBayesModel;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.SyncPipe;

import java.io.File;
import java.util.List;

/**
 *
 * Created by saurabh on 12/21/14.
 */
public class FolderBinaryNaiveBayesTrainerPipe extends SyncPipe<String, List<String>> {

    @Override
    public List<String> process(String folderName) {
        BinaryNaiveBayesModel model = (BinaryNaiveBayesModel)config.get(0);
        int categoryType = (Integer)config.get(1);

        File folder = new File(folderName);
        File[] listOfFiles = folder.listFiles();
        for (File file:listOfFiles){
            Pipeline<String, List<String>> fileAnalyzer =
                    new Pipeline.Builder<String, String>()
                            .add(Pipes.FILE_BINARY_NAIVE_BAYES_TRAINER,
                                    Lists.newArrayList(model, categoryType))
                            .build();
            fileAnalyzer.process(folderName+'/'+file.getName());
        }

        //System.out.println(model.showWordsImportance());
        return null;
    }


}
