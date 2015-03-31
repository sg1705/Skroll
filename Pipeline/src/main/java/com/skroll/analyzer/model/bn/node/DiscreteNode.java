package com.skroll.analyzer.model.bn.node;

import com.fasterxml.jackson.annotation.*;
import com.skroll.analyzer.model.RandomVariableType;
import com.skroll.analyzer.model.bn.inference.BNInference;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wei2learn on 3/1/2015.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = com.skroll.analyzer.model.bn.node.LogProbabilityDiscreteNode.class, name = "LogProbabilityDiscreteNode"),
        @JsonSubTypes.Type(value = com.skroll.analyzer.model.bn.node.ProbabilityDiscreteNode.class, name = "ProbabilityDiscreteNode"),
        @JsonSubTypes.Type(value = com.skroll.analyzer.model.bn.node.TrainingDiscreteNode.class, name = "TrainingDiscreteNode")})
@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="@id")
public class DiscreteNode{
    RandomVariableType[] familyVariables;

    // store the parameters and probability values in a one dimensional array.
    // convert multi-index to the index of the one dimensional array
    // by treating the multi-index as a multi-base representation of integer,
    // least significant index is index 0.

    double[] parameters; // used to calculate probability and represent how much piror experience exists.
    // todo: in the future, when models are fixed and updated using a lot of data, may consider multiplying by some positive decaying constant less than 1 to reduce the weight of older experiences

    DiscreteNode[] parents, children;
    int observedValue = -1; // -1 means unobserved
    @JsonIgnore
    public Integer getObservation() {
        return observedValue;
    }

    public void setObservation(Integer observedValue) {
        this.observedValue = observedValue;
    }

    public void clearObservation() {
        this.observedValue = -1;
    }

    // calculation of the size up to (not include) the variable at the specified index.
    int sizeUpTo(int index){
        int size=1;
        for (int i=0; i<index; i++)
            size *= familyVariables[i].getFeatureSize();
        return size;
    }

    public DiscreteNode(){
    }

    @JsonIgnore
    int getParentNodeIndex(DiscreteNode parentNode){
        for (int i=0; i<parents.length; i++)
            if (parents[i] == parentNode) return i;
        return -1;
    }

    public DiscreteNode(List<RandomVariableType> randomVariables){
        this.familyVariables =  randomVariables.toArray(new RandomVariableType[randomVariables.size()]);

        int totalSize = sizeUpTo(familyVariables.length);
        parameters = new double[totalSize];
    }

    @JsonIgnore
    int[] getRandomVariableSizes(List<RandomVariableType> randomVariables){
        int[] sizes = new int[randomVariables.size()];
        for (int i=0; i<sizes.length;i++)
            sizes[i] = randomVariables.get(i).getFeatureSize();
        return sizes;
    }

    @JsonIgnore
    int getIndex(int [] multiIndex){ // least significant digit on the left.
        int index=0;
        for (int i=multiIndex.length-1; i>=0; i--){
            index *= familyVariables[i].getFeatureSize();
            index += multiIndex[i];
        }
        return index;
    }

    double[] normalize(double weight){
        return BNInference.normalize(parameters, weight);
    }

    @JsonIgnore
    public double getParameter(int index){
        return parameters[index];
    }

    public double[] getParameters(){
        return parameters;
    }

    public void setParameters(double[] parameters) {
        this.parameters = parameters;
    }

    public void setChildren(DiscreteNode[] children) {
        this.children = children;
    }

    public void setParents(DiscreteNode[] parents) {
        this.parents = parents;
    }

    @JsonIgnore
    public RandomVariableType getVariable(){
        return familyVariables[0];
    }

    @JsonIgnore
    public RandomVariableType[] getFamilyVariables(){
        return familyVariables;
    }


    @Override
    public String toString() {
        return "DiscreteNode{" +
                "familyVariables=" + Arrays.toString(familyVariables) +
                ", parameters=" + Arrays.toString(parameters) +

                '}';
    }
}
