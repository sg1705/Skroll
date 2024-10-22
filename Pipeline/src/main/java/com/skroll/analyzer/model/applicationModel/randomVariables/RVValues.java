package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.skroll.analyzer.model.RandomVariable;
import com.skroll.classifier.Category;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.Token;
import com.skroll.document.annotation.CategoryAnnotationHelper;

import java.util.*;

/**
 * Created by wei on 5/9/15.
 */
public class RVValues {

    // RVAMap is used to get RVs values directly from annotations
    static private Map<RandomVariable, Class> rvaMap = new HashMap<>();

    // If some RV's value does not come directly from some annotation,
    // then valueComputerMap gets the computer of the value
    static private Map<RandomVariable, RVValueComputer> valueComputerMap = new HashMap<>();
    static private Map<RandomVariable, RVValueSetter> valueSetterMap = new HashMap<>();
    static private Map<RandomVariable, WRVValueComputer> wordLevelValueComputerMap = new HashMap<>();
    static private Map<RandomVariable, RVWordsComputer> wordsComputerMap = new HashMap<>();
    static private Map<RandomVariable, RandomVariable> negationRVMap = new HashMap<>();


    static Class getAnnotationClass(RandomVariable rv) {
        return rvaMap.get(rv);
    }

    static void addWordsComputer(RandomVariable rv, RVWordsComputer computer) {
        wordsComputerMap.put(rv, computer);
    }

    static void addWRVValueComputer(RandomVariable rv, WRVValueComputer computer) {
        wordLevelValueComputerMap.put(rv, computer);
    }

    static void addValueComputer(RandomVariable rv, RVValueComputer computer) {
        valueComputerMap.put(rv, computer);
    }

    public static void addValueSetter(RandomVariable rv, RVValueSetter setter) {
        valueSetterMap.put(rv, setter);
    }

    static void addAnnotationLink(RandomVariable rv, Class ann) {
        rvaMap.put(rv, ann);
    }


    public static String[] getWords(RandomVariable rv, CoreMap m) {
        RVWordsComputer computer = wordsComputerMap.get(rv);
        if (computer != null) return computer.getWords(m);
        return null;
    }

    // not really used for now, since the paragraphs are preprocessed to remove words in the back
    public static String[] getWords(RandomVariable rv, CoreMap m, int maxNumWords) {
        RVWordsComputer computer = wordsComputerMap.get(rv);
        if (computer != null) return computer.getWords(m, maxNumWords);
        return null;
    }

    static int getValueFromMap(RandomVariable rv, List<CoreMap> ms) {
        Class ann = rvaMap.get(rv);
        Object val = null;
        for (CoreMap m: ms){
            val = m.get(ann);
            if (val != null) break;
        }

        if (val == null) return 0; // todo: missing annotation is used as false. May not be the best thing to do.

//        Class valType = val.getClass();
//        if (valType == null) return 0; //todo: should we make null represent 0 or false? should we hanle this differently for different type of annotations?
//        if (valType.equals(Boolean.class)) {
        if (val instanceof Boolean) {
            return val.equals(Boolean.TRUE) ? 1 : 0;
//        } else if (valType.equals(Integer.class)){
        } else if (val instanceof Integer) {
            int intVal = (Integer) val;
            return intVal < rv.getFeatureSize() ? intVal : rv.getFeatureSize() - 1;
        }
        return -1;

    }
    public static int getValue(RandomVariable rv, List<CoreMap> m) {
        RandomVariable negationRV = negationRVMap.get(rv); // only for binary RVs
        if (negationRV != null){
            int negVal = getValueHelper(negationRV, m);
            if (negVal == -1)
                return -1; // unobserved
            return 1-negVal;
        }
        return getValueHelper(rv,m);
   }

    public static int getValueHelper(RandomVariable rv, List<CoreMap> m){
        RVValueComputer processor = valueComputerMap.get(rv);
        if (processor != null) return processor.getValue(m);
        return getValueFromMap(rv, m);
    }


    // this is for setting user observation of the paragraph category
    public static void setValue(RandomVariable rv, int val, CoreMap m, List<List<Token>> terms) {
        RVValueSetter setter = valueSetterMap.get(rv);
        setter.setValue(val, m, terms);
    }

    public static void addTerms(RandomVariable rv, CoreMap m, List<Token> terms, int value) {
        RVValueSetter setter = valueSetterMap.get(rv);
        setter.addTerms(m, terms, value);
    }

    public static void clearValue(RandomVariable rv, CoreMap m) {
        RVValueSetter setter = valueSetterMap.get(rv);
        setter.clearValue(m);
    }

    public static int getWordLevelRVValue(RandomVariable rv, Token token, List<CoreMap> paras) {

        WRVValueComputer processor = wordLevelValueComputerMap.get(rv);
        if (processor != null) return processor.getValue(token, paras);
        return getValueFromMap(rv, Arrays.asList(token));
    }

    public static void addNegationRV(RandomVariable negationRv,RandomVariable rv){
        negationRVMap.put(negationRv, rv);
    }



    /**
     * returns 0 if input parameter false, 1 if true
     *
     * @param b
     * @return
     */
    static int booleanToInt(Boolean b) {
        if (b == null) return 0;
        return b ? 1 : 0;
    }


    public static void printAnnotatedDoc(Document doc) {

        List<CoreMap> defParas = CategoryAnnotationHelper.getParagraphsAnnotatedWithCategory(doc, Category.DEFINITION);
        for (int i = 0; i < defParas.size(); i++) {
            System.out.println(defParas.get(i).getText());
            System.out.print(i);
            System.out.println(CategoryAnnotationHelper.getTokensForCategory(defParas.get(i), Category.DEFINITION));
        }
     System.out.println(CategoryAnnotationHelper.getParagraphsAnnotatedWithCategory(doc, Category.DEFINITION).size());
    }
}
