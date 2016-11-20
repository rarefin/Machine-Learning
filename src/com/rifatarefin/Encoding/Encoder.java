package com.rifatarefin.Encoding;

import com.rifatarefin.Exception.DataFormatException;
import com.rifatarefin.Exception.InputFileNotFoundException;
import com.rifatarefin.Exception.OutputFileNotFoundException;
import com.rifatarefin.Exception.StringParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by ROBIN on 11/20/2016.
 */
public class Encoder {
    public void encode(String inputFile, String outputFile, String headerIndex, String separator,
                       boolean hasHeader, String indicesString, String encoderType, String fileType) throws StringParseException, InputFileNotFoundException, OutputFileNotFoundException {
        BufferedReader br;
        try{
            if(fileType.equals("ARFF")){
                inputFile = convertArffToString(inputFile);
            }

            br = new BufferedReader(new FileReader(inputFile));
            String line = "";
            int noOfColumns;
            line = br.readLine();
            String[] words = line.split(separator);
            noOfColumns = words.length;
            br.close();

            boolean[] areCategorical = new boolean[noOfColumns];
            for (int i = 0; i < areCategorical.length; i++) {
                areCategorical[i] = false;
            }
            indicesString = indicesString.trim();
            if(!indicesString.equals("")){
                String[] indices = indicesString.split(",");
                for (int i = 0; i < indices.length; i++) {
                    try {
                        int index = Integer.parseInt(indices[i]);
                        if(index < 0 || index > noOfColumns){
                            throw new ArrayIndexOutOfBoundsException();
                        }else {
                            areCategorical[index] = true;
                        }
                    }catch (Exception e){
                        throw new StringParseException();
                    }
                }
            }else {
                for (int i = 0; i < areCategorical.length; i++) {
                    areCategorical[i] = true;
                }
                if(headerIndex.equals("first")){
                    areCategorical[0] = false;
                }else if(headerIndex.equals("last")){
                    areCategorical[noOfColumns-1] = false;
                }
            }

            HashMap<Integer, ArrayList<String>> categoryForAllFeatures = new CategoryIdentifier().getCategory(inputFile,
                    hasHeader, separator, areCategorical);

            if(encoderType.equals("Normal Encoding")){
                new NormalEncoder().performEncoding(inputFile, outputFile, hasHeader, separator, areCategorical, categoryForAllFeatures);
            }else if(encoderType.equals("One Hot Encoding")){
                new OneHotEncoder().performEncoding(inputFile, outputFile, hasHeader, separator, areCategorical, categoryForAllFeatures);
            }else if(encoderType.equals("Binary Encoding")) {
                new BinaryEncoder().performEncoding(inputFile, outputFile, hasHeader, separator, areCategorical, categoryForAllFeatures);
            }
            if(fileType.equals("ARFF")){
               File file = new File(inputFile);
                file.delete();
            }

        }catch (IOException e){
            e.printStackTrace();
            throw new InputFileNotFoundException();
        }
    }

    private String convertArffToString(String inputFile) {
        File file = new File(inputFile);
        String dir = "";
        if (file.isDirectory()){
            dir=file.getAbsolutePath();
        }else{
            dir=file.getAbsolutePath().replaceAll(file.getName(), "");
        }
        String outputFile = dir + "_"+file.getName();

        FileWriter fw = null;
        FileReader fr = null;
        Scanner input = null;
        try {
            fw = new FileWriter(outputFile);
            fr = new FileReader(new File(inputFile));
            input = new Scanner(fr);
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
            String line  = "";
            for (int i = 0; i < featureNames.size(); i++) {
                line += featureNames.get(i);
                if(i < (featureNames.size()-1)){
                    line += ",";
                }
            }
            fw.write(line + "\n");
            while(input.hasNext()){
                String str=input.next();
                str = str.trim();
                if(!str.equals("")){
                    fw.write(str + "\n");
                }
            }
            fw.close();
            fr.close();
            input.close();
        } catch (IOException e) {

        }
        return outputFile;
    }
}
