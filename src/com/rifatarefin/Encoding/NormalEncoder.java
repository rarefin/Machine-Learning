package com.rifatarefin.Encoding;

import com.rifatarefin.Exception.InputFileNotFoundException;
import com.rifatarefin.Exception.OutputFileNotFoundException;
import com.rifatarefin.Exception.StringParseException;

import java.io.*;
import java.util.*;

/**
 * Created by ROBIN on 9/22/2016.
 */
public class NormalEncoder {
    public void encode(String inputFile, String outputFile, String headerIndex, String separator,
                       boolean hasHeader, String indicesString) throws OutputFileNotFoundException, InputFileNotFoundException, StringParseException {
        BufferedReader br;
        try{
            /*File file = new File(inputFile);
            String dir="";
            if (file.isDirectory())
            {
                dir=file.getAbsolutePath();
            }
            else
            {
                dir=file.getAbsolutePath().replaceAll(file.getName(), "");
            }

            outputFile = dir + "_"+file.getName();*/
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
            performEncoding(inputFile, outputFile, hasHeader, separator, areCategorical, categoryForAllFeatures);
        } catch (IOException e) {
            e.printStackTrace();
            throw new InputFileNotFoundException();
        }
    }
    public void encode(String inputFile, String outputFile, int[] categoricalFeaturesIndices,
                       Boolean hasHeader, String separator){
        FileWriter fw;
        BufferedReader br;
        try{
            br = new BufferedReader(new FileReader(inputFile));
            fw = new FileWriter(outputFile);

            String line = "";
            int noOfColumns;
            if(hasHeader){
                line = br.readLine();
                String[] words = line.split(separator);
                noOfColumns = words.length;
                fw.write(line + "\n");
            }else {
                BufferedReader tempBufferedReader = new BufferedReader(new FileReader(inputFile));
                String line1 = tempBufferedReader.readLine();
                String[] words = line1.split(separator);
                noOfColumns = words.length;
                tempBufferedReader.close();
            }
            boolean[] areCategorical = new boolean[noOfColumns];
            for (int i = 0; i < categoricalFeaturesIndices.length; i++) {
                areCategorical[categoricalFeaturesIndices[i]] = true;
            }
            HashMap<Integer, ArrayList<String>> categoryForAllFeatures = new CategoryIdentifier().getCategory(inputFile,
                    hasHeader, separator, areCategorical);
            performEncoding(inputFile, outputFile, hasHeader, separator, areCategorical, categoryForAllFeatures);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void performEncoding(String inputFile, String outputFile, boolean hasHeader,
                       String separator, boolean[] areCategorical, HashMap<Integer, ArrayList<String>> categoryForAllFeatures) throws OutputFileNotFoundException {

        FileWriter fw;
        BufferedReader br;
        try{
            br = new BufferedReader(new FileReader(inputFile));
            fw = new FileWriter(outputFile);

            String line = "";
            if(hasHeader){
                line = br.readLine();
                fw.write(line + "\n");
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
                        Collections.sort(categories);
                        int encodedValue = categories.indexOf(words[i]);
                        if(encodedValue == -1){
                            newLine += "55555"; // missing value treatment
                        }else {
                            newLine += encodedValue + "";
                        }
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
        } catch (IOException e) {
            e.printStackTrace();
            throw new OutputFileNotFoundException();
        }
    }
}
