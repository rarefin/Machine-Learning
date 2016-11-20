package com.rifatarefin.Tree;

import com.rifatarefin.Statistics.Stat;

import java.util.*;

/**
 * Created by ROBIN on 8/24/2016.
 */
public class SplitCalculator {
    public static HashMap<String, String> calculateBestSplit(ArrayList<Double>[] features, ArrayList<Integer> labels, int noOfClass){
        int noOfPredictors = features.length;

        HashMap<String, String> bestSplit = new HashMap<String, String>();
        int bestFeatureIndex = 0;
        double bestThreshold = 0;
        double parentEntropy = getEntropy(labels, noOfClass);
        double maxInfoGain = -99999;
        for (int i = 0; i < noOfPredictors; i++) {
            ArrayList<Double> candidateThresholds = Stat.getUnique(features[i]);

            for (int j = 0; j < candidateThresholds.size(); j++) {
                ArrayList<Integer>[] childrenOutComes = getChildrenOutcomes(features[i], labels, candidateThresholds.get(j));
                ArrayList<Integer> leftChildLabels = childrenOutComes[0];
                ArrayList<Integer> rightChildLabels= childrenOutComes[1];

                double weightedStd = ((leftChildLabels.size()/labels.size())*getEntropy(leftChildLabels,noOfClass)) +
                        ((rightChildLabels.size()/labels.size())*getEntropy(rightChildLabels, noOfClass));
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
    /****All the class label must start with 0. For example if the problem is binary calssification then clases will
            be 0 and 1... for three class problem classes will be 0, 1 and 2..... *****/
    private static double getEntropy(ArrayList<Integer> classes, int noOfClass){
        HashSet<Integer> hashSet = new HashSet<Integer>();
        int totalSamples = classes.size();
        for (int i = 0; i < totalSamples; i++) {
            hashSet.add(classes.get(i));
        }
        HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
        for (int i = 0; i < classes.size(); i++) {
            if(hashMap.get(classes.get(i)) == null){
                hashMap.put(classes.get(i), 1);
            }else{
                hashMap.put(classes.get(i), (hashMap.get(classes.get(i))+1));
            }
        }
        Iterator<Integer> iterator = hashSet.iterator();
        double entropy = 0;
        while (iterator.hasNext()){
            int label = iterator.next();
            double p = (1.0)*hashMap.get(label)/totalSamples;
            entropy += p * logX(p, noOfClass);
        }
        return -entropy;
    }

    private static double logX(double value, int base){
        double newValue = Math.log10(value) / Math.log10(base);

        return  newValue;
    }

    private static ArrayList<Integer>[] getChildrenOutcomes(ArrayList<Double> feature, ArrayList<Integer> labels, Double threshold) {
        ArrayList<Integer>[] childrenOutcomes  = new ArrayList[2];
        for (int i = 0; i < childrenOutcomes.length; i++) {
            childrenOutcomes[i] = new ArrayList<Integer>();
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


    private static ArrayList<Double> getStocasticCandidateThresholds(ArrayList<Double> distictValues) {
        ArrayList<Double> candidateThresholds = new ArrayList<Double>();
        int splitNo = (int)(distictValues.size()*.10);
        double stepSize = (distictValues.get(distictValues.size()-1) - distictValues.get(0)) / splitNo;
        candidateThresholds.add(stepSize);
        for (int i = 1; i < 99; i++) {
            candidateThresholds.add(candidateThresholds.get(i-1)+stepSize);
        }

        return candidateThresholds;
    }

    public static HashMap<String, String> calculateBestSplit(ArrayList<Double>[] features, ArrayList<Integer> labels, ArrayList<Integer> selectedFeatures, int noOfClass) {
        int noOfPredictors = features.length;

        HashMap<String, String> bestSplit = new HashMap<String, String>();
        int bestFeatureIndex = 0;
        double bestThreshold = 0;
        double parentEntropy = getEntropy(labels, noOfClass);
        double maxInfoGain = -99999;
        for (int i = 0; i < selectedFeatures.size(); i++) {
            //ArrayList<Double> distictValues = Stat.getUnique(features[selectedFeatures.get(i)]);
            int featureIndex = selectedFeatures.get(i);
            ArrayList<Double> candidateThresholds = Stat.getUnique(features[featureIndex]);
            /*if(distictValues.size() > 1){
                candidateThresholds = getCandidateThresholds(distictValues);
                //candidateThresholds = getStocasticCandidateThresholds(distictValues);
            }else {
                candidateThresholds = distictValues;
            }*/
            for (int j = 0; j < candidateThresholds.size(); j++) {
                ArrayList<Integer>[] childrenOutComes = getChildrenOutcomes(features[selectedFeatures.get(i)], labels, candidateThresholds.get(j));
                ArrayList<Integer> leftChildLabels = childrenOutComes[0];
                ArrayList<Integer> rightChildLabels= childrenOutComes[1];

                double leftEntropy = getEntropy(leftChildLabels, noOfClass);
                double rightEntropy = getEntropy(rightChildLabels, noOfClass);

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
