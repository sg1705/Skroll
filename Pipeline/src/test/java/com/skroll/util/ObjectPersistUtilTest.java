package com.skroll.util;

import com.skroll.analyzer.model.applicationModel.DefModelRVSetting;
import com.skroll.analyzer.model.applicationModel.TrainingDocumentAnnotatingModel;
import com.skroll.classifier.Category;
import com.skroll.classifier.ModelFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.fail;

public class ObjectPersistUtilTest {
    public static final Logger logger = LoggerFactory
            .getLogger(ObjectPersistUtilTest.class);
    @Test
    public void testPersistReadObject() throws Exception {
        ModelFactory modelFactory = new ModelFactory();
        TrainingDocumentAnnotatingModel model = modelFactory.createModel(new DefModelRVSetting(Category.DEFINITION,Category.DEFINITION_NAME));

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