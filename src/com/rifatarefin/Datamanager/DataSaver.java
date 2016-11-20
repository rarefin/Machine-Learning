package com.rifatarefin.Datamanager;

import com.rifatarefin.Exception.DataFormatException;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ROBIN on 11/20/2016.
 */
public class DataSaver {
    public void saveData(String fileName, ArrayList<Double>[] features, ArrayList<Double> labels, ArrayList<String> featureNames,
                         String labelName, boolean hasHeader, String seperator, String labelIndex, ArrayList<String> classes) throws IOException, DataFormatException {
        int noOfFeatures = features.length;
        int nofInstances = features[0].size();

        if(noOfFeatures != featureNames.size() || nofInstances != labels.size()){
            throw new DataFormatException();
        }

        FileWriter fw = new FileWriter(fileName, true);
        if(hasHeader){
            String line = "";
            if(labelIndex.equals("last")){
                for (int i = 0; i < featureNames.size(); i++) {
                    line += featureNames.get(i) + seperator;
                }
                line += labelName;
            }else if(labelIndex.equals("first")){
                line += labelName;
                for (int i = 0; i < noOfFeatures; i++) {
                    line += seperator + featureNames.get(i);
                }
            }
            if(!line.equals("")){
                fw.write(line + "\n");
            }
        }

        for (int i = 0; i <nofInstances; i++) {
            String line = "";
            if(labelIndex.equals("last")){
                for (int j = 0; j < noOfFeatures; j++) {
                    line += features[j].get(i) + seperator;
                }
                double abc = labels.get(i);
                line += classes.get((int)abc);
            }else if(labelIndex.equals("first")){
                double abc = labels.get(i);
                line += classes.get((int)abc);
                for (int j = 0; j < noOfFeatures; j++) {
                    line += seperator + features[j].get(i);
                }
            }
            //if(!line.equals("abc")){
                fw.write(line + "\n");
            //}
        }
        fw.close();
    }
}
