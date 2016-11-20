package com.rifatarefin.CrossValidation;

import com.rifatarefin.Tree.Node;
import com.rifatarefin.Exception.RefinedForestNotFoundException;
import com.rifatarefin.Forest.GlobalRefinedForest;
import com.rifatarefin.Forest.ClassificationForest;
import com.rifatarefin.Serializatin.ClassifierModel;
import com.rifatarefin.Prediction.Predictor;
import de.bwaldvogel.liblinear.*;

import java.util.ArrayList;

/**
 * Created by ROBIN on 10/23/2016.
 */
public class Evaluator {
    public ArrayList<Double> evaluate(ArrayList<Double>[] trainFeatures, ArrayList<Double> trainLabels, ArrayList<Double>[] testFeatures, ForestSettings forestSettings){

        ArrayList<Node> forest = new ClassificationForest().createForest(trainFeatures, trainLabels, forestSettings);
        ArrayList<Double> predictedLabels;
        if(forestSettings.getForestName().equals("RefinedForest")){
            GlobalRefinedForest refinedForest = new GlobalRefinedForest();
            Model model = refinedForest.train(trainFeatures, trainLabels, forest);
            predictedLabels = refinedForest.test(testFeatures, forest, model);
        }else if(forestSettings.getForestName().equals("WeightedForest")){
            predictedLabels = Predictor.predictUsingWeightedVoting(forest, testFeatures);
        }else if(forestSettings.getForestName().equals("RandomForest")){
            predictedLabels = Predictor.predictUsingMajorityVoting(forest, testFeatures);
        }else {
            predictedLabels = Predictor.predictUsingSoftVoting(forest, testFeatures);
        }
        return predictedLabels;
    }
    public ClassifierModel evaluateAndGetModel(ArrayList<Double>[] trainFeatures, ArrayList<Double> trainLabels, ArrayList<Double>[] testFeatures, ForestSettings forestSettings){

        ArrayList<Node> forest = new ClassificationForest().createForest(trainFeatures, trainLabels, forestSettings);
        ArrayList<Double> predictedLabels;
        ClassifierModel classifierModel = new ClassifierModel();
        classifierModel.setForest(forest);
        if(forestSettings.getForestName().equals("RefinedForest")){
            GlobalRefinedForest refinedForest = new GlobalRefinedForest();
            Model model = refinedForest.train(trainFeatures, trainLabels, forest);
            predictedLabels = refinedForest.test(testFeatures, forest, model);
            classifierModel.setModel(model);
        }else if(forestSettings.getForestName().equals("WeightedForest")){
            predictedLabels = Predictor.predictUsingWeightedVoting(forest, testFeatures);
        }else if(forestSettings.getForestName().equals("RandomForest")){
            predictedLabels = Predictor.predictUsingMajorityVoting(forest, testFeatures);
        }else {
            predictedLabels = Predictor.predictUsingSoftVoting(forest, testFeatures);
        }
        classifierModel.setPredictedLabels(predictedLabels);
        return classifierModel;
    }
    public  ArrayList<Double> evaluateWithModel(ArrayList<Double>[] testFeatures, ClassifierModel classifierModel, String forestName) throws RefinedForestNotFoundException {

        ArrayList<Node> forest = classifierModel.getForest();

        Model libLinearModel = null;
        if(forestName.equals("Refined Forest")){
            libLinearModel = classifierModel.getModel();
            if(libLinearModel == null){
                throw new RefinedForestNotFoundException();
            }
        }

        ArrayList<Double> predictedLabels;
        if(forestName.equals("Refined Forest")){
            GlobalRefinedForest refinedForest = new GlobalRefinedForest();
            predictedLabels = refinedForest.test(testFeatures, forest, libLinearModel);
        }else if(forestName.equals("Weighted Forest")){
            predictedLabels = Predictor.predictUsingWeightedVoting(forest, testFeatures);
        }else if(forestName.equals("Random Forest")){
            predictedLabels = Predictor.predictUsingMajorityVoting(forest, testFeatures);
        }else {
            predictedLabels = Predictor.predictUsingSoftVoting(forest, testFeatures);
        }

        return predictedLabels;
    }
}
