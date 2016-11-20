package com.rifatarefin.CrossValidation;

import com.rifatarefin.EvaluationMeasure;
import com.rifatarefin.Forest.GlobalRefinedForest;
import com.rifatarefin.Utility;
import de.bwaldvogel.liblinear.*;

import java.util.ArrayList;

/**
 * Created by ROBIN on 10/23/2016.
 */
public class CrossValidator {
    private int noOfFolds;

    public CrossValidator(int noOfFolds) {
        this.noOfFolds = noOfFolds;
    }

    public ArrayList<Double> validate(ArrayList<Double>[] features, ArrayList<Double> labels, ForestSettings forestSettings){
        int noOfInstances = labels.size();
        int noOfFeatures = features.length;
        int noOfInstancesPerFold = noOfInstances/noOfFolds;
        int startOfFold = 0;

        ArrayList<Double> accuracyOfAllFolds = new ArrayList<Double>();
        ArrayList<Double> allPredicted = new ArrayList<Double>();
        for (int i = 1; i <= noOfFolds; i++) {
            int testDataStartIndex = startOfFold;
            int testDataEndIndex = startOfFold + noOfInstancesPerFold - 1;

            ArrayList<Double>[] trainFeatures = new ArrayList[noOfFeatures];
            ArrayList<Double> trainLabels = new ArrayList<Double>();
            ArrayList<Double>[] testFeatures = new ArrayList[noOfFeatures];
            ArrayList<Double> testLabels = new ArrayList<Double>();
            for (int f = 0; f < noOfFeatures; f++) {
                trainFeatures[f] = new ArrayList<Double>();
                testFeatures[f] = new ArrayList<Double>();
            }

            for (int j = 0; j < noOfInstances; j++) {
                if((j >= testDataStartIndex && j <= testDataEndIndex) || (i == noOfFolds && j >= testDataStartIndex)){
                    testLabels.add(labels.get(j));
                    for (int f = 0; f < noOfFeatures; f++) {
                        testFeatures[f].add(features[f].get(j));
                    }
                }else {
                    trainLabels.add(labels.get(j));
                    for (int f = 0; f < noOfFeatures; f++) {
                        trainFeatures[f].add(features[f].get(j));
                    }
                }
            }

            ArrayList<Double> predictedLabels = new Evaluator().evaluate(trainFeatures, trainLabels, testFeatures, forestSettings);
            for (int j = 0; j < predictedLabels.size(); j++) {
                allPredicted.add(predictedLabels.get(j));
            }

            double accuracy = EvaluationMeasure.getAccuracy(testLabels, predictedLabels);
            System.out.println("Iteration--> " + i + " : " + accuracy);
            accuracyOfAllFolds.add(accuracy);

            startOfFold = startOfFold + noOfInstancesPerFold;
        }
        System.out.println("All Accuracy : " + EvaluationMeasure.getAccuracy(labels, allPredicted));
        //return accuracyOfAllFolds;
        return allPredicted;
    }
}
