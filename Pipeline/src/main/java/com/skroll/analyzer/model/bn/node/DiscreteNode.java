package com.skroll.analyzer.model.bn.node;

import com.fasterxml.jackson.annotation.*;
import com.skroll.analyzer.model.RandomVariableType;
import com.skroll.analyzer.model.bn.inference.BNInference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wei2learn on 3/1/2015.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="@id")



public class DiscreteNode{

    @JsonIgnore
    public static final Logger logger = LoggerFactory.getLogger(DiscreteNode.class);

    @JsonProperty("familyVariables")
    private RandomVariableType[] familyVariables;

    // store the parameters and probability values in a one dimensional array.
    // convert multi-index to the index of the one dimensional array
    // by treating the multi-index as a multi-base representation of integer,
    // least significant index is index 0.

    private double[] parameters; // used to calculate probability and represent how much piror experience exists.
    // todo: in the future, when models are fixed and updated using a lot of data, may consider multiplying by some positive decaying constant less than 1 to reduce the weight of older experiences

    DiscreteNode[] parents;//, children;
    int observedValue = -1; // -1 means unobserved

    public DiscreteNode(DiscreteNode[] parents){
        this.parents = parents;
    }


    // calculation of the size up to (not include) the variable at the specified index.
    int sizeUpTo(int index){
        int size=1;
        for (int i=0; i<index; i++)
            size *= familyVariables[i].getFeatureSize();
        return size;
    }

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


    @JsonIgnore
    int getParentNodeIndex(DiscreteNode parentNode){
        for (int i=0; i<parents.length; i++)
            if (parents[i] == parentNode) return i;
        return -1;
    }


    public void setFamilyVariables(RandomVariableType[] familyVariables) {
        if (parents.length == (familyVariables.length - 1)) {
            this.familyVariables = familyVariables;
        } else {
            logger.error("Parents and FamilyVariable mismatch");
        }

    }



    @JsonIgnore
    public double getParameter(int index){
        return parameters[index];
    }

    @JsonIgnore
    public int numValues(){
        return familyVariables[0].getFeatureSize();
    }

    public double[] getParameters(){
        return parameters;
    }

    public double[] copyOfParameters(){
        return parameters.clone();
    }


    public void setParameters(double[] parameters) {
        this.parameters = parameters;
    }

    public void setParents(DiscreteNode[] parents) {
        this.parents = parents;
    }

    public DiscreteNode[] getParents() {
        return parents;
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
