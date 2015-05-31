package com.skroll.classifier;

import com.google.common.io.Files;
import com.skroll.analyzer.model.applicationModel.DefModelRVSetting;
import com.skroll.analyzer.model.applicationModel.ProbabilityDocumentAnnotatingModel;
import com.skroll.analyzer.model.applicationModel.TrainingDocumentAnnotatingModel;
import com.skroll.document.Document;
import com.skroll.parser.Parser;
import com.skroll.pipeline.util.Constants;
import com.skroll.util.ObjectPersistUtil;
import junit.framework.TestCase;

import java.io.File;

public class ModelFactoryTest extends TestCase {

    public void testGetTrainingModel() {
        ModelFactory modelFactory = new ModelFactory();
        TrainingDocumentAnnotatingModel model = modelFactory.getTrainingModel(new DefModelRVSetting(Category.DEFINITION,Category.DEFINITION_NAME));
        if(model==null){
            fail("failed to create training model");
        }
    }

    public void testCreateModel() throws Exception {
        ModelFactory modelFactory = new ModelFactory();
        TrainingDocumentAnnotatingModel model = modelFactory.createModel(new DefModelRVSetting(Category.DEFINITION,Category.DEFINITION_NAME));
        if(model==null){
            fail("failed to create training model");
        }
    }

    public void testGetBNIModel() throws Exception {
         ModelFactory modelFactory = new ModelFactory();
        Document doc =  Parser.parseDocumentFromHtml(Files.toString(new File("src/test/resources/classifier/smaller-indenture.html"), Constants.DEFAULT_CHARSET));
        ProbabilityDocumentAnnotatingModel model = modelFactory.getBNIModel(new DefModelRVSetting(Category.DEFINITION,Category.DEFINITION_NAME), doc);
        if(model==null){
            fail("failed to create training model");
        }
    }

    public void testSaveTrainingModel() throws Exception {
        ModelFactory modelFactory = new ModelFactory();
        try {
            modelFactory.saveTrainingModel(new DefModelRVSetting(Category.DEFINITION,Category.DEFINITION_NAME));
        } catch (ObjectPersistUtil.ObjectPersistException e) {
            e.printStackTrace();
            fail("failed to persist the model");
        }
    }
}