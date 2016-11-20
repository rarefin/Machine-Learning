package com.rifatarefin.Tree;

import com.rifatarefin.CrossValidation.ForestSettings;

import java.util.*;

/**
 * Created by ROBIN on 10/15/2016.
 */
public class PerfectRandomTree implements TreeInterface {

    private int leafIndex;
    private int seed;
    private Random random;
    private double alfa;
    private int minLeaf;
    public PerfectRandomTree(int seed){
        this.seed = seed;
        this.leafIndex = 0;
        random = new Random(seed);
        alfa = random.nextDouble();
        minLeaf = 1;
    }


    public int getLeafIndex() {
        return leafIndex;
    }


    public void createTree(Node root, ArrayList<Double>[] features, ArrayList<Double> labels, ForestSettings forestSettings, int noOfClasses){
       forestSettings.setMinLeafSize(minLeaf);
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
            int noOfTries = 10;
            //Random random = new Random(seed);
            int r1 = -1;
            int r2 = -1;
            for (int i = 0; i < noOfTries; i++) {
                int a = random.nextInt(labels.size());
                int b = random.nextInt(labels.size());
                if(labels.get(a) != labels.get(b)){
                    r1 = a;
                    r2 = b;
                    break;
                }
            }
            /* two data points from the node until these two belong to different classes. If this is not possible, all the data points in
                the node must be from the same class and the node is terminal */
            if(r1 == -1 || r2 == -1){
                this.leafIndex++;
                root.setLeaf(true);
                //root.setDecisionInstances(labels);
                root.setLeafIndex(this.leafIndex);
                root.setClassProbabilities(getProbability(labels));
                return;
            }else {
                int featureIndex = random.nextInt(features.length);
                //double alfa = random.nextDouble();
                double p = features[featureIndex].get(r1);
                double q = features[featureIndex].get(r2);
                double threshold = alfa*p + (1-alfa)*q;

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

                if(leftChildLabels.size() > 0 && rightChildLabels.size() > 0){
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
}
