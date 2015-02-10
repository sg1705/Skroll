package com.skroll.analyzer.model.hmm;

import com.skroll.pipeline.util.Constants;

import java.util.*;
//todo: think how to handle words with different type of cases

/**
 * Created by wei2learn on 12/23/2014.
 */
public class HiddenMarkovModel {
    static final int DEFAULT_MODEL_LENGTH = 12;
    static final int MAX_MODEL_LENGTH = 20;
    static final int DEFAULT_NUM_STATE_VALUES = 2;
    static final double PRIOR_COUNT = 40;

    //static final int NUMBER_FEATURES =2;
    //static final int[] FEATURE_VALUES={2,MAX_MODEL_LENGTH};
    int numberFeatures=2;
    int [] featureSizes={2,MAX_MODEL_LENGTH};

    static final int IN_QUOTE_FEATURE =0;
    static final int STATE_NUMBER_FEATURE =1;

    static final boolean USE_QUOTE= Constants.DEFINITION_CLASSIFICATION_HMM_USE_QUOTE;
    static final boolean USE_NEXT_TOKEN=true; // it seems the next token is an important feature

    static final int IN_QUOTE_FALSE=0;


    int[][] transitionCounts;
    int totalStateValueCounts[];
    Map<String, Integer>[] tokenCounts; // count for each state value, each token
    Map<String, Integer>[] nextTokenCounts; // count for each state value, each token
    List<List<int[]>> stateFeatureValueCounts; // use list of arrays because need to preallocate the count arrays

    int[][] stateNumberCounts; // count for each state value, each state number

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

