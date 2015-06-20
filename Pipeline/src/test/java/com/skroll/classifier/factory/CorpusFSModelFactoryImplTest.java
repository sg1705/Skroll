package com.skroll.classifier.factory;

import com.google.common.io.Files;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.skroll.analyzer.model.applicationModel.DefModelRVSetting;
import com.skroll.analyzer.model.applicationModel.ProbabilityDocumentAnnotatingModel;
import com.skroll.analyzer.model.applicationModel.TrainingDocumentAnnotatingModel;
import com.skroll.classifier.Category;
import com.skroll.document.Document;
import com.skroll.parser.Parser;
import com.skroll.pipeline.util.Constants;
import com.skroll.util.Configuration;
import com.skroll.util.SkrollTestGuiceModule;
import com.skroll.util.TestConfiguration;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.fail;


public class CorpusFSModelFactoryImplTest {

    protected ModelFactory factory;
    protected Configuration configuration;

    @Before
    public void setUp() throws Exception {
        try {
            Injector injector = Guice.createInjector(new SkrollTestGuiceModule());
            factory = injector.getInstance(ModelFactory.class);
            configuration = injector.getInstance(TestConfiguration.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testConfiguration() {
        System.out.println("ModelFolder:" + configuration.get("modelFolder"));
        assert(configuration.get("modelFolder").equals("build/resources/test/train/models/"));
    }
    @Test
    public void testGetTrainingModel() {
        TrainingDocumentAnnotatingModel model = factory.getTrainingModel(new DefModelRVSetting(Category.DEFINITION,Category.DEFINITION_NAME));
        if(model==null){
            fail("failed to create training model");
        }
    }
    @Test
    public void testCreateModel() throws Exception {
        TrainingDocumentAnnotatingModel model = factory.createModel(new DefModelRVSetting(Category.DEFINITION,Category.DEFINITION_NAME));
        if(model==null){
            fail("failed to create training model");
        }
    }

    @Test
    public void testGetBNIModel() throws Exception {
        Document doc =  Parser.parseDocumentFromHtml(Files.toString(new File("src/test/resources/classifier/smaller-indenture.html"), Constants.DEFAULT_CHARSET));
        ProbabilityDocumentAnnotatingModel model = factory.createBNIModel(new DefModelRVSetting(Category.DEFINITION,Category.DEFINITION_NAME), doc);
        if(model==null){
            fail("failed to get BNI training model");
        }
    }

    @Test
    public void testSaveTrainingModel() throws Exception {
        try {
            factory.saveTrainingModel(new DefModelRVSetting(Category.DEFINITION,Category.DEFINITION_NAME));
        } catch (Exception e) {
            e.printStackTrace();
            fail("failed to persist the model");
        }
    }

}