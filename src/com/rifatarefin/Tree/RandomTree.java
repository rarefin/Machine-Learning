package com.rifatarefin.Tree;

import com.rifatarefin.CrossValidation.ForestSettings;
import com.rifatarefin.Statistics.Stat;

import java.util.*;

/**
 * Created by ROBIN on 8/26/2016.
 */
public class RandomTree implements TreeInterface{


    private int leafIndex;
    private int seed;
    public RandomTree(int seed){
        this.seed = seed;
        this.leafIndex = 0;
    }


    public int getLeafIndex() {
        return leafIndex;
    }


    public void createTree(Node root, ArrayList<Double>[] features, ArrayList<Double> labels, ForestSettings forestSettings, int noOfClasses){
        // Checking purity of a node
        HashSet<Double> hashSet = new HashSet<Double>();
        for (int i = 0; i < labels.size(); i++) {
            hashSet.add(labels.get(i));
        }
        if(hashSet.size() == 1){
            this.leafIndex++;
            root.setLeaf(true);
            //root.setDecisionInstances(labels);
            root.setLeafIndex(this.leafIndex);
            root.setClassProbabilities(getProbability(labels));
            return;
        }else if(root.getDepth() == forestSettings.getMaxTreeDepth()){
            this.leafIndex++;
            root.setLeaf(true);
            //root.setDecisionInstances(labels);
            root.setLeafIndex(this.leafIndex);
            root.setClassProbabilities(getProbability(labels));
            return;
        }else if(labels.size() <= forestSettings.getMinLeafSize()){
            this.leafIndex++;
            root.setLeaf(true);
            //root.setDecisionInstances(labels);
            root.setLeafIndex(this.leafIndex);
            root.setClassProbabilities(getProbability(labels));
            return;
        }else {
            ArrayList<Integer> selectedFeatures = getSelectedFeatures(features, forestSettings.getNoOfRandomFeatures());

            HashMap<String, String> bestSplit =  calculateBestSplit(features, labels, selectedFeatures, noOfClasses);
            int featureIndex = Integer.parseInt(bestSplit.get("BestFeatureIndex"));
            double threshold = Double.parseDouble(bestSplit.get("BestThreshold"));
            double infoGain = Double.parseDouble(bestSplit.get("MaxInfoGain"));

            if(infoGain < forestSettings.getMinInfoGain()){
                this.leafIndex++;
                root.setLeaf(true);
                //root.setDecisionInstances(labels);
                root.setLeafIndex(this.leafIndex);
                root.setClassProbabilities(getProbability(labels));
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


                if(leftChildLabels.size() > 0 && rightChildLabels.size() > 0){
                    Node leftChild = new Node();
                    Node rightChild = new Node();
                    root.setLeftChild(leftChild);
                    root.setRightChild(rightChild);
                    root.setSelectedFeatureIndex(featureIndex);
                    root.setThreshold(threshold);
                    root.setLeaf(false);
                    leftChild.setDepth(root.getDepth()+1);
                    rightChild.setDepth(root.getDepth()+1);
                    createTree(leftChild, leftChildFeatures, leftChildLabels, forestSettings, noOfClasses);
                    createTree(rightChild, rightChildFeatures, rightChildLabels, forestSettings, noOfClasses);
                }else{
                    this.leafIndex++;
                    root.setLeaf(true);
                    //root.setDecisionInstances(labels);
                    root.setLeafIndex(this.leafIndex);
                    root.setClassProbabilities(getProbability(labels));
                    return;
                }
            }
        }
    }

    private ArrayList<Integer> getSelectedFeatures(ArrayList<Double>[] features, int noOfRandomFeatures){
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i=0; i<features.length; i++) {
            list.add(i);
        }
        ArrayList<Integer> selectedFeatures = new ArrayList<Integer>();
        Random random = new Random(this.seed+this.leafIndex);
        for (int i=0; i<noOfRandomFeatures; i++) {
            int index = random.nextInt(list.size());
            selectedFeatures.add(list.get(index));
            list.remove(index);
        }

        return selectedFeatures;
    }

    private HashMap<Double, Double> getProbability(ArrayList<Double> labels) {
        HashMap<Double, Double> hashMap = new HashMap<Double, Double>();

        for (int i = 0; i < labels.size(); i++) {
            if (hashMap.get(labels.get(i)) == null) {
                hashMap.put(labels.get(i), 1.0);
            } else {
                hashMap.put(labels.get(i), (hashMap.get(labels.get(i)) + 1.0));

            }
        }

        for (Map.Entry<Double, Double> entry : hashMap.entrySet())
        {
            double p = entry.getValue()/labels.size();
            hashMap.put(entry.getKey(), p);
        }

        return hashMap;
    }

    private ArrayList<Double>[] splitData(ArrayList<Double> feature, ArrayList<Double> labels, double threshold) {
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

    private HashMap<String, String> calculateBestSplit(ArrayList<Double>[] features, ArrayList<Double> labels, ArrayList<Integer> selectedFeatures, int noOfClass) {
        HashMap<String, String> bestSplit = new HashMap<String, String>();
        int bestFeatureIndex = 0;
        double bestThreshold = 0;
        double parentEntropy = Stat.getEntropy(labels, noOfClass);
        double maxInfoGain = -99999;
        for (int i = 0; i < selectedFeatures.size(); i++) {
            int featureIndex = selectedFeatures.get(i);
            ArrayList<Double> candidateThresholds = Stat.getUnique(features[featureIndex]);
            for (int j = 0; j < candidateThresholds.size(); j++) {
                ArrayList<Double>[] childrenOutComes = splitData(features[featureIndex], labels, candidateThresholds.get(j));
                ArrayList<Double> leftChildLabels = childrenOutComes[0];
                ArrayList<Double> rightChildLabels= childrenOutComes[1];

                double leftEntropy = Stat.getEntropy(leftChildLabels, noOfClass);
                double rightEntropy = Stat.getEntropy(rightChildLabels, noOfClass);

                double weightedStd = (((leftChildLabels.size()*1.0)/labels.size()) * leftEntropy) +
                        (((rightChildLabels.size()*1.0)/labels.size()) * rightEntropy);
                double infoGain = parentEntropy-weightedStd;
                if(infoGain > maxInfoGain){
                    maxInfoGain = infoGain;
                    bestFeatureIndex = featureIndex;
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
