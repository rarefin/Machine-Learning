package com.rifatarefin.Tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ROBIN on 8/23/2016.
 */
public class Node implements Serializable{
    private boolean isLeaf;
    private ArrayList<Double> decisionInstances;
    private double entropy;
    private double threshold;
    private int selectedFeatureIndex;
    private Node leftChild;
    private Node rightChild;
    private int leafIndex;
    private HashMap<Double, Double> classProbabilities;
    private int noOfLeaves;
    private double oobAccuracy;
    private int depth;

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getNoOfLeaves() {
        return noOfLeaves;
    }

    public void setNoOfLeaves(int noOfLeaves) {
        this.noOfLeaves = noOfLeaves;
    }

    public double getOobAccuracy() {
        return oobAccuracy;
    }

    public void setOobAccuracy(double oobAccuracy) {
        this.oobAccuracy = oobAccuracy;
    }

    public HashMap<Double, Double> getClassProbabilities() {
        return classProbabilities;
    }

    public void setClassProbabilities(HashMap<Double, Double> classProbabilities) {
        this.classProbabilities = classProbabilities;
    }

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
