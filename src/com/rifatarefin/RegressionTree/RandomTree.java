package com.rifatarefin.RegressionTree;

import java.util.*;

/**
 * Created by ROBIN on 8/26/2016.
 */
public class RandomTree {
    private int leafIndex;
    public RandomTree(){
        this.leafIndex = 0;
    }

    public void createTree(Node root, ArrayList<Double>[] features, ArrayList<Double> labels, int minLeafSize, double minInfoGain, int noOfRandomFeatures){

        if(labels.size() <= minLeafSize){
            root.setLeaf(true);
            root.setDecisionInstances(labels);
            root.setLeafIndex(this.leafIndex);
            this.leafIndex++;
            return;
        }else {
            ArrayList<Integer> list = new ArrayList<Integer>();
            for (int i=0; i<features.length; i++) {
                list.add(i);
            }
            Collections.shuffle(list);
            ArrayList<Integer> selectedFeatures = new ArrayList<Integer>();
            for (int i=0; i<noOfRandomFeatures; i++) {
                selectedFeatures.add(list.get(i));
            }
            HashMap<String, String> bestSplit =  SplitCalculator.calculateBestSplit(features, labels, selectedFeatures);
            int featureIndex = Integer.parseInt(bestSplit.get("BestFeatureIndex"));
            double threshold = Double.parseDouble(bestSplit.get("BestThreshold"));
            double infoGain = Double.parseDouble(bestSplit.get("MaxInfoGain"));

            if(infoGain < minInfoGain){
                root.setLeaf(true);
                root.setDecisionInstances(labels);
                root.setLeafIndex(this.leafIndex);
                this.leafIndex++;
                return;
            }else {
                ArrayList<Double>[] leftChildFeatures = new ArrayList[features.length];
                ArrayList<Double>[] rightChildFeatures = new ArrayList[features.length];
                for (int i = 0; i < features.length; i++) {
                    leftChildFeatures[i] = new ArrayList<Double>();
                    rightChildFeatures[i] = new ArrayList<Double>();
                }
                ArrayList<Double> leftChildLabels = new ArrayList<Double>();
                ArrayList<Double> rightChildLabels = new ArrayList<Double>();

                for (int i = 0; i < features[featureIndex].size(); i++) {
                    if(features[featureIndex].get(i) <= threshold){
                        for (int j = 0; j < features.length; j++) {
                            leftChildFeatures[j].add(features[j].get(i));
                        }
                        leftChildLabels.add(labels.get(i));
                    }else {
                        for (int j = 0; j < features.length; j++) {
                            rightChildFeatures[j].add(features[j].get(i));
                        }
                        rightChildLabels.add(labels.get(i));
                    }
                }

                Node leftChild = new Node();
                Node rightChild = new Node();
                root.setLeftChild(leftChild);
                root.setRightChild(rightChild);
                root.setSelectedFeatureIndex(featureIndex);
                root.setThreshold(threshold);
                root.setLeaf(false);

                if(leftChildLabels.size() > 0){
                    createTree(leftChild, leftChildFeatures, leftChildLabels, minLeafSize, minInfoGain, noOfRandomFeatures);
                }

                if(rightChildLabels.size() > 0){
                    createTree(rightChild, rightChildFeatures, rightChildLabels, minLeafSize, minInfoGain, noOfRandomFeatures);
                }
            }
        }
    }
}
