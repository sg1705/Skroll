package com.skroll.analyzer.model.topic;

import cc.mallet.pipe.*;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.*;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.util.Configuration;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by wei2l on 1/17/2016.
 */
@Singleton
public class SkrollTopicModel {

    @Inject
    private Configuration configuration;

//	static final String STOP_LIST_PATH = "src/main/resources/stoplists/en.txt";
//	static final String MODEL_PATH = "src/main/resources/topicModel";
	static final int RANDOM_SEED = 07041;
    private ParallelTopicModel model;
    // pipe for processing text for mallet to read
    private Pipe pipe;

    private String modelPath;


    private String stopListPath;

    @Inject
	public SkrollTopicModel(Configuration configuration) {
        //get all the paths
        this.modelPath = configuration.get("topicModelFile");
        this.stopListPath = configuration.get("stopListFile");
		this.buildModel(modelPath, stopListPath);
	}

    private void buildModel(String modelPath, String stopListPath) {
        this.model = readModel(modelPath);
        this.pipe = buildPipe(stopListPath, model.getAlphabet());
    }

	/**
	 * Creates Pipe used by Mallet to processing input data
	 * @param dataAlphabet specifies all possible words in the data
	 * @return Pipe used by Mallet to processing input data
     */
	public Pipe buildPipe(Alphabet dataAlphabet){
		return buildPipe(this.stopListPath, dataAlphabet);
	}

	/**
	 * Creates Pipe used by Mallet to processing input data
	 * @param stopList text file containing stop words
	 * @param dataAlphabet specifies all possible words in the data
	 * @return Pipe used by Mallet to processing input data
     */
	public Pipe buildPipe(String stopList, Alphabet dataAlphabet){
        // Begin by importing documents from text to feature sequences
		ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

		// Pipes: lowercase, tokenize, remove stopwords, map to features
		pipeList.add( new CharSequenceLowercase() );
		pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
		pipeList.add( new TokenSequenceRemoveStopwords(new File(stopList), "UTF-8", false, false, false) );
		pipeList.add( new TokenSequence2FeatureSequence(dataAlphabet) );

		return new SerialPipes(pipeList); // pipe for processing text to mallet
	}

	/**
	 * Loads a previously stored topic model
	 * @param inputModelName
	 * @return
     */
	public ParallelTopicModel readModel(String inputModelName){
		ParallelTopicModel model = null;
		try {
		 	model = ParallelTopicModel.read(new File(inputModelName));

		}
		catch (FileNotFoundException e){
			System.err.println(inputModelName + " not found");
		}
		catch (Exception e){
			e.printStackTrace();
		};

		return model;

	}

	/**
	 * Provides an reader-friendly overview of the topic model, by showing the top representative words for each model.
	 * @return
     */
	public String representativeWordsForTopics(){

		// The data alphabet maps word IDs to strings
		Alphabet dataAlphabet = model.getAlphabet();
		Formatter out = new Formatter(new StringBuilder(), Locale.US);
		ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
		// Show top 5 words in topics with proportions for the first document
		for (int topic = 0; topic < model.getNumTopics(); topic++) {
			Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();

			out.format("%d\t", topic);
			int rank = 0;
			while (iterator.hasNext() && rank < 5) {
				IDSorter idCountPair = iterator.next();
				out.format("%s (%.0f) ", dataAlphabet.lookupObject(idCountPair.getID()), idCountPair.getWeight());
				rank++;
			}
			out.format("\n");
		}
		return out.toString();
	}

	/**
	 * infer the topic distributions for the text in the paragraph
	 * @param para
	 * @return
     */
	public double[] infer(CoreMap para){
		String text = para.getText();
		return infer(text);
	}

	/**
	 * infer the topic distribution for the given text
	 * @param text
	 * @return
     */
	public double[] infer(String text){
		InstanceList instances = new InstanceList(pipe);
		if (text==null) return new double[model.getNumTopics()];
		instances.addThruPipe(new Instance(text, null, null, null));
		TopicInferencer inferencer = model.getInferencer();
		inferencer.setRandomSeed(RANDOM_SEED);
		return inferencer.getSampledDistribution(instances.get(0), 10, 1, 5);
	}

    public String getStopListPath() {
        return stopListPath;
    }


    /**
	 * infer the topic distributions for the paragraphs
	 * @param paras
	 * @return
     */
	public double[][] infer(List<CoreMap> paras){
		return  paras.stream().map(para -> infer(para)).toArray(size -> new double[size][]);
	}

	/**
	 * infer the topic distributions for the paragraphs in the doc
	 * @param doc
	 * @return
     */
	public double[][] infer(Document doc){
		return infer(doc.getParagraphs());
	}

	public ParallelTopicModel getModel() {
		return model;
	}
}
