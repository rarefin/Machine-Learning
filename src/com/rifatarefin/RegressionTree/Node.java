package com.rifatarefin.RegressionTree;

import java.util.ArrayList;

/**
 * Created by ROBIN on 8/23/2016.
 */
public class Node {
    private boolean isLeaf;
    private ArrayList<Double> decisionInstances;
    private double entropy;
    private double threshold;
    private int selectedFeatureIndex;
    private Node leftChild;
    private Node rightChild;
    private int leafIndex;

    public int getLeafIndex() {
        return leafIndex;
    }

    public void setLeafIndex(int leafIndex) {
        this.leafIndex = leafIndex;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public int getSelectedFeatureIndex() {
        return selectedFeatureIndex;
    }

    public void setSelectedFeatureIndex(int selectedFeatureIndex) {
        this.selectedFeatureIndex = selectedFeatureIndex;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean isLeaf) {
        this.isLeaf = isLeaf;
    }

    public ArrayList<Double> getDecisionInstances() {
        return decisionInstances;
    }

    public void setDecisionInstances(ArrayList<Double> decisionInstances) {
        this.decisionInstances = decisionInstances;
    }

    public double getEntropy() {
        return entropy;
    }

    public void setEntropy(double entropy) {
        this.entropy = entropy;
    }

    public Node getLeftChild() {
        return leftChild;
    }

    public void setLeftChild(Node leftChild) {
        this.leftChild = leftChild;
    }

    public Node getRightChild() {
        return rightChild;
    }

    public void setRightChild(Node rightChild) {
        this.rightChild = rightChild;
    }
}
