package com.rifatarefin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ROBIN on 10/13/2016.
 */
public class Utility {
    public static double[] toDoubleArray(List<Double> list) {
        double[] array = new double[list.size()];
        int i = 0;
        for (Double n : list) {
            array[i++] = n.doubleValue();
        }
        return array;
    }

    public static ArrayList<double[]> convertFeaturesTo2DArray(List<Double>[] features){
        int noOfFeatures = features.length;
        ArrayList<double[]> listOfFeatures = new ArrayList<double[]>();

        for (int i = 0; i < noOfFeatures; i++) {
            double[] array = toDoubleArray(features[i]);
            listOfFeatures.add(array);
        }
        return listOfFeatures;
    }

    public static ArrayList<Double>[] removeFeature(ArrayList<Double>[] features, int index){
        int noOfFeatures = features.length;
        ArrayList<Double>[] newFeatures = new ArrayList[noOfFeatures-1];
        int k = 0;
        for (int i = 0; i < noOfFeatures; i++) {
            if(i == index){
                continue;
            }

            if(k < noOfFeatures-1){
                newFeatures[k] = features[i];
            }
            k++;
        }
        return newFeatures;
    }

    public static ArrayList<Double>[] getSelectedFeatures(ArrayList<Double>[] trainFeatures, ArrayList<Integer> selectedFeatureIndices) {
        int noOfFeaturesToSelect = selectedFeatureIndices.size();
        ArrayList<Double>[] selectedFeatures = new ArrayList[noOfFeaturesToSelect];
        for (int i = 0; i < noOfFeaturesToSelect; i++) {
            selectedFeatures[i] = trainFeatures[selectedFeatureIndices.get(i)];
        }
        return selectedFeatures;
    }

    public static ArrayList<String> getAttributeNames(ArrayList<String> attributeNames, ArrayList<Integer> selectedFeatureIndices) {
        int noOfFeaturesToSelect = selectedFeatureIndices.size();
        ArrayList<String> selectedFeaturesNames = new ArrayList<String>();
        for (int i = 0; i < noOfFeaturesToSelect; i++) {
            selectedFeaturesNames.add(attributeNames.get(selectedFeatureIndices.get(i)));
        }
        return selectedFeaturesNames;
    }
}
