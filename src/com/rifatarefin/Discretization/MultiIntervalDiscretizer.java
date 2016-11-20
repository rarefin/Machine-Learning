package com.rifatarefin.Discretization;

import com.rifatarefin.Statistics.Stat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by ROBIN on 10/25/2016.
 */
public class MultiIntervalDiscretizer {
    public ArrayList<Double>[] getSelectedFeatures(ArrayList<Double>[] features, ArrayList<Double> labels){
        int noOfFeatures = features.length;
        int noOfInstances = features[0].size();
        ArrayList<Double>[] discretizedFeatures = new ArrayList[noOfFeatures];
        for (int i = 0; i < noOfFeatures; i++) {
            discretizedFeatures[i] = new ArrayList<Double>();
        }
        for (int i = 0; i < noOfFeatures; i++) {
            ArrayList<Double> cutPoints = new ArrayList<Double>();

            getCutpoints(features[i], labels, cutPoints, 0);
            Collections.sort(cutPoints);
            for (int j = 0; j < noOfInstances; j++) {
                double value = features[i].get(j);
                double discrete = 1.0 * getIndex(cutPoints, value);
                discretizedFeatures[i].add(discrete);
            }
        }
        return discretizedFeatures;
    }

    private double getIndex(ArrayList<Double> cutPoints, double value) {
        int index = 5555555;
        for (int i = 0; i < cutPoints.size(); i++) {

            if(value < cutPoints.get(0)){
                index = 0;
            }else if(value > cutPoints.get(cutPoints.size()-1)){
                index = cutPoints.size() + 1;
            }else {
                for (int j = 0; j < cutPoints.size()-1; j++) {
                    if(value >= cutPoints.get(j) && value <= cutPoints.get(j+1)){
                        index = j+1;
                    }
                }
            }
        }
        if(index==5555555){
            System.out.println(index);
        }
        return index;
    }

    private void getCutpoints(ArrayList<Double> feature, ArrayList<Double> labels, ArrayList<Double> cutPoints, double threshold) {
        if(labels.size()<2){
            return;
        }
        HashMap<String, Double> bestInfo = getBestCutPointAndInfoGain(feature, labels);
        double bestCutPoint = bestInfo.get("BestCutPoint");
        double bestInfoGain = bestInfo.get("BestInfoGain");

        if(bestInfoGain > threshold){
            cutPoints.add(bestCutPoint);
            ArrayList<Double> leftChildFeature = new ArrayList<Double>();
            ArrayList<Double> rightChildFeature = new ArrayList<Double>();

            ArrayList<Double> leftChildLabels = new ArrayList<Double>();
            ArrayList<Double> rightChildLabels = new ArrayList<Double>();

            for (int i = 0; i < feature.size(); i++) {
                if(feature.get(i) <= bestCutPoint){
                    leftChildFeature.add(feature.get(i));
                    leftChildLabels.add(labels.get(i));
                }else {
                    rightChildFeature.add(feature.get(i));
                    rightChildLabels.add(labels.get(i));
                }
            }
            if (leftChildLabels.size() > 2 && rightChildLabels.size() > 2){
                getCutpoints(leftChildFeature, leftChildLabels, cutPoints, threshold);
                getCutpoints(rightChildFeature, rightChildLabels, cutPoints, threshold);
            }
        }else {
            return;
        }

    }
    private HashMap<String, Double> getBestCutPointAndInfoGain(ArrayList<Double> feature, ArrayList<Double> labels){
        ArrayList<Double> candidateThresholds = Stat.getUnique(feature);
        double bestThreshold = 0;
        double parentEntropy = Stat.getEntropy(labels, 2);
        double maxInfoGain = -Double.MAX_VALUE;
        for (int j = 0; j < candidateThresholds.size(); j++) {
            ArrayList<Double>[] childrenOutComes = splitData(feature, labels, candidateThresholds.get(j));
            ArrayList<Double> leftChildLabels = childrenOutComes[0];
            ArrayList<Double> rightChildLabels= childrenOutComes[1];

            double leftEntropy = Stat.getEntropy(leftChildLabels, 2);
            double rightEntropy = Stat.getEntropy(rightChildLabels, 2);

            double weightedStd = (((leftChildLabels.size()*1.0)/labels.size()) * leftEntropy) +
                    (((rightChildLabels.size()*1.0)/labels.size()) * rightEntropy);
            double infoGain = parentEntropy-weightedStd;
            if(infoGain > maxInfoGain){
                maxInfoGain = infoGain;
                bestThreshold = candidateThresholds.get(j);
            }
        }
        HashMap<String, Double> bestSplit = new HashMap<String, Double>();
        bestSplit.put("BestCutPoint", bestThreshold);
        bestSplit.put("BestInfoGain", maxInfoGain);

        return  bestSplit;
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
