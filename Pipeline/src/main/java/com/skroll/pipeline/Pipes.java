package com.skroll.pipeline;

/**
 * Created by sagupta on 12/14/14.
 */
public enum Pipes {

    LINE_REMOVE_NBSP_FILTER ("com.google.paragraph.LineRemoveNBSPPipe"),
    PARAGRAPH_NOT_STARTS_WITH_QUOTE_FILTER("com.google.paragraph.ParagraphsNotStartsWithQuotePipe"),
    PARAGRAPH_STARTS_WITH_QUOTE_FILTER("com.google.paragraph.ParagraphsStartsWithQuotePipe"),
    LIST_TO_CSV_FILE("com.google.paragraph.ListToCSVFilePipe"),
    DOCUMENT_COUNT_WORD ("com.google.paragraph.DocumentCountWordPipe"),
    DOCUMENT_TOKENIZE_WORD ("com.google.paragraph.DocumentTokenizeWordPipe"),
    PARAGRAPH_CHUNKER ("com.google.paragraph.DocumentChunkParagraphPipe"),
    SINK_PIPE ("com.google.pipes.SinkPipe"),
    STOP_WORD_FILTER ("com.google.word.StringStopWordTokenizerPipe"),
    PARAGRAPH_STOP_WORDS_FILTER ("com.google.word.ParagraphStopWordTokenizerPipe"),
    PARAGRAPH_REMOVE_BLANK ("com.google.paragraph.ParagraphsRemoveBlankPipe");

    private final String className;

    Pipes(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

}
