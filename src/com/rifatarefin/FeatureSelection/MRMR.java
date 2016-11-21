package com.rifatarefin.FeatureSelection;

import JavaMI.MutualInformation;
import com.rifatarefin.Statistics.Stat;
import com.rifatarefin.Utility;

import java.util.*;

/**
 * Created by ROBIN on 10/25/2016.
 */
public class MRMR {
    public ArrayList<Integer> getSelectedFeatures(ArrayList<Double>[] features, ArrayList<Double> labels, int noOfFeaturesToSelect){
        // Convert the data format required to use mutual information library
        ArrayList<double[]> featuresInArray = Utility.convertFeaturesTo2DArray(features);
        double[] labelsArray = Utility.toDoubleArray(labels);


        int noOfFeatures = features.length;
        ArrayList<Double> miWithClass = new ArrayList<Double>();
        for (int i = 0; i < noOfFeatures; i++) {
            double mi = MutualInformation.calculateMutualInformation(featuresInArray.get(i), labelsArray);
            miWithClass.add(mi);
        }
        HashSet<Integer> selectedFeatures = new HashSet<Integer>();
        HashSet<Integer> remainingFeatures = new HashSet<Integer>();

        // Find the first selected feature which has maximum mutual information with the class
        int firstSelectedFeature = miWithClass.indexOf(Collections.max(miWithClass));
        selectedFeatures.add(firstSelectedFeature);
        for (int i = 0; i < noOfFeatures; i++) {
            if (i == firstSelectedFeature){
                continue;
            }
            remainingFeatures.add(i);
        }
        // Select rest of the features based on mrmr method
        while (selectedFeatures.size() < noOfFeaturesToSelect){
            Iterator<Integer> remainingIterator = remainingFeatures.iterator();

            int newSelectedFeature = -1;
            double maxGain = -Double.MAX_VALUE;
            while (remainingIterator.hasNext()){
                int remainingFeature = remainingIterator.next();
                ArrayList<Double> miWithSelectedFeatures = new ArrayList<Double>();
                Iterator<Integer> selectedIterator = selectedFeatures.iterator();
                while (selectedIterator.hasNext()){
                    int selectedFeature = selectedIterator.next();
                    double mi = MutualInformation.calculateMutualInformation(featuresInArray.get(remainingFeature), featuresInArray.get(selectedFeature));
                    miWithSelectedFeatures.add(mi);
                }
                double relevance = miWithClass.get(remainingFeature);
                double redundancy = Stat.getMean(miWithSelectedFeatures);
                double gain = relevance - redundancy;

                if(gain > maxGain){
                    maxGain = gain;
                    newSelectedFeature = remainingFeature;
                }
            }
            selectedFeatures.add(newSelectedFeature);
            remainingFeatures.remove(newSelectedFeature);
        }
        return new ArrayList<Integer>(selectedFeatures);
    }
}
