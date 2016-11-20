package com.rifatarefin.RegressionTree;

import com.rifatarefin.Statistics.Stat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by ROBIN on 8/24/2016.
 */
public class SplitCalculator {
    public static HashMap<String, String> calculateBestSplit(ArrayList<Double>[] features, ArrayList<Double> labels, ArrayList<Integer> selectedFeatures) {
        HashMap<String, String> bestSplit = new HashMap<String, String>();
        int bestFeatureIndex = 0;
        double bestThreshold = 0;
        double parentStd = Stat.getStd(labels);
        double maxInfoGain = -99999;
        for (int i = 0; i < selectedFeatures.size(); i++) {
            int featureIndex = selectedFeatures.get(i);
            ArrayList<Double> candidateThresholds = Stat.getUnique(features[featureIndex]);
            for (int j = 0; j < candidateThresholds.size(); j++) {
                ArrayList<Double>[] childrenOutComes = getChildrenOutcomes(features[selectedFeatures.get(i)], labels, candidateThresholds.get(j));
                ArrayList<Double> leftChildLabels = childrenOutComes[0];
                ArrayList<Double> rightChildLabels= childrenOutComes[1];

                double leftStd = Stat.getStd(leftChildLabels);
                double rightStd = Stat.getStd(rightChildLabels);

                double weightedStd = (((leftChildLabels.size()*1.0)/labels.size()) * leftStd) +
                        (((rightChildLabels.size()*1.0)/labels.size()) * rightStd);
                double infoGain = parentStd-weightedStd;
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
    private static ArrayList<Double>[] getChildrenOutcomes(ArrayList<Double> feature, ArrayList<Double> labels, Double threshold) {
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
