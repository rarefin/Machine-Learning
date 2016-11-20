package com.rifatarefin.Datamanager;

import com.rifatarefin.Data.DataSet;
import com.rifatarefin.Exception.DataFormatException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by ROBIN on 11/14/2016.
 */

class ARFFReader {
    public DataSet readArff(String fileName) throws IOException, DataFormatException {
        FileReader fr = new FileReader(new File(fileName));

        Scanner input = new Scanner(fr);

        ArrayList<String> featureNames = new ArrayList<String>();

        while(input.hasNext()){
            String str=input.nextLine();

            if(str.contains("@attribute")){
                String[] words = str.split(" ");
                for (int i = 0; i < words.length; i++) {
                    words[i] = words[i].trim();
                }
                featureNames.add(words[1]);
            }

            if(str.equals("@data"))
                break;
        }
        String labelName = featureNames.get(featureNames.size()-1);
        featureNames.remove(featureNames.size()-1);

        ArrayList<String> classValues = getClassValues(fileName);
        ArrayList<Double>[] features = new ArrayList[featureNames.size()];
        ArrayList<Double> labels = new ArrayList<Double>();

        for (int i = 0; i < features.length; i++) {
            features[i] = new ArrayList<Double>();
        }

        while(input.hasNext()){
            String str=input.next();
            String[] words = str.split(",");

            if(words.length != (featureNames.size()+1)){
                throw new DataFormatException();
            }else {
                int featureIndex = 0;
                for (int i = 0; i < words.length; i++) {
                    words[i] = words[i].trim();
                    if(i == (words.length-1)){
                        labels.add(classValues.indexOf(words[i])*1.0);
                    }else{
                        try{
                            double value = Double.parseDouble(words[i]);
                            features[featureIndex].add(value);
                            featureIndex++;
                        }catch (Exception e){
                            throw new NumberFormatException();
                        }
                    }
                }
            }
        }

        int noOfFeatures = features.length;
        int noOfInstances = features[0].size();
        ArrayList<Integer> abc = new ArrayList<Integer>();
        for (int i = 0; i < noOfInstances; i++) {
            abc.add(i);
        }
        ArrayList<Double>[] randomizedFeatures = new ArrayList[noOfFeatures];
        ArrayList<Double> randomizedLabels = new ArrayList<Double>();

        for (int i = 0; i < noOfFeatures; i++) {
            randomizedFeatures[i] = new ArrayList<Double>();
        }
        Random random = new Random(0);
        for (int i = 0; i < noOfInstances; i++) {
            int randomIndex = random.nextInt(abc.size());
            int index = abc.get(randomIndex);
            abc.remove(randomIndex);
            for (int j = 0; j < noOfFeatures; j++) {
                randomizedFeatures[j].add(features[j].get(index));
            }
            randomizedLabels.add(labels.get(index));
        }
        DataSet dataSet = new DataSet(randomizedFeatures, randomizedLabels, featureNames, labelName, classValues);

        return dataSet;
    }
    public ArrayList<String> getClassValues(String fileName) throws IOException {
        FileReader fr = new FileReader(new File(fileName));
        Scanner input = new Scanner(fr);
        while(input.hasNext()){
            String str=input.nextLine();

            if(str.equals("@data"))
                break;
        }
        HashSet<String> classes = new HashSet<String>();
        while(input.hasNext()){
            String str=input.next();
            String[] words = str.split(",");

            if(words.length > 0){
                String label = words[words.length-1].trim();
                classes.add(label);
            }
        }
        fr.close();
        input.close();
        return new ArrayList<String>(classes);
    }

}


