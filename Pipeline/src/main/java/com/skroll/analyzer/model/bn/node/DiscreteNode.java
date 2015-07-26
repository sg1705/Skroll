package com.skroll.analyzer.model.bn.node;

import com.fasterxml.jackson.annotation.*;
import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.bn.inference.BNInference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wei2learn on 3/1/2015.
 */
@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="@id")

public class DiscreteNode{

    @JsonIgnore
    public static final Logger logger = LoggerFactory.getLogger(DiscreteNode.class);

    @JsonProperty("familyVariables")
    private RandomVariable[] familyVariables;

    // store the parameters and probability values in a one dimensional array.
    // convert multi-index to the index of the one dimensional array
    // by treating the multi-index as a multi-base representation of integer,
    // least significant index is index 0.
    @JsonProperty("parameters")
    private double[] parameters; // used to calculate probability and represent how much piror experience exists.
    // todo: in the future, when models are fixed and updated using a lot of data, may consider multiplying by some positive decaying constant less than 1 to reduce the weight of older experiences

    @JsonProperty("parents")
    DiscreteNode[] parents;//, children;
    @JsonProperty("observedValue")
    int observedValue = -1; // -1 means unobserved

    public DiscreteNode(DiscreteNode[] parents){
        this.parents = parents;
    }

    @JsonCreator
    public DiscreteNode(
            @JsonProperty("parents")DiscreteNode[] parents,
            @JsonProperty("parameters")double[] parameters,
            @JsonProperty("observedValue")int observedValue,
            @JsonProperty("familyVariables")RandomVariable[] familyVariables){
        this.parents = parents;
        this.parameters = parameters;
        this.observedValue = observedValue;
        this.familyVariables = familyVariables;
    }

    @JsonIgnore
    public Integer getObservation() {
        return observedValue;
    }

    @JsonIgnore
    public void setObservation(Integer observedValue) {
        this.observedValue = observedValue;
    }

    @JsonIgnore
    public void clearObservation() {
        this.observedValue = -1;
    }


    @JsonIgnore
    int getParentNodeIndex(DiscreteNode parentNode){
        for (int i=0; i<parents.length; i++)
            if (parents[i] == parentNode) return i;
        return -1;
    }


    @JsonIgnore
    public void setFamilyVariables(RandomVariable[] familyVariables) {
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

    @JsonIgnore
    public double[] getParameters(){
        return parameters;
    }

    @JsonIgnore
    public double[] copyOfParameters(){
        return parameters.clone();
    }

    @JsonIgnore
    public void setParameters(double[] parameters) {
        this.parameters = parameters;
    }

    @JsonIgnore
    public void setParents(DiscreteNode[] parents) {
        this.parents = parents;
    }

    @JsonIgnore
    public DiscreteNode[] getParents() {
        return parents;
    }

    @JsonIgnore
    public RandomVariable getVariable() {
        return familyVariables[0];
    }

    @JsonIgnore
    public RandomVariable[] getFamilyVariables() {
        return familyVariables;
    }



    @Override
    public String toString() {
        return "DiscreteNode{" +
                "familyVariables=" + Arrays.toString(familyVariables) +
                ", parameters=" + Arrays.toString(parameters) +
                '}';
    }

    public boolean equals(DiscreteNode dn) {
        boolean isEquals = true;
        isEquals = isEquals && RandomVariable.compareRVList(this.familyVariables, dn.familyVariables);
        isEquals = isEquals && compareDNList(this.parents, dn.parents);
        return isEquals;
    }

    public static boolean compareDNList(DiscreteNode[] list, DiscreteNode[] list2) {
        if (list.length != list2.length) {
            return false;
        }
        boolean isEqual = true;
        for(int ii = 0; ii < list.length; ii++) {
            isEqual = isEqual && list[ii].equals(list2[ii]);
        }
        return isEqual;
    }

    public static boolean compareDNList(List<DiscreteNode> list, List<DiscreteNode> list2) {
        if (list.size() != list2.size()) {
            return false;
        }
        boolean isEqual = true;
        for(int ii = 0; ii < list.size(); ii++) {
            isEqual = isEqual && list.get(ii).equals(list2.get(ii));
        }
        return isEqual;
    }


}
