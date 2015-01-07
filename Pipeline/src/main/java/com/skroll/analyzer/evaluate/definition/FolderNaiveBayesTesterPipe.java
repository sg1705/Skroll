package com.skroll.analyzer.evaluate.definition;

import com.google.common.collect.Lists;
import com.skroll.analyzer.model.nb.NaiveBayes;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.SyncPipe;

import java.io.File;

/**
 * Created by wei2learn on 1/5/2015.
 */
public class FolderNaiveBayesTesterPipe extends SyncPipe<String, String> {

    @Override
    public String process(String folderName) {
        NaiveBayes model = (NaiveBayes)config.get(0);
        String output="";
        File folder = new File(folderName);
        File[] listOfFiles = folder.listFiles();
        for (File file:listOfFiles){
            Pipeline<String, String> fileAnalyzer =
                    new Pipeline.Builder<String, String>()
                            .add(Pipes.FILE_NAIVE_BAYES_TESTER,
                                    Lists.newArrayList((Object) model))
                            .build();
            output += fileAnalyzer.process(folderName+'/'+file.getName());
        }

        //System.out.println(document.showWordsImportance());
        return output;
    }


}
