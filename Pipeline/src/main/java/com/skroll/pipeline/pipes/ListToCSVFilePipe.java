package com.skroll.pipeline.pipes;

import com.google.common.base.Joiner;
import com.skroll.pipeline.SyncPipe;
import com.skroll.pipeline.util.Utils;

import java.util.List;

/**
 * Created by sagupta on 12/16/14.
 */
public class ListToCSVFilePipe extends SyncPipe<List<String>, List<String>> {

    @Override
    public List<String> process(List<String> input) {
        //get file name from config
        String fileName = (String)config.get(0);
        String csvString = Joiner.on("\n").join(input);
        Utils.writeToFile(fileName, csvString);
        return this.target.process(input);
    }
}