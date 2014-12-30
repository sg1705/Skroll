package com.skroll.parser.extractor.file.html;

import com.skroll.document.Document;
import com.skroll.document.Document;
import com.skroll.document.ModelHelper;
import com.skroll.pipeline.SyncPipe;
import com.skroll.pipeline.util.Utils;

/**
 * Created by sagupta on 12/16/14.
 */
public class SaveHtmlDocumentToFilePipe extends SyncPipe<Document, Document> {

    @Override
    public Document process(Document input) {
        //get file name from config
        String fileName = (String)config.get(0);
        String jsonString = ModelHelper.getJson(input);
        Utils.writeToFile(fileName, jsonString);
        return this.target.process(input);
    }
}