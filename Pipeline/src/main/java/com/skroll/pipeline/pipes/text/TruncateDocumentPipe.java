package com.skroll.pipeline.pipes.text;

import com.skroll.pipeline.SyncPipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sagupta on 12/14/14.
 */
public class TruncateDocumentPipe extends SyncPipe<List<String>, List<String>> {

    @Override
    public List<String> process(List<String> input) {
        List<String> newList = new ArrayList<String>();
        int count = 1000;
        if (input.size()  < 1000) {
            count = input.size();
        }
        for(int ii = 0; ii < count; ii++) {
            String str = input.get(ii);
            newList.add(str);
        }
        // do something
        return this.target.process(newList);
    }

}