    // features[0] is the number of state values
    public HiddenMarkovModel(int modelLength, int numStateValues, int [] featureSizes){
        this.modelLength = modelLength;
        this.featureSizes = featureSizes;
        numberFeatures = featureSizes.length; //number of features excluding the state type
        this.numStateValues = numStateValues;
        transitionCounts = new int [numStateValues][numStateValues];
        totalStateValueCounts = new int [numStateValues];
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
            for (int j=0; j<numberFeatures; j++){
                int[] valueCounts = new int[featureSizes[j]];
                featureValueCounts.add(valueCounts);
            }
            stateFeatureValueCounts.add(featureValueCounts);
        }
        featureValueProbabilityGivenState = new ArrayList<List<double[]>>();
        for (int i=0; i<numStateValues; i++){
            List<double[]> featureValueProb = new ArrayList<double[]>();
            for (int j=0; j<numberFeatures; j++){
                double[] valueProbs = new double[featureSizes[j]];
                featureValueProb.add(valueProbs);
            }
            featureValueProbabilityGivenState.add(featureValueProb);
        }

    }
    public HiddenMarkovModel(int modelLength){
        this.modelLength = modelLength;
        transitionCounts = new int [numStateValues][numStateValues];
        totalStateValueCounts = new int [numStateValues];
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
            for (int j=0; j<numberFeatures; j++){
                int[] valueCounts = new int[featureSizes[j]];
                featureValueCounts.add(valueCounts);
            }
            stateFeatureValueCounts.add(featureValueCounts);
        }
        featureValueProbabilityGivenState = new ArrayList<List<double[]>>();
        for (int i=0; i<numStateValues; i++){
            List<double[]> featureValueProb = new ArrayList<double[]>();
            for (int j=0; j<numberFeatures; j++){
                double[] valueProbs = new double[featureSizes[j]];
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
        for (int n: totalStateValueCounts) total += n+PRIOR_COUNT;
        for (int i=0; i<numStateValues; i++)
            stateValueProbability[i] = (double)(totalStateValueCounts[i]+PRIOR_COUNT)/total;

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
            for (int j=0; j<numberFeatures; j++){
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

    public void updateCounts(String[] tokens, int[] stateValues, int[][] features){

        //skip samples that are too short
        if (tokens.length < modelLength || stateValues.length<modelLength || features.length<modelLength) return;

        for (int i=0;i<modelLength && i < tokens.length; i++){
            for (int f =0; f<features[0].length; f++){
                stateFeatureValueCounts.get(stateValues[i]).get(f)[features[i][f]]++;
            }
            totalStateValueCounts[stateValues[i]]++;

            String token = tokens[i];
            String nextToken=null;
            // nextToken will be null if token is the last token
            if (i+1 < tokens.length) { // do not update if at the last state
                transitionCounts[stateValues[i]][stateValues[i + 1]]++;
                nextToken = tokens[i+1];
            }


            Integer c = tokenCounts[stateValues[i]].get(token);
            if (c==null) c=0;
            tokenCounts[stateValues[i]].put(token, c + 1);

            if (!USE_NEXT_TOKEN || nextToken==null) continue;
            c = nextTokenCounts[stateValues[i]].get(nextToken);
            if (c==null) c=0;
            nextTokenCounts[stateValues[i]].put(nextToken, c + 1);
        }
        probabilitiesUpToDate = false;
    }

    // todo: this method should be removed. But it's still used in some places
    public void updateCounts(String[] tokens, int[] stateValues){

        //skip samples that are too short
        if (tokens.length < modelLength || stateValues.length<modelLength) return;

        int stateNumber =0; //this skips the quotes
        int inQuote=0; // a flag toggles between 0 and 1 each time a quote is encountered


        int length=modelLength;

        for (int i=0;i<length && i < tokens.length; i++){


            String token = tokens[i];
            int inc=1; // inc is used to get the next token
            if (i+inc <tokens.length && tokens[i+inc].equals("\"")) {
                inc++;
                //if (i+inc >= tokens.length) return; // if the token is the last
            }

            // nextToken will be null if token is the last token
            String nextToken=null;
            if (i+inc < tokens.length) nextToken = tokens[i+inc];

            //todo: eventually we want to have this put in training data file instead of putting it here
            // handling quote feature
            if (token.equals("\"")) {
                if (USE_QUOTE) inQuote = 1 - inQuote;
                length++;
                continue;
            }

            stateFeatureValueCounts.get(stateValues[i]).get(IN_QUOTE_FEATURE)[inQuote]++;
            stateFeatureValueCounts.get(stateValues[i]).get(STATE_NUMBER_FEATURE)[stateNumber]++;
            stateNumber++; //increase the stateNumber, not counting the quotes
            //stateNumberCounts[stateValues[i]][i]++;



            totalStateValueCounts[stateValues[i]]++;
            if (i+inc < tokens.length) // do not update if at the last state
                transitionCounts[stateValues[i]][stateValues[i+inc]]++;

            Integer c = tokenCounts[stateValues[i]].get(token);
            if (c==null) c=0;
            tokenCounts[stateValues[i]].put(token, c + 1);

            if (!USE_NEXT_TOKEN || nextToken==null) continue;
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

    // combine and normalize the probabilities
    double[][] combine( double[][] forward, double[][] back){
        double [][] prob = new double[modelLength][numStateValues];
        for (int i=0; i<modelLength; i++){
            double s=0;

            // calculate joint probability
            for (int j=0; j<numStateValues; j++){
                prob[i][j] = forward[i][j]*back[i][j];
                s += prob[i][j];
            }

            // normalize to get conditional probability given observations
            for (int j=0; j<numStateValues;j++){
                if (s==0)  // todo: convert probability calculations to log based to avoid problems of underflow
                    prob[i][j] =0;
                else
                    prob[i][j] /= s;
            }
        }

        return prob;
    }


    // todo: should probably remove this method or remove the part that creats features
    public  int[] mostLikelyStateSequence(String[] tokens) {
        double logUniformProb = -Math.log(numStateValues);
        double[] logPriorProb = new double[numStateValues];
        Arrays.fill(logPriorProb,logUniformProb);

        String[] newTokens = new String[tokens.length];
        int[][] features = new int[tokens.length][numberFeatures];;
        int length = createFeatures( tokens, newTokens, features);
        length = Math.min(length, modelLength);

        int[] path = viterbiLog(newTokens, features, logPriorProb);
        //int[] path = viterbi(newTokens, features, length);

        // added back states
        length = Math.min(modelLength, tokens.length);
        int[] hackedResult = new int[length];
        for (int i=0, k=0; i<length;i++){
            if (tokens[i].equals("\"")) {
                hackedResult[i] = 0;
            }
            else
                hackedResult[i] = path[k++];
        }
        return hackedResult;
    }

    public  int[] mostLikelyStateSequence(String[] tokens, int [][] features){
        double logUniformProb = -Math.log(numStateValues);
        double[] logPriorProb = new double[numStateValues];
        Arrays.fill(logPriorProb,logUniformProb);
        return viterbiLog(tokens, features, logPriorProb);

    }

    public  int[] mostLikelyStateSequence(String[] tokens, int [][] features, double[] logPriorProb){

        return viterbiLog(tokens, features, logPriorProb);


    }

    public  double[][] infer(String[] tokens){
        String[] newTokens = new String[tokens.length];
        int[][] features = new int[tokens.length][numberFeatures];;
        int length = createFeatures( tokens, newTokens, features);
        length = Math.min(length, modelLength);
        double[][] probsForward = inferForward(newTokens, features, length);
        double[][] probsBack = inferBackward(newTokens, features, length);
        double[][] probs = combine(probsForward, probsBack);
        //double[][] probs = probsForward;


        // added back quotes probabilities
        length = Math.min(modelLength, tokens.length);
        double[][] hackedResult = new double[length][numberFeatures];
        for (int i=0, k=0; i<length;i++){
            if (tokens[i].equals("\"")) {
                hackedResult[i] = new double[]{0,0};
            }
            else
                hackedResult[i] = probs[k++];
        }
        return hackedResult;
    }

    double[][] inferBackward(String tokens[], int[][] features, int length){
        double [][] observationProbGivenState = new double[modelLength+1][numStateValues];
        Arrays.fill(observationProbGivenState[length],1); // base case.
        for (int i=length-1; i>=0; i--){
            double [] probObservationGivenCurrState= new double[numStateValues];
            double [] probObservationGivenNextState= new double[numStateValues];

            for (int s = 0; s<numStateValues; s++){ // value of the next state
                probObservationGivenNextState[s] = observationProbGivenState[i+1][s];
                for (int f=0; f<numberFeatures; f++){
                    probObservationGivenNextState[s] *= featureValueProbabilityGivenState.get(s).get(f)[features[i][f]];
                }
            }
            for (int t=0; t<numStateValues; t++) { // value of the current state
                for (int s = 0; s < numStateValues; s++) {
                    observationProbGivenState[i][t] += probObservationGivenNextState[s] * transitionProbability[t][s];
                }
            }
        }
        return observationProbGivenState;
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

        // loop skips the last index, because the last state does not have the next token feature.
        // if we remove the next token feature,
        // todo: remove the next token feature and make use of the last index to see what happens.
        for (int i=0;i<length;i++){
            stateProbGivenPrevObservations[i] =  inferStateProbabilitiesGivenObservation(i,priorProb,tokens, features);
            priorProb = inferNextStateProbabilities(stateProbGivenPrevObservations[i]);
            //last calculation is not used for now, because last token is used in the inference of the second last state.
        }
        return stateProbGivenPrevObservations;
    }

    public int [] viterbiLog(String[] tokens, int[][] features, double[] logPriorProb){
        double[] logProb= logPriorProb;

        // for each next state value, stores the most likely state value leads to it
        int [][] paths = new int[modelLength][numStateValues];

        //highest possible probabilities of the observation produced by a state sequence
        double [][] logMaxObservationProbGivenState = new double[modelLength][numStateValues];

        int length = Math.min(modelLength, features.length);

        for (int s=0;s<length;s++){ // for each state
            System.arraycopy(logProb, 0 , logMaxObservationProbGivenState[s], 0, numStateValues);
            for (int sv=0; sv<numStateValues; sv++){ // for each state value

                //todo: to improve efficiency, should avoid store log probabilities afte update probabilities with frequency counts, instead of calculating it here
                logMaxObservationProbGivenState[s][sv] += Math.log(tokenProbGivenStateValue(sv, tokens, s))+
                        Math.log(nextTokenProbGivenStateValue(sv, tokens, s));

                for (int f=0; f<numberFeatures; f++){ // for each feature
                    logMaxObservationProbGivenState[s][sv] +=
                            Math.log(featureValueProbabilityGivenState.get(sv).get(f)[features[s][f]]);
                }
            }
            Arrays.fill(logProb,Double.NEGATIVE_INFINITY);
            for (int sv=0; sv<numStateValues; sv++) { // for each state value
                for (int svNext = 0; svNext < numStateValues; svNext++) { // for each next state value
                    double p = logMaxObservationProbGivenState[s][sv] + Math.log(transitionProbability[sv][svNext]);
                    if (p > logProb[svNext]) {
                        logProb[svNext] = p;
                        paths[s][svNext] = sv; //update the most likely state going to the next state svNext
                    }
                }
            }
            //last calculation is not used for now, because last token is used in the inference of the second last state.
        }

        int path[] = new int[length];
        if (length==0) return path;

        path[length-1] = maxIndex(logMaxObservationProbGivenState[length-1]);
        for (int s=length - 2; s>=0; s--){
            path[s] = paths[s] [path[s+1]];
        }
        return path;
    }

    public int [] viterbi(String[] tokens, int[][] features, int length){
        int states[] = new int[length];

        // for each next state value, stores the most likely state value leads to it
        int [][] paths = new int[modelLength][numStateValues];

        double [][] maxObservationProbGivenState = new double[modelLength][numStateValues];

        //todo: need to check to see which prior is better
        // initialize state probability uniformly
        double uniformProb = 1.0/numStateValues;
        double[] priorProb = new double[numStateValues];
        // double[] priorProb = stateValueProbabilities();

        Arrays.fill(priorProb,uniformProb);
        //Arrays.fill(stateProbGivenPrevObservations[0], uniformProb);

        for (int s=0;s<length;s++){ // for each state
            System.arraycopy(priorProb, 0 , maxObservationProbGivenState[s], 0, numStateValues);
            for (int sv=0; sv<numStateValues; sv++){ // for each state value
                maxObservationProbGivenState[s][sv] *=tokenProbGivenStateValue(sv, tokens, s)*
                        nextTokenProbGivenStateValue(sv, tokens, s);

                for (int f=0; f<numberFeatures; f++){ // for each feature
                    maxObservationProbGivenState[s][sv] *=
                            featureValueProbabilityGivenState.get(sv).get(f)[features[s][f]];
                }
            }
            Arrays.fill(priorProb,0);
            for (int sv=0; sv<numStateValues; sv++) { // for each state value
                for (int svNext = 0; svNext < numStateValues; svNext++) { // for each next state value
                    double p = maxObservationProbGivenState[s][sv] * transitionProbability[sv][svNext];
                    if (p > priorProb[svNext]) {
                        priorProb[svNext] = p;
                        paths[s][svNext] = sv; //update the most likely state going to the next state svNext
                    }
                }
            }
            //last calculation is not used for now, because last token is used in the inference of the second last state.
        }

        int path[] = new int[length];
        if (length==0) return path;

        path[length-1] = maxIndex(maxObservationProbGivenState[length-1]);
        for (int s=length - 2; s>=0; s--){
            path[s] = paths[s] [path[s+1]];
        }
        return path;
    }
    int maxIndex(double[] vals){
        int maxI=0;
        for (int i=1; i<vals.length; i++)
            if (vals[maxI] < vals[i]) maxI = i;
        return maxI;
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

    double tokenProbGivenStateValue(int stateValue, String[] tokens, int stateNumber){
        String token  = tokens[stateNumber];
        if (! tokenProbabilityGivenStateValue[stateValue].containsKey(token)){
            setZeroCountAndProbabilities(tokenCounts, tokenProbabilityGivenStateValue,token);
        }
        return tokenProbabilityGivenStateValue[stateValue].get(token);
    }

    double nextTokenProbGivenStateValue(int stateValue, String[] tokens, int stateNumber){
        if (stateNumber+1 == tokens.length) return 1; //no more next token
        String token = tokens[stateNumber+1];
        if (! nextTokenProbabilityGivenStateValue[stateValue].containsKey(token)){
            setZeroCountAndProbabilities(tokenCounts, nextTokenProbabilityGivenStateValue,token);
        }
        return nextTokenProbabilityGivenStateValue[stateValue].get(token);
    }

    double[] inferJointProbabilitiesStateAndObservation(int stateNumber, double priorProb[], String[] tokens, int[][] features){

        double[] prob = new double[numStateValues];
        for (int i=0;i<numStateValues; i++){

            // initialize token entry in the maps if it's not seen before
//            if ( ! tokenProbabilityGivenStateValue[i].containsKey(tokens[stateNumber]))
//                setZeroCountAndProbabilities(tokenCounts, tokenProbabilityGivenStateValue, tokens[stateNumber]);
//            if ( ! nextTokenProbabilityGivenStateValue[i].containsKey(tokens[stateNumber+1]))
//                setZeroCountAndProbabilities(nextTokenCounts, nextTokenProbabilityGivenStateValue, tokens[stateNumber+1]);
            prob[i] = priorProb[i] * tokenProbGivenStateValue(i, tokens, stateNumber);
            prob[i] *=  nextTokenProbGivenStateValue(i, tokens, stateNumber);
            //prob[i] *= nextTokenProbabilityGivenStateValue[i].get(tokens[stateNumber+1]);
            //prob[i] *= stateNumberProbabilityGivenStateValue[i][stateNumber];
            for (int j=0; j<numberFeatures; j++){
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

    @Override
    public String toString() {
        return "HiddenMarkovModel{\n "+ showCounts()+showProbabilities()+"\n}";
    }
}
