package com.rifatarefin.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

/**
 * Created by ROBIN on 8/26/2016.
 */
public class DataSet {
    private ArrayList<Double>[] features;
    private ArrayList<Double> labels;
    private ArrayList<String> featureNames;
    private String labelName;
    private ArrayList<String> classValues;
    private ArrayList<String> ids;

    public DataSet(ArrayList<Double>[] features, ArrayList<Double> labels) {
        this.features = features;
        this.labels = labels;
    }

    public DataSet(ArrayList<Double>[] features, ArrayList<Double> labels, ArrayList<String> featureNames, String labelName) {
        this.features = features;
        this.labels = processLabels(labels);
        this.featureNames = featureNames;
        this.labelName = labelName;
    }
    public DataSet(ArrayList<Double>[] features, ArrayList<Double> labels, ArrayList<String> ids) {
        this.features = features;
        this.labels = processLabels(labels);
        this.ids = ids;
    }
    public DataSet(ArrayList<Double>[] features, ArrayList<Double> labels, ArrayList<String> featureNames, String labelName, ArrayList<String> classValues) {
        this.features = features;
        this.labels = processLabels(labels);
        this.featureNames = featureNames;
        this.labelName = labelName;
        this.classValues = classValues;
    }

    public ArrayList<String> getClassValues() {
        return classValues;
    }

    public void setClassValues(ArrayList<String> classValues) {
        this.classValues = classValues;
    }

    public ArrayList<String> getIds() {
        return ids;
    }

    public void setIds(ArrayList<String> ids) {
        this.ids = ids;
    }

    public String getLabelName() {
        return labelName;
    }

    public ArrayList<String> getFeatureNames() {
        return featureNames;
    }

    public void setFeatureNames(ArrayList<String> featureNames) {
        this.featureNames = featureNames;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public ArrayList<Double>[] getFeatures() {
        return features;
    }

    public void setFeatures(ArrayList<Double>[] features) {
        this.features = features;
    }

    public ArrayList<Double> getLabels() {
        return labels;
    }

    public void setLabels(ArrayList<Double> labels) {
        this.labels = labels;
    }

    public ArrayList<Double> processLabels(ArrayList<Double> labels) {
        ArrayList<Double> processedLabels = new ArrayList<Double>();
        HashSet<Double> classes = new HashSet<Double>();
        for (int i = 0; i < labels.size(); i++) {
            classes.add(labels.get(i));
        }
        ArrayList<Double> abc = new ArrayList<Double>(classes);
        Collections.sort(abc);
        for (int i = 0; i < labels.size(); i++) {
            processedLabels.add(abc.indexOf(labels.get(i))*1.0);
        }

        return processedLabels;
    }
}
