package com.skroll.pipeline;

/**
 * Created by sagupta on 12/14/14.
 */
public enum Pipes {

    //TRAINING PIPES
    SAVE_TRAINED_DATA("com.skroll.pipeline.pipes.training.SaveTrainedData"),


    //HtmlDocumentPipes
    PARSE_HTML_TO_DOC("com.skroll.pipeline.pipes.document.ParseHtmlToDocumentPipe"),
    SAVE_HTML_DOCUMENT_TO_FILE("com.skroll.pipeline.pipes.document.SaveHtmlDocumentToFilePipe"),
    REMOVE_BLANK_PARAGRAPH_FROM_HTML_DOC("com.skroll.pipeline.pipes.document.RemoveBlankParagraphFromHtmlDocumentPipe"),
    REMOVE_NBSP_IN_HTML_DOC("com.skroll.pipeline.pipes.document.RemoveNBSPInHtmlDocumentPipe"),
    REPLACE_SPECIAL_QUOTE_IN_HTML_DOC("com.skroll.pipeline.pipes.document.ReplaceSpecialQuotesInHtmlDocumentPipe"),
    TOKENIZE_PARAGRAPH_IN_HTML_DOC("com.skroll.pipeline.pipes.document.TokenizeParagraphInHtmlDocumentPipe"),
    EXTRACT_DEFINITION_FROM_PARAGRAPH_IN_HTML_DOC("com.skroll.pipeline.pipes.training.ExtractDefinitionsFromParagraphInHtmlDocumentPipe"),
    FILTER_STARTS_WITH_QUOTE_IN_HTML_DOC("com.skroll.pipeline.pipes.document.FilterStartsWithQuoteInHtmlDocumentPipe"),

    // NB analyzer pipes

    // testing pipes
    //added by saurabh@
    BINARY_SIMPLE_NAIVE_BAYES_TESTER("com.skroll.analyzer.nb.BinaryNaiveBayesSimpleTestingPipe"),
    BINARY_NAIVE_BAYES_TESTING ("com.skroll.analyzer.nb.BinaryNaiveBayesTestingPipe"),
    FILE_BINARY_NAIVE_BAYES_TESTER("com.skroll.analyzer.pipes.FileBinaryNaiveBayesTesterPipe"),
    FILE_BINARY_NAIVE_BAYES_TESTER_WITH_WORDS_IMPORTANCE("com.skroll.analyzer.pipes.FileBinaryNaiveBayesTesterWithWordsImportancePipe"),
    FOLDER_BINARY_NAIVE_BAYES_TESTER("com.skroll.analyzer.pipes.FolderBinaryNaiveBayesTesterPipe"),
    HTML_DOC_BINARY_NAIVE_BAYES_TESTER("com.skroll.analyzer.pipes.HtmlDocumentNaiveBayesTester"),

    // training pipes
    BINARY_NAIVE_BAYES_TRAINING ("com.skroll.analyzer.nb.BinaryNaiveBayesTrainingPipe"),
    FILE_BINARY_NAIVE_BAYES_TRAINER("com.skroll.analyzer.pipes.FileBinaryNaiveBayesTrainerPipe"),
    FILES_BINARY_NAIVE_BAYES_TRAINER("com.skroll.analyzer.pipes.FilesBinaryNaiveBayesTrainerPipe"),
    FOLDER_BINARY_NAIVE_BAYES_TRAINER("com.skroll.analyzer.pipes.FolderBinaryNaiveBayesTrainerPipe"),


    //HMM
    HTML_HIDDEN_MARKOV_MODEL_TRAINING_PIPE("com.skroll.analyzer.hmm.HTMLHiddenMarkovModelTrainingPipe"),
    HTML_HIDDEN_MARKOV_MODEL_TESTING_PIPE("com.skroll.analyzer.hmm.HTMLHiddenMarkovModelTestingPipe"),
    FOLDER_HTML_HIDDEN_MARKOV_MODEL_TRAINING_PIPE("com.skroll.analyzer.pipes.FolderHTMLHiddenMarkovModelTrainerPipe"),
    FOLDER_HTML_HIDDEN_MARKOV_MODEL_TESTING_PIPE("com.skroll.analyzer.pipes.FolderHTMLHiddenMarkovModelTesterPipe"),


    //parser pipes
    REPLACE_SPECIAL_QUOTES_WITH_QUOTES("com.skroll.pipeline.pipes.ReplaceSpecialQuotesWithQuotes"),
    CSV_SPLIT_INTO_LIST_OF_STRING("com.skroll.pipeline.pipes.CSVSplitIntoListOfString"),
    FILE_INTO_LIST_OF_STRING ("com.skroll.pipeline.pipes.files.ConvertFileIntoListOfStringsPipe"),
    LINE_REMOVE_NBSP_FILTER ("com.skroll.pipeline.pipes.LineRemoveNBSPPipe"),
    PARAGRAPH_NOT_STARTS_WITH_QUOTE_FILTER("com.skroll.pipeline.pipes.ParagraphsNotStartsWithQuotePipe"),
    PARAGRAPH_STARTS_WITH_QUOTE_FILTER("com.skroll.pipeline.pipes.ParagraphsStartsWithQuotePipe"),
    LIST_TO_CSV_FILE("com.skroll.pipeline.pipes.ListToCSVFilePipe"),
    DOCUMENT_COUNT_WORD ("com.skroll.pipeline.pipes.DocumentCountWordPipe"),
    DOCUMENT_TOKENIZE_WORD ("com.skroll.pipeline.pipes.DocumentTokenizeWordPipe"),
    PARAGRAPH_CHUNKER ("com.skroll.pipeline.pipes.DocumentChunkParagraphPipe"),
    SINK_PIPE ("com.skroll.pipeline.pipes.SinkPipe"),
    STOP_WORD_FILTER ("com.skroll.pipeline.pipes.StringStopWordTokenizerPipe"),
    PARAGRAPH_STOP_WORDS_FILTER ("com.skroll.pipeline.pipes.ParagraphStopWordTokenizerPipe"),
    PARAGRAPH_REMOVE_BLANK ("com.skroll.pipeline.pipes.ParagraphsRemoveBlankPipe");

    private final String className;

    Pipes(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

}
