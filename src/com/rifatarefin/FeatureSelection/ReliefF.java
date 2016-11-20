package com.rifatarefin.FeatureSelection;

import java.util.*;

/**
 * Created by ROBIN on 10/18/2016.
 */
public class ReliefF {
    public double[] getWeights(ArrayList<Double>[] features, ArrayList<Double> labels, int k, int seed){
        int noOfFeatures = features.length;
        int noOfInstances = features[0].size();
        int m = 100;
        double[] weights = new double[noOfFeatures];

        HashMap<Double, Integer> histogram = new HashMap<Double, Integer>();
        HashSet<Double> classes = new HashSet<Double>();
        for (int i = 0; i < labels.size(); i++) {
            classes.add(labels.get(i));
            if (histogram.get(labels.get(i)) == null) {
                histogram.put(labels.get(i), 1);
            } else {
                histogram.put(labels.get(i), (histogram.get(labels.get(i)) + 1));
            }
        }

        Random random = new Random(seed);
        for (int i = 0; i < m; i++) {
            int randomIndex = random.nextInt(noOfInstances);
            HashMap<Double, ArrayList<Integer>> neighbours = getNeighbours(features, labels, classes, randomIndex, k);
            double[] hitScores = getHitScores(features, labels, randomIndex, neighbours, m, k);
            double[] missScores = getMissScores(features, labels, randomIndex, neighbours, histogram, m, k);
            for (int j = 0; j < noOfFeatures; j++) {
                weights[j] += (missScores[j]-hitScores[j]);
            }

        }
        return weights;
    }

    public ArrayList<Integer> getSelectedFeatures(ArrayList<Double>[] features, ArrayList<Double> labels, int k, int seed){
        ArrayList<Integer> selectedIndices = new ArrayList<Integer>();
        double threshold = 10;
        double[] weights = getWeights(features, labels, k, seed);
        for (int i = 0; i < weights.length; i++) {
            if (weights[i] > threshold){
                selectedIndices.add(i);
            }
        }
        return selectedIndices;
    }
    private double[] getHitScores(ArrayList<Double>[] features, ArrayList<Double> labels, int randomIndex, HashMap<Double, ArrayList<Integer>> neighbours, int m, int k) {
        int noOfFeatures = features.length;
        double label = labels.get(randomIndex);
        ArrayList<Integer> hits = neighbours.get(label);
        double[] hitScores = new double[noOfFeatures];
        double[] randomInstance = getInstance(features, randomIndex);
        for (int j = 0; j < k; j++) {
            double[] X = getInstance(features, hits.get(j));
            for (int l = 0; l < noOfFeatures; l++) {
                hitScores[l] += Math.abs(X[l]-randomInstance[l]) / (m*k);
            }
        }
        return hitScores;
    }

    private double[] getMissScores(ArrayList<Double>[] features, ArrayList<Double> labels, int randomIndex, HashMap<Double, ArrayList<Integer>> neighbours,HashMap<Double, Integer> histogram, int m, int k) {
        int noOfFeatures = features.length;
        double label = labels.get(randomIndex);
        double[] randomInstance = getInstance(features, randomIndex);
        double[] missScores = new double[noOfFeatures];
        for (Map.Entry<Double, ArrayList<Integer>> entry : neighbours.entrySet())
        {
           if(entry.getKey() == label){
               continue;
           }
            ArrayList<Integer> misses = entry.getValue();
            double[] tempScores = new double[noOfFeatures];
            for (int j = 0; j < k; j++) {
                double[] X = getInstance(features, misses.get(j));
                for (int l = 0; l < noOfFeatures; l++) {
                    tempScores[l] += Math.abs(X[l]-randomInstance[l]) / (m*k);
                }
            }
            for (int l = 0; l < noOfFeatures; l++) {
                missScores[l] += tempScores[l] * ((histogram.get(entry.getKey())*1.0)/(labels.size()-histogram.get(label)));
            }
        }

        return missScores;
    }

    private HashMap<Double, ArrayList<Integer>> getNeighbours(ArrayList<Double>[] features, ArrayList<Double> labels, HashSet<Double> classes, int randomIndex, int k) {
        HashMap<Double, ArrayList<Integer>> neighbours = new HashMap<Double, ArrayList<Integer>>();
        int noOfInstances = features[0].size();
        int noOfFeatures = features.length;

        double[] randomInstance = getInstance(features, randomIndex);
        HashMap<Integer, Double> distanceMap = new HashMap<Integer, Double>();
        long start = System.currentTimeMillis();

        double[] ranges = new double[noOfFeatures];
        for (int i = 0; i < noOfFeatures; i++) {
            double max = Collections.max(features[i]);
            double min = Collections.min(features[i]);
            ranges[i] = max- min;
        }
        for (int i = 0; i < noOfInstances; i++) {
            if(i==randomIndex){
                continue;
            }
            double distance = 0;
            for (int j = 0; j < noOfFeatures; j++) {

                if(ranges[j] == 0){
                    distance += Math.abs(features[j].get(i)-randomInstance[j]);
                }else {
                    distance += Math.abs(features[j].get(i)-randomInstance[j]) / ranges[j];
                }
            }
            distanceMap.put(i, distance);
        }
        long end = System.currentTimeMillis();
        long diff = (end-start)/1000;
        List<Map.Entry<Integer, Double>> sortedMap = sortByValue(distanceMap);
        Iterator<Double> iterator = classes.iterator();
        while (iterator.hasNext()){
            neighbours.put(iterator.next(), new ArrayList<Integer>());
        }
        for (Map.Entry<Integer, Double> entry : sortedMap)
        {
            int key = entry.getKey();
            ArrayList<Integer> tempList = neighbours.get(labels.get(key));
            if(tempList.size() < k){
                tempList.add(key);
            }
            if(isFoundAllNeighbours(neighbours, k)){
                break;
            }
        }
        return neighbours;
    }

    private boolean isFoundAllNeighbours(HashMap<Double, ArrayList<Integer>> neighbours, int k) {
        for (Map.Entry<Double, ArrayList<Integer>> entry : neighbours.entrySet())
        {
            if(entry.getValue().size() < k){
                return false;
            }

        }
        return true;
    }

    public double[] getInstance(ArrayList<Double>[] features, int index){
        int noOfFeatures = features.length;
        double[] X = new double[noOfFeatures];
        for (int j = 0; j < noOfFeatures; j++) {
            X[j] = features[j].get(index);
        }
        return X;
    }

    private List<Map.Entry<Integer, Double>> sortByValue(HashMap<Integer, Double> unsortMap) {

        List<Map.Entry<Integer, Double>> list =
                new LinkedList<Map.Entry<Integer, Double>>(unsortMap.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position forInteger, Double a different order
        Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
            public int compare(Map.Entry<Integer, Double> o1,
                               Map.Entry<Integer, Double> o2) {
                return Double.compare(o1.getValue(), o2.getValue());
            }
        });


        return list;
    }

}
