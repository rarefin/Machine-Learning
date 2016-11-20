package com.rifatarefin.Forest;

import com.rifatarefin.CrossValidation.ForestSettings;
import com.rifatarefin.Tree.*;
import com.rifatarefin.EvaluationMeasure;
import com.rifatarefin.ReSampling.BootstrapSampler;
import com.rifatarefin.Prediction.Predictor;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by ROBIN on 9/6/2016.
 */
public class ClassificationForest {

    public ArrayList<Node> createForest(ArrayList<Double>[] trainFeatures, ArrayList<Double> trainLabels, ForestSettings forestSettings){
        HashSet<Double> classes = new HashSet<Double>();
        for (int i = 0; i < trainLabels.size(); i++) {
            classes.add(trainLabels.get(i));
        }
        ArrayList<Node> forest = new ArrayList<Node>();
        for (int i = 0; i < forestSettings.getNoOfTrees(); i++) {
            BootstrapSampler bootstrapSampler = null;

            if(!forestSettings.getTreeName().equals("PerfectRandomTree") && (forestSettings.getForestName().equals("WeightedForest") || forestSettings.isUseBootstrapSample())){
                bootstrapSampler = new BootstrapSampler();
                bootstrapSampler.createSample(trainFeatures, trainLabels, i);
                trainFeatures = bootstrapSampler.getBootstrapedFeatures();
                trainLabels = bootstrapSampler.getBootstrapedLabel();
            }

            TreeInterface treeCreator = null;
            if(forestSettings.getTreeName().equals("RandomTree")){
                treeCreator = new RandomTree(i);
            }else if(forestSettings.getTreeName().equals("ExtraTree")){
                // variation 1 : For Extra tree forest
                treeCreator = new ExtraTree(i);
            }else if(forestSettings.getTreeName().equals("PerfectRandomTree")){
                // variation 2 : For Perfect Random tree forest
                treeCreator = new PerfectRandomTree(i);
            }

            Node tree = new Node();
            treeCreator.createTree(tree, trainFeatures, trainLabels, forestSettings, classes.size());

            if(forestSettings.getForestName().equals("RefinedForest")){
                // variation 3 : Global refined forest
                tree.setNoOfLeaves(treeCreator.getLeafIndex());
            }else if(forestSettings.getForestName().equals("WeightedForest")){
                // variation 4 : Weighted forest
                ArrayList<Double> predicted = Predictor.predictUsingTree(bootstrapSampler.getOobFeatures(), tree);
                double oobAccuracy = EvaluationMeasure.getAccuracy(bootstrapSampler.getOobLabels(), predicted);
                tree.setOobAccuracy(oobAccuracy);
            }
            forest.add(tree);
        }

        return forest;
    }
}
