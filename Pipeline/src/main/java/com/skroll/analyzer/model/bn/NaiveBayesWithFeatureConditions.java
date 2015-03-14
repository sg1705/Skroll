package com.skroll.analyzer.model.bn;

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

    public  NaiveBayesWithFeatureConditions(){

    }

    void generateParentsAndChildren(){
        categoryNode.setChildren(featureNodeArray);
        int i=0;
        for (; i<documentFeatureNodeArray.length; i++){
            featureNodeArray[i].setParents(Arrays.asList(categoryNode, documentFeatureNodeArray[i]).
                    toArray(new DiscreteNode[documentFeatureNodeArray.length]));
            documentFeatureNodeArray[i].setChildren( Arrays.asList( documentFeatureNodeArray[i]).
                    toArray( new DiscreteNode[1]));
        }
        for (; i<featureNodeArray.length; i++)
            featureNodeArray[i].setParents(Arrays.asList(categoryNode).
                    toArray(new DiscreteNode[documentFeatureNodeArray.length]));
    }

    public DiscreteNode[] getDocumentFeatureNodeArray() {
        return documentFeatureNodeArray;
    }


}
