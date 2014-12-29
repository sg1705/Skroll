package com.skroll.parser.extractor.file.html;

import com.skroll.document.HtmlDocument;
import com.skroll.document.ModelHelper;
import com.skroll.pipeline.SyncPipe;
import com.skroll.pipeline.util.Utils;

/**
 * Created by sagupta on 12/16/14.
 */
public class SaveHtmlDocumentToFilePipe extends SyncPipe<HtmlDocument, HtmlDocument> {

    @Override
    public HtmlDocument process(HtmlDocument input) {
        //get file name from config
        String fileName = (String)config.get(0);
        String jsonString = ModelHelper.getJson(input);
        Utils.writeToFile(fileName, jsonString);
        return this.target.process(input);
    }
}