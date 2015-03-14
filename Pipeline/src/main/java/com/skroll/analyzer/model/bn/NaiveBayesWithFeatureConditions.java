package com.skroll.analyzer.model.bn;

import com.google.common.collect.ObjectArrays;
import com.skroll.analyzer.model.bn.node.DiscreteNode;
import com.skroll.analyzer.model.RandomVariableType;
import com.skroll.analyzer.model.bn.node.TrainingDiscreteNode;
import com.skroll.analyzer.model.bn.node.WordNode;
import com.skroll.analyzer.model.nb.*;

import java.util.*;

/**
 * Created by wei2learn on 1/3/2015.
 */
public class NaiveBayesWithFeatureConditions extends NaiveBayes{

    DiscreteNode[]  documentFeatureNodeArray;
    DiscreteNode[] featureExistAtDocLevelArray;

    public  NaiveBayesWithFeatureConditions(){

    }

    void generateParentsAndChildren(){
        categoryNode.setChildren( ObjectArrays.concat
                (featureExistAtDocLevelArray, documentFeatureNodeArray, DiscreteNode.class));
        for (int i=0; i<documentFeatureNodeArray.length; i++){
            featureExistAtDocLevelArray[i].setParents(Arrays.asList(categoryNode, documentFeatureNodeArray[i]).
                    toArray(new DiscreteNode[documentFeatureNodeArray.length]));
            documentFeatureNodeArray[i].setChildren( Arrays.asList( featureExistAtDocLevelArray[i]).
                    toArray( new DiscreteNode[1]));
        }
        for (int i=0; i<featureNodeArray.length; i++)
            featureNodeArray[i].setParents(Arrays.asList(categoryNode).toArray(new DiscreteNode[1]));
    }

    public DiscreteNode[] getDocumentFeatureNodeArray() {
        return documentFeatureNodeArray;
    }

    public DiscreteNode[] getFeatureExistAtDocLevelArray() {
        return featureExistAtDocLevelArray;
    }
}
