package com.skroll.classifier;

import com.google.common.io.Files;
import com.skroll.analyzer.model.ProbabilityDocumentAnnotatingModel;
import com.skroll.analyzer.model.TrainingDocumentAnnotatingModel;
import com.skroll.document.Document;
import com.skroll.parser.Parser;
import com.skroll.pipeline.util.Constants;
import com.skroll.util.ObjectPersistUtil;
import junit.framework.TestCase;

import java.io.File;

public class ModelFactoryTest extends TestCase {

    public void testGetTrainingModel() {
        ModelFactory modelFactory = new ModelFactory();
        TrainingDocumentAnnotatingModel model = modelFactory.getTrainingModel(new Category(Category.DEFINITION,"definition"));
        if(model==null){
            fail("failed to create training model");
        }
    }

    public void testCreateModel() throws Exception {
        ModelFactory modelFactory = new ModelFactory();
        TrainingDocumentAnnotatingModel model = modelFactory.createModel(new Category(Category.DEFINITION, "definition"));
        if(model==null){
            fail("failed to create training model");
        }
    }

    public void testGetBNIModel() throws Exception {
         ModelFactory modelFactory = new ModelFactory();
        Document doc =  Parser.parseDocumentFromHtml(Files.toString(new File("src/test/resources/classifier/smaller-indenture.html"), Constants.DEFAULT_CHARSET));
        ProbabilityDocumentAnnotatingModel model = modelFactory.getBNIModel(new Category(Category.DEFINITION,"definition"), doc);
        if(model==null){
            fail("failed to create training model");
        }
    }

    public void testSaveTrainingModel() throws Exception {
        ModelFactory modelFactory = new ModelFactory();
        try {
            modelFactory.saveTrainingModel(new Category(Category.DEFINITION, "definition"));
        } catch (ObjectPersistUtil.ObjectPersistException e) {
            e.printStackTrace();
            fail("failed to persist the model");
        }
    }
}