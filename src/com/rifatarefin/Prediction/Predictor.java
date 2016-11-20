package com.rifatarefin.Prediction;

import com.rifatarefin.Tree.Node;


import java.util.*;

/**
 * Created by ROBIN on 8/27/2016.
 */
public class Predictor {
    public static ArrayList<Double> predictUsingTree(ArrayList<Double>[] features, Node tree){
        ArrayList<Double> predictedLabels = new ArrayList<Double>();

        for (int i = 0; i < features[0].size(); i++) {
            double[] X = new double[features.length];
            for (int j = 0; j < features.length; j++) {
                X[j] = features[j].get(i);
            }
            HashMap<Double, Double> decisionLabels = traverseTreeForProbability(tree, X);
            predictedLabels.add(predictWithProbability(decisionLabels));
        }

        return predictedLabels;
    }

    private static double predict(ArrayList<Double> decisionLabels) {
        HashMap<Double, Integer> hashMap = new HashMap<Double, Integer>();
        for (int i = 0; i < decisionLabels.size(); i++) {
            if(hashMap.get(decisionLabels.get(i)) == null){
                hashMap.put(decisionLabels.get(i), 1);
            }else{
                hashMap.put(decisionLabels.get(i), (hashMap.get(decisionLabels.get(i))+1));
            }
        }
        double predictedLabel = 0;
        int freq = 0;

        for (Map.Entry<Double, Integer> entry : hashMap.entrySet())
        {
            if(entry.getValue() > freq){
                freq = entry.getValue();
                predictedLabel = entry.getKey();
            }
        }

        return predictedLabel;
    }

    private static ArrayList<Double> traverseTree(Node tree, double[] x) {
        ArrayList<Double> decisionLabels;

        while (true){
            if(tree.isLeaf()){
                decisionLabels = tree.getDecisionInstances();
                break;
            }else{
                if(x[tree.getSelectedFeatureIndex()] <= tree.getThreshold()){
                    tree = tree.getLeftChild();
                }else {
                    tree = tree.getRightChild();
                }
            }
        }

        return decisionLabels;
    }

    private static int getLeafIndex(Node tree, double[] x) {
        int leafIndex;

        while (true){
            if(tree.isLeaf()){
                leafIndex = tree.getLeafIndex();
                break;
            }else{
                if(x[tree.getSelectedFeatureIndex()] <= tree.getThreshold()){
                    tree = tree.getLeftChild();
                }else {
                    tree = tree.getRightChild();
                }
            }
        }

        return leafIndex;
    }

    // Random forest prediction
    public static ArrayList<Double> predictUsingForest(ArrayList<Node> forest, ArrayList<Double>[] features){
        ArrayList<Double> predictedLabels = new ArrayList<Double>();

        for (int i = 0; i < features[0].size(); i++) {
            double[] X = new double[features.length];
            for (int j = 0; j < features.length; j++) {
                X[j] = features[j].get(i);
            }
            HashMap<Double, Double> hashMap = new HashMap<Double, Double>();
            for (int j = 0; j < forest.size(); j++) {
                ArrayList<Double> decisionLabels = traverseTree(forest.get(j), X);
                double label = predict(decisionLabels);
                if(hashMap.get(label) == null){
                    hashMap.put(label, 1.0);
                }else {
                    hashMap.put(label, (hashMap.get(label)+1.0));
                }
            }

            predictedLabels.add(getMajorityVote(hashMap));
        }

        return predictedLabels;
    }

    private static double getMajorityVote(HashMap<Double, Double> hashMap) {
        double label = 0;
        double vote = 0;
        for (Map.Entry<Double, Double> entry : hashMap.entrySet())
        {
            if(entry.getValue() > vote){
                vote = entry.getValue();
                label = entry.getKey();
            }
        }
        return label;
    }

