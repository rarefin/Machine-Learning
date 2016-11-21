package com.rifatarefin;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by ROBIN on 9/6/2016.
 */
public class EvaluationMeasure {
    public static double getAccuracy(ArrayList<Double> actualLabel, ArrayList<Double> predictedLabel){
        if(actualLabel.size() != predictedLabel.size()){
            throw new IllegalArgumentException("No of predicted instances is not equal to no of actual instances!!!!");
        }

        int count = 0;
        for (int i = 0; i < actualLabel.size(); i++) {
            //System.out.println(Double.compare(predictedLabel.get(i), actualLabel.get(i)));
            if(Double.compare(predictedLabel.get(i), actualLabel.get(i)) == 0){
                count++;
            }
        }

        return (1.0*count)/actualLabel.size();
    }

    public static int[][] getConfusionMatrix(ArrayList<Double> actualLabel, ArrayList<Double> predictedLabel, HashSet<Double> classes ){
        if(actualLabel.size() != predictedLabel.size()){
            throw new IllegalArgumentException("No of predicted instances is not equal to no of actual instances!!!!");
        }

        //HashSet<Double> classes = new HashSet<Double>();
        //for (int i = 0; i < actualLabel.size(); i++) {
          //  classes.add(actualLabel.get(i));
        //}
        int[][] confusionMatrix = new int[classes.size()][classes.size()];
        for (int i = 0; i < actualLabel.size(); i++) {
            double actual = actualLabel.get(i);
            double predicted = predictedLabel.get(i);
            confusionMatrix[(int)actual][(int)predicted]++;
        }
        return confusionMatrix;
    }
    public static double getPrecision(ArrayList<Double> actualLabel, ArrayList<Double> predictedLabel, double label, HashSet<Double> classes ){
        if(actualLabel.size() != predictedLabel.size()){
            throw new IllegalArgumentException("No of predicted instances is not equal to no of actual instances!!!!");
        }
        int[][] confusionMatrix = getConfusionMatrix(actualLabel, predictedLabel, classes);
        int noOfClassifiedTarget = 0;
        int labelIndex = (int)label;
        for (int i = 0; i <confusionMatrix.length; i++) {
            noOfClassifiedTarget += confusionMatrix[i][labelIndex];
        }
        double precision = 0;
        if(noOfClassifiedTarget > 0){
            int noOfCorrectTarget = confusionMatrix[labelIndex][labelIndex];
            precision = (noOfCorrectTarget*1.0) / noOfClassifiedTarget;
        }
        return precision;
    }
    public static double getPrecision(int[][] confusionMatrix, double label){
        int noOfClassifiedTarget  = 0;
        int labelIndex = (int)label;
        for (int i = 0; i <confusionMatrix.length; i++) {
            noOfClassifiedTarget  += confusionMatrix[i][labelIndex];
        }
        double precision = 0;
        if(noOfClassifiedTarget  > 0){
            int noOfCorrectTarget = confusionMatrix[labelIndex][labelIndex];
            precision = (noOfCorrectTarget*1.0) / noOfClassifiedTarget;
        }
        return precision;
    }

    public static double getRecall(int[][] confusionMatrix, double label){
        int noOfActualTarget = 0;
        int labelIndex = (int)label;
        for (int i = 0; i <confusionMatrix[labelIndex].length; i++) {
            noOfActualTarget += confusionMatrix[labelIndex][i];
        }
        double recall = 0;
        if(noOfActualTarget > 0){
            int noOfCorrectTarget = confusionMatrix[labelIndex][labelIndex];
            recall = (noOfCorrectTarget*1.0) / noOfActualTarget;
        }
        return recall;
    }
    public static double getRecall(ArrayList<Double> actualLabel, ArrayList<Double> predictedLabel, double label, HashSet<Double> classes ){
        if(actualLabel.size() != predictedLabel.size()){
            throw new IllegalArgumentException("No of predicted instances is not equal to no of actual instances!!!!");
        }
        int[][] confusionMatrix = getConfusionMatrix(actualLabel, predictedLabel, classes );
        int noOfActualTarget = 0;
        int labelIndex = (int)label;
        for (int i = 0; i <confusionMatrix[labelIndex].length; i++) {
            noOfActualTarget += confusionMatrix[labelIndex][i];
        }
        double recall = 0;
        if(noOfActualTarget > 0){
            int noOfCorrectTarget = confusionMatrix[labelIndex][labelIndex];
            recall = (noOfCorrectTarget*1.0) / noOfActualTarget;
        }
        return recall;
    }

    public static double getFMeasure(double precision, double recall){
        double fScore = 0;

        double a = precision + recall;
        if(a > 0){
            fScore = (2*precision*recall) / a;
        }
        return fScore;
    }

    public static double getFMeasure(ArrayList<Double> actualLabel, ArrayList<Double> predictedLabel, double label, HashSet<Double> classes ){
        double precision = getPrecision(actualLabel, predictedLabel, label, classes);
        double recall = getRecall(actualLabel, predictedLabel, label, classes);

        return getFMeasure(precision, recall);
    }

    public static double getFMeasure(int[][] confusionMatrix, double label){
        double precision = getPrecision(confusionMatrix, label);
        double recall = getRecall(confusionMatrix, label);

        return getFMeasure(precision, recall);
    }

    public static double getRMSE(ArrayList<Double> actualLabels, ArrayList<Double> predictedLabels){
        if(actualLabels.size() != predictedLabels.size()){
            throw new IllegalArgumentException("No of predicted instances is not equal to no of actual instances!!!!");
        }

        int count = 0;
        double mse = 0;
        for (int i = 0; i < actualLabels.size(); i++) {
            mse += (actualLabels.get(i)-predictedLabels.get(i))*(actualLabels.get(i)-predictedLabels.get(i));
        }
        double rmse = Math.sqrt(mse/actualLabels.size());

        return rmse;
    }
}
