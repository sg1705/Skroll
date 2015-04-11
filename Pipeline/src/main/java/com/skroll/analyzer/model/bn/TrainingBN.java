package com.skroll.analyzer.model.bn;

import com.skroll.analyzer.model.bn.node.DiscreteNode;
import com.skroll.analyzer.model.bn.node.WordNode;

/**
 * Created by wei2learn on 4/9/2015.
 */
public interface TrainingBN {
    public DiscreteNode[] getDiscreteNodeArray();
    public WordNode[] getWordNodeArray();
    public void addSample(SimpleDataTuple tuple);
    public void setObservation(SimpleDataTuple tuple);
    public void clearObservation();
}
