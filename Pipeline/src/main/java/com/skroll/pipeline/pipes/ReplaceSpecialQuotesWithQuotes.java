package com.skroll.pipeline.pipes;

import com.skroll.pipeline.SyncPipe;
import org.jsoup.helper.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sagupta on 12/14/14.
 */
public class ReplaceSpecialQuotesWithQuotes extends SyncPipe<List<String>, List<String>> {

    @Override
    public List<String> process(List<String> input) {
        List<String> newList = new ArrayList<String>();
        for(int ii = 0; ii < input.size(); ii++) {
            String str = input.get(ii);
            str = str.replace("\u201c","\"");
            str = str.replace("\u201d","\"");
            newList.add(str);
        }
        // do something
        return this.target.process(newList);
    }

}
