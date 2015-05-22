package com.skroll.analyzer.model;

import com.skroll.analyzer.model.bn.SimpleDataTuple;
import com.skroll.classifier.Category;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.Token;
import com.skroll.document.annotation.CategoryAnnotationHelper;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.util.WordHelper;

import java.util.*;

/**
 * Created by wei2learn on 1/24/2015.
 */
public class DocumentAnnotatingHelper {


    //PARAGRAPH_NUMBER_TOKENS can be from 0 to PFS_TOKENS_NUMBER_FEATURE_MAX,
    //so number of possibilities is one more than PFS_TOKENS_NUMBER_FEATURE_MAX
    public static final int PFS_TOKENS_NUMBER_FEATURE_MAX =
            RandomVariableType.PARAGRAPH_NUMBER_TOKENS.getFeatureSize()-1;



    // create a copy of paragraph and annotate it further for training
    // may consider use different method for training different paragraph type.
    static CoreMap processParagraph(CoreMap paragraph, int numWords){
        CoreMap trainingParagraph = new CoreMap();
        List<Token> tokens = paragraph.getTokens();
        List<Token> newTokens = new ArrayList<>();

        Set<String> wordSet = new LinkedHashSet<>(); //use LinkedHashSet to maintain order.

        if (tokens.size()>0 && WordHelper.isQuote(tokens.get(0).getText()))
            trainingParagraph.set(CoreAnnotations.StartsWithQuote.class, true);

        boolean inQuotes=false; // flag for annotating if a token is in quotes or not
        int i=0;
        for (Token token: tokens){
            if (i==numWords) break;

            if (WordHelper.isQuote(token.getText())) {
                inQuotes = !inQuotes;
                continue;
            }
            if (inQuotes){
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
       // List<List<Token>> definedTokens = paragraph.get(CoreAnnotations.DefinedTermTokensAnnotation.class);
       // if (definedTokens != null && definedTokens.size()>0) {
           //  trainingParagraph.set(CoreAnnotations.IsDefinitionAnnotation.class, true);
       // }
        CategoryAnnotationHelper.setDInCategoryAnnotation(trainingParagraph,CategoryAnnotationHelper.getDefinedTermTokensInParagraph(paragraph));

        CategoryAnnotationHelper.setCategoryAnnotation(trainingParagraph,CategoryAnnotationHelper.getTokensInParagraph(paragraph,Category.TOC_1),Category.TOC_1);

        return trainingParagraph;
    }


    static List<String[]> getWordsList(CoreMap processedPara, List<RandomVariableType> wordVarList){
        List<String[]> words = new ArrayList<>();
        for (int i=0; i<wordVarList.size();i++) {
            words.add(getWords(processedPara, wordVarList.get(i)));
        }
        return words;
    }
    public static SimpleDataTuple makeDataTuple(CoreMap originalPara, CoreMap processedPara, RandomVariableType paraCategory,
                                                List<RandomVariableType> paraFeatures, int[] documentFeatures,
                                                List<RandomVariableType> wordVarList){

        int [] values = new int[paraFeatures.size() + documentFeatures.length+1];
        int index=0;
        values[index++] = getParagraphFeature(originalPara, processedPara, paraCategory);

        for (int i=0; i<paraFeatures.size();i++){
            values[index++] = getParagraphFeature(originalPara,processedPara, paraFeatures.get(i));
        }
        for (int i=0; i<documentFeatures.length; i++)
            values[index++] = documentFeatures[i];

        return new SimpleDataTuple( getWordsList(processedPara, wordVarList), values);
    }




    public static SimpleDataTuple makeDataTupleWithOnlyFeaturesObserved(
            CoreMap originalPara, CoreMap processedPara,
            List<RandomVariableType> features, int docFeatureLen, List<RandomVariableType> wordVarList){

        int [] featureValues = new int[features.size() + docFeatureLen+1];
        int index=0;
        featureValues[index++] = -1;

        for (int i=0; i<features.size();i++){
            featureValues[index++] = getParagraphFeature(originalPara, processedPara, features.get(i));
        }
        for (int i=0; i<docFeatureLen; i++)
            featureValues[index++] = -1;

        return new SimpleDataTuple(getWordsList(processedPara, wordVarList),featureValues);
    }

    //todo: we're check both processedParagraphs and originalParas. But should probably combine the information and just check one.
    public static int[] generateDocumentFeatures(List<CoreMap> originalParas, List<CoreMap> processedParagraphs,
                                                 RandomVariableType paraCategory,
                                                   List<RandomVariableType> docFeatures,
                                                   List<RandomVariableType> paraFeaturesExistAtDocLevel){
        int[] docFeatureValues = new int[docFeatures.size()];

        Arrays.fill(docFeatureValues, 1);
        //for( CoreMap paragraph : processedParagraphs) {
        for (int p=0; p<processedParagraphs.size();p++){
            CoreMap paragraph = processedParagraphs.get(p);
            for (int f=0; f< docFeatureValues.length; f++){
                if (getParagraphFeature(originalParas.get(p), paragraph, paraCategory)==1)
                    docFeatureValues[f] &= (getParagraphFeature(originalParas.get(p), paragraph, paraFeaturesExistAtDocLevel.get(f)));
            }
        }
        return docFeatureValues;
    }

    // remove quotes and duplicate words
    static String[] getNBWords(CoreMap paragraph){
        Set<String> wordSet = paragraph.get(CoreAnnotations.WordSetForTrainingAnnotation.class);
        return wordSet.toArray(new String[wordSet.size()]);
    }

    static String[] getWords(CoreMap paragraph, RandomVariableType wordVar){
        if (paragraph==null) return null;
        Set<String> wordSet = paragraph.get(CoreAnnotations.WordSetForTrainingAnnotation.class);
        if (wordSet == null || wordSet.size()==0) return new String[0];

        String[] wordArray =  wordSet.toArray(new String[wordSet.size()]);
        switch (wordVar) {
            case FIRST_WORD:
                return new String[]{wordArray[0]};
            case WORD:
                return wordArray;
        }
        return null;
    }

    public static void addParagraphTermAnnotation(CoreMap paragraph, RandomVariableType paraType, List<Token> terms){
        if (paragraph==null) return;
        switch (paraType) {
            case PARAGRAPH_HAS_DEFINITION:
                CategoryAnnotationHelper.addDefinedTokensInCategoryAnnotation(paragraph, terms);
                return;
            case PARAGRAPH_HAS_TOC_1:
                CategoryAnnotationHelper.addTokensInCategoryAnnotation(paragraph, terms, Category.TOC_1);
                return;
            case PARAGRAPH_HAS_TOC_2:
                CategoryAnnotationHelper.addTokensInCategoryAnnotation(paragraph, terms, Category.TOC_2);
                return;
            case PARAGRAPH_HAS_TOC_3:
                CategoryAnnotationHelper.addTokensInCategoryAnnotation(paragraph, terms, Category.TOC_3);
                return;
            case PARAGRAPH_HAS_TOC_4:
                CategoryAnnotationHelper.addTokensInCategoryAnnotation(paragraph, terms, Category.TOC_4);
                return;
            case PARAGRAPH_HAS_TOC_5:
                CategoryAnnotationHelper.addTokensInCategoryAnnotation(paragraph, terms, Category.TOC_5);
                return;
        }
        return;
    }

    static void setParagraphFeature(CoreMap paragraph, RandomVariableType feature, int value){
        if (paragraph==null) return;
        switch (feature) {
            case PARAGRAPH_INDEX:
                paragraph.set(CoreAnnotations.IndexInteger.class, value);
                return;

        }
        return;
    }

    static boolean isParaObserved(CoreMap para){
        Boolean isObserved = para.get(CoreAnnotations.IsUserObservationAnnotation.class);
        if (isObserved==null) isObserved = false;
        return  isObserved;
    }


    /**
     *
     * @param paragraph
     * @param processedPara contains extra annotation that are computed from the original annotations from paragraph
     * @param feature
     * @return
     */
    static int getParagraphFeature(CoreMap paragraph, CoreMap processedPara, RandomVariableType feature){
        // return false for empty paragraph
        if (paragraph==null) return 0;
        List<Token> tokens = processedPara.getTokens();
        if (tokens==null || tokens.size()==0) return 0;
        switch (feature){
            case PARAGRAPH_HAS_DEFINITION:
                return booleanToInt(CategoryAnnotationHelper.isCategoryId(paragraph,Category.DEFINITION));
            case PARAGRAPH_NUMBER_TOKENS:
                Set<String> words = processedPara.get(CoreAnnotations.WordSetForTrainingAnnotation.class);
                int num=0;
                if (words!=null) num = words.size();
                return Math.min(num, PFS_TOKENS_NUMBER_FEATURE_MAX);

            case PARAGRAPH_STARTS_WITH_QUOTE:
                return booleanToInt(processedPara.get(CoreAnnotations.StartsWithQuote.class));
            case PARAGRAPH_STARTS_WITH_BOLD:
                return booleanToInt(tokens.get(0).get(CoreAnnotations.IsBoldAnnotation.class ));
            case PARAGRAPH_STARTS_WITH_UNDERLINE:
                return booleanToInt(tokens.get(0).get(CoreAnnotations.IsUnderlineAnnotation.class ));
            case PARAGRAPH_STARTS_WITH_ITALIC:
                return booleanToInt(tokens.get(0).get(CoreAnnotations.IsItalicAnnotation.class ));
            case PARAGRAPH_INDEX:  return paragraph.get(CoreAnnotations.IndexInteger.class );

            case PARAGRAPH_WORDS_STARTS_WITH_UPPERCASE_COUNT:
                int tokenSize = paragraph.get(CoreAnnotations.TokenAnnotation.class).size();
                if (tokenSize != 0) {
                    float fraction =  paragraph.get(CoreAnnotations.StartsWithUpperCaseCountInteger.class) / tokenSize;
                    if (fraction > 0.7)
                        return 1;
                }
                return 0;
            case PARAGRAPH_ALL_WORDS_UPPERCASE:
                return booleanToInt(paragraph.get(CoreAnnotations.IsItalicAnnotation.class));
            case PARAGRAPH_IS_CENTER_ALIGNED:
                return booleanToInt(paragraph.get(CoreAnnotations.IsCenterAlignedAnnotation.class ));
            case PARAGRAPH_HAS_ANCHOR:
                return booleanToInt(paragraph.get(CoreAnnotations.IsAnchorAnnotation.class ));
            case PARAGRAPH_HAS_TOC_1:
                return booleanToInt(CategoryAnnotationHelper.isCategoryId(paragraph, Category.TOC_1));
            case PARAGRAPH_HAS_TOC_2:
                return booleanToInt(CategoryAnnotationHelper.isCategoryId(paragraph, Category.TOC_2));
            case PARAGRAPH_HAS_TOC_3:
                return booleanToInt(CategoryAnnotationHelper.isCategoryId(paragraph, Category.TOC_3));
            case PARAGRAPH_HAS_TOC_4:
                return booleanToInt(CategoryAnnotationHelper.isCategoryId(paragraph, Category.TOC_4));
            case PARAGRAPH_HAS_TOC_5:
                return booleanToInt(CategoryAnnotationHelper.isCategoryId(paragraph, Category.TOC_5));

            case PARAGRAPH_NOT_IN_TABLE:
                return 1-booleanToInt(paragraph.get(CoreAnnotations.IsInTableAnnotation.class));

        }
        return -1;
    }

    /**
     * returns 0 if input parameter false, 1 if true
     * @param b
     * @return
     */
    static int booleanToInt(Boolean b){
        if (b==null) return 0;
        return b ? 1:0;
    }
    /**
     * todo: consider removing paragraph parameter, and store info in just word tokens.
     * @param paragraph
     * @param word
     * @param feature
     * @return
     */
    static int getWordFeature(CoreMap paragraph, Token word, RandomVariableType feature){
        switch (feature){
            case WORD_IS_DEFINED_TERM:
                List<List<Token>> tokens =  CategoryAnnotationHelper.getDefinedTermTokensInParagraph(paragraph);
                if (tokens==null) return 0;
                for (List<Token> list: tokens)
                    for (Token t:list) //todo: matching string instead of matching token reference is a hack here.
                        if (t.getText().equals(word.getText())) return 1;
                return 0;
            case WORD_IN_QUOTES:

                return booleanToInt(word.get(CoreAnnotations.InQuotesAnnotation.class ));
            case WORD_IS_BOLD:
                return booleanToInt(word.get(CoreAnnotations.IsBoldAnnotation.class ));
            case WORD_IS_UNDERLINED:
                return booleanToInt(word.get(CoreAnnotations.IsUnderlineAnnotation.class ));
            case WORD_IS_ITALIC:
                return booleanToInt(word.get(CoreAnnotations.IsItalicAnnotation.class ));
            case WORD_INDEX:  return word.get(CoreAnnotations.IndexInteger.class );


            case WORD_IS_TOC_1_TERM:

                List<Token> tocTokens =  CategoryAnnotationHelper.getTokensInParagraph(paragraph, Category.TOC_1);
                if (tocTokens==null) return 0;
                for (Token t:tocTokens)
                    if (t.getText().equals(word.getText())) return 1;

                return 0;
            case WORD_IS_TOC_2_TERM:

                tocTokens =  CategoryAnnotationHelper.getTokensInParagraph(paragraph, Category.TOC_2);
                if (tocTokens==null) return 0;
                for (Token t:tocTokens)
                    if (t.getText().equals(word.getText())) return 1;

                return 0;
            case WORD_IS_TOC_3_TERM:

                tocTokens =  CategoryAnnotationHelper.getTokensInParagraph(paragraph, Category.TOC_3);
                if (tocTokens==null) return 0;
                for (Token t:tocTokens)
                    if (t.getText().equals(word.getText())) return 1;
                return 0;
            case WORD_IS_TOC_4_TERM:

                tocTokens =  CategoryAnnotationHelper.getTokensInParagraph(paragraph, Category.TOC_4);
                if (tocTokens==null) return 0;
                for (Token t:tocTokens)
                    if (t.getText().equals(word.getText())) return 1;
                return 0;
            case WORD_IS_TOC_5_TERM:

                tocTokens =  CategoryAnnotationHelper.getTokensInParagraph(paragraph, Category.TOC_5);
                if (tocTokens==null) return 0;
                for (Token t:tocTokens)
                    if (t.getText().equals(word.getText())) return 1;

                return 0;

        }
        return -1;
    }


    static List<Token> getTokensWithoutQuotes(CoreMap paragraph){
        int length = DefinedTermExtractionModel.HMM_MODEL_LENGTH;
        List<Token> tokens = paragraph.getTokens();
        List<Token> tokensWithoutQuotes = new ArrayList<>();
        for (int i=0; i<tokens.size() && tokensWithoutQuotes.size() <= length; i++){
            tokensWithoutQuotes.add(tokens.get(i));
        }
        return tokensWithoutQuotes;
    }

    int getWordFeature(Token word, RandomVariableType feature){
        switch (feature){
            //case WORD_IS_DEFINED_TERM: return DocumentHelper.isDefinedTerm(word) ?1:0;
        }
        return -1;
    }

    public static void printAnnotatedDoc(Document doc){

        List<CoreMap> defParas = CategoryAnnotationHelper.getParaWithCategoryAnnotation(doc, Category.DEFINITION);
        for ( int i=0; i<defParas.size();i++){
            System.out.println(defParas.get(i).getText());
            System.out.print(i);
            System.out.println(CategoryAnnotationHelper.getDefinedTermTokensInParagraph(defParas.get(i)));
        }

        System.out.println( CategoryAnnotationHelper.getParaWithCategoryAnnotation(doc, Category.DEFINITION).size());
    }

    public static void clearParagraphCateoryAnnotation(CoreMap para, RandomVariableType paraType){
        switch (paraType) {
            case PARAGRAPH_HAS_TOC_1:
                CategoryAnnotationHelper.clearCategoryAnnotation(para, Category.TOC_1);
                return;
            case PARAGRAPH_HAS_TOC_2:
                CategoryAnnotationHelper.clearCategoryAnnotation(para, Category.TOC_2);
                return;
            case PARAGRAPH_HAS_TOC_3:
                CategoryAnnotationHelper.clearCategoryAnnotation(para, Category.TOC_3);
                return;
            case PARAGRAPH_HAS_TOC_4:
                CategoryAnnotationHelper.clearCategoryAnnotation(para, Category.TOC_4);
                return;
            case PARAGRAPH_HAS_TOC_5:
                CategoryAnnotationHelper.clearCategoryAnnotation(para, Category.TOC_5);
                return;
            case PARAGRAPH_HAS_DEFINITION:
                CategoryAnnotationHelper.clearCategoryAnnotation(para, Category.DEFINITION);
        }
    }

}
