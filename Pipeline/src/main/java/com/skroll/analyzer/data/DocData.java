package com.skroll.analyzer.data;

import com.skroll.analyzer.model.ModelRVSetting;
import com.skroll.analyzer.model.bn.config.NBFCConfig;
import com.skroll.analyzer.model.bn.SimpleDataTuple;
import com.skroll.document.Document;

import java.util.List;

/**
 * Created by wei on 5/10/15.
 */
public class DocData {

    NBFCConfig config;
    SimpleDataTuple[] tuples;
    int[] docFeatureValues;

    public DocData(Document doc, NBFCConfig config) {
        this.config = config;
        tuples = new SimpleDataTuple[doc.getParagraphs().size()];
    }

    public SimpleDataTuple[] getTuples() {
        return tuples;
    }

    public int[] getDocFeatureValues() {
        return docFeatureValues;
    }

    public void setTuples(SimpleDataTuple[] tuples) {
        this.tuples = tuples;
    }

    public void setDocFeatureValues(int[] docFeatureValues) {
        this.docFeatureValues = docFeatureValues;
    }
}
