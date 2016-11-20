package com.rifatarefin.Datamanager;

import com.rifatarefin.Exception.DataFormatException;
import com.rifatarefin.Data.DataSet;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/**
 * Created by ROBIN on 8/9/2016.
 */
public class CSVReader {
    // delimeter for attribute seperation
    private String sep;

    // has the csv contains header or not
    private boolean header;

    // class index
    private int labelIndex;
    private String labelIndexName;

    public CSVReader() {
        this.sep = ",";
        this.header = true;
        this.labelIndex = 0;
        this.labelIndexName = "";
    }

    public DataSet readCSV(String fileName) throws NumberFormatException, IOException, DataFormatException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
        String line;
        int noOfColumns;
        ArrayList<String> featureNames = new ArrayList<String>();

        String labelName = "label";
        if(header){
            String line2 = bufferedReader.readLine();
            String[] words = line2.split(sep);
            if(words.length < 2){
                throw new DataFormatException();
            }
            noOfColumns = words.length;
            if(labelIndexName.equals("last")){
                this.labelIndex = noOfColumns-1;
            }
            for (int i = 0; i < noOfColumns; i++) {
                if(i == labelIndex){
                    labelName = words[i];
                }else {
                    featureNames.add(words[i]);
                }
            }
        }else {
            BufferedReader tempBufferedReader = new BufferedReader(new FileReader(fileName));
            String line1 = tempBufferedReader.readLine();
            String[] words = line1.split(sep);
            noOfColumns = words.length;
            tempBufferedReader.close();

            for (int i = 0; i < noOfColumns-1; i++) {
                featureNames.add("feature" + i);
            }
        }

        if(labelIndexName.equals("last")){
            this.labelIndex = noOfColumns-1;
        }
        ArrayList<Double>[] features = new ArrayList[noOfColumns-1];
        ArrayList<Double> labels = new ArrayList<Double>();


        for (int i = 0; i < features.length; i++) {
            features[i] = new ArrayList<Double>();
        }
        ArrayList<String> classValues = getClassValues(fileName, labelIndex, header);
        while ((line=bufferedReader.readLine()) != null && !line.equals("")){

            String[] words = line.split(sep);
            if(words.length != noOfColumns){
                throw new DataFormatException();
            }else {
                int featureIndex = 0;
                for (int i = 0; i < words.length; i++) {
                    words[i] = words[i].trim();
                    if(i == labelIndex){
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
        //DataSet dataSet = new DataSet(features, labels, featureNames, labelName);

        return dataSet;
    }

    private int stringToInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }

    private ArrayList<String> getClassValues(String filePath, int classIndex, boolean hasHeader){
        BufferedReader bufferedReader = null;
        HashSet<String> classValues = new HashSet<String>();
        try {
            bufferedReader = new BufferedReader(new FileReader(filePath));
            String line = "";
            if(hasHeader){
                line = bufferedReader.readLine();
            }
            while ((line=bufferedReader.readLine()) != null && !line.equals("")){
                String[] words = line.split(sep);
                if(words.length > 0){
                    classValues.add(words[classIndex]);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return new ArrayList<String>(classValues);
    }

    private double stringToDouble(String value){
        try{
            return Double.parseDouble(value);
        }catch (Exception e){
            return 0;
        }
    }

    public DataSet readCSV(String fileName, String seperator, boolean hasHeader, int classIndex) throws NumberFormatException, IOException, DataFormatException {
        this.sep = seperator;
        this.header = hasHeader;
        this.labelIndex = classIndex;

        return readCSV(fileName);
    }
    public DataSet readCSV (String fileName, String seperator, boolean hasHeader, String classIndexName) throws NumberFormatException, IOException, DataFormatException {
        this.sep = seperator;
        this.header = hasHeader;
        this.labelIndexName = classIndexName;

        return readCSV(fileName);
    }

    public DataSet readCSV(String fileName, boolean hasHeader, int classIndex) throws NumberFormatException, IOException, DataFormatException{
        this.header = hasHeader;
        this.labelIndex = classIndex;

        return readCSV(fileName);
    }

    public DataSet readCSV(String fileName, String seperator, int classIndex) throws NumberFormatException, IOException, DataFormatException{
        this.sep = seperator;
        this.labelIndex = classIndex;

        return readCSV(fileName);
    }

    public DataSet readCSV(String fileName, String seperator, boolean hasHeader) throws NumberFormatException, IOException, DataFormatException{
        this.sep = seperator;
        this.header = hasHeader;

        return readCSV(fileName);
    }

    public DataSet readCSV(String fileName, String seperator) throws NumberFormatException, IOException, DataFormatException {
        this.sep = seperator;

        return readCSV(fileName);
    }

    public DataSet readCSV(String fileName, int classIndex) throws NumberFormatException, IOException, DataFormatException {
        this.labelIndex = classIndex;

        return readCSV(fileName);
    }

    public DataSet readCSV(String fileName, boolean hasHeader) throws NumberFormatException, IOException, DataFormatException {
        this.header = hasHeader;

        return readCSV(fileName);
    }

    public DataSet loadTestData(String fileName, String seperator, boolean hasHeader, String classIndexName,
                                boolean hasLabel, boolean hasId, int noOfFeatures, ArrayList<String> classValues)
            throws NumberFormatException, IOException, DataFormatException {
        this.sep = seperator;
        this.header = hasHeader;
        this.labelIndexName = classIndexName;

        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
        String line;
        int noOfColumns;
        if(header){
            String line2 = bufferedReader.readLine();
            String[] words = line2.split(sep);
            if(words.length < 2){
                throw new DataFormatException();
            }
            noOfColumns = words.length;
        }else {
            BufferedReader tempBufferedReader = new BufferedReader(new FileReader(fileName));
            String line1 = tempBufferedReader.readLine();
            String[] words = line1.split(sep);
            noOfColumns = words.length;
            tempBufferedReader.close();
        }
        if(hasLabel && labelIndexName.equals("last")){
            this.labelIndex = noOfColumns-1;
        }

        ArrayList<Double>[] features = new ArrayList[noOfFeatures];
        ArrayList<Double> labels = null;
        if(hasLabel){
           labels = new ArrayList<Double>();
        }
        ArrayList<String> ids = null;
        if(hasId){
           ids = new ArrayList<String>();
        }

        if((hasId && !hasLabel && (noOfColumns-1 != noOfFeatures)) || (!hasId && hasLabel && (noOfColumns-1 != noOfFeatures))
            || (!hasId && !hasLabel && (noOfColumns != noOfFeatures)) || (hasId && hasLabel && (noOfColumns-2 != noOfFeatures))){
            throw new DataFormatException();
        }

        for (int i = 0; i < features.length; i++) {
            features[i] = new ArrayList<Double>();
        }
        while ((line=bufferedReader.readLine()) != null && !line.equals("")){

            String[] words = line.split(sep);
            if(words.length != noOfColumns){
                throw new DataFormatException();
            }else {
                int featureIndex = 0;
                for (int i = 0; i < words.length; i++) {
                    words[i] = words[i].trim();
                    if(hasId && i == 0){
                        ids.add(words[i]);
                    }else if(hasLabel && i == labelIndex){
                        int index = classValues.indexOf(words[i]);
                        if(index >= 0){
                            labels.add(index*1.0);
                        }else {
                            throw new DataFormatException();
                        }
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
        bufferedReader.close();
        DataSet dataSet = new DataSet(features, labels, ids);

        return dataSet;
    }
}
