package com.skroll.analyzer.model.bn.node;

import com.fasterxml.jackson.annotation.*;
import com.skroll.analyzer.model.RandomVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wei2learn on 3/1/2015.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")


// todo: should this be extened from a base node class? maybe have DiscreteNode as the superclass of both MultiplexNode and StandardNode?
/**
 * MultiplexNode is just collection of nodes, selected by a special parent Node.
 */
public class MultiplexNode {

    @JsonIgnore
    public static final Logger logger = LoggerFactory.getLogger(MultiplexNode.class);

    DiscreteNode selectingNode;
    DiscreteNode[] nodes; //The multiplex node can be simplified to independent simpler nodes selected by the selectingNode.

    /**
     * this constructor assumes the 1st parent is the selectingNode.
     *
//     * @param parents
     */
//    public MultiplexNode(DiscreteNode[] parents) {
//        selectingNode = parents[0];
//    }
    public MultiplexNode(DiscreteNode selectingNode) {
       this.selectingNode = selectingNode;
    }
    @JsonCreator
    public MultiplexNode(
            @JsonProperty("nodes") DiscreteNode[] nodes,
            @JsonProperty("selectingNode") DiscreteNode selectingNode
    ) {
        this.nodes = nodes;
        this.selectingNode = selectingNode;
    }


    // all nodes should have the same observation.
    public Integer getObservation() {
        if (nodes.length > 0) return nodes[0].getObservation();
        logger.error("MultiplexNode is empty");
        return -1;
    }

    @JsonIgnore
    public void setObservation(Integer observedValue) {
        for (DiscreteNode node : nodes)
            node.setObservation(observedValue);
    }

    @JsonIgnore
    public void clearObservation() {
        for (DiscreteNode node : nodes)
            node.clearObservation();
    }

    public DiscreteNode[] getNodes() {
        return nodes;
    }

    public void setNodes(DiscreteNode[] nodes) {
        this.nodes = nodes;
    }

    @JsonIgnore
    public DiscreteNode getActiveNode() {
        int selectedIndex = selectingNode.getObservation();
        return nodes[selectedIndex];
    }

    public DiscreteNode getSelectingNode() {
        return selectingNode;
    }

    @Override
    public String toString() {
        return "MultiplexNode{" +
                "selectingNode=" + selectingNode +
                ", nodes=" + Arrays.toString(nodes) +
                '}';
    }
//
//    @JsonIgnore
//    int getParentNodeIndex(MultiplexNode parentNode){
//        for (int i=0; i<parents.length; i++)
//            if (parents[i] == parentNode) return i;
//        return -1;
//    }
//
//
//    @JsonIgnore
//    public void setFamilyVariables(RandomVariable[] familyVariables) {
//        if (parents.length == (familyVariables.length - 1)) {
//            this.familyVariables = familyVariables;
//        } else {
//            logger.error("Parents and FamilyVariable mismatch");
//        }
//
//    }
//
//
//
//    @JsonIgnore
//    public double getParameter(int index){
//        return parameters[index];
//    }
//
//    @JsonIgnore
//    public int numValues(){
//        return familyVariables[0].getFeatureSize();
//    }
//
//    @JsonIgnore
//    public double[] getParameters(){
//        return parameters;
//    }
//
//    @JsonIgnore
//    public double[] copyOfParameters(){
//        return parameters.clone();
//    }
//
//    @JsonIgnore
//    public void setParameters(double[] parameters) {
//        this.parameters = parameters;
//    }
//
//    @JsonIgnore
//    public void setParents(MultiplexNode[] parents) {
//        this.parents = parents;
//    }
//
//    @JsonIgnore
//    public MultiplexNode[] getParents() {
//        return parents;
//    }
//
//    @JsonIgnore
//    public RandomVariable getVariable() {
//        return familyVariables[0];
//    }
//
//    @JsonIgnore
//    public RandomVariable[] getFamilyVariables() {
//        return familyVariables;
//    }
//
//
//
//    @Override
//    public String toString() {
//        return "DiscreteNode{" +
//                "familyVariables=" + Arrays.toString(familyVariables) +
//                ", parameters=" + Arrays.toString(parameters) +
//                '}';
//    }
//
public boolean equals(MultiplexNode dn) {
    boolean isEquals = true;
    isEquals = isEquals && DiscreteNode.compareDNList(
            Arrays.asList(this.getNodes()), Arrays.asList(dn.getNodes()));
    isEquals = isEquals && selectingNode.equals(dn.getSelectingNode());
    return isEquals;
}
//
//    public static boolean compareDNList(MultiplexNode[] list, MultiplexNode[] list2) {
//        if (list.length != list2.length) {
//            return false;
//        }
//        boolean isEqual = true;
//        for(int ii = 0; ii < list.length; ii++) {
//            isEqual = isEqual && list[ii].equals(list2[ii]);
//        }
//        return isEqual;
//    }
//
//    public static boolean compareDNList(List<MultiplexNode> list, List<MultiplexNode> list2) {
//        if (list.size() != list2.size()) {
//            return false;
//        }
//        boolean isEqual = true;
//        for(int ii = 0; ii < list.size(); ii++) {
//            isEqual = isEqual && list.get(ii).equals(list2.get(ii));
//        }
//        return isEqual;
//    }
//

}
