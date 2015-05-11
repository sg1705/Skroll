package com.skroll.analyzer.model;

import com.skroll.document.CoreMap;
import com.skroll.document.DocumentHelper;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.util.WordHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by wei on 5/10/15.
 */
public class ParaProcessor {
    // create a copy of paragraph and annotate it further for training
    static CoreMap processParagraph(CoreMap paragraph) {
        CoreMap trainingParagraph = new CoreMap();
        List<Token> tokens = paragraph.getTokens();
        List<Token> newTokens = new ArrayList<>();

        Set<String> wordSet = new HashSet<>();
        if (tokens.size() > 0 && WordHelper.isQuote(tokens.get(0).getText()))
            trainingParagraph.set(CoreAnnotations.StartsWithQuote.class, true);

        boolean inQuotes = false; // flag for annotating if a token is in quotes or not
        int i = 0;
        for (Token token : tokens) {
            if (WordHelper.isQuote(token.getText())) {
                inQuotes = !inQuotes;
                continue;
            }
            if (inQuotes) {
                token.set(CoreAnnotations.InQuotesAnnotation.class, true);
            }
            token.set(CoreAnnotations.IndexInteger.class, i++);
            wordSet.add(token.getText());
            newTokens.add(token);
        }

        trainingParagraph.set(CoreAnnotations.WordSetForTrainingAnnotation.class, wordSet);
        trainingParagraph.set(CoreAnnotations.TokenAnnotation.class, newTokens);

        // put defined terms from paragraph in trainingParagraph
        // todo: may remove this later if trainer creates a training paragraph and put defined terms there directly
        List<List<Token>> definedTokens = paragraph.get(CoreAnnotations.DefinedTermTokensAnnotation.class);
        if (definedTokens != null && definedTokens.size() > 0) {
            trainingParagraph.set(CoreAnnotations.IsDefinitionAnnotation.class, true);
        }
        trainingParagraph.set(CoreAnnotations.DefinedTermTokensAnnotation.class,
                paragraph.get(CoreAnnotations.DefinedTermTokensAnnotation.class));

        return trainingParagraph;
    }


    // print processedPara for testing purpose
    static void print(CoreMap processedPara) {
        Set<String> wordSet = processedPara.get(CoreAnnotations.WordSetForTrainingAnnotation.class);
        System.out.println((wordSet));
        List<Token> processedTokens = processedPara.get(CoreAnnotations.TokenAnnotation.class);
        List<String> strings = DocumentHelper.getTokenString(processedTokens);
        System.out.println(strings);

        for (Token token : processedTokens) {
            System.out.print(token.get(CoreAnnotations.InQuotesAnnotation.class) + " ");
        }
        System.out.println();

    }

}
