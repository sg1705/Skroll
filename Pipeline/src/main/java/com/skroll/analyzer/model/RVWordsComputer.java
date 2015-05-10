package com.skroll.analyzer.model;

import com.skroll.document.CoreMap;

import java.util.List;

/**
 * Created by wei on 5/9/15.
 */
public interface RVWordsComputer {
    public List<String> getWords(CoreMap m);

    public List<String> getWords(CoreMap m, int maxNum);
}
