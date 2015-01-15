package com.skroll.analyzer.train.definition.data;

import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.pipeline.SyncPipe;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sagupta on 12/14/14.
 */
public class ExtractDefinitionsFromParagraphInHtmlDocumentPipe extends SyncPipe<Document, Document> {

    @Override
    public Document process(Document input) {

        for(CoreMap paragraph : input.getParagraphs()) {
            List<String> newList = new ArrayList<String>();
            String str = paragraph.getText();
            Pattern p = Pattern.compile( "\"([^\"]*)\"" );
            Matcher m = p.matcher( str );
            if (paragraph.getText()
                    //.replaceFirst(" +","") //uncomment to ignore starting spaces
                    .startsWith("\"")) {
                while (m.find()) {
                    newList.add(m.group(1));
                }
            }
            DocumentHelper.setDefinition(newList, paragraph);
//            paragraph.setDefinitions(newList);
//            if (newList.size() > 0) {
//                paragraph.setDefinition(true);
//            } else {
//                paragraph.setDefinition(false);
//            }
        }
        return this.target.process(input);
    }

}
