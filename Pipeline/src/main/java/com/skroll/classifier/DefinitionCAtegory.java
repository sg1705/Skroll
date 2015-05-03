package com.skroll.classifier;

import com.skroll.analyzer.model.RandomVariableType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Created by saurabhagarwal on 1/18/15.
 */
public class DefinitionCategory extends Category{

    public static final Logger logger = LoggerFactory
            .getLogger(DefinitionCategory.class);

   // private  String modelName = "com.skroll.analyzer.model.TrainingDocumentAnnotatingModel.DefinitionDTEM";
   public DefinitionCategory() {
       super(1, "DefinitionCategory");
        wordType = RandomVariableType.WORD_IS_DEFINED_TERM;
        paraType = RandomVariableType.PARAGRAPH_HAS_DEFINITION;

        wordFeatures = Arrays.asList(
               RandomVariableType.WORD_IN_QUOTES,
               RandomVariableType.WORD_IS_UNDERLINED
       );
       paraFeatures = Arrays.asList(
               RandomVariableType.PARAGRAPH_NUMBER_TOKENS);

        paraDocFeatures = Arrays.asList(
               RandomVariableType.PARAGRAPH_STARTS_WITH_UNDERLINE,
               RandomVariableType.PARAGRAPH_STARTS_WITH_BOLD,

               RandomVariableType.PARAGRAPH_STARTS_WITH_QUOTE
       );

        docFeatures = Arrays.asList(
               RandomVariableType.DOCUMENT_DEFINITIONS_IS_UNDERLINED,
               RandomVariableType.DOCUMENT_DEFINITIONS_IS_BOLD,
               RandomVariableType.DOCUMENT_DEFINITIONS_IN_QUOTES

       );

       wordVarList = Arrays.asList(
               RandomVariableType.WORD
       );
   }

}
