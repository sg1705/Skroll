package com.skroll.analyzer.model.applicationModel;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.applicationModel.randomVariables.NumberTokensComputer;
import com.skroll.analyzer.model.applicationModel.randomVariables.RVCreater;
import com.skroll.analyzer.model.applicationModel.randomVariables.UniqueWordsComputer;
import com.skroll.classifier.Category;
import com.skroll.document.Document;
import com.skroll.document.annotation.CoreAnnotations;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by saurabh on 5/26/15.
 */
public class TrainingDocumentAnnotatingModelPersistenceTest {

    int maxNumWords = 20;
    static final List<Integer> TEST_DEF_CATEGORY_IDS =  new ArrayList<>(Arrays.asList(Category.NONE,Category.DEFINITION));
    static final int TEST_DEF_CLASSIFIER_ID = 1;

    static final List<RandomVariable> DEFAULT_PARA_FEATURE_VARS = Arrays.asList(
            RVCreater.createDiscreteRVWithComputer(new NumberTokensComputer(), "numTokens")
    );
    static final List<RandomVariable> DEFAULT_PARA_DOC_FEATURE_VARS = Arrays.asList(
            RVCreater.createParagraphStartsWithRV(CoreAnnotations.InQuotesAnnotation.class)
    );

    static final List<RandomVariable> DEFAULT_WORD_VARS = Arrays.asList(
            RVCreater.createWordsRVWithComputer(new UniqueWordsComputer(), "uniqueWords")
    );

    Document doc = new Document();
    ModelRVSetting setting = new ModelRVSetting(
             DefModelRVSetting.DEFAULT_WORD_FEATURES,
            DEFAULT_PARA_FEATURE_VARS, DEFAULT_PARA_DOC_FEATURE_VARS, DEFAULT_WORD_VARS,
            TEST_DEF_CATEGORY_IDS);

    @Before
    public void setUp() throws Exception {
        doc = TestHelper.setUpTestDoc();
    }

    //TODO: need to rewrite this test case
    @Test
    public void testPersistModel() throws Exception {
        ModelRVSetting setting = new DefModelRVSetting(TEST_DEF_CATEGORY_IDS);
        TrainingDocumentAnnotatingModel model = new TrainingDocumentAnnotatingModel(TEST_DEF_CLASSIFIER_ID,setting);
        doc = TestHelper.setUpTestDoc();
        //doc.getParagraphs().get(0).set(CoreAnnotations.IsTOCAnnotation.class, true);
        model.updateWithDocument(doc);
        Writer writer = null;
        ByteArrayOutputStream f_out = new ByteArrayOutputStream();
        try {
            writer = new OutputStreamWriter(f_out, "UTF-8");
            ObjectMapper mapper = new ObjectMapper();
            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            mapper.writerWithDefaultPrettyPrinter().writeValue(writer, model);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            writer.close();
            f_out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String json = new String(f_out.toByteArray());
        System.out.println(json);

        ByteArrayInputStream b_in = new ByteArrayInputStream(json.getBytes());
        Reader reader = null;
        try {
            reader = new InputStreamReader(b_in, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw e;
        }

        TrainingDocumentAnnotatingModel obj = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            obj = mapper.readValue(reader, TrainingDocumentAnnotatingModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            reader.close();
            b_in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert (obj != null);
        assert (obj.equals(model));
    }

}
