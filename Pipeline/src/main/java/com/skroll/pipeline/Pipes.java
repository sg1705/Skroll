package com.skroll.pipeline;

/**
 * Created by sagupta on 12/14/14.
 */
public enum Pipes {

    //TRAINING PIPES
    SAVE_TRAINED_DATA("com.skroll.analyzer.train.definition.data.SaveTrainedData"),


    //HtmlDocumentPipes
    PARSE_HTML_TO_DOC("com.skroll.parser.extractor.file.html.ParseHtmlToDocumentPipe"),
    SAVE_HTML_DOCUMENT_TO_FILE("com.skroll.parser.extractor.file.html.SaveHtmlDocumentToFilePipe"),
    POST_EXTRACTION_PIPE("com.skroll.parser.tokenizer.PostExtractionPipe"),
    REMOVE_BLANK_PARAGRAPH_FROM_HTML_DOC("com.skroll.parser.tokenizer.RemoveBlankParagraphFromHtmlDocumentPipe"),
    REMOVE_NBSP_IN_HTML_DOC("com.skroll.parser.tokenizer.RemoveNBSPInHtmlDocumentPipe"),
    REMOVE_STARTING_SPACES_IN_HTML_DOC("com.skroll.parser.tokenizer.RemoveStartingSpacesInHtmlDocumentPipe"),

    REPLACE_SPECIAL_QUOTE_IN_HTML_DOC("com.skroll.parser.tokenizer.ReplaceSpecialQuotesInHtmlDocumentPipe"),
    TOKENIZE_PARAGRAPH_IN_HTML_DOC("com.skroll.parser.tokenizer.TokenizeParagraphInHtmlDocumentPipe"),
    FILTER_STARTS_WITH_QUOTE_IN_HTML_DOC("com.skroll.parser.tokenizer.FilterStartsWithQuoteInHtmlDocumentPipe"),

    // data.definition
    STRINGS_TO_NAIVE_BAYES_DATA_TUPLE("com.skroll.analyzer.data.definition.StringsToNaiveBayesDataTuplePipe"),

    //model.hmm
    HIDDEN_MARKOV_MODEL_SIMPLE_TESTING_PIPE("com.skroll.analyzer.model.hmm.HiddenMarkovModelSimpleTestingPipe"),
    HTML_HIDDEN_MARKOV_MODEL_TRAINING_PIPE("com.skroll.analyzer.model.hmm.HTMLHiddenMarkovModelTrainingPipe"),
    HTML_HIDDEN_MARKOV_MODEL_TESTING_PIPE("com.skroll.analyzer.model.hmm.HTMLHiddenMarkovModelTestingPipe"),
    HTML_HIDDEN_MARKOV_MODEL_STATE_SEQUENCE_TESTING_PIPE("com.skroll.analyzer.model.hmm.HTMLHiddenMarkovModelStateSequenceTestingPipe"),

    //train.definition.data
    EXTRACT_DEFINITION_FROM_PARAGRAPH_IN_HTML_DOC("com.skroll.analyzer.train.definition.data.ExtractDefinitionsFromParagraphInHtmlDocumentPipe"),

    //parser pipes
    REPLACE_SPECIAL_QUOTES_WITH_QUOTES("com.skroll.pipeline.pipes.text.ReplaceSpecialQuotesWithQuotes"),
    CSV_SPLIT_INTO_LIST_OF_STRING("com.skroll.pipeline.pipes.text.CSVSplitIntoListOfString"),
    FILE_INTO_LIST_OF_STRING ("com.skroll.pipeline.pipes.files.ConvertFileIntoListOfStringsPipe"),
    LINE_REMOVE_NBSP_FILTER ("com.skroll.pipeline.pipes.text.LineRemoveNBSPPipe"),
    PARAGRAPH_NOT_STARTS_WITH_QUOTE_FILTER("com.skroll.pipeline.pipes.text.ParagraphsNotStartsWithQuotePipe"),
    PARAGRAPH_STARTS_WITH_QUOTE_FILTER("com.skroll.pipeline.pipes.text.ParagraphsStartsWithQuotePipe"),
    LIST_TO_CSV_FILE("com.skroll.pipeline.pipes.text.ListToCSVFilePipe"),
    DOCUMENT_COUNT_WORD ("com.skroll.pipeline.pipes.text.DocumentCountWordPipe"),
    DOCUMENT_TOKENIZE_WORD ("com.skroll.pipeline.pipes.text.DocumentTokenizeWordPipe"),
    PARAGRAPH_CHUNKER ("com.skroll.pipeline.pipes.text.DocumentChunkParagraphPipe"),
    SINK_PIPE ("com.skroll.pipeline.pipes.SinkPipe"),
    STOP_WORD_FILTER ("com.skroll.pipeline.pipes.text.StringStopWordTokenizerPipe"),
    PARAGRAPH_STOP_WORDS_FILTER ("com.skroll.pipeline.pipes.text.ParagraphStopWordTokenizerPipe"),
    TRUNCATE_DOCUMENT("com.skroll.pipeline.pipes.text.TruncateDocumentPipe"),
    PARAGRAPH_REMOVE_BLANK ("com.skroll.pipeline.pipes.text.ParagraphsRemoveBlankPipe");

    //parser.annotator


    private final String className;

    Pipes(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

}
