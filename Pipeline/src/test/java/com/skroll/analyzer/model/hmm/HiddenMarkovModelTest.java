package com.skroll.analyzer.model.hmm;

import junit.framework.TestCase;

import java.util.Arrays;

public class HiddenMarkovModelTest extends TestCase {

    public void testUpdateProbabilities() throws Exception {
        HiddenMarkovModel hmm = new HiddenMarkovModel(3);
//        String[] token={"a","b","c"};
        String[] token={"affiliate","means","respect"};
        int[] tokenType={1,0,0};

        hmm.updateCounts(token, tokenType, new int[3][0]);
        System.out.println(hmm.showProbabilities());

        hmm.updateProbabilities();
        System.out.println(hmm.showProbabilities());

        assert(hmm.showProbabilities().endsWith("[means=0.5063179547458125]\n"));
        double[][] result = hmm.infer(token);
        System.out.println(Arrays.deepToString((result)));


        assert( result.length == token.length);
    }

    public void testUpdateCounts() throws Exception {
        HiddenMarkovModel hmm = new HiddenMarkovModel(10);
//        String[] token={"a","b","c"};
        //String[] token={"\"","affiliate","\"","means","respect"};
        //String[] token={"\"","Agreement","\"","has", "the", "meaning", "specified", "in", "the", "introductory", "paragraph", "hereof"};
        String[] token={"Agreement","has", "the", "meaning", "specified", "in", "the", "introductory", "paragraph", "hereof"};
        int[] tokenType={1,0,0,0,0,0,0,0,0,0};
        hmm.updateCounts(token, tokenType, new int[10][0]);
        System.out.println(hmm.showCounts());
        assert(hmm.showCounts().endsWith("[has=1]\n"));

    }

    public void testInferForward() throws Exception {
        System.out.println("testInferForward");
        double [] prob = {0.5,0.5};
        HiddenMarkovModel hmm = new HiddenMarkovModel(3);
        String[] token={"affiliate","means","respect"};
        int[] tokenType={1,0,0};

        hmm.updateCounts(token, tokenType, new int[3][0]);
        System.out.println(hmm.showCounts());
        System.out.println(hmm.showProbabilities());

        hmm.updateProbabilities();
        System.out.println(hmm.showProbabilities());

        String[] newTokens = new String[token.length];
        int[][] features = new int[token.length][2];;
        int length = hmm.createFeatures(token, newTokens, features);

        System.out.println("state prob "+Arrays.toString(prob)+'\n');
        System.out.println(Arrays.deepToString(hmm.inferForward(newTokens, features, length)) );
        assert((int)(100*hmm.inferForward(newTokens, features, length)[2][1])==47);

    }

    public void testInferNextStateProbabilities() throws Exception {
        System.out.println("testInferNextStateProbabilities");
        double [] prob = {0.5,0.5};
        HiddenMarkovModel hmm = new HiddenMarkovModel(3);
        String[] token={"affiliate","means","respect"};
        int[] tokenType={1,0,0};

        hmm.updateCounts(token, tokenType, new int[3][0]);
        System.out.println(hmm.showCounts());
        System.out.println(hmm.showProbabilities());

        hmm.updateProbabilities();
        System.out.println(hmm.showProbabilities());


        System.out.println("state prob "+Arrays.toString(prob)+'\n');
        System.out.println(Arrays.toString(hmm.inferNextStateProbabilities(prob)) );
        assert((int)(100*hmm.inferNextStateProbabilities(prob)[1])==48);


    }

    public void testInferJointProbabilitiesStateAndObservation() throws Exception {
        System.out.println("testInferJointProbabilitiesStateAndObservation");

        double [] prob = {0.5,0.5};
        HiddenMarkovModel hmm = new HiddenMarkovModel(3);
        String[] token={"affiliate","means","respect"};
        int[] tokenType={1,0,0};

        hmm.updateCounts(token, tokenType, new int[3][0]);
        System.out.println(hmm.showCounts());
        System.out.println(hmm.showProbabilities());

        hmm.updateProbabilities();
        System.out.println(hmm.showProbabilities());



        int[][] features = new int[token.length][2];;

        System.out.println("state prob "+Arrays.toString(prob)+'\n');
        System.out.println(Arrays.toString(hmm.inferJointProbabilitiesStateAndObservation(2, prob, token, features)) );
        assert((int)(100*hmm.inferJointProbabilitiesStateAndObservation(2, prob, token, features)[1])==5);

    }

