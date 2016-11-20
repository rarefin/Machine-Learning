package com.rifatarefin;

import com.rifatarefin.CrossValidation.CrossValidator;
import com.rifatarefin.CrossValidation.ForestSettings;
import com.rifatarefin.Exception.DataFormatException;
import com.rifatarefin.FeatureSelection.GreedySelection;
import com.rifatarefin.Data.DataSet;
import com.rifatarefin.Datamanager.CSVReader;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) throws NumberFormatException, IOException, DataFormatException {

        //new BinaryEncoder().encode("C:\\Users\\ROBIN\\Desktop\\Encoder\\train.csv", "C:\\Users\\ROBIN\\Desktop\\Encoder\\train1.csv",
          //      "last", ";", true, true);

        String treeName = "";
        int[] noOfTreesArray = {50};
        //String selectedForest = "Random Forest";
        //String selectedForest = "Refined Forest";
        //String selectedForest = "Improved Random Forest";
        //String selectedForest = "Weighted Forest";
        String selectedForest = "Extra Tree Forest";
        //String selectedForest = "Perfect Random Tree Ensemble";
        //String selectedForest = "Refined Extra Tree Ensemble";


        FileWriter fw = new FileWriter("C:\\Users\\ROBIN\\Desktop\\DataSets\\Procesed\\Result\\"+ selectedForest +"_50.csv", true);
        FileWriter fw1 = new FileWriter("C:\\Users\\ROBIN\\Desktop\\DataSets\\Procesed\\Result\\" + "avg_" + selectedForest + "_50.csv", true);
        String[] fileNames = {"_car", "_chess-krvkp", "_credit_screening", "_tictactoe", "biodeg","glass", "iris",
                "optical",  "sonar",  "spambase"};
        //String[] fileNames = {"sonar"};

        int noOfFiles = fileNames.length;
        for (int t = 0; t < noOfTreesArray.length; t++) {
            int noOfTrees = noOfTreesArray[t];
            double avgAccuracy = 0;
            double avgPrecision = 0;
            double avgRecall = 0;
            double avgFMeasure = 0;
            double avgTime = 0;
            for (int i = 0; i <noOfFiles; i++) {
                System.out.println(fileNames[i] + " , Tree : " + noOfTrees);
                fw.append("FileName: " +fileNames[i] + " , No of Trees" + noOfTrees + "---------------->\n");
                String filePath = "C:\\Users\\ROBIN\\Desktop\\DataSets\\Procesed\\" + fileNames[i] + ".csv";
                String sep = ",";
                boolean hasHeader = false;
                String labelIndex = "last";

                CSVReader csvReader = new CSVReader();
                DataSet trainSet = csvReader.readCSV(filePath, sep, hasHeader, labelIndex);
                ArrayList<Double>[] features = trainSet.getFeatures();
                ArrayList<Double> trainLabels = trainSet.getLabels();

                int noOfFeatures = features.length;
                int noOfInstances = features[0].size();

                String forestName = "";
                //String treeName = "";
                if(selectedForest.equals("Random Forest")){
                    forestName = "RandomForest";
                    treeName = "RandomTree";
                }else if(selectedForest.equals("Refined Forest")){
                    forestName = "RefinedForest";
                    treeName = "RandomTree";
                }else if(selectedForest.equals("Extra Tree Forest")){
                    forestName = "RandomForest";
                    treeName = "ExtraTree";
                }else if(selectedForest.equals("Perfect Random Tree Ensemble")){
                    forestName = "RandomForest";
                    treeName = "PerfectRandomTree";
                }else if(selectedForest.equals("Weighted Forest")){
                    forestName = "WeightedForest";
                    treeName = "RandomTree";
                }else if(selectedForest.equals("Improved Random Forest")){
                    forestName = "RandomForest";
                    treeName = "RandomTree";
                }else if(selectedForest.equals("Refined Extra Tree Ensemble")){
                    forestName = "RefinedForest";
                    treeName = "ExtraTree";
                }


                int noOfFolds = 5;
                int minLeafSize = 5;
                int maxDepth = 10;
                int noOfRandomFeatures = (int) Math.round(Math.sqrt(noOfFeatures));
                ForestSettings forestSettings = new ForestSettings(noOfRandomFeatures, forestName, treeName);
                forestSettings.setNoOfTrees(noOfTrees);
                forestSettings.setMinLeafSize(minLeafSize);
                forestSettings.setUseBootstrapSample(false); //0.8507695594892377
                forestSettings.setMaxTreeDepth(maxDepth);

                ArrayList<Double> predicted;
                long start = System.currentTimeMillis();
                if(selectedForest.equals("Improved Random Forest")){
                    int k = Math.round(features.length * (3/6));
                    ArrayList<Integer> selectedFeatureIndices  = new GreedySelection().getSelectedFeatures("ReliefF", features,
                            trainLabels, k);
                    ArrayList<Double>[] selectedTrainFeatures = Utility.getSelectedFeatures(features, selectedFeatureIndices);
                    predicted = new CrossValidator(noOfFolds).validate(selectedTrainFeatures, trainLabels, forestSettings);
                }else{
                    predicted = new CrossValidator(noOfFolds).validate(features, trainLabels, forestSettings);
                }

                long end = System.currentTimeMillis();
                long duration = (end-start);
                double durationInSec = (duration*1.0)/1000;


                double accuracy = EvaluationMeasure.getAccuracy(trainLabels, predicted);
                HashSet<Double> hashSet = new HashSet<Double>();
                HashMap<Double, Double> hashMap = new HashMap<Double, Double>();
                for (int j = 0; j < trainLabels.size(); j++) {
                    hashSet.add(trainLabels.get(j));
                    if(hashMap.get(trainLabels.get(j)) == null){
                        hashMap.put(trainLabels.get(j), 1.0);
                    }else {
                        hashMap.put(trainLabels.get(j), hashMap.get(trainLabels.get(j))+1.0);
                    }
                }
                for (Map.Entry<Double, Double> entry :hashMap.entrySet())
                {
                    double p = entry.getValue() / trainLabels.size();
                    //System.out.println(p);
                    hashMap.put(entry.getKey(), p);
                }
                ArrayList<Double> classes = new ArrayList<Double>(hashSet);
                Collections.sort(classes);

                int[][] confusionMatrix = EvaluationMeasure.getConfusionMatrix(trainLabels, predicted);
                double sumOfPrecisions = 0;
                double sumOfRecalls = 0;
                double sumOfFMeasures = 0;
                for (int j = 0; j < classes.size(); j++) {
                    double classLabel = classes.get(j);
                    double precision = EvaluationMeasure.getPrecision(confusionMatrix, classLabel);
                    double recall = EvaluationMeasure.getRecall(confusionMatrix, classLabel);
                    double fMeasure = EvaluationMeasure.getFMeasure(precision, recall);

                    //System.out.println(hashMap.get(classLabel));
                    sumOfPrecisions += precision * hashMap.get(classLabel);
                    sumOfRecalls += recall * hashMap.get(classLabel);
                    sumOfFMeasures += fMeasure * hashMap.get(classLabel);
                }

                String line = "dataset : " + fileNames[i] + " .... " +", accuracy : "+ accuracy + ", " + " precision: " +sumOfPrecisions +
                        ", " + ", Recall : " + sumOfRecalls + ", " + ", fMeasure : "+sumOfFMeasures + ", " + ", time : " + durationInSec + "\n";
                fw.append(line);

                avgAccuracy += accuracy;
                avgPrecision += sumOfPrecisions;
                avgRecall += sumOfRecalls;
                avgFMeasure += sumOfFMeasures;
                avgTime += durationInSec;

            }
            System.out.println("No of Trees : " + noOfTrees + " avg accuracy : "+ avgAccuracy/noOfFiles);
            String line1 ="Tree: " + treeName + ", forest : " +selectedForest + ", No of Trees:  " + noOfTrees + ", accuracy: " + avgAccuracy/noOfFiles + ", Precision: " + avgPrecision/noOfFiles + ", " +
                    "Recall: " + avgRecall/noOfFiles + ", FMeasure: " + avgFMeasure/noOfFiles + ", Time: " + avgTime/noOfFiles + "\n";
            String line = "forest : " +selectedForest + "accuracy: " + avgAccuracy/noOfFiles + ", Precision: " + avgPrecision/noOfFiles + ", " +
                    "Recall: " + avgRecall/noOfFiles + ", FMeasure: " + avgFMeasure/noOfFiles + ", Time: " + avgTime/noOfFiles + "\n";
            fw.append(line);
            fw1.append(line1);

            fw.append("\n----------------------------------------------------------------------------------------------------------------\n");
            fw1.append("\n----------------------------------------------------------------------------------------------------------------\n");
        }
        fw.close();
        fw1.close();

        //DataSet testSet = dataReader.readCSV("C:\\Users\\ROBIN\\Desktop\\Experiment\\optical.test",",", false, "last");



        // Randomize data






        //new MultiIntervalDiscretizer().getSelectedFeatures(features, labels);





        // Get the Java runtime
        //Runtime runtime = Runtime.getRuntime();
        // Run the garbage collector
        //runtime.gc();
        // Calculate the used memory
        //long memory = runtime.totalMemory() - runtime.freeMemory();
        //System.out.println("Used memory is bytes: " + memory);
        //System.out.println("Used memory is megabytes: " + bytesToMegabytes(memory));

    }
    private static final long MEGABYTE = 1024L * 1024L;

    public static long bytesToMegabytes(long bytes) {
        return bytes / MEGABYTE;
    }
}
