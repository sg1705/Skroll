package com.skroll.analyzer.model.topic;

import cc.mallet.pipe.Pipe;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.*;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.skroll.util.SkrollTestGuiceModule;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

/**
 * Created by wei2l on 1/17/2016.
 */
public class TopicModelCreatorTest {
    static final String TEST_FILE = "src/test/resources/analyzer/topics/testFile";
	static final int NUM_TOPICS = 10;
    SkrollTopicModel stm;

    @Before
    public void setup() throws Exception {
        Injector injector = Guice.createInjector(new SkrollTestGuiceModule());
        stm = injector.getInstance(SkrollTopicModel.class);
    }


    @Test
    public void testTrain() throws Exception {

        ParallelTopicModel model = TopicModelCreator.train(TEST_FILE, NUM_TOPICS);

		// The data alphabet maps word IDs to strings
		Alphabet dataAlphabet = model.getAlphabet();
//		Pipe pipe = SkrollTopicModel.buildPipe(dataAlphabet);
        Pipe pipe = stm.buildPipe(dataAlphabet);

		// Show the words and topics in the first instance
		FeatureSequence tokens = (FeatureSequence) model.getData().get(0).instance.getData();
		LabelSequence topics = model.getData().get(0).topicSequence;

		Formatter out = new Formatter(new StringBuilder(), Locale.US);
		for (int position = 0; position < tokens.getLength(); position++) {
			out.format("%s-%d ", dataAlphabet.lookupObject(tokens.getIndexAtPosition(position)), topics.getIndexAtPosition(position));
		}
		System.out.println(out);

		// Estimate the topic distribution of the first instance,
		//  given the current Gibbs state.
		double[] topicDistribution = model.getTopicProbabilities(0);

		// Get an array of sorted sets of word ID/count pairs
		ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();

		// Show top 5 words in topics with proportions for the first document
		for (int topic = 0; topic < NUM_TOPICS; topic++) {
			Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();

			out = new Formatter(new StringBuilder(), Locale.US);
			out.format("%d\t%.3f\t", topic, topicDistribution[topic]);
			int rank = 0;
			while (iterator.hasNext() && rank < 5) {
				IDSorter idCountPair = iterator.next();
				out.format("%s (%.0f) ", dataAlphabet.lookupObject(idCountPair.getID()), idCountPair.getWeight());
				rank++;
			}
			System.out.println(out);
		}

		// Create a new instance with high probability of topic 0
		StringBuilder topicZeroText = new StringBuilder();
		Iterator<IDSorter> iterator = topicSortedWords.get(0).iterator();

		int rank = 0;
		while (iterator.hasNext() && rank < 5) {
			IDSorter idCountPair = iterator.next();
			topicZeroText.append(dataAlphabet.lookupObject(idCountPair.getID()) + " ");
			rank++;
		}

		// Create a new instance named "test instance" with empty target and source fields.
		InstanceList testing = new InstanceList(pipe);
		Instance instance = new Instance(topicZeroText.toString(), null, "test instance", null);
		testing.addThruPipe(instance);

		TopicInferencer inferencer = model.getInferencer();
		inferencer.setRandomSeed(07041);

		// get topic distribution for the words in the first topic
		System.out.println(testing.get(0).getName().toString() + " " + testing.get(0).getData().toString());
		double[] testProbabilities = inferencer.getSampledDistribution(testing.get(0), 10, 1, 5);
		System.out.println("0\t" + Arrays.toString(testProbabilities));

		assert(testProbabilities[0]>0.5);

    }
}
