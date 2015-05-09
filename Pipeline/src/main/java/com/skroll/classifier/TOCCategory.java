package com.skroll.classifier;

import com.skroll.analyzer.model.RandomVariableType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * TOC Classifier
 * <p/>
 * Created by saurabh on 3/27/2015
 */
public class TOCCategory extends Category {

    public static final Logger logger = LoggerFactory.getLogger(TOCCategory.class);

    //private String modelName = "com.skroll.analyzer.model.TrainingDocumentAnnotatingModel.TOC";

    public TOCCategory() {
        super(2,"com.skroll.classifier.TOCCategory");
        wordType = RandomVariableType.WORD_IS_TOC_TERM;
        paraType = RandomVariableType.PARAGRAPH_HAS_TOC;

        wordFeatures = Arrays.asList(
                RandomVariableType.WORD_IN_QUOTES,
                RandomVariableType.WORD_INDEX,
                RandomVariableType.WORD_IS_BOLD,
                RandomVariableType.WORD_IS_UNDERLINED,
                RandomVariableType.WORD_IS_ITALIC);

        paraFeatures = Arrays.asList(
                RandomVariableType.PARAGRAPH_NUMBER_TOKENS
        );

        paraDocFeatures = Arrays.asList(
                RandomVariableType.PARAGRAPH_NOT_IN_TABLE,
                RandomVariableType.PARAGRAPH_STARTS_WITH_BOLD,
                RandomVariableType.PARAGRAPH_STARTS_WITH_ITALIC,
                RandomVariableType.PARAGRAPH_STARTS_WITH_UNDERLINE,
                RandomVariableType.PARAGRAPH_ALL_WORDS_UPPERCASE,
                RandomVariableType.PARAGRAPH_IS_CENTER_ALIGNED,
                RandomVariableType.PARAGRAPH_HAS_ANCHOR
        );

        docFeatures = Arrays.asList(
                RandomVariableType.DOCUMENT_TOC_NOT_IN_TABLE,
                RandomVariableType.DOCUMENT_TOC_IS_BOLD,
                RandomVariableType.DOCUMENT_TOC_IS_ITALIC,
                RandomVariableType.DOCUMENT_TOC_IS_UNDERLINED,
                RandomVariableType.DOCUMENT_TOC_HAS_WORDS_UPPERCASE,
                RandomVariableType.DOCUMENT_TOC_IS_CENTER_ALIGNED,
                RandomVariableType.DOCUMENT_TOC_HAS_ANCHOR
        );
        wordVarList = Arrays.asList(
                RandomVariableType.WORD,
                RandomVariableType.FIRST_WORD
        );


    }

}
