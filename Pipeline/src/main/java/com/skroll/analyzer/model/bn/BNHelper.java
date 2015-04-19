package com.skroll.analyzer.model.bn;

import com.skroll.analyzer.model.RandomVariableType;
import com.skroll.analyzer.model.bn.node.DiscreteNode;
import com.skroll.analyzer.model.bn.node.TrainingDiscreteNode;
import com.skroll.analyzer.model.bn.node.TrainingWordNode;
import com.skroll.analyzer.model.bn.node.WordNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wei2learn on 4/9/2015.
 */
public class BNHelper {
    public static void addSample(TrainingBN bn, SimpleDataTuple tuple){
        addSample(bn, tuple, 1.0);
    }

    public static void addSample(TrainingBN bn, SimpleDataTuple tuple, double weight){
        bn.setObservation(tuple);
        for (DiscreteNode node: bn.getDiscreteNodeArray()){
            ((TrainingDiscreteNode) node).updateCount(weight);
        }

        WordNode[] wordNodes=bn.getWordNodeArray();
        for (int i=0; i<wordNodes.length; i++){
            ((TrainingWordNode) wordNodes[i]).updateCount(weight);
        }
        bn.clearObservation(); // probably unnecessary
    }

//    public static <T> void  createWordNodes(NaiveBayes nb, List<RandomVariableType> wordVarList){
//        List<T> wordNodes = new ArrayList<>();
//        T[] wordNodeArray = new T[wordVarList.size()];
//        for (int i=0; i<wordVarList.size(); i++){
//            wordNodeArray[i] =  new TrainingWordNode((TrainingDiscreteNode)categoryNode);
//        }
//    }
}
