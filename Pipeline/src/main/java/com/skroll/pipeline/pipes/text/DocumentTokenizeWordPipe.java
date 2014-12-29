package com.skroll.pipeline.pipes.text;

import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.SyncPipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by sagupta on 12/15/14.
 */
public class DocumentTokenizeWordPipe extends SyncPipe<List<String>, List<String>> {

    @Override
    public List<String> process(List<String> input) {
        // create a pipeline for each word
        Pipeline<String, List<String>> pipeline = new Pipeline.Builder<List<String>, List<String>>()
                .add(Pipes.STOP_WORD_FILTER)
                .build();

        List<String> output = new ArrayList<String>();
        // for each item in the list
        Iterator<String> iterator = input.iterator();
        while (iterator.hasNext()) {
            List<String> out = new ArrayList<String>();
            out = pipeline.process(iterator.next());
            output.addAll(out);
        }
        return this.target.process(output);
    }
}
