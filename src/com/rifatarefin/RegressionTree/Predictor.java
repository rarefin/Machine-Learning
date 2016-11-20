package com.rifatarefin.RegressionTree;

import com.rifatarefin.Statistics.Stat;

import java.util.*;

/**
 * Created by ROBIN on 8/27/2016.
 */
public class Predictor {

    private static double travercseTree(Node tree, double[] x) {
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

        return Stat.getMean(decisionLabels);
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


    public static ArrayList<Double> predictUsingRegressionForest(Node[] forest, ArrayList<Double>[] features){
        ArrayList<Double> predictedLabels = new ArrayList<Double>();

        for (int i = 0; i < features[0].size(); i++) {
            double[] X = new double[features.length];
            for (int j = 0; j < features.length; j++) {
                X[j] = features[j].get(i);
            }
            double forestPrediction = 0;
            for (int j = 0; j < forest.length; j++) {
                double treePrediction = travercseTree(forest[j], X);
                forestPrediction += treePrediction;
            }
            forestPrediction = forestPrediction/forest.length;
            predictedLabels.add(forestPrediction);
        }

        return predictedLabels;
    }

}
