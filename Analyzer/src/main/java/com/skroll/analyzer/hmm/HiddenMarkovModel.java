package com.skroll.analyzer.hmm;

import com.skroll.pipeline.pipes.StringStopWordTokenizerPipe;

import java.util.*;

/**
 * Created by wei2learn on 12/23/2014.
 */
public class HiddenMarkovModel {
    static final int DEFAULT_MODEL_LENGTH = 12;
    static final int MAX_MODEL_LENGTH = 20;
    static final int DEFAULT_NUM_STATE_VALUES = 2;
    static final double PRIOR_COUNT = 40;

    static final int NUMBER_FEATURES =2;
    static final int[] FEATURE_VALUES={MAX_MODEL_LENGTH,2};

    static final int STATE_NUMBER_FEATURE =0;

    static final boolean USE_QUOTE=false;
    static final int IN_QUOTE_FEATURE =1;
    static final int IN_QUOTE_FALSE=0;


    int[][] transitionCounts;
    int totalStateValueCounts[];
    Map<String, Integer>[] tokenCounts; // count for each state value, each token
    Map<String, Integer>[] nextTokenCounts; // count for each state value, each token
    List<List<int[]>> stateFeatureValueCounts; // use list of arrays because need to preallocate the count arrays

    int[][] stateNumberCounts; // count for each state value, each state number
    int[][] inQuoteCounts; // count for each state value, if the token is inside quotes
    int numStateValues = DEFAULT_NUM_STATE_VALUES;
    int modelLength = DEFAULT_MODEL_LENGTH;

    double [][] transitionProbability;
    double[] stateValueProbability;
    Map<String, Double>[] tokenProbabilityGivenStateValue;
    Map<String, Double>[] nextTokenProbabilityGivenStateValue;
    double [][] stateNumberProbabilityGivenStateValue;
    double [][] inQuoteProbabilityGivenStateValue;
    List<List<double[]>> featureValueProbabilityGivenState;

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

