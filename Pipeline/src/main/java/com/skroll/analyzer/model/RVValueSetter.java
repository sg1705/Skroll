package com.skroll.analyzer.model;

import com.skroll.document.CoreMap;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotation;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.document.annotation.TypesafeMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wei on 5/19/15.
 */
// may consider using interface if more types of needed
public class RVValueSetter {
    Class typeKey;
    Class<TypesafeMap.Key<List<List<Token>>>> termsKey;

    public RVValueSetter(Class typeKey, Class termsKey) {
        this.typeKey = typeKey;
        this.termsKey = termsKey;
    }

    // assuming value represent boolean. 0== false, 1 ==true
    void setValue(int value, CoreMap m, List<List<Token>> terms) {
        m.set(typeKey, value != 1);
        m.set(termsKey, terms);
    }

    void addTerms(CoreMap m, List<Token> terms) {
        if (terms == null) return;
        if (terms.size() > 0) m.set(typeKey, true);
        List<List<Token>> termsList = m.get(termsKey);

        if (termsList == null) {
            termsList = new ArrayList<>();
            m.set(termsKey, termsList);
        }
        termsList.add(terms);
    }

    void clearValue(CoreMap m) {
        m.set(typeKey, false);
        m.set(termsKey, null);
    }
}