    // Random forest prediction using probability
    private static HashMap<Double, Double> traverseTreeForProbability(Node tree, double[] x) {
        HashMap<Double, Double> hashMap;
        double pathEntropy = 0;
        int count = 0;
        while (true){
            pathEntropy += tree.getEntropy();
            count++;
            if(tree.isLeaf()){
                hashMap = tree.getClassProbabilities();
                break;
            }else{
                if(x[tree.getSelectedFeatureIndex()] <= tree.getThreshold()){
                    tree = tree.getLeftChild();
                }else {
                    tree = tree.getRightChild();
                }
            }
        }
        pathEntropy /= count;
      //pathEntropy /= 1/pathEntropy;
        /*for (Map.Entry<Double, Double> entry : hashMap.entrySet())
        {
            double p = hashMap.get(entry.getKey());
            hashMap.put(entry.getKey(), p*(pathEntropy));
        }*/
        return hashMap;
    }
    public static ArrayList<Double> predictUsingSoftVoting(ArrayList<Node> forest, ArrayList<Double>[] features){
        ArrayList<Double> predictedLabels = new ArrayList<Double>();

        for (int i = 0; i < features[0].size(); i++) {
            double[] X = new double[features.length];
            for (int j = 0; j < features.length; j++) {
                X[j] = features[j].get(i);
            }
            HashMap<Double, Double> hashMap = new HashMap<Double, Double>();
            for (int j = 0; j < forest.size(); j++) {
                HashMap<Double, Double> decisionProbabilities =  traverseTreeForProbability(forest.get(j), X);
                for (Map.Entry<Double, Double> entry :decisionProbabilities.entrySet())
                {
                    if(hashMap.get(entry.getKey()) == null){
                        hashMap.put(entry.getKey(), entry.getValue());
                    }else {
                        hashMap.put(entry.getKey(), (hashMap.get(entry.getKey())+entry.getValue()));
                    }
                }

            }

            predictedLabels.add(getMajorityVote(hashMap));
        }

        return predictedLabels;
    }
    public static ArrayList<Double> predictUsingMajorityVoting(ArrayList<Node> forest, ArrayList<Double>[] features){
        ArrayList<Double> predictedLabels = new ArrayList<Double>();

        for (int i = 0; i < features[0].size(); i++) {
            double[] X = new double[features.length];
            for (int j = 0; j < features.length; j++) {
                X[j] = features[j].get(i);
            }
            HashMap<Double, Double> hashMap = new HashMap<Double, Double>();
            for (int j = 0; j < forest.size(); j++) {
                HashMap<Double, Double> decisionProbabilities =  traverseTreeForProbability(forest.get(j), X);
                double label = getMajorityVote(decisionProbabilities);
                if(hashMap.get(label) == null){
                    hashMap.put(label, 1.0);
                }else {
                    hashMap.put(label, (hashMap.get(label)+1.0));
                }
            }

            predictedLabels.add(getMajorityVote(hashMap));
        }

        return predictedLabels;
    }

    public static ArrayList<Double> predictUsingWeightedVoting(ArrayList<Node> forest, ArrayList<Double>[] features){
        ArrayList<Double> predictedLabels = new ArrayList<Double>();

        for (int i = 0; i < features[0].size(); i++) {
            double[] X = new double[features.length];
            for (int j = 0; j < features.length; j++) {
                X[j] = features[j].get(i);
            }
            HashMap<Double, Double> hashMap = new HashMap<Double, Double>();
            for (int j = 0; j < forest.size(); j++) {
                Node tree = forest.get(j);
                HashMap<Double, Double> decisionProbabilities =  traverseTreeForProbability(tree, X);
                for (Map.Entry<Double, Double> entry :decisionProbabilities.entrySet())
                {
                    if(hashMap.get(entry.getKey()) == null){
                        hashMap.put(entry.getKey(), (entry.getValue()*tree.getOobAccuracy()));
                    }else {
                        hashMap.put(entry.getKey(), hashMap.get(entry.getKey())+(entry.getValue()));
                    }
                }

            }

            predictedLabels.add(getMajorityVote(hashMap));
        }

        return predictedLabels;
    }
    public static ArrayList<Double> predictUsingForestProbability(ArrayList<Node> forest, ArrayList<Double>[] features){
        ArrayList<Double> predictedLabels = new ArrayList<Double>();

        for (int i = 0; i < features[0].size(); i++) {
            double[] X = new double[features.length];
            for (int j = 0; j < features.length; j++) {
                X[j] = features[j].get(i);
            }
            HashMap<Double, Double> hashMap = new HashMap<Double, Double>();
            for (int j = 0; j < forest.size(); j++) {
                HashMap<Double, Double> decisionProbabilities =  traverseTreeForProbability(forest.get(j), X);
                double label = predictWithProbability(decisionProbabilities);
                if(hashMap.get(label) == null){
                    hashMap.put(label, 1.0);
                }else {
                    hashMap.put(label, (hashMap.get(label)+1.0));
                }
            }

            predictedLabels.add(getMajorityVote(hashMap));
        }

        return predictedLabels;
    }

    private static double predictWithProbability(HashMap<Double, Double> hashMap) {
        double label = 0;
        double p = 0;
        for (Map.Entry<Double, Double> entry : hashMap.entrySet())
        {
            if(entry.getValue() > p){
                p = entry.getValue();
                label = entry.getKey();
            }
        }
        return label;
    }
}
