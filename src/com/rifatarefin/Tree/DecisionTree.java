package com.rifatarefin.Tree;

import com.rifatarefin.Statistics.Stat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by ROBIN on 8/26/2016.
 */
public class DecisionTree {

    public void createTree(Node root, ArrayList<Double>[] features, ArrayList<Double> labels, int minLeafSize, double minInfoGain, int noOfClass) {
        // Checking purity of a node
        HashSet<Double> hashSet = new HashSet<Double>();
        for (int i = 0; i < labels.size(); i++) {
            hashSet.add(labels.get(i));
        }

        if (hashSet.size() == 1) {
            root.setLeaf(true);
            root.setDecisionInstances(labels);
        } else if (labels.size() <= minLeafSize) {
            root.setLeaf(true);
            root.setDecisionInstances(labels);
            return;
        } else {
            HashMap<String, String> bestSplit =  calculateBestSplit(features, labels, noOfClass);
            int featureIndex = Integer.parseInt(bestSplit.get("BestFeatureIndex"));
            double threshold = Double.parseDouble(bestSplit.get("BestThreshold"));
            double infoGain = Double.parseDouble(bestSplit.get("MaxInfoGain"));

            if(infoGain < minInfoGain){
                root.setLeaf(true);
                root.setDecisionInstances(labels);
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
                    if (features[featureIndex].get(i) <= threshold) {
                        for (int j = 0; j < features.length; j++) {
                            leftChildFeatures[j].add(features[j].get(i));
                        }
                        leftChildLabels.add(labels.get(i));
                    } else {
                        for (int j = 0; j < features.length; j++) {
                            rightChildFeatures[j].add(features[j].get(i));
                        }
                        rightChildLabels.add(labels.get(i));
                    }
                }


                if (leftChildLabels.size() > 0 && rightChildLabels.size() > 0) {
                    Node leftChild = new Node();
                    Node rightChild = new Node();
                    root.setLeftChild(leftChild);
                    root.setRightChild(rightChild);
                    root.setSelectedFeatureIndex(featureIndex);
                    root.setThreshold(threshold);
                    root.setLeaf(false);
                    leftChild.setDepth(root.getDepth() + 1);
                    rightChild.setDepth(root.getDepth() + 1);
                    createTree(leftChild, leftChildFeatures, leftChildLabels, minLeafSize, minInfoGain, noOfClass);
                    createTree(rightChild, rightChildFeatures, rightChildLabels, minLeafSize, minInfoGain, noOfClass);
                } else {
                    root.setLeaf(true);
                    root.setDecisionInstances(labels);
                    return;
                }
            }
        }
    }



    private ArrayList<Double>[] splitData(ArrayList<Double> feature, ArrayList<Double> labels, Double threshold) {
        ArrayList<Double>[] childrenOutcomes  = new ArrayList[2];
        for (int i = 0; i < childrenOutcomes.length; i++) {
            childrenOutcomes[i] = new ArrayList<Double>();
        }

        for (int i = 0; i < feature.size(); i++) {
            if(feature.get(i) <= threshold){
                childrenOutcomes[0].add(labels.get(i));
            }else {
                childrenOutcomes[1].add(labels.get(i));
            }
        }
        return childrenOutcomes;
    }

    private HashMap<String, String> calculateBestSplit(ArrayList<Double>[] features, ArrayList<Double> labels, int noOfClass) {

        HashMap<String, String> bestSplit = new HashMap<String, String>();
        int bestFeatureIndex = 0;
        double bestThreshold = 0;
        double parentEntropy = Stat.getEntropy(labels, noOfClass);
        double maxInfoGain = -99999;
        for (int i = 0; i < features.length; i++) {
            ArrayList<Double> candidateThresholds = Stat.getUnique(features[i]);
            for (int j = 0; j < candidateThresholds.size(); j++) {
                ArrayList<Double>[] childrenOutComes = splitData(features[i], labels, candidateThresholds.get(j));
                ArrayList<Double> leftChildLabels = childrenOutComes[0];
                ArrayList<Double> rightChildLabels= childrenOutComes[1];

                double leftEntropy = Stat.getEntropy(leftChildLabels, noOfClass);
                double rightEntropy = Stat.getEntropy(rightChildLabels, noOfClass);

                double weightedStd = (((leftChildLabels.size()*1.0)/labels.size()) * leftEntropy) +
                        (((rightChildLabels.size()*1.0)/labels.size()) * rightEntropy);
                double infoGain = parentEntropy-weightedStd;
                if(infoGain > maxInfoGain){
                    maxInfoGain = infoGain;
                    bestFeatureIndex = i;
                    bestThreshold = candidateThresholds.get(j);
                }
            }

        }
        bestSplit.put("BestFeatureIndex", bestFeatureIndex+"");
        bestSplit.put("BestThreshold", bestThreshold+"");
        bestSplit.put("MaxInfoGain", maxInfoGain+"");

        return bestSplit;
    }
}
