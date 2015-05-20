package com.skroll.classifier;

import com.skroll.analyzer.model.RandomVariableType;

import java.util.Arrays;
import java.util.List;

/**
 * Created by saurabhagarwal on 1/5/15.
 */
public class Category {

    public static final int NONE = 0;
    public static final int DEFINITION = 1;
    //public static final int TOC = 2;
    public static final int TOC_1 = 2;
    public static final int TOC_2 = 3;
    public static final int TOC_3 = 4;
    public static final int TOC_4 = 5;
    public static final int TOC_5 = 6;

    int id;
    String name;

    public static List<Integer> getCategories()  {
        return Arrays.asList(Category.DEFINITION,Category.TOC_1,Category.TOC_2,Category.TOC_3,Category.TOC_4,Category.TOC_5 );
    }

    RandomVariableType wordType = RandomVariableType.WORD_IS_TOC_TERM;
    RandomVariableType paraType = RandomVariableType.PARAGRAPH_HAS_TOC;

    List<RandomVariableType> wordFeatures = Arrays.asList(
            RandomVariableType.WORD_IN_QUOTES,
            RandomVariableType.WORD_INDEX,
            RandomVariableType.WORD_IS_BOLD,
            RandomVariableType.WORD_IS_UNDERLINED,
            RandomVariableType.WORD_IS_ITALIC);

    List<RandomVariableType> paraFeatures = Arrays.asList(
            RandomVariableType.PARAGRAPH_NUMBER_TOKENS
    );

    List<RandomVariableType> paraDocFeatures = Arrays.asList(
            RandomVariableType.PARAGRAPH_NOT_IN_TABLE,
            RandomVariableType.PARAGRAPH_STARTS_WITH_BOLD,
            RandomVariableType.PARAGRAPH_STARTS_WITH_ITALIC,
            RandomVariableType.PARAGRAPH_STARTS_WITH_UNDERLINE,
            RandomVariableType.PARAGRAPH_ALL_WORDS_UPPERCASE,
            RandomVariableType.PARAGRAPH_IS_CENTER_ALIGNED,
            RandomVariableType.PARAGRAPH_HAS_ANCHOR
    );

    List<RandomVariableType> docFeatures = Arrays.asList(
            RandomVariableType.DOCUMENT_TOC_NOT_IN_TABLE,
            RandomVariableType.DOCUMENT_TOC_IS_BOLD,
            RandomVariableType.DOCUMENT_TOC_IS_ITALIC,
            RandomVariableType.DOCUMENT_TOC_IS_UNDERLINED,
            RandomVariableType. DOCUMENT_TOC_HAS_WORDS_UPPERCASE,
            RandomVariableType.DOCUMENT_TOC_IS_CENTER_ALIGNED,
            RandomVariableType.DOCUMENT_TOC_HAS_ANCHOR
    );
    List<RandomVariableType> wordVarList = Arrays.asList(
            RandomVariableType.WORD,
            RandomVariableType.FIRST_WORD
    );

    public Category(int id, String name){
        this.id =id;
        this.name=name;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString(){
        return "id("+id+"),name("+name +")";
    }

    public RandomVariableType getWordType() {
        return wordType;
    }

    public RandomVariableType getParaType() {
        return paraType;
    }

    public List<RandomVariableType> getWordFeatures() {
        return wordFeatures;
    }

    public List<RandomVariableType> getParaFeatures() {
        return paraFeatures;
    }

    public List<RandomVariableType> getParaDocFeatures() {
        return paraDocFeatures;
    }

    public List<RandomVariableType> getDocFeatures() {
        return docFeatures;
    }

    public List<RandomVariableType> getWordVarList() {
        return wordVarList;
    }
}


