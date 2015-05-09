package com.skroll.util;

import com.skroll.analyzer.model.TrainingDocumentAnnotatingModel;
import com.skroll.classifier.Category;
import com.skroll.classifier.DefinitionCategory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.fail;

public class ObjectPersistUtilTest {
    public static final Logger logger = LoggerFactory
            .getLogger(ObjectPersistUtilTest.class);
    @Test
    public void testPersistReadObject() throws Exception {

        Category category = new DefinitionCategory();
        TrainingDocumentAnnotatingModel localTrainingModel = new TrainingDocumentAnnotatingModel(category.getWordType(),
                category.getWordFeatures(),
                category.getParaType(),
                category.getParaFeatures(),
                category.getParaDocFeatures(),
                category.getDocFeatures(),
                category.getWordVarList());

        ObjectPersistUtil objectPersistUtil = new ObjectPersistUtil("/tmp");

        try {
            objectPersistUtil.persistObject(null, new TrainingDocumentAnnotatingModel(), "TrainingDocumentAnnotatingModel");
        } catch (ObjectPersistUtil.ObjectPersistException e) {
            e.printStackTrace();
            fail("failed persist Object");
        }
        Object obj = null;
        try {
            obj = objectPersistUtil.readObject(null, "TrainingDocumentAnnotatingModel");
        } catch (ObjectPersistUtil.ObjectPersistException e) {
            e.printStackTrace();
            fail("failed readObject");
        }
        //logger.info(obj.getClass().getName() + ":" + (PersistModelTestClass) obj);
        if ( obj instanceof TrainingDocumentAnnotatingModel) {
            TrainingDocumentAnnotatingModel readPersistModelTestClass = (TrainingDocumentAnnotatingModel)obj;
            logger.info(readPersistModelTestClass.toString());
            assert (readPersistModelTestClass.toString().contains("tnbfModel"));
        }
    }

    }