    public void testInferStateProbabilitiesGivenObservation() throws Exception {

        System.out.println("testInferStateProbabilitiesGivenObservation");

        double [] prob = {0.5,0.5};
        HiddenMarkovModel hmm = new HiddenMarkovModel(3);
        String[] token={"affiliate","means","respect"};
        int[] tokenType={1,0,0};

        hmm.updateCounts(token, tokenType, new int[3][0]);
        System.out.println(hmm.showCounts());
        System.out.println(hmm.showProbabilities());

        hmm.updateProbabilities();
        System.out.println(hmm.showProbabilities());

        int[][] features = new int[token.length][2];;

        System.out.println("state prob "+Arrays.toString(prob)+'\n');
        System.out.println(Arrays.toString(hmm.inferStateProbabilitiesGivenObservation(1, prob, token, features)) );
        assert((int)(100*hmm.inferStateProbabilitiesGivenObservation(1, prob, token, features)[1])==47);

    }

    // todo: hmm.createFeatures is no longer used by the new model. Should remove this after incorporating the new model.
    public void testCreateFeatures() throws Exception {
        HiddenMarkovModel hmm = new HiddenMarkovModel(5);
        String[] tokens={"\"","affiliate","\"","means","respect"};
        String[] newTokens = new String[tokens.length];
        int[][] features = new int[tokens.length][2];;
        int length = hmm.createFeatures(tokens, newTokens, features);
        System.out.println("length = " + length);
        System.out.println("newTokens = "+Arrays.toString(newTokens) );
        System.out.println("features = "+Arrays.deepToString(features) );
    }

    public void testInfer() throws Exception {
        System.out.println("testInfer");
        double [] prob = {0.5,0.5};
        HiddenMarkovModel hmm = new HiddenMarkovModel(3);
        String[] token={"affiliate","means","respect"};
        int[] tokenType={1,0,0};

        hmm.updateCounts(token, tokenType, new int[3][0]);
        System.out.println(hmm.showCounts());
        System.out.println(hmm.showProbabilities());

        hmm.updateProbabilities();
        System.out.println(hmm.showProbabilities());

        String[] newTokens = new String[token.length];
        int[][] features = new int[token.length][2];;
        int length = hmm.createFeatures(token, newTokens, features);

        System.out.println(Arrays.deepToString(hmm.infer(token)) );
        System.out.println(Arrays.deepToString(hmm.inferForward(newTokens, features, length)) );
        assert((int)(100*hmm.infer(token)[0][0])==47);


    }

    public void testInferBackward() throws Exception {
        System.out.println("testInferForward");
        double [] prob = {0.5,0.5};
        HiddenMarkovModel hmm = new HiddenMarkovModel(3);
        String[] token={"affiliate","means","respect"};
        int[] tokenType={1,0,0};

        hmm.updateCounts(token, tokenType, new int[3][0]);
        System.out.println(hmm.showCounts());
        System.out.println(hmm.showProbabilities());

        hmm.updateProbabilities();
        System.out.println(hmm.showProbabilities());

        String[] newTokens = new String[token.length];
        int[][] features = new int[token.length][2];;
        int length = hmm.createFeatures(token, newTokens, features);

        System.out.println("state prob "+Arrays.toString(prob)+'\n');
        System.out.println("infer forward");
        System.out.println(Arrays.deepToString(hmm.inferForward(newTokens, features, length)) );


        System.out.println("state prob "+Arrays.toString(prob)+'\n');
        System.out.println("infer backward");
        System.out.println(Arrays.deepToString(hmm.inferBackward(newTokens, features, length)) );
        assert((int)(1000*hmm.inferBackward(newTokens, features, length)[0][0])==12);

    }
}