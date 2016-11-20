package com.rifatarefin.Forest;

import com.rifatarefin.Tree.Node;
import com.rifatarefin.Utility;
import de.bwaldvogel.liblinear.*;

import java.util.ArrayList;

/**
 * Created by ROBIN on 9/6/2016.
 */
public class GlobalRefinedForest {
    public Model train(ArrayList<Double>[] features, ArrayList<Double> labels, ArrayList<Node> forest){
        Feature[][] trainData = createNewFeatures(features, forest);
        Problem problem = new Problem();
        problem.l = labels.size(); // number of training examples
        problem.n = trainData[0].length; // number of features
        problem.x = trainData; // feature nodes
        problem.y = Utility.toDoubleArray(labels); // target values

        SolverType solver = SolverType.L2R_L2LOSS_SVC; // -s 0
        double C = 1;    // cost of constraints violation
        double eps = 0.01; // stopping criteria

        Parameter parameter = new Parameter(solver, C, eps);
        Model model = Linear.train(problem, parameter);

       return model;
    }

    public ArrayList<Double> test(ArrayList<Double>[] newFeatures, ArrayList<Node> forest, Model model){
        Feature[][] testData = createNewFeatures(newFeatures, forest);
        ArrayList<Double> predictedLabels = new ArrayList<Double>();
        for (int j = 0; j < testData.length; j++) {
            double label = Linear.predict(model, testData[j]);
            predictedLabels.add(label);
        }
        return predictedLabels;
    }

    public Feature[][] createNewFeatures(ArrayList<Double>[] oldInstances, ArrayList<Node> forest) {
        int noOfTrees = forest.size();
        int noOfInstances = oldInstances[0].size();
        int noOfOldFeatures = oldInstances.length;
        int noOfBinaryFeatures = 0;
        for (int i = 0; i < forest.size(); i++) {
            noOfBinaryFeatures += forest.get(i).getNoOfLeaves();
        }
        System.gc();
        Feature[][] instances = new Feature[noOfInstances][noOfBinaryFeatures];

        for (int i = 0; i < noOfInstances; i++) {
            double[] X = new double[noOfOldFeatures];
            for (int j = 0; j < noOfOldFeatures; j++) {
                X[j] = oldInstances[j].get(i);
            }

            int index = 0;
            for (int j = 0; j < noOfTrees; j++) {
                int indicator = getLeafIndex(forest.get(j), X);
                for (int k = 0; k < forest.get(j).getNoOfLeaves(); k++) {
                    if((indicator-1) == k){
                        instances[i][index] = new FeatureNode(index+1, 1);
                    }else{
                        instances[i][index] = new FeatureNode(index+1, 0);
                    }
                    index++;
                }
            }

        }
        return instances;
    }

    private static int getLeafIndex(Node tree, double[] x) {
        int leafIndex;

        while (true){
            if(tree.isLeaf()){
                leafIndex = tree.getLeafIndex();
                break;
            }else{
                if(x[tree.getSelectedFeatureIndex()] <= tree.getThreshold()){
                    tree = tree.getLeftChild();
                }else {
                    tree = tree.getRightChild();
                }
            }
        }

        return leafIndex;
    }
}
