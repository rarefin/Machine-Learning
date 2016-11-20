package com.rifatarefin.ReSampling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by ROBIN on 10/14/2016.
 */
public class BootstrapSampler {
    private ArrayList<Double>[] bootstrapedFeatures;
    private ArrayList<Double>[] oobFeatures;
    private ArrayList<Double> bootstrapedLabels;
    private ArrayList<Double> oobLabels;


    public ArrayList<Double>[] getOobFeatures() {
        return oobFeatures;
    }

    public ArrayList<Double>[] getBootstrapedFeatures() {
        return bootstrapedFeatures;
    }

    public ArrayList<Double> getOobLabels() {
        return oobLabels;
    }

    public ArrayList<Double> getBootstrapedLabel() {
        return bootstrapedLabels;
    }

    public void createSample(ArrayList<Double>[] features, ArrayList<Double> labels, int seed){
        int noOfInstances = features[0].size();
        int noOfFeatures = features.length;
        HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
        Random random = new Random(seed);

        ArrayList<Double>[] sampledFeatures = new ArrayList[noOfFeatures];
        ArrayList<Double> sampledLabels = new ArrayList<Double>();
        ArrayList<Double>[] unsampledFeatures = new ArrayList[noOfFeatures];
        ArrayList<Double> unsampledLabels = new ArrayList<Double>();

        for (int j = 0; j < noOfFeatures; j++) {
            sampledFeatures[j] = new ArrayList<Double>();
            unsampledFeatures[j] = new ArrayList<Double>();
        }
        for (int i = 0; i <noOfInstances; i++) {
            int randomIndex = random.nextInt(noOfInstances);
            hashMap.put(randomIndex, 1);
            for (int j = 0; j < noOfFeatures; j++) {
                sampledFeatures[j].add(features[j].get(i));
            }
            sampledLabels.add(labels.get(i));
        }

        for (int i = 0; i <noOfInstances; i++) {
            if(hashMap.get(i) == null){
                for (int j = 0; j < noOfFeatures; j++) {
                    unsampledFeatures[j].add(features[j].get(i));
                }
                unsampledLabels.add(labels.get(i));
            }
        }
        this.bootstrapedFeatures = sampledFeatures;
        this.bootstrapedLabels = sampledLabels;
        this.oobFeatures = unsampledFeatures;
        this.oobLabels = unsampledLabels;
    }
}
