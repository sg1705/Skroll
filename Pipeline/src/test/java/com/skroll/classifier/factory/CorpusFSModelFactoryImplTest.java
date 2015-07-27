package com.skroll.classifier.factory;

import com.google.common.io.Files;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.skroll.analyzer.model.applicationModel.DefModelRVSetting;
import com.skroll.analyzer.model.applicationModel.ModelRVSetting;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.fail;


public class CorpusFSModelFactoryImplTest {

    static final List<Integer> TEST_DEF_CATEGORY_IDS =  new ArrayList<>(Arrays.asList(Category.NONE, Category.DEFINITION));
    static final int TEST_DEF_CLASSIFIER_ID = 2;
    protected ModelFactory factory;
    protected Configuration configuration;
    ModelRVSetting setting;

    int modelId = TEST_DEF_CLASSIFIER_ID;
    @Before
    public void setUp() throws Exception {
        try {
            Injector injector = Guice.createInjector(new SkrollTestGuiceModule());
            factory = injector.getInstance(ModelFactory.class);
            configuration = injector.getInstance(TestConfiguration.class);
            setting = new DefModelRVSetting(TEST_DEF_CATEGORY_IDS);
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
        TrainingDocumentAnnotatingModel model = factory.getTrainingModel(modelId, setting);
        if(model==null){
            fail("failed to create training model");
        }
    }
    @Test
    public void testCreateModel() throws Exception {
          TrainingDocumentAnnotatingModel model = factory.createModel(modelId, setting);
        if(model==null){
            fail("failed to create training model");
        }
    }

    @Test
    public void testGetBNIModel() throws Exception {
        Document doc =  Parser.parseDocumentFromHtml(Files.toString(new File("src/test/resources/classifier/smaller-indenture.html"), Constants.DEFAULT_CHARSET));
        ProbabilityDocumentAnnotatingModel model = factory.createBNIModel(modelId, setting, doc);
        if(model==null){
            fail("failed to get BNI training model");
        }
    }

    @Test
    public void testSaveTrainingModel() throws Exception {
        try {
            factory.saveTrainingModel(modelId, setting);

        } catch (Exception e) {
            e.printStackTrace();
            fail("failed to persist the model");
        }
    }

}