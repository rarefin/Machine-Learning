package com.rifatarefin.CrossValidation;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by ROBIN on 11/12/2016.
 */
public class HoldoutValidator {
    public ArrayList<Double>[] validate(ArrayList<Double>[] features, ArrayList<Double> labels, int noOfTrainInstances, ForestSettings forestSettings){
        int noOfFeatures = features.length;
        int noOfInstances = features[0].size();
        ArrayList<Integer> abc = new ArrayList<Integer>();
        for (int i = 0; i < noOfInstances; i++) {
            abc.add(i);
        }
        ArrayList<Double>[] trainFeatures = new ArrayList[noOfFeatures];
        ArrayList<Double> trainLabels = new ArrayList<Double>();
        ArrayList<Double>[] testFeatures = new ArrayList[noOfFeatures];
        ArrayList<Double> testLabels = new ArrayList<Double>();

        for (int i = 0; i < noOfFeatures; i++) {
            trainFeatures[i] = new ArrayList<Double>();
            testFeatures[i] = new ArrayList<Double>();
        }
        Random random = new Random(0);
        for (int i = 0; i < noOfTrainInstances; i++) {
            int randomIndex = random.nextInt(abc.size());
            int index = abc.get(randomIndex);
            abc.remove(randomIndex);
            for (int j = 0; j < noOfFeatures; j++) {
                trainFeatures[j].add(features[j].get(index));
            }
            trainLabels.add(labels.get(index));
        }
        for (int i = 0; i < abc.size(); i++) {
            int randomIndex = random.nextInt(abc.size());
            int index = abc.get(randomIndex);
            abc.remove(randomIndex);
            for (int j = 0; j < noOfFeatures; j++) {
                testFeatures[j].add(features[j].get(index));
            }
            testLabels.add(labels.get(index));
        }
        ArrayList<Double> predicted = new Evaluator().evaluate(trainFeatures, trainLabels, testFeatures, forestSettings);

        ArrayList<Double>[] results = new ArrayList[2];
        results[0] = testLabels;
        results[1] = predicted;

        return results;
    }
}
