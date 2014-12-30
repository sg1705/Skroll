package com.skroll.pipeline.pipes.files;


import com.skroll.document.Document;
import com.skroll.pipeline.SyncPipe;
import com.skroll.pipeline.util.Utils;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sagupta on 12/14/14.
 */
public class ConvertFileIntoListOfStringsPipe extends SyncPipe<String, List<String>> {

    @Override
    public List<String> process(String input) {
        // input is the file name
        File file = new File(input);
        List<String> newList = new ArrayList<String>();
        try {
            newList = Files.readLines(file, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newList;
    }

}
