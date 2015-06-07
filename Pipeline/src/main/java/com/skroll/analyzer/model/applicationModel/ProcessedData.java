package com.skroll.analyzer.model.applicationModel;

import com.skroll.analyzer.data.NBFCData;
import com.skroll.document.CoreMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wei on 6/7/15.
 */
public class ProcessedData {
    List<CoreMap> paras = new ArrayList<>();
    NBFCData data = new NBFCData();

    public ProcessedData(List<CoreMap> paras, NBFCData data) {
        this.paras = paras;
        this.data = data;
    }

    public List<CoreMap> getParas() {
        return paras;
    }

    public NBFCData getData() {
        return data;
    }
}
