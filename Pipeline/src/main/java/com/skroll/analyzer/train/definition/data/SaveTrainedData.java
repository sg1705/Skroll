package com.skroll.analyzer.train.definition.data;

import com.google.common.base.Joiner;
import com.skroll.document.*;
import com.skroll.pipeline.SyncPipe;
import com.skroll.pipeline.util.Constants;

import java.util.List;

/**
 * Saves a list of paragraphs in the specified format
 *
 * <pre>
 *     abc xyz abc\n
 *     _pdef,term1, term2,... \n
 *     __;\n
 *
 * </pre>
 *
 *
 * Each paragraph is a collection of lines separated by a '__;\n' separator
 *
 * Created by saurabh on 12/26/14.
 */
public class SaveTrainedData extends SyncPipe<Document, String> {

    @Override
    public String process(Document input) {
        StringBuilder paragraphs = new StringBuilder();
        List<Entity> paragraphList = input.getParagraphs();
        for(Entity paragraph : paragraphList) {
            StringBuilder paraString = new StringBuilder();
            // first line is the actual text
            paraString.append(paragraph.getText()).append(Constants.TRAINING_MODEL_LINE_SEPARATOR);
            // second line is the definition
            if (DocumentHelper.isDefinition(paragraph)) {
                // add a definition
                paraString
                        .append(Constants.TRAINING_MODEL_TERM_IDENTIFIER)
                        .append(Constants.TRAINING_MODEL_TOKEN_SEPARATOR)
                        .append(Joiner.
                                on(Constants.TRAINING_MODEL_TOKEN_SEPARATOR)
                                .join(DocumentHelper.getDefinedTerms(paragraph)))
                        .append(Constants.TRAINING_MODEL_LINE_SEPARATOR);
            }
            // third line is a line ender
            paraString.append(Constants.TRAINING_MODEL_PARA_SEPARATOR);
            paragraphs.append(paraString.toString());
        }
        return paragraphs.toString();
    }
}
