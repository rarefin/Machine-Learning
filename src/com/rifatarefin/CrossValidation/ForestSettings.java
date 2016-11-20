package com.rifatarefin.CrossValidation;

/**
 * Created by ROBIN on 10/23/2016.
 */
public class ForestSettings {
    private int noOfTrees;
    private int minLeafSize;
    private double minInfoGain;
    private int noOfRandomFeatures;
    private String forestName;
    private String treeName;
    private boolean useBootstrapSample;
    private int maxTreeDepth;

    public ForestSettings(int noOfRandomFeatures, String forestName, String treeName) {
        this.noOfTrees = 30;
        this.minLeafSize = 5;
        this.minInfoGain = 0;
        this.noOfRandomFeatures = noOfRandomFeatures;
        this.forestName = forestName;
        this.treeName = treeName;
        this.useBootstrapSample = false;
        this.maxTreeDepth = 999999;
    }

    public int getMaxTreeDepth() {
        return maxTreeDepth;
    }

    public void setMaxTreeDepth(int maxTreeDepth) {
        this.maxTreeDepth = maxTreeDepth;
    }

    public String getTreeName() {
        return treeName;
    }

    public void setTreeName(String treeName) {
        this.treeName = treeName;
    }

    public boolean isUseBootstrapSample() {
        return useBootstrapSample;
    }

    public void setUseBootstrapSample(boolean useBootstrapSample) {
        this.useBootstrapSample = useBootstrapSample;
    }

    public int getNoOfTrees() {
        return noOfTrees;
    }

    public void setNoOfTrees(int noOfTrees) {
        this.noOfTrees = noOfTrees;
    }

    public int getMinLeafSize() {
        return minLeafSize;
    }

    public void setMinLeafSize(int minLeafSize) {
        this.minLeafSize = minLeafSize;
    }

    public double getMinInfoGain() {
        return minInfoGain;
    }

    public void setMinInfoGain(double minInfoGain) {
        this.minInfoGain = minInfoGain;
    }

    public int getNoOfRandomFeatures() {
        return noOfRandomFeatures;
    }

    public void setNoOfRandomFeatures(int noOfRandomFeatures) {
        this.noOfRandomFeatures = noOfRandomFeatures;
    }

    public String getForestName() {
        return forestName;
    }

    public void setForestName(String forestName) {
        this.forestName = forestName;
    }
}
