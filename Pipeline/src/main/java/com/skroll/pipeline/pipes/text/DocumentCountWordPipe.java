package com.skroll.pipeline.pipes.text;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.SyncPipe;

import java.util.*;

/**
 * Created by sagupta on 12/15/14.
 */
public class DocumentCountWordPipe extends SyncPipe<List<String>, List<String>> {

    @Override
    public List<String> process(List<String> input) {
        // create a pipeline for each word
        Pipeline<List<String>, List<String>> pipeline = new Pipeline.Builder<List<String>, List<String>>()
                .add(Pipes.DOCUMENT_TOKENIZE_WORD)
                .build();

        List<String> words = pipeline.process(input);

        // for each item in the list
        Iterator<String> iterator = words.iterator();

        HashMap<String, Integer> wordMap = Maps.newHashMap();
        for (int ii = 0; ii < words.size(); ii++) {
            Integer count = wordMap.get(words.get(ii));
            if (count == null) {
                count = 1;
            } else {
                count = count + 1;
            }
            wordMap.put(words.get(ii), count);
        }
        // covert to entry
        List<Map.Entry<String, Integer>> entries = new ArrayList<Map.Entry<String, Integer>>(wordMap.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(
                    Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) {
                return entry1.getValue().compareTo(entry2.getValue()) * -1;
            }
        });
        ;
        List<String> output = new ArrayList<String>();
        //print top 100
        for (int jj = 0; jj < entries.size(); jj++) {
            String out = Joiner.on(",").join(entries.get(jj).getKey(),entries.get(jj).getValue() );
            output.add(out);
        }

        return this.target.process(output);
    }
}
