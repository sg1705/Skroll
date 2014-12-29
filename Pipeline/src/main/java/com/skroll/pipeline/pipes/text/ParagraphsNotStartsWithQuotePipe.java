package com.skroll.pipeline.pipes.text;

import com.skroll.pipeline.SyncPipe;

import java.util.ArrayList;
import java.util.List;

/**
 * For a given List<String>, it filters only the String that starts with a quote
 *
 * Created by sagupta on 12/14/14.
 */
public class ParagraphsNotStartsWithQuotePipe extends SyncPipe<List<String>, List<String>> {

    @Override
    public List<String> process(List<String> input) {
        List<String> newList = new ArrayList<String>();
        String quote = "\"";
        for(int ii = 0; ii < input.size(); ii++) {
            String str = input.get(ii);
            if (!str.startsWith(quote)) {
                newList.add(str);
            }
        }
        return this.target.process(newList);
    }

}
