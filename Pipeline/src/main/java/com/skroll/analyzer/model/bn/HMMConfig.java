package com.skroll.analyzer.model.bn;

/**
 * Created by wei on 4/16/15.
 */
public class HMMConfig {
    static final int DEFAULT_LENGTH = 12;
    NBFCConfig nbfcConfig;
    int length;
    public HMMConfig(NBFCConfig nbfcConfig){
        this(nbfcConfig, DEFAULT_LENGTH);
    }

    public HMMConfig(NBFCConfig nbfcConfig, int length){
        this.nbfcConfig = nbfcConfig;
        this.length = length;
    }

    public NBFCConfig getNbfcConfig() {
        return nbfcConfig;
    }

    public int getLength() {
        return length;
    }
}
