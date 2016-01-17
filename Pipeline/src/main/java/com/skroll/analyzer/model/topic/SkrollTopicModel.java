package com.skroll.analyzer.model.topic;

import cc.mallet.pipe.*;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.LabelSequence;

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
		readModel(inputModelName);
	}

	void readModel(String inputModelName){
		try {
		 	model = ParallelTopicModel.read(new File(inputModelName));

		} catch (Exception e){
			e.printStackTrace();
		};

		// Begin by importing documents from text to feature sequences
		ArrayList<Pipe> pipeList = new ArrayList<Pipe>();


		// The data alphabet maps word IDs to strings
		Alphabet dataAlphabet = model.getAlphabet();

		// Pipes: lowercase, tokenize, remove stopwords, map to features
		pipeList.add( new CharSequenceLowercase() );
		pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
		pipeList.add( new TokenSequenceRemoveStopwords(new File(STOP_LIST_PATH), "UTF-8", false, false, false) );
		pipeList.add( new TokenSequence2FeatureSequence(dataAlphabet) );

		pipe = new SerialPipes(pipeList); // pipe for processing text to mallet
	}


}
