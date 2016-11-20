package com.rifatarefin.FeatureSelection;

import java.util.*;

/**
 * Created by ROBIN on 11/4/2016.
 */
public class GreedySelection {
    public ArrayList<Integer> getSelectedFeatures(String selectionCrieteria, ArrayList<Double>[] features, ArrayList<Double> labels, int noOfFeaturesToSelect){
        ArrayList<Integer> selectedFeature = new ArrayList<Integer>();
        if(selectionCrieteria.equals("ReliefF")){
            double[] weights = new ReliefF().getWeights(features, labels, 5, 0);
            List<Map.Entry<Integer, Double>> list = getSortedBasedOnWeights(weights);
            for (Map.Entry<Integer, Double> entry : list)
            {
                selectedFeature.add(entry.getKey());
                if(noOfFeaturesToSelect == selectedFeature.size()){
                    break;
                }
            }
        }
        return selectedFeature;
    }

    public List<Map.Entry<Integer, Double>> getSortedBasedOnWeights(double[] array){
        HashMap<Integer, Double> hashMap = new HashMap<Integer, Double>();
        for (int i = 0; i < array.length; i++) {
            hashMap.put(i, array[i]);
        }
        List<Map.Entry<Integer, Double>> list = new LinkedList<Map.Entry<Integer, Double>>(hashMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
            public int compare(Map.Entry<Integer, Double> o1,
                               Map.Entry<Integer, Double> o2) {
                return Double.compare(o1.getValue(), o2.getValue());
            }
        });

        return list;
    }
}
