package com.skroll.analyzer.hmm;

import com.skroll.pipeline.pipes.StringStopWordTokenizerPipe;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wei2learn on 12/23/2014.
 */
public class HiddenMarkovModel {
    static final int DEFAULT_MODEL_LENGTH = 8;
    static final int DEFAULT_NUM_STATE_VALUES = 2;
    static final int PRIOR_COUNT = 10;

    int[][] transitionCounts;
    int totalStateValueCounts[];
    Map<String, Integer>[] tokenCounts; // count for each state value, each token
    Map<String, Integer>[] nextTokenCounts; // count for each state value, each token
    int[][] stateNumberCounts; // count for each state value, each state number
    int numStateValues = DEFAULT_NUM_STATE_VALUES;
    int modelLength = DEFAULT_MODEL_LENGTH;

    double [][] transitionProbability;
    double[] stateValueProbability;
    Map<String, Double>[] tokenProbabilityGivenStateValue;
    Map<String, Double>[] nextTokenProbabilityGivenStateValue;
    double [][] stateNumberProbabilityGivenStateValue;

    boolean probabilitiesUpToDate=false;

    public HiddenMarkovModel(){
        this(DEFAULT_MODEL_LENGTH);
    }
    public HiddenMarkovModel(int modelLength){
        this.modelLength = modelLength;
        transitionCounts = new int [numStateValues][numStateValues];
        totalStateValueCounts = new int [numStateValues];
        stateNumberCounts = new int[numStateValues][modelLength];
        tokenCounts = new HashMap[numStateValues];
        nextTokenCounts = new HashMap[numStateValues];

        transitionProbability = new double[numStateValues][numStateValues];
        stateValueProbability = new double[numStateValues];
        tokenProbabilityGivenStateValue = new HashMap[numStateValues];
        nextTokenProbabilityGivenStateValue = new HashMap[numStateValues];
        stateNumberProbabilityGivenStateValue = new double[numStateValues][modelLength];

        for (int i=0; i<numStateValues; i++){
            tokenCounts[i] = new HashMap<String, Integer>();
            nextTokenCounts[i] = new HashMap<String, Integer>();
            tokenProbabilityGivenStateValue[i] = new HashMap<String, Double>();
            nextTokenProbabilityGivenStateValue[i] = new HashMap<String, Double>();
        }
    }
    public String showProbabilities(){
        String s="";
        s+= "stateValueProbability " + Arrays.toString(stateValueProbability) +'\n';
        s+= "transitionProbability " + Arrays.deepToString(transitionProbability) +'\n';
        s+= "stateNumberProbabilityGivenStateValue " + Arrays.deepToString(stateNumberProbabilityGivenStateValue) +'\n';
        for (Map<String, Double> m: tokenProbabilityGivenStateValue)
            s+= "tokenCounts " +  Arrays.toString(m.entrySet().toArray())  +'\n';
        for (Map<String, Double> m: nextTokenProbabilityGivenStateValue)
            s+= "nextTokenProbabilityGivenStateValue " + Arrays.toString(m.entrySet().toArray())  +'\n';
        return s;
    }

    public String showCounts(){
        String s="";
        s+= "totalStateValueCounts " + Arrays.toString(totalStateValueCounts) +'\n';
        s+= "transitionCounts " + Arrays.deepToString(transitionCounts) +'\n';
        s+= "stateNumberCounts " + Arrays.deepToString(stateNumberCounts) +'\n';
        for (Map<String, Integer> m: tokenCounts)
            s+= "tokenCounts " +  Arrays.toString(m.entrySet().toArray())  +'\n';
        for (Map<String, Integer> m: nextTokenCounts)
            s+= "nextTokenCounts " + Arrays.toString(m.entrySet().toArray())  +'\n';
        return s;
    }

    public void updateProbabilities(){

        // update state value probability
        int total =0;
        for (int n: totalStateValueCounts) total+=n;
        for (int i=0; i<numStateValues; i++) stateValueProbability[i] = (double)totalStateValueCounts[i]/total;

        for (int i=0; i<numStateValues; i++){
            double priorCountPartI = PRIOR_COUNT * stateValueProbability[i];
            double totalCountIWithPrior = totalStateValueCounts[i] + PRIOR_COUNT;
            for (int j=0;j<numStateValues;j++){
                transitionProbability[i][j] = (transitionCounts[i][j] + priorCountPartI)/ totalCountIWithPrior;
            }
            for (int j=0; j< modelLength-1; j++)
                stateNumberProbabilityGivenStateValue[i][j] =
                        (stateNumberCounts[i][j] + priorCountPartI)/ totalCountIWithPrior;

            for (String k: tokenCounts[i].keySet()){
                tokenProbabilityGivenStateValue[i].put(k,
                        (tokenCounts[i].get(k) + priorCountPartI) /totalCountIWithPrior );
            }
            for (String k: nextTokenCounts[i].keySet())
                nextTokenProbabilityGivenStateValue[i].put(k,
                        (nextTokenCounts[i].get(k) + priorCountPartI) /totalCountIWithPrior );
        }
        probabilitiesUpToDate = true;
    }

