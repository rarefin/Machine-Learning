package com.rifatarefin.Statistics;

import java.util.*;

/**
 * Created by ROBIN on 8/26/2016.
 */
public class Stat {
    public static double getMean(ArrayList<Double> feature){
        int noOfInstances = feature.size();
        double sum = 0;
        for (int i = 0; i < noOfInstances; i++) {
            sum += feature.get(i);
        }

        return sum/noOfInstances;
    }
    public static double getSum(ArrayList<Double> feature){
        int noOfInstances = feature.size();
        double sum = 0;
        for (int i = 0; i < noOfInstances; i++) {
            sum += feature.get(i);
        }

        return sum;
    }
    public static double getStd(ArrayList<Double> feature){
        double mean = getMean(feature);
        int noOfInstances = feature.size();
        double sum = 0;
        for (int i = 0; i < noOfInstances; i++) {
            sum += (feature.get(i)-mean)*(feature.get(i)-mean);
        }
        sum = sum/noOfInstances;

        return Math.sqrt(sum);
    }

    public static ArrayList<Double> getUnique(ArrayList<Double> feature){
        HashSet<Double> hashSet = new HashSet<Double>();

        int noOfInstances = feature.size();
        for (int i = 0; i < noOfInstances; i++) {
            hashSet.add(feature.get(i));
        }

        ArrayList<Double> vector = new ArrayList<Double>(hashSet);
        Collections.sort(vector);
        return vector;
    }


    public static double getEntropy(ArrayList<Double> classes, int noOfClass){
        HashMap<Double, Double> hashMap = new HashMap<Double, Double>();
        int totalSamples = classes.size();
        for (int i = 0; i < totalSamples; i++) {
            if (hashMap.get(classes.get(i)) == null) {
                hashMap.put(classes.get(i), 1.0);
            } else {
                hashMap.put(classes.get(i), (hashMap.get(classes.get(i)) + 1.0));

            }
        }

        double entropy = 0;
        for (Map.Entry<Double, Double> entry : hashMap.entrySet())
        {
            double p = (entry.getValue())/totalSamples;
            entropy += p * logX(p, noOfClass);
        }

        return -entropy;
    }

    private static double logX(double value, int base){
        double newValue = Math.log10(value) / Math.log10(base);

        return  newValue;
    }


}