        stateFeatureValueCounts = new ArrayList<List<int[]>>();
        for (int i=0; i<numStateValues; i++){
            List<int[]> featureValueCounts = new ArrayList<int[]>();
            for (int j=0; j<NUMBER_FEATURES; j++){
                int[] valueCounts = new int[FEATURE_VALUES[j]];
                featureValueCounts.add(valueCounts);
            }
            stateFeatureValueCounts.add(featureValueCounts);
        }
        featureValueProbabilityGivenState = new ArrayList<List<double[]>>();
        for (int i=0; i<numStateValues; i++){
            List<double[]> featureValueProb = new ArrayList<double[]>();
            for (int j=0; j<NUMBER_FEATURES; j++){
                double[] valueProbs = new double[FEATURE_VALUES[j]];
                featureValueProb.add(valueProbs);
            }
            featureValueProbabilityGivenState.add(featureValueProb);
        }

    }

    public int size(){
        return modelLength;
    }
    public String showProbabilities(){
        String s="";
        s+= "stateValueProbability " + Arrays.toString(stateValueProbability) +'\n';
        s+= "transitionProbability " + Arrays.deepToString(transitionProbability) +'\n';
        s+= "featureValueProbabilityGivenState \n";
        for (List<double[]> featureValueProb: featureValueProbabilityGivenState){
            for( double[] values: featureValueProb){
                s += Arrays.toString(values);
            }
            System.out.println();
        }

        //s+= "stateNumberProbabilityGivenStateValue " + Arrays.deepToString(stateNumberProbabilityGivenStateValue) +'\n';
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
        //s+= "stateNumberCounts " + Arrays.deepToString(stateNumberCounts) +'\n';
        for (List<int[]> featureValueCount: stateFeatureValueCounts){
            for( int[] values: featureValueCount){
                s += Arrays.toString(values);
            }
            s+='\n';
        }

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
//            for (int j=0; j< modelLength-1; j++)
//                stateNumberProbabilityGivenStateValue[i][j] =
//                        (stateNumberCounts[i][j] + priorCountPartI)/ totalCountIWithPrior;

            List<double[]> featureValueProb =featureValueProbabilityGivenState.get(i);
            for (int j=0; j<NUMBER_FEATURES; j++){
                double[] valueProbs=featureValueProb.get(j);
                for (int k=0; k<valueProbs.length; k++) {
                    valueProbs[k] =
                            (stateFeatureValueCounts.get(i).get(j)[k] + priorCountPartI)/ totalCountIWithPrior;
                }
            }


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

        int stateNumber =0; //this skips the quotes
        int inQuote=0; // a flag toggles between 0 and 1 each time a quote is encountered



        // to make the totalStateValueCounts consistent,
        // we do not use the last values of the input arrays for updating the various counts,
        // except the last stateValues entry is used to update the transition counts.
        // so when passing in input data,
        // we would like to have stateValues contain at least non-definition words at the end.
        for (int i=0;i<modelLength-1; i++){


            String token = tokens[i];
            int inc=1; // inc is used to get the next token
            if (tokens[i+inc].equals("\"")) {
                inc++;
                if (i+inc >= tokens.length) return; // if the token is the last
            }
            String nextToken = tokens[i+inc];

            //todo: eventually we want to have this put in training data file instead of putting it here
            // handling quote feature
            if (token.equals("\"")) {
                if (USE_QUOTE) inQuote = 1 - inQuote;
                continue;
            }

            stateFeatureValueCounts.get(stateValues[i]).get(IN_QUOTE_FEATURE)[inQuote]++;
            stateFeatureValueCounts.get(stateValues[i]).get(STATE_NUMBER_FEATURE)[stateNumber]++;
            stateNumber++; //increase the stateNumber, not counting the quotes
            //stateNumberCounts[stateValues[i]][i]++;



            totalStateValueCounts[stateValues[i]]++;
            transitionCounts[stateValues[i]][stateValues[i+inc]]++;

            Integer c = tokenCounts[stateValues[i]].get(token);
            if (c==null) c=0;
            tokenCounts[stateValues[i]].put(token, c + 1);

            c = nextTokenCounts[stateValues[i]].get(nextToken);
            if (c==null) c=0;
            nextTokenCounts[stateValues[i]].put(nextToken, c + 1);
        }
        probabilitiesUpToDate = false;
    }

    // todo: features should probably be read from a training datafile later, which will replace this method
    int createFeatures(String[] tokens, String[] newTokens, int[][] features){
        int length=0;
        int inQuote = IN_QUOTE_FALSE;
        for (int i=0; i<tokens.length;i++){
            if (tokens[i].equals("\"")){
                inQuote = 1-inQuote;
                continue;
            }
            newTokens[length] = tokens[i];
            features[length][STATE_NUMBER_FEATURE] = length;
            features[length][IN_QUOTE_FEATURE] =inQuote;
            length++;
        }
        return length;
    }

    public  double[][] infer(String[] tokens){
        String[] newTokens = new String[tokens.length];
        int[][] features = new int[tokens.length][NUMBER_FEATURES];;
        int length = createFeatures( tokens, newTokens, features);
        return inferForward(newTokens, features, length);
    }

    double[][] inferForward(String[] tokens, int[][] features, int length){
        double [][] stateProbGivenPrevObservations = new double[modelLength][numStateValues];

        //todo: need to check to see which prior is better
        // initialize state probability uniformly
        double uniformProb = 1.0/numStateValues;
        double[] priorProb = new double[numStateValues];
        // double[] priorProb = stateValueProbabilities();

        Arrays.fill(priorProb,uniformProb);
        //Arrays.fill(stateProbGivenPrevObservations[0], uniformProb);

        length = Math.min(modelLength, length);
        for (int i=0;i<length-1;i++){
            stateProbGivenPrevObservations[i] =  inferStateProbabilitiesGivenObservation(i,priorProb,tokens, features);
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

    double[] inferJointProbabilitiesStateAndObservation(int stateNumber, double priorProb[], String[] tokens, int[][] features){

        double[] prob = new double[numStateValues];
        for (int i=0;i<numStateValues; i++){

            // initialize token entry in the maps if it's not seen before
            if ( ! tokenProbabilityGivenStateValue[i].containsKey(tokens[stateNumber]))
                setZeroCountAndProbabilities(tokenCounts, tokenProbabilityGivenStateValue, tokens[stateNumber]);
            if ( ! nextTokenProbabilityGivenStateValue[i].containsKey(tokens[stateNumber+1]))
                setZeroCountAndProbabilities(nextTokenCounts, nextTokenProbabilityGivenStateValue, tokens[stateNumber+1]);
            prob[i] = priorProb[i] * tokenProbabilityGivenStateValue[i].get(tokens[stateNumber]);
            prob[i] *= nextTokenProbabilityGivenStateValue[i].get(tokens[stateNumber+1]);
            //prob[i] *= stateNumberProbabilityGivenStateValue[i][stateNumber];
            for (int j=0; j<NUMBER_FEATURES; j++){
                //prob[i] *= featureValueProbabilityGivenState.get(i).get(j)[stateNumber];
                prob[i] *= featureValueProbabilityGivenState.get(i).get(j)[features[stateNumber][j]];

            }
        }
        return prob;
    }

    public double[] inferStateProbabilitiesGivenObservation(int stateNumber, double priorProb[], String[] tokens, int [][] features){
        double [] prob = inferJointProbabilitiesStateAndObservation(stateNumber, priorProb, tokens, features);
        // normalize
        double sum =0;
        for (double p:prob) sum+=p;
        for (int i=0;i<numStateValues; i++) prob[i] /= sum;
        return prob;
    }
}