    public void updateCounts(String[] tokens, int[] stateValues){

        //skip samples that are too short
        if (tokens.length < modelLength || stateValues.length<modelLength) return;

        // to make the totalStateValueCounts consistent,
        // we do not use the last values of the input arrays for updating the various counts,
        // except the last stateValues entry is used to update the transition counts.
        // so when passing in input data,
        // we would like to have stateValues contain at least non-definition words at the end.
        for (int i=0;i<modelLength-1; i++){
            totalStateValueCounts[stateValues[i]]++;
            transitionCounts[stateValues[i]][stateValues[i+1]]++;
            stateNumberCounts[stateValues[i]][i]++;

            Integer c = tokenCounts[stateValues[i]].get(tokens[i]);
            if (c==null) c=0;
            tokenCounts[stateValues[i]].put(tokens[i], c + 1);

            c = nextTokenCounts[stateValues[i]].get(tokens[i+1]);
            if (c==null) c=0;
            nextTokenCounts[stateValues[i]].put(tokens[i+1], c + 1);
        }
        probabilitiesUpToDate = false;
    }

    public double[][] inferForward( String[] tokens){
        double [][] stateProbGivenPrevObservations = new double[modelLength][numStateValues];

        //todo: need to check to see which prior is better
        // initialize state probability uniformly
        double uniformProb = 1.0/numStateValues;
        double[] priorProb = new double[numStateValues];
        // double[] priorProb = stateValueProbabilities();

        Arrays.fill(priorProb,uniformProb);
        int length = Math.min(modelLength, tokens.length);
        for (int i=0;i<length-1;i++){
            stateProbGivenPrevObservations[i] =  inferStateProbabilitiesGivenObservation(i,priorProb,tokens);
            priorProb = inferNextStateProbabilities(stateProbGivenPrevObservations[i]);
            //last calculation is not used for now, because last token is used in the inference of the second last state.
        }
        return stateProbGivenPrevObservations;
    }

    public double[] inferNextStateProbabilities(double[] prevStateProb){
        double[] prob=new double[numStateValues];
        for (int i=0;i<numStateValues;i++){
            prob[i]=0;
            for (int j=0;j<numStateValues;j++) {
                prob[i] += prevStateProb[j] * transitionProbability[j][i];
            }
        }
        return prob;

    }

    // a helper method to add zero to the counts and update corresponding probabilities in the hashmap
    void setZeroCountAndProbabilities(Map<String, Integer>[] countMap, Map<String, Double> []probMap, String token){
        for (int i=0;i<numStateValues;i++) {
            if (probMap[i].containsKey(token)) continue;
            countMap[i].put(token,0);
            double priorCountPartI = PRIOR_COUNT * stateValueProbability[i];
            double totalCountIWithPrior = totalStateValueCounts[i] + PRIOR_COUNT;
            probMap[i].put(token, priorCountPartI/totalCountIWithPrior);
        }
    }

    double[] inferJointProbabilitiesStateAndObservation(int stateNumber, double priorProb[], String[] tokens){

        double[] prob = new double[numStateValues];
        for (int i=0;i<numStateValues; i++){

            // initialize token entry in the maps if it's not seen before
            if ( ! tokenProbabilityGivenStateValue[i].containsKey(tokens[stateNumber]))
                setZeroCountAndProbabilities(tokenCounts, tokenProbabilityGivenStateValue, tokens[stateNumber]);
            if ( ! nextTokenProbabilityGivenStateValue[i].containsKey(tokens[stateNumber+1]))
                setZeroCountAndProbabilities(nextTokenCounts, nextTokenProbabilityGivenStateValue, tokens[stateNumber+1]);
            prob[i] = priorProb[i] * tokenProbabilityGivenStateValue[i].get(tokens[stateNumber]);
            prob[i] *= nextTokenProbabilityGivenStateValue[i].get(tokens[stateNumber+1]);
            prob[i] *= stateNumberProbabilityGivenStateValue[i][stateNumber];
        }
        return prob;
    }

    public double[] inferStateProbabilitiesGivenObservation(int stateNumber, double priorProb[], String[] tokens){
        double [] prob = inferJointProbabilitiesStateAndObservation(stateNumber, priorProb, tokens);
        // normalize
        double sum =0;
        for (double p:prob) sum+=p;
        for (int i=0;i<numStateValues; i++) prob[i] /= sum;
        return prob;
    }
}
