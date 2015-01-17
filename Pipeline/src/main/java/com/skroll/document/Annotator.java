package com.skroll.document;

import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.annotation.CoreAnnotation;
import com.skroll.document.annotation.TypesafeMap;

import java.util.List;

/**
 * Created by saurabh on 1/3/15.
 */
public interface Annotator {

    public List<Class<?  extends TypesafeMap.Key>> requirements();


}
