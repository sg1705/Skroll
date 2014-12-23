package com.skroll.pipeline.pipes.document;

import com.google.common.base.Joiner;
import com.skroll.model.HtmlDocument;
import com.skroll.model.ModelHelper;
import com.skroll.pipeline.SyncPipe;
import com.skroll.pipeline.util.Utils;

import java.util.List;

/**
 * Created by sagupta on 12/16/14.
 */
public class HtmlDocumentToFilePipe extends SyncPipe<HtmlDocument, HtmlDocument> {

    @Override
    public HtmlDocument process(HtmlDocument input) {
        //get file name from config
        String fileName = (String)config.get(0);
        String jsonString = ModelHelper.getJson(input);
        Utils.writeToFile(fileName, jsonString);
        return this.target.process(input);
    }
}