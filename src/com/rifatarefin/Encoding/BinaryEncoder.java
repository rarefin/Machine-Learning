package com.rifatarefin.Encoding;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ROBIN on 9/22/2016.
 */
public class BinaryEncoder {
    public void performEncoding(String inputFile, String outputFile, boolean hasHeader,
                       String separator, boolean[] areCategorical, HashMap<Integer, ArrayList<String>> categoryForAllFeatures){

        FileWriter fw;
        BufferedReader br;
        try{
            br = new BufferedReader(new FileReader(inputFile));
            fw = new FileWriter(outputFile);

            String line = "";
            if(hasHeader){
                String tempLine = "";
                line = br.readLine();
                String[] categoryNames = line.split(separator);
                int noOfColumns = categoryNames.length;
                for (int i = 0; i < noOfColumns; i++) {
                    if(areCategorical[i]){
                        for (int j = 0; j < 16; j++) {
                            tempLine += categoryNames[i] + j;
                            if(j < 15){
                                tempLine += separator;
                            }
                        }
                    }else {
                        tempLine += categoryNames[i];
                    }
                    if(i < noOfColumns-1){
                        tempLine += separator;
                    }
                }
                fw.write(tempLine + "\n");
            }
            while ((line=br.readLine()) != null){
                String newLine = "";

                String[] words = line.split(separator);
                for (int i = 0; i < words.length; i++) {
                    words[i] = words[i].trim();
                }

                for (int i = 0; i < words.length; i++) {
                    if(areCategorical[i]) {
                        ArrayList<String> categories = categoryForAllFeatures.get(i);
                        int encodedValue = categories.indexOf(words[i]);
                        if(encodedValue==-1){
                            encodedValue = 0;
                        }
                        String binaryString = Integer.toBinaryString(encodedValue);
                        String[] bits = binaryString.split("");
                        String newFeature = "";
                        for (int j = 0; j < 16; j++) {
                            if(j > (bits.length-1)){
                                newFeature += "0";
                            }else {
                                newFeature += bits[j];
                            }
                            if (j < 15){
                                newFeature += separator;
                            }
                        }

                        newLine += newFeature + "";
                    }else {
                        newLine += words[i];
                    }
                    if(i < words.length-1){
                        newLine += separator;
                    }
                }

                fw.write(newLine + "\n");
            }
            fw.close();
            br.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
