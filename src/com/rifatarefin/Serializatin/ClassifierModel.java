package com.rifatarefin.Serializatin;

import com.rifatarefin.Tree.Node;
import de.bwaldvogel.liblinear.Model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ROBIN on 11/18/2016.
 */
public class ClassifierModel implements Serializable{
    private ArrayList<Node> forest;
    private Model model;
    private ArrayList<Double> predictedLabels;

    public ArrayList<Node> getForest() {
        return forest;
    }

    public ArrayList<Double> getPredictedLabels() {
        return predictedLabels;
    }

    public void setPredictedLabels(ArrayList<Double> predictedLabels) {
        this.predictedLabels = predictedLabels;
    }

    public void setForest(ArrayList<Node> forest) {
        this.forest = forest;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

}
