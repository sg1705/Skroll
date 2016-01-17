package com.skroll.analyzer.model.topic;

import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.regex.Pattern;

/**
 * This file is adapted from the mallet topic modeling example.
 * This is a stand alone tool to create a topic model from a input file,
 * which should be a text file readable by mallet,
 * and created from the docs in the preevaluated folder.
 */
public class TopicModelCreater {
	static final String INPUT_FILE = "build/resources/main/preEvaluatedTxtOneFile.txt";
	static final int NUM_TOPICS = 100;

	public static void main(String[] args) throws Exception {

		ParallelTopicModel model = TopicModelCreater.train(INPUT_FILE, NUM_TOPICS);
		model.write(new File("build/resources/main/topicModel"));


	}

	public static ParallelTopicModel train(String inputFileName, int numTopics){

		ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

		// Pipes: lowercase, tokenize, remove stopwords, map to features
		pipeList.add( new CharSequenceLowercase() );
		pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
//		pipeList.add( new TokenSequenceRemoveStopwords(new File("stoplists/en.txt"), "UTF-8", false, false, false) );
		pipeList.add( new TokenSequenceRemoveStopwords(
				new File(SkrollTopicModel.STOP_LIST_PATH), "UTF-8", false, false, false) );
		pipeList.add( new TokenSequence2FeatureSequence() );

		InstanceList instances = new InstanceList(new SerialPipes(pipeList));

		try {
			Reader fileReader = new InputStreamReader(new FileInputStream(new File(inputFileName)), "UTF-8");
			instances.addThruPipe(new CsvIterator(fileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"),
					3, 2, 1)); // data, label, name fields
		} catch(Exception e){
			e.printStackTrace();
		}


		// Create a model with 100 topics, alpha_t = 0.01, beta_w = 0.01
		//  Note that the first parameter is passed as the sum over topics, while
		//  the second is
		ParallelTopicModel model = new ParallelTopicModel(numTopics, 1.0, 0.01);

		model.addInstances(instances);

		// Use two parallel samplers, which each look at one half the corpus and combine
		//  statistics after every iteration.
		model.setNumThreads(2);

		// Run the model for 50 iterations and stop (this is for testing only,
		//  for real applications, use 1000 to 2000 iterations)
		model.setNumIterations(50);

		model.setRandomSeed(07041);

		try {
			model.estimate();
		} catch(Exception e){
			e.printStackTrace();
		}

		return model;

	}

}