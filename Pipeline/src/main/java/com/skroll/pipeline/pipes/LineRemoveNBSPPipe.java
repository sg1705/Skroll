package com.skroll.pipeline.pipes;


import com.skroll.pipeline.SyncPipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sagupta on 12/14/14.
 */
public class LineRemoveNBSPPipe extends SyncPipe<List<String>, List<String>> {

    @Override
    public List<String> process(List<String> input) {
        List<String> newList = new ArrayList<String>();
        for(int ii = 0; ii < input.size(); ii++) {
            String str = input.get(ii);
            str = str.replace("\u00a0", "");
            newList.add(str);
        }
        // do something
        return this.target.process(newList);
    }

}
