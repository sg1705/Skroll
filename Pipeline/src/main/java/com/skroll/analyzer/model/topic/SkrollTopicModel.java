package com.skroll.analyzer.model.topic;

import cc.mallet.pipe.*;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.*;
import com.skroll.document.CoreMap;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by wei2l on 1/17/2016.
 */
public class SkrollTopicModel {
	static final String STOP_LIST_PATH = "src/main/resources/stoplists/en.txt";
	static final String MODEL_PATH = "src/main/resources/topicModel";
    private ParallelTopicModel model;
	private Pipe pipe; // pipe for processing text for mallet to read


	public SkrollTopicModel(){
		new SkrollTopicModel(MODEL_PATH);
	}

	public SkrollTopicModel(String inputModelName){
		model = readModel(inputModelName);
		pipe = buildPipe(STOP_LIST_PATH, model.getAlphabet());
	}

	public static Pipe buildPipe(Alphabet dataAlphabet){
		return buildPipe(STOP_LIST_PATH, dataAlphabet);
	}
	public static Pipe buildPipe(String stopList, Alphabet dataAlphabet){
        // Begin by importing documents from text to feature sequences
		ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

		// Pipes: lowercase, tokenize, remove stopwords, map to features
		pipeList.add( new CharSequenceLowercase() );
		pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
		pipeList.add( new TokenSequenceRemoveStopwords(new File(stopList), "UTF-8", false, false, false) );
		pipeList.add( new TokenSequence2FeatureSequence(dataAlphabet) );

		return new SerialPipes(pipeList); // pipe for processing text to mallet
	}

	public static ParallelTopicModel readModel(String inputModelName){
		ParallelTopicModel model = null;
		try {
		 	model = ParallelTopicModel.read(new File(inputModelName));

		} catch (Exception e){
			e.printStackTrace();
		};

		return model;

	}

	double[] infer(CoreMap para){
		InstanceList instances = new InstanceList(pipe);
		instances.addThruPipe(new Instance(para.getText(), null, null, null));
		TopicInferencer inferencer = model.getInferencer();
		return inferencer.getSampledDistribution(instances.get(0), 10, 1, 5);
	}
}
