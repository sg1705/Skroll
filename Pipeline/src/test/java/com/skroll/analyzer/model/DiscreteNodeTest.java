package com.skroll.analyzer.model;

import com.skroll.analyzer.model.bn.node.DiscreteNode;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;

public class DiscreteNodeTest extends TestCase {

    public void testToString() throws Exception {
        DiscreteNode node = new DiscreteNode(new ArrayList<>(Arrays.asList(
                RandomVariableType.PARAGRAPH_STARTS_WITH_QUOTE,
                RandomVariableType.PARAGRAPH_HAS_DEFINITION,
                RandomVariableType.DOCUMENT_DEFINITIONS_IN_QUOTES
        )));

        System.out.println(node);

    }

    public void testIncrement() throws Exception {
        DiscreteNode node = new DiscreteNode(new ArrayList<>(Arrays.asList(
                RandomVariableType.PARAGRAPH_STARTS_WITH_QUOTE,
                RandomVariableType.PARAGRAPH_HAS_DEFINITION,
                RandomVariableType.DOCUMENT_DEFINITIONS_IN_QUOTES
        )));

        System.out.println(node);
        node.updateCount(new int[]{0, 0, 0});
        node.updateCount(new int[]{0, 1, 0});
        node.updateCount(new int[]{0, 0, 1});
        node.updateCount(new int[]{0, 1, 1});
        node.updateCount(new int[]{1, 1, 1});
        System.out.println(node);

    }

    public void testIncrement1() throws Exception {

    }

    public void testUpdateProbabilities() throws Exception {
        DiscreteNode node = new DiscreteNode(new ArrayList<>(Arrays.asList(
                RandomVariableType.PARAGRAPH_STARTS_WITH_QUOTE,
                RandomVariableType.PARAGRAPH_HAS_DEFINITION,
                RandomVariableType.DOCUMENT_DEFINITIONS_IN_QUOTES
        )));

        System.out.println(node);
        node.updateCount(new int[]{0, 0, 0});
        node.updateCount(new int[]{0, 1, 0});
        node.updateCount(new int[]{0, 0, 1});
        node.updateCount(new int[]{0, 1, 1});
        node.updateCount(new int[]{1, 1, 1});
        node.updateCount(new int[]{1, 1, 1});

        System.out.println(node);
        node.updateProbabilities();
        System.out.println(node);
    }
}