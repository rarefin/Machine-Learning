package com.rifatarefin.Forest;


import com.rifatarefin.RegressionTree.Node;
import com.rifatarefin.RegressionTree.RandomTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by ROBIN on 9/6/2016.
 */
public class RegressionForest {
    // Data
    private ArrayList<Double>[] features;
    private ArrayList<Double> labels;

    // parameters
    private int noOfRandomFeatures;     // no of random features selected for each tree
    private int minLeafSize = 5;        // minimum size of each leaf node
    private int noOfTrees = 100;        // no of trees in a forest
    private double minInfoGain = 0;     // minimum information gain for splitting


    // Constructor for by giving only features amd labels without parameters
    public RegressionForest(ArrayList<Double>[] features, ArrayList<Double> labels) {
        this.features = features;
        this.labels = labels;
        this.noOfRandomFeatures = (int)Math.round(Math.sqrt(features.length));
    }

    public RegressionForest(ArrayList<Double>[] features, ArrayList<Double> labels, int minLeafSize, int noOfTrees) {
        this.features = features;
        this.labels = labels;
        this.noOfRandomFeatures = (int)Math.round(Math.sqrt(features.length));
        this.minLeafSize = minLeafSize;
        this.noOfTrees = noOfTrees;
    }

    public RegressionForest(ArrayList<Double>[] features, ArrayList<Double> labels, int noOfRandomFeatures, int minLeafSize, int noOfTrees) {
        this.features = features;
        this.labels = labels;
        this.noOfRandomFeatures = noOfRandomFeatures;
        this.minLeafSize = minLeafSize;
        this.noOfTrees = noOfTrees;
    }

    public RegressionForest(ArrayList<Double>[] features, ArrayList<Double> labels, int minLeafSize, int noOfTrees, double minInfoGain) {
        this.features = features;
        this.labels = labels;
        this.noOfRandomFeatures = (int)Math.round(Math.sqrt(features.length));
        this.minLeafSize = minLeafSize;
        this.noOfTrees = noOfTrees;
        this.minInfoGain = minInfoGain;
    }

    public RegressionForest(ArrayList<Double>[] features, ArrayList<Double> labels, int noOfRandomFeatures, int minLeafSize, int noOfTrees, double minInfoGain) {
        this.features = features;
        this.labels = labels;
        this.noOfRandomFeatures = noOfRandomFeatures;
        this.minLeafSize = minLeafSize;
        this.noOfTrees = noOfTrees;
        this.minInfoGain = minInfoGain;
    }

    public int getNoOfRandomFeatures(ArrayList<Double>[] features, ArrayList<Integer> labels) {
        return noOfRandomFeatures;
    }

    public void setNoOfRandomFeatures(int noOfRandomFeatures) {
        this.noOfRandomFeatures = noOfRandomFeatures;
    }

    public int getMinLeafSize() {
        return minLeafSize;
    }

    public void setMinLeafSize(int minLeafSize) {
        this.minLeafSize = minLeafSize;
    }

    public int getNoOfTrees() {
        return noOfTrees;
    }

    public void setNoOfTrees(int noOfTrees) {
        this.noOfTrees = noOfTrees;
    }

    public double getMinInfoGain() {
        return minInfoGain;
    }

    public void setMinInfoGain(double minInfoGain) {
        this.minInfoGain = minInfoGain;
    }

    public Node[] createForest(){
        Node[] forest = new Node[this.noOfTrees];
        for (int i = 0; i < forest.length; i++) {
            Node tree = new Node();
            RandomTree randomTree = new RandomTree();
            randomTree.createTree(tree, this.features, this.labels, this.minLeafSize, this.minInfoGain, this.noOfRandomFeatures);
            forest[i] = tree;
        }
        return forest;
    }
}
