package com.rifatarefin.Encoding;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by ROBIN on 9/22/2016.
 */
public class CategoryIdentifier {
    public HashMap<Integer, ArrayList<String>> getCategory(String fileName, boolean hasHeader, String separator, boolean[] areCategorical) {
        HashMap<Integer, HashSet<String>> hashMapCategory = new HashMap<Integer, HashSet<String>>();
        HashMap<Integer, ArrayList<String>> categoryForAllFeatures = new HashMap<Integer, ArrayList<String>>();
        for (int i = 0; i < areCategorical.length; i++) {
            if (areCategorical[i]){
                hashMapCategory.put(i, new HashSet<String>());
            }
        }
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(fileName));
            String line = "";

            if (hasHeader) {
                line = br.readLine();
            }
            while ((line = br.readLine()) != null) {
                String[] words = line.split(separator);
                for (int i = 0; i < words.length; i++) {
                    words[i] = words[i].trim();
                }

                for (int i = 0; i < words.length; i++) {
                    if (areCategorical[i]){
                        hashMapCategory.get(i).add(words[i]);
                    }
                }

            }
            br.close();
            for (Map.Entry<Integer, HashSet<String>> entry : hashMapCategory.entrySet())
            {
                categoryForAllFeatures.put(entry.getKey(), new ArrayList<String>(entry.getValue()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return categoryForAllFeatures;
    }

}
