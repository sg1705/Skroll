package com.skroll.pipeline.pipes.text;

import com.aliasi.util.Strings;
import com.skroll.pipeline.SyncPipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sagupta on 12/14/14.
 */
public class CSVSplitIntoListOfString extends SyncPipe<List<String>, List<List<String>>> {

    @Override
    public List<List<String>> process(List<String> input) {
        List<List<String>> newList = new ArrayList<List<String>>();

        for(int ii = 0; ii < input.size(); ii++) {
            newList.add(Arrays.asList(Strings.split(input.get(ii),',')));
        }
        // do something
        return newList;
    }

}
