package com.rifatarefin.UI;

import com.rifatarefin.CrossValidation.CrossValidator;
import com.rifatarefin.CrossValidation.Evaluator;
import com.rifatarefin.CrossValidation.ForestSettings;
import com.rifatarefin.CrossValidation.HoldoutValidator;
import com.rifatarefin.Datamanager.DataReader;
import com.rifatarefin.Encoding.Encoder;
import com.rifatarefin.EvaluationMeasure;
import com.rifatarefin.Exception.*;
import com.rifatarefin.FeatureSelection.GreedySelection;
import com.rifatarefin.FeatureSelection.MRMR;
import com.rifatarefin.Serialization.ClassifierModel;
import com.rifatarefin.Serialization.ObjectSerializer;
import com.rifatarefin.Utility;
import com.rifatarefin.Data.DataSet;
import com.rifatarefin.Datamanager.CSVReader;
import com.rifatarefin.Datamanager.DataSaver;
import com.rifatarefin.Discretization.MultiIntervalDiscretizer;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 * Created by ROBIN on 11/1/2016.
 */
public class MainFrame {
    private ArrayList<Double>[] features;
    private ArrayList<Double> labels;
    private ArrayList<String> attributeNames;
    private String labelStringName;
    private ArrayList<Double> classes;
    private ArrayList<String> originalClassValues;
    private ClassifierModel classifierModel;

    private JFrame mainFrame;
    private String fileName;

    JTextArea resultTextArea;
    JButton evaluationButton;
    JButton saveClassifierButton;

    String forestName = "Random Forest";
    public MainFrame(){
        evaluationButton = new JButton("Evaluate");
        saveClassifierButton = new JButton("Save Classifier");
        JPanel submitPanel = new JPanel();
        GridLayout submitPanelLayout = new GridLayout(0, 2);
        submitPanelLayout.setHgap(10);
        submitPanel.setLayout(submitPanelLayout);
        submitPanel.add(evaluationButton);
        submitPanel.add(saveClassifierButton);
        saveClassifierButton.setEnabled(false);

        mainFrame = new JFrame("Machine Learning");
        mainFrame.setSize(750, 625);

        Container container = mainFrame.getContentPane();
        container.setPreferredSize(new Dimension(725, 610));
        JTabbedPane tabbedPane = new JTabbedPane();
        container.add(tabbedPane);

        JPanel preProcessingPanel = new JPanel();
        preProcessingPanel.setLayout(new GridLayout(0, 2));
        JPanel column1 = new JPanel();
        column1.setLayout(new FlowLayout());
        column1.add(getFileInputPanel());
        column1.add(getEncoderPanel());

        JPanel column2 = new JPanel();
        column2.setLayout(new FlowLayout());
        column2.add(getFilterPanel());
        column2.add(getFeaturesPanel());

        preProcessingPanel.add(column1);
        preProcessingPanel.add(column2);

        JPanel classificationPanel = new JPanel();

        JPanel classificationColumn1 = new JPanel();
        classificationColumn1.setLayout(new FlowLayout());
        classificationColumn1.add(getClassificationPanel());
        classificationColumn1.add(getEvaluationOption());
        classificationColumn1.add(submitPanel);


        JPanel classificationColumn2 = new JPanel();
        //classificationColumn2.setLayout(new FlowLayout());
        classificationColumn2.add(getResultPanel());


        classificationPanel.setLayout(new GridLayout(0, 2));
        classificationPanel.add(classificationColumn1);
        classificationPanel.add(classificationColumn2);


        tabbedPane.addTab("Pre Processing", preProcessingPanel);
        tabbedPane.addTab("Classification", classificationPanel);


        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);

        startEvaluation();
    }

    public void startEvaluation(){
        evaluationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(providePreTrainedModelCheckBox.isSelected()){
                    if(preTrainedFileChooser.getSelectedFile() != null){
                        try {
                            ClassifierModel preTrainedModel = ObjectSerializer.deserialize(preTrainedFileChooser.getSelectedFile().getAbsolutePath());
                            String forestName = forestNames.getSelectedItem().toString();
                            ArrayList<Double> predicted = null;
                            if (evaluationOptionNames.getSelectedItem().equals("Using Train Set")) {

                                try {
                                    long start = System.currentTimeMillis();
                                    predicted = new Evaluator().evaluateWithModel(features, preTrainedModel, forestName);
                                    long end = System.currentTimeMillis();
                                    long duration = (end-start);
                                    double durationInSec = (duration*1.0)/1000;
                                    populateResultArea(labels, predicted, durationInSec);
                                } catch (RefinedForestNotFoundException e1) {
                                    JOptionPane.showMessageDialog(new JFrame(), "You haven't select correct forest!!!", "Error",
                                            JOptionPane.ERROR_MESSAGE);
                                    e1.printStackTrace();
                                }
                            }else if (evaluationOptionNames.getSelectedItem().equals("Using Test Set")) {
                                if(testSetInputFileChooser.getSelectedFile() != null){
                                    String testDataFilePath =  testSetInputFileChooser.getSelectedFile().getAbsolutePath();
                                    String separator = (String) separatorOptions.getSelectedItem();
                                    String labelIndex = (String) labelIndexOption.getSelectedItem();
                                    boolean hasHeader = hasHeaderCheckBox.isSelected();
                                    boolean hasId = hasIdCheckBox.isSelected();
                                    boolean hasLabel = hasLabelCheckBox.isSelected();

                                    CSVReader csvReader = new CSVReader();
                                    try {
                                        DataSet testSet = csvReader.loadTestData(testDataFilePath, separator, hasHeader,labelIndex, hasLabel,
                                                hasId, features.length, originalClassValues);
                                        ArrayList<Double>[] testFeatures = testSet.getFeatures();
                                        ArrayList<Double> testLabels = testSet.getLabels();
                                        ArrayList<String> ids = testSet.getIds();

                                        if(hasLabel && testLabels != null){
                                            try {
                                                long start = System.currentTimeMillis();
                                                predicted = new Evaluator().evaluateWithModel(testFeatures, preTrainedModel, forestName);
                                                long end = System.currentTimeMillis();
                                                long duration = (end-start);
                                                double durationInSec = (duration*1.0)/1000;
                                                populateResultArea(testLabels, predicted, durationInSec);
                                            } catch (RefinedForestNotFoundException e1) {
                                                JOptionPane.showMessageDialog(new JFrame(), "You haven't select correct forest!!!", "Error",
                                                        JOptionPane.ERROR_MESSAGE);
                                                e1.printStackTrace();
                                            }
                                        }else {
                                            try {
                                                predicted = new Evaluator().evaluateWithModel(testFeatures, preTrainedModel, forestName);
                                            } catch (RefinedForestNotFoundException e1) {
                                                JOptionPane.showMessageDialog(new JFrame(), "You haven't select correct forest!!!", "Error",
                                                        JOptionPane.ERROR_MESSAGE);
                                                e1.printStackTrace();
                                            }
                                        }
                                        if(testSetResultFileChooser.getSelectedFile() != null){
                                            FileWriter fw;
                                            try{
                                                fw = new FileWriter(testSetResultFileChooser.getSelectedFile().getAbsolutePath());
                                                if(hasId && predicted != null && predicted.size() == ids.size()){
                                                    for (int i = 0; i < predicted.size(); i++) {
                                                        double p = predicted.get(i);
                                                        fw.write(ids.get(i) + "," + originalClassValues.get((int)p) + "\n");
                                                    }
                                                }else {
                                                    for (int i = 0; i < predicted.size(); i++) {
                                                        double p = predicted.get(i);
                                                        fw.write(originalClassValues.get((int)p) + "\n");
                                                    }
                                                }
                                                fw.close();
                                            }catch (IOException ioe){
                                                JOptionPane.showMessageDialog(new JFrame(), "OutputFile not find..result was not saved!!", "Error",
                                                        JOptionPane.ERROR_MESSAGE);
                                            }
                                        }
                                    } catch (NumberFormatException e1) {
                                        e1.printStackTrace();
                                        JOptionPane.showMessageDialog(new JFrame(), "File may contain categorical string values...Please encode to numeric!!!", "Error",
                                                JOptionPane.ERROR_MESSAGE);
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                        JOptionPane.showMessageDialog(new JFrame(), "Data file not found!!!", "Error",
                                                JOptionPane.ERROR_MESSAGE);
                                    } catch (DataFormatException e1) {
                                        e1.printStackTrace();
                                        JOptionPane.showMessageDialog(new JFrame(), "Data format or selected separator might not correct or not the correct file!!!", "Error",
                                                JOptionPane.ERROR_MESSAGE);
                                    }
                                }else {
                                    JOptionPane.showMessageDialog(new JFrame(), "Please provide test data file", "Error",
                                            JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        } catch (IOException e1) {
                            e1.printStackTrace();
                            JOptionPane.showMessageDialog(new JFrame(), "Pre-trained model file not found!!!!", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        } catch (ClassNotFoundException e1) {
                            e1.printStackTrace();
                            JOptionPane.showMessageDialog(new JFrame(), "Unable to load pre-trained model..model might be corrupted!!!!", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }else{
                        JOptionPane.showMessageDialog(new JFrame(), "Please provide pre-trained model path!!!!", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }else {
                    boolean shouldClassify = true;
                    String treeName = "RandomTree";
                    if (forestNames.getSelectedItem().equals("Random Forest")) {
                        forestName = "RandomForest";
                        treeName = "RandomTree";
                    } else if (forestNames.getSelectedItem().equals("Refined Forest")) {
                        forestName = "RefinedForest";
                        if(refinableForestsNames.getSelectedItem().toString().equals("Random Forest")){
                            treeName = "RandomTree";
                        }else if(refinableForestsNames.getSelectedItem().toString().equals("Extra Tree Forest")){
                            treeName = "ExtraTree";
                        }if(refinableForestsNames.getSelectedItem().toString().equals("Perfect Random Tree Ensemble")){
                            treeName = "PerfectRandomTree";
                        }

                    } else if (forestNames.getSelectedItem().equals("Extra Tree Forest")) {
                        forestName = "RandomForest";
                        treeName = "ExtraTree";
                    } else if (forestNames.getSelectedItem().equals("Perfect Random Tree Ensemble")) {
                        forestName = "RandomForest";
                        treeName = "PerfectRandomTree";
                    } else if (forestNames.getSelectedItem().equals("Weighted Forest")) {
                        forestName = "WeightedForest";
                        treeName = "RandomTree";
                    } else if (forestNames.getSelectedItem().equals("Improved Random Forest")) {
                        forestName = "RandomForest";
                        treeName = "RandomTree";
                    }

                    int minLeafSize = 5;
                    double minInfoGain = 0;
                    int noOfTrees = 50;
                    int noOfRandomFeatures = (int) Math.round(Math.sqrt(features.length));
                    int noOfFolds = 5;
                    int maxDepth = 10;
                    if (!minLeafSizeTextField.getText().trim().equals("")) {
                        try {
                            minLeafSize = Integer.parseInt(minLeafSizeTextField.getText());
                            if(minLeafSize <= 0){
                                JOptionPane.showMessageDialog(new JFrame(), "Minimum leaf size should be greater than zero!!!!", "Error",
                                        JOptionPane.ERROR_MESSAGE);
                                shouldClassify = false;
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(new JFrame(), "Minimum leaf size should be integer!!!!", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            shouldClassify = false;
                        }
                    }
                    if (!minInfoGainTextField.getText().trim().equals("")) {
                        try {
                            minInfoGain = Double.parseDouble(minInfoGainTextField.getText());
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(new JFrame(), "Minimum info gain size should be numeric!!!", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            shouldClassify = false;
                        }
                    }
                    if (!noOfTreesTextField.getText().trim().equals("")) {
                        try {
                            noOfTrees = Integer.parseInt(noOfTreesTextField.getText());
                            if(noOfTrees <= 0){
                                JOptionPane.showMessageDialog(new JFrame(), "No of trees should be greater than zero!!!!", "Error",
                                        JOptionPane.ERROR_MESSAGE);
                                shouldClassify = false;
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(new JFrame(), " No of trees should be integer!!!!", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            shouldClassify = false;
                        }
                    }
                    if (!noOfRandomFeaturesTextField.getText().trim().equals("")) {
                        try {
                            noOfRandomFeatures = Integer.parseInt(noOfRandomFeaturesTextField.getText());
                            if(noOfRandomFeatures > features.length){
                                JOptionPane.showMessageDialog(new JFrame(), "Random selected features should be less than total no of features: " + features.length + "!!!!", "Error",
                                        JOptionPane.ERROR_MESSAGE);
                                shouldClassify = false;
                            }else if(noOfRandomFeatures <= 0){
                                JOptionPane.showMessageDialog(new JFrame(), "No of random feature should be greater than zero!!!!", "Error",
                                        JOptionPane.ERROR_MESSAGE);
                                shouldClassify = false;
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(new JFrame(), "No of random features should be integer!!!!", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            shouldClassify = false;
                        }
                    }

                    if (!maxDepthTextField.getText().trim().equals("")) {
                        try {
                            maxDepth = Integer.parseInt(maxDepthTextField.getText());
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(new JFrame(), "Max depth should be integer!!!!", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            shouldClassify = false;
                        }
                    }

                    ForestSettings forestSettings = new ForestSettings(noOfRandomFeatures, forestName, treeName);
                    forestSettings.setNoOfTrees(noOfTrees);
                    forestSettings.setMinLeafSize(minLeafSize);
                    forestSettings.setUseBootstrapSample(true);
                    forestSettings.setMinInfoGain(minInfoGain);
                    if (maxDepth > 0) {
                        forestSettings.setMaxTreeDepth(maxDepth);
                    } else if (maxDepth <= 0) {
                        JOptionPane.showMessageDialog(new JFrame(), "Tree depth must be greater than zero!!!!", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        shouldClassify = false;
                    }
                    ArrayList<Double> predicted;
                    if (shouldClassify) {
                        if (evaluationOptionNames.getSelectedItem().equals("Cross Validation")) {
                            if (!noOfFoldsTextField.getText().trim().equals("")) {
                                try {
                                    noOfFolds = Integer.parseInt(noOfFoldsTextField.getText());
                                } catch (Exception ex) {
                                    JOptionPane.showMessageDialog(new JFrame(), "Number of folds should be integer!!!!", "Error",
                                            JOptionPane.ERROR_MESSAGE);
                                }
                            }
                            if(noOfFolds > 2){
                                long start = System.currentTimeMillis();
                                if(forestNames.getSelectedItem().equals("Improved Random Forest")){
                                    int k = Math.round(features.length * (3/6));
                                    ArrayList<Integer> selectedFeatureIndices  = new GreedySelection().getSelectedFeatures("ReliefF",
                                            features, labels, k);
                                    ArrayList<Double>[] selectedTrainFeatures = Utility.getSelectedFeatures(features, selectedFeatureIndices);
                                    predicted = new CrossValidator(noOfFolds).validate(selectedTrainFeatures, labels, forestSettings);
                                }else{
                                    predicted = new CrossValidator(noOfFolds).validate(features, labels, forestSettings);
                                }
                                long end = System.currentTimeMillis();
                                long duration = (end-start);
                                double durationInSec = (duration*1.0)/1000;
                                populateResultArea(labels, predicted, durationInSec);
                            }else {
                                JOptionPane.showMessageDialog(new JFrame(), "Number of folds must be greater than 2!!!!", "Error",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        }else if (evaluationOptionNames.getSelectedItem().equals("Using Train Set")) {
                            long start = System.currentTimeMillis();
                            classifierModel = new Evaluator().evaluateAndGetModel(features, labels, features, forestSettings);
                            predicted = classifierModel.getPredictedLabels();
                            long end = System.currentTimeMillis();
                            long duration = (end-start);
                            double durationInSec = (duration*1.0)/1000;
                            populateResultArea(labels, predicted, durationInSec);
                            saveClassifierButton.setEnabled(true);
                        }else if (evaluationOptionNames.getSelectedItem().equals("Using Test Set")) {
                            if(testSetInputFileChooser.getSelectedFile() != null){
                                String testDataFilePath =  testSetInputFileChooser.getSelectedFile().getAbsolutePath();
                                String separator = (String) separatorOptions.getSelectedItem();
                                String labelIndex = (String) labelIndexOption.getSelectedItem();
                                boolean hasHeader = hasHeaderCheckBox.isSelected();
                                boolean hasId = hasIdCheckBox.isSelected();
                                boolean hasLabel = hasLabelCheckBox.isSelected();

                                CSVReader csvReader = new CSVReader();
                                try {
                                    DataSet testSet = csvReader.loadTestData(testDataFilePath, separator, hasHeader,labelIndex, hasLabel,
                                            hasId, features.length, originalClassValues);
                                    ArrayList<Double>[] testFeatures = testSet.getFeatures();
                                    ArrayList<Double> testLabels = testSet.getLabels();
                                    ArrayList<String> ids = testSet.getIds();

                                    if(hasLabel && testLabels != null){
                                        long start = System.currentTimeMillis();
                                        classifierModel = new Evaluator().evaluateAndGetModel(features, labels, testFeatures, forestSettings);
                                        predicted = classifierModel.getPredictedLabels();
                                        long end = System.currentTimeMillis();
                                        long duration = (end-start);
                                        double durationInSec = (duration*1.0)/1000;
                                        populateResultArea(testLabels, predicted, durationInSec);
                                    }else {
                                        classifierModel = new Evaluator().evaluateAndGetModel(features, labels, testFeatures, forestSettings);
                                        predicted = classifierModel.getPredictedLabels();
                                    }
                                    if(testSetResultFileChooser.getSelectedFile() != null){
                                        FileWriter fw;
                                        try{
                                            fw = new FileWriter(testSetResultFileChooser.getSelectedFile().getAbsolutePath());
                                            if(hasId && predicted.size() == ids.size()){
                                                for (int i = 0; i < predicted.size(); i++) {
                                                    double p = predicted.get(i);
                                                    fw.write(ids.get(i) + "," + originalClassValues.get((int)p) + "\n");
                                                }
                                            }else {
                                                for (int i = 0; i < predicted.size(); i++) {
                                                    double p = predicted.get(i);
                                                    fw.write(originalClassValues.get((int)p) + "\n");
                                                }
                                            }
                                            fw.close();
                                        }catch (IOException ioe){
                                            JOptionPane.showMessageDialog(new JFrame(), "OutputFile not find..result was not saved!!", "Error",
                                                    JOptionPane.ERROR_MESSAGE);
                                        }
                                    }
                                    saveClassifierButton.setEnabled(true);

                                } catch (NumberFormatException e1) {
                                    e1.printStackTrace();
                                    JOptionPane.showMessageDialog(new JFrame(), "File may contain categorical string values...Please encode to numeric!!!", "Error",
                                            JOptionPane.ERROR_MESSAGE);
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                    JOptionPane.showMessageDialog(new JFrame(), "Data file not found!!!", "Error",
                                            JOptionPane.ERROR_MESSAGE);
                                } catch (DataFormatException e1) {
                                    e1.printStackTrace();
                                    JOptionPane.showMessageDialog(new JFrame(), "Data format is not correct or not the correct file!!!", "Error",
                                            JOptionPane.ERROR_MESSAGE);
                                }
                            }else {
                                JOptionPane.showMessageDialog(new JFrame(), "Please test data file", "Error",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        }else if (evaluationOptionNames.getSelectedItem().equals("Holdout Set")) {
                            double trainSetPercentage = 66.33;
                            boolean shouldHoldoutValidate = true;
                            if(!trainSetPercentageTextField.getText().equals("")){
                                try {
                                    trainSetPercentage = Double.parseDouble(trainSetPercentageTextField.getText());
                                    if(trainSetPercentage == 100.0){
                                        JOptionPane.showMessageDialog(new JFrame(), "Train set percentage should should less than 100", "Error",
                                                JOptionPane.ERROR_MESSAGE);
                                        shouldHoldoutValidate = false;
                                    }else if(trainSetPercentage == 0){
                                        JOptionPane.showMessageDialog(new JFrame(), "Train set percentage should should be greater than 0", "Error",
                                                JOptionPane.ERROR_MESSAGE);
                                        shouldHoldoutValidate = false;
                                    }else if(trainSetPercentage < 0 || trainSetPercentage > 100 ){
                                        JOptionPane.showMessageDialog(new JFrame(), "Train set percentage should between 0 to 100", "Error",
                                                JOptionPane.ERROR_MESSAGE);
                                        shouldHoldoutValidate = false;
                                    }
                                } catch (Exception ex) {
                                    JOptionPane.showMessageDialog(new JFrame(), "Set percentage is not valid!!!!", "Error",
                                            JOptionPane.ERROR_MESSAGE);
                                    shouldHoldoutValidate = false;
                                }
                            }

                            if(shouldHoldoutValidate){
                                int noOfInstances = labels.size();
                                int noOfTrainInstances = (int)((trainSetPercentage/100)*noOfInstances);
                                if(noOfTrainInstances >= noOfInstances){
                                    JOptionPane.showMessageDialog(new JFrame(), "No test data available", "Error",
                                            JOptionPane.ERROR_MESSAGE);
                                }else {
                                    long start = System.currentTimeMillis();
                                    ArrayList<Double>[] results = new HoldoutValidator().validate(features, labels,
                                            noOfTrainInstances, forestSettings);
                                    long end = System.currentTimeMillis();
                                    long duration = (end-start);
                                    double durationInSec = (duration*1.0)/1000;
                                    populateResultArea(results[0], results[1], durationInSec);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    private void populateResultArea(ArrayList<Double> actual, ArrayList<Double> predicted, double duration){
        resultTextArea.setText("");
        resultTextArea.append("\n  "+fileName);
        resultTextArea.append("\n\n  Running time : " + duration + "sec");
        double accuracy = EvaluationMeasure.getAccuracy(actual, predicted);

        String result = "";
        result = "\n\n  Accuracy :  " +accuracy + "\n\n";
        resultTextArea.append(result);
        HashSet<Double> hashSet = new HashSet<Double>();
        HashMap<Double, Double> hashMap = new HashMap<Double, Double>();
        for (int i = 0; i < actual.size(); i++) {
            hashSet.add(actual.get(i));
            if(hashMap.get(actual.get(i)) == null){
                hashMap.put(actual.get(i), 1.0);
            }else {
                hashMap.put(actual.get(i), hashMap.get(actual.get(i))+1.0);
            }
        }
        for (Map.Entry<Double, Double> entry :hashMap.entrySet())
        {
            double p = entry.getValue() / actual.size();
            hashMap.put(entry.getKey(), p);
        }
        classes = new ArrayList<Double>(hashSet);
        Collections.sort(classes);

        int[][] confusionMatrix = EvaluationMeasure.getConfusionMatrix(actual, predicted);
        double sumOfPrecisions = 0;
        double sumOfRecalls = 0;
        double sumOfFMeasures = 0;
        result = "  Class  |    Precision  |    Recall       |     F-Measure \n";
        resultTextArea.append(result);
        resultTextArea.append("  ---------------------------------------------------------------\n");
        NumberFormat formatter = new DecimalFormat("#0.00000");
        for (int i = 0; i < classes.size(); i++) {
            double classLabel = classes.get(i);
            double precision = EvaluationMeasure.getPrecision(confusionMatrix, classLabel);
            double recall = EvaluationMeasure.getRecall(confusionMatrix, classLabel);
            double fMeasure = EvaluationMeasure.getFMeasure(precision, recall);

            System.out.println(hashMap.get(classLabel));
            sumOfPrecisions += precision * hashMap.get(classLabel);
            sumOfRecalls += recall * hashMap.get(classLabel);
            sumOfFMeasures += fMeasure * hashMap.get(classLabel);
            result = "    " + (int)classLabel + "       |    " + formatter.format(precision) + "    |    " + formatter.format(recall)
                    + "    |    " + formatter.format(fMeasure)+ "\n";
            resultTextArea.append(result);
            resultTextArea.append("  ---------------------------------------------------------------\n");
        }

        result = "  Avg       |     " + formatter.format(sumOfPrecisions) + "   |    " +
                formatter.format(sumOfRecalls) + "    |    " + formatter.format(sumOfFMeasures) + "\n\n";
        resultTextArea.append(result);

        resultTextArea.append("  Confusion matrix : \n\n");

        result = "  Classified As" + " ->  ";
        for (int i = 0; i < originalClassValues.size(); i++) {
            result += originalClassValues.get(i);
            int noOfClassifiedAsDigits = countDigit(originalClassValues.get(i));
            for (int k = 0; k < 10 - noOfClassifiedAsDigits*2; k++) {
                result += " ";
            }
        }
        resultTextArea.append(result+"\n");
        result = "";
        for (int i = 0; i < originalClassValues.size() * 10 + 10; i++) {
            result += "-";
        }
        resultTextArea.append(result+"\n");
        for (int i = 0; i < confusionMatrix.length; i++) {
            int noOfClassDigits = countDigit(originalClassValues.get(i));
            result = "  " + originalClassValues.get(i) + " ->";
            for (int j = 0; j < 25 - noOfClassDigits; j++) {
                result += " ";
            }

            for (int j = 0; j < confusionMatrix[0].length; j++) {
                result += confusionMatrix[i][j];
                int noOfDigits = countDigit(Integer.toString(confusionMatrix[i][j]));
                for (int k = 0; k < 10 - noOfDigits*2; k++) {
                    result += " ";
                }
            }
            result += "\n";
            resultTextArea.append(result);
        }
    }

    private int countDigit(String value){
        String[] digits = value.split("");

        return digits.length;
    }

    /* This is a JPanel used for getting parameter of input file */


    JComboBox fileFormatOptions;
    JCheckBox hasHeaderCheckBox;
    JComboBox labelIndexOption;
    JComboBox separatorOptions;
    JButton loadButton;
    // Input File chooser
    final JFileChooser inputFileSelectionFileChooser = new JFileChooser();
    final DefaultComboBoxModel fileTypeNames = new DefaultComboBoxModel();
    private JPanel getFileInputPanel(){

        // Option For file type for e.g. csv or arff format

        fileTypeNames.addElement("CSV");
        fileTypeNames.addElement("ARFF");
        fileFormatOptions = new JComboBox(fileTypeNames);
        fileFormatOptions.setSelectedIndex(0);

        // does the file have header ??
        hasHeaderCheckBox = new JCheckBox();

        // File column separator
        DefaultComboBoxModel separatorNames = new DefaultComboBoxModel();
        separatorNames.addElement(",");
        separatorNames.addElement(";");
        separatorNames.addElement("\\t");
        separatorNames.addElement(" ");
        separatorNames.addElement("-");
        separatorNames.addElement("_");
        separatorOptions = new JComboBox(separatorNames);
        separatorOptions.setSelectedIndex(0);

        // File column separator
        DefaultComboBoxModel indicesNames = new DefaultComboBoxModel();
        indicesNames.addElement("first");
        indicesNames.addElement("last");
        labelIndexOption = new JComboBox(indicesNames);
        labelIndexOption.setSelectedIndex(1);

        // File loader button
        loadButton = new JButton("Load");
        final JButton inputFilePathSelectionButton = new JButton("Browse...");
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(inputFileSelectionFileChooser.getSelectedFile() != null){
                    String filePath =  inputFileSelectionFileChooser.getSelectedFile().getAbsolutePath();//inputFilePathSelectionButton.getText();
                    String separator = (String) separatorOptions.getSelectedItem();
                    String labelIndex = (String) labelIndexOption.getSelectedItem();
                    boolean hasHeader = hasHeaderCheckBox.isSelected();
                    File file = new File(filePath);
                    fileName = file.getName();
                    String fileType = fileTypeNames.getSelectedItem().toString();

                    DataReader dataReader = new DataReader();
                    try {
                        DataSet trainSet = dataReader.readData(filePath, separator, hasHeader, labelIndex, fileType);
                        attributeNames = trainSet.getFeatureNames();
                        labelStringName = trainSet.getLabelName();
                        features = trainSet.getFeatures();

                        labels = trainSet.getLabels();
                        originalClassValues = trainSet.getClassValues();
                        populateFeatureList();
                    } catch (NumberFormatException e1) {
                        e1.printStackTrace();
                        JOptionPane.showMessageDialog(new JFrame(), "File may contain categorical string values...Please encode to numeric!!!", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                        JOptionPane.showMessageDialog(new JFrame(), "Data file not found!!!", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    } catch (DataFormatException e1) {
                        e1.printStackTrace();
                        JOptionPane.showMessageDialog(new JFrame(), "Data format is not correct or not the correct file!!!", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }else {
                    JOptionPane.showMessageDialog(new JFrame(), "Please select file!!!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        fileFormatOptions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox combo = (JComboBox)e.getSource();
                String fileType = (String)combo.getSelectedItem();
                if(fileType.equals("ARFF")){
                    hasHeaderCheckBox.setEnabled(false);
                    labelIndexOption.setEnabled(false);
                    separatorOptions.setEnabled(false);
                }else {
                    hasHeaderCheckBox.setEnabled(true);
                    labelIndexOption.setEnabled(true);
                    separatorOptions.setEnabled(true);
                }
            }
        });
        inputFilePathSelectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = inputFileSelectionFileChooser.showOpenDialog(new JFrame());
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    inputFilePathSelectionButton.setText(inputFileSelectionFileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        });

        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(325, 205));
        panel.setBorder(createBorder("Open File"));
        GridLayout layout = new GridLayout(0, 2);
        layout.setHgap(5);
        layout.setVgap(5);
        panel.setLayout(layout);
        panel.add(new JLabel("File Path : "));
        panel.add(inputFilePathSelectionButton);
        panel.add(new JLabel("File Type : "));
        panel.add(fileFormatOptions);
        panel.add(new JLabel("Has Header : "));
        panel.add(hasHeaderCheckBox);
        panel.add(new JLabel("Separator : "));
        panel.add(separatorOptions);
        panel.add(new JLabel("Label Index : "));
        panel.add(labelIndexOption);
        panel.add(loadButton);

        return panel;
    }

    private void populateFeatureList() {
        featureNames.removeAllElements();
        for (int i = 0; i < attributeNames.size(); i++) {
            featureNames.addElement(attributeNames.get(i));
        }
        featureNames.addElement(labelStringName);
    }


    /*  This is the panel for showing feature list
        All the features are shown in a list.If user wants he/she can delete a particular
        feature before classification using the delete button
     */
    DefaultListModel featureNames;
    JList featureList;
    JButton featureRemoveButton;

    private JPanel getFeaturesPanel(){

        featureNames = new DefaultListModel();

        featureList = new JList(featureNames);
        featureList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        featureList.setSelectedIndex(0);
        featureList.setVisibleRowCount(3);

        JScrollPane fruitListScrollPane = new JScrollPane(featureList);
        fruitListScrollPane.setPreferredSize(new Dimension(300, 300));
        featureRemoveButton = new JButton("Remove");
        featureRemoveButton.setSize(325, 10);

        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(325, 370));
        panel.setBorder(createBorder("Feature List"));
        FlowLayout layout = new FlowLayout();
        panel.setLayout(layout);
        panel.add(fruitListScrollPane);
        panel.add(featureRemoveButton);

        featureRemoveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(featureNames != null && featureNames.size() > 0){
                    int noOfItems = featureNames.size();
                    int selectedIndex = featureList.getSelectedIndex();
                    if(selectedIndex >= 0 && selectedIndex != noOfItems-1){
                        featureNames.remove(selectedIndex);
                        features = Utility.removeFeature(features, selectedIndex);
                        attributeNames.remove(selectedIndex);
                    }
                }else {
                    JOptionPane.showMessageDialog(new JFrame(), "No features to remove...Please load data!!!!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;
    }

    private JPanel getEncoderPanel(){
        // File column separator
        final DefaultComboBoxModel encodingNames = new DefaultComboBoxModel();
        encodingNames.addElement("Normal Encoding");
        encodingNames.addElement("One Hot Encoding");
        encodingNames.addElement("Binary Encoding");
        JComboBox encoders = new JComboBox(encodingNames);
        encoders.setSelectedIndex(0);

        // File loader button
        JButton saveButton = new JButton("Save");

        // Input File chooser
        final JFileChooser  saveFileChooser = new JFileChooser();
        final JButton showFileDialogButton = new JButton("Browse...");

        showFileDialogButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = saveFileChooser.showOpenDialog(new JFrame());
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    showFileDialogButton.setText(saveFileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        });

        final JTextField categoricalIndicesTextField = new JTextField();

        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(325, 150));
        panel.setBorder(createBorder("Categorical Feature Encoding"));
        GridLayout layout = new GridLayout(0, 2);
        layout.setHgap(5);
        layout.setVgap(5);
        panel.setLayout(layout);
        panel.add(new JLabel("Output File Path : "));
        panel.add(showFileDialogButton);
        panel.add(new JLabel("Select Encoder : "));
        panel.add(encoders);
        panel.add(new JLabel("Feature Indices : "));
        panel.add(categoricalIndicesTextField);
        panel.add(saveButton);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(inputFileSelectionFileChooser.getSelectedFile() == null){
                    JOptionPane.showMessageDialog(new JFrame(), "Please select input file!!!!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }else if(saveFileChooser.getSelectedFile() == null){
                    JOptionPane.showMessageDialog(new JFrame(), "Please select output file!!!!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }else {

                    String outputFile = saveFileChooser.getSelectedFile().getAbsolutePath();
                    String inputFile = inputFileSelectionFileChooser.getSelectedFile().getAbsolutePath();
                    String stringIndices = categoricalIndicesTextField.getText();
                    String encodingType = (String) encodingNames.getSelectedItem();

                    String separator = ",";
                    String labelIndex = "last";
                    boolean hasHeader = true;

                    String fileType = fileTypeNames.getSelectedItem().toString();
                    if(fileType.equals("CSV")){
                        separator = (String) separatorOptions.getSelectedItem();
                        labelIndex = (String) labelIndexOption.getSelectedItem();
                        hasHeader = hasHeaderCheckBox.isSelected();
                    }

                    try {
                        new Encoder().encode(inputFile, outputFile, labelIndex, separator, hasHeader, stringIndices, encodingType, fileType);
                    } catch (OutputFileNotFoundException e1) {
                        JOptionPane.showMessageDialog(new JFrame(), "Output file not found!!!!", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        e1.printStackTrace();
                    } catch (InputFileNotFoundException e1) {
                        JOptionPane.showMessageDialog(new JFrame(), "Output file not found!!!!", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        e1.printStackTrace();
                    } catch (StringParseException e1) {
                        JOptionPane.showMessageDialog(new JFrame(), "Please enter valid integer as index separated by comma!!!!", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        e1.printStackTrace();
                    }
                }

            }
        });

        return panel;
    }

    private Border createBorder(String title) {
        return BorderFactory.createTitledBorder(BorderFactory.createMatteBorder(2, 2,
                2, 2, Color.lightGray), title, TitledBorder.DEFAULT_POSITION,TitledBorder.DEFAULT_POSITION,new Font("font name",Font.BOLD,16));
    }

    private JPanel getFilterPanel(){

        // File column separator
        final DefaultComboBoxModel filterNames = new DefaultComboBoxModel();
        filterNames.addElement("MRMR Selection");
        filterNames.addElement("RELIEF-F Selection");
        filterNames.addElement("MultiInterval Discretization");
        final JComboBox filters = new JComboBox(filterNames);
        filters.setSelectedIndex(0);


        // File loader button
        JButton filterApplyButton = new JButton("Apply");

        // Input File chooser
        final JFileChooser  filteredFeaturesSavingFileChooser = new JFileChooser();
        final JButton showFileDialogButton = new JButton("Browse...");

        showFileDialogButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = filteredFeaturesSavingFileChooser.showOpenDialog(new JFrame());
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    showFileDialogButton.setText(filteredFeaturesSavingFileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        });


        final JTextField noOfFeatureTextField = new JTextField();
        filters.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox combo = (JComboBox)e.getSource();
                String filterType = (String)combo.getSelectedItem();
                if(filterType.equals("MultiInterval Discretization")){
                    noOfFeatureTextField.setEnabled(false);
                }else {
                    noOfFeatureTextField.setEnabled(true);
                }
            }
        });

        filterApplyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filterType = (String) filters.getSelectedItem();
                boolean filterSuccessful = false;
                if(filterType.equals("MultiInterval Discretization")){
                    features = new MultiIntervalDiscretizer().getSelectedFeatures(features, labels);
                    filterSuccessful = true;
                }else{
                    if(features == null){
                        JOptionPane.showMessageDialog(new JFrame(), "Please load data!!!!", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }else if(features.length < 2){
                        JOptionPane.showMessageDialog(new JFrame(), "Not enough features for selection!!!!", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }else if(features[0].size() != labels.size() || labels.size() == 0){
                        JOptionPane.showMessageDialog(new JFrame(), "Data is not available for feature selection", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }else if(noOfFeatureTextField.getText().trim().equals("")){
                        JOptionPane.showMessageDialog(new JFrame(), "Please enter no of features to select...", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }else {
                        try{
                            int noOfFeaturesToSelect = Integer.parseInt(noOfFeatureTextField.getText().trim());
                            if(noOfFeaturesToSelect > features.length){
                                JOptionPane.showMessageDialog(new JFrame(), "No of selected features is greater than original feature!!!", "Error",
                                        JOptionPane.ERROR_MESSAGE);
                            }else {
                                if(filterType.equals("MRMR Selection")){
                                    ArrayList<Integer> selectedFeatureIndices = new MRMR().getSelectedFeatures(features, labels, noOfFeaturesToSelect);
                                    features = Utility.getSelectedFeatures(features, selectedFeatureIndices);
                                    attributeNames = Utility.getAttributeNames(attributeNames, selectedFeatureIndices);
                                    populateFeatureList();
                                    filterSuccessful = true;
                                }else if(filterType.equals("RELIEF-F Selection")){
                                    ArrayList<Integer> selectedFeatureIndices  = new GreedySelection().getSelectedFeatures("ReliefF", features, labels,
                                            noOfFeaturesToSelect);
                                    features = Utility.getSelectedFeatures(features, selectedFeatureIndices);
                                    attributeNames = Utility.getAttributeNames(attributeNames, selectedFeatureIndices);
                                    populateFeatureList();
                                    filterSuccessful = true;
                                }
                            }
                        }catch (Exception ex){
                            JOptionPane.showMessageDialog(new JFrame(), "Given no of feature is not a valid integer!!!!", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
                if(filterSuccessful && filteredFeaturesSavingFileChooser.getSelectedFile() != null){
                    String fileName = filteredFeaturesSavingFileChooser.getSelectedFile().getAbsolutePath();
                    String separator = (String) separatorOptions.getSelectedItem();
                    String labelIndex = (String) labelIndexOption.getSelectedItem();
                    boolean hasHeader = hasHeaderCheckBox.isSelected();
                    try {
                        new DataSaver().saveData(fileName, features, labels, attributeNames, labelStringName,
                                hasHeader, separator, labelIndex, originalClassValues);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                        JOptionPane.showMessageDialog(new JFrame(), "Unable to find the file!!!!", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    } catch (DataFormatException e1) {
                        e1.printStackTrace();
                        JOptionPane.showMessageDialog(new JFrame(), "Data format is not correct!!!!", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }

            }
        });

        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(325, 150));
        //panel.setBorder(BorderFactory.createTitledBorder("Open File"));
        panel.setBorder(createBorder("Feature Selection & Discretization"));
        GridLayout layout = new GridLayout(0, 2);
        layout.setHgap(5);
        layout.setVgap(5);
        panel.setLayout(layout);
        panel.add(new JLabel("Select Option : "));
        panel.add(filters);
        panel.add(new JLabel("Output File Path : "));
        panel.add(showFileDialogButton);
        panel.add(new JLabel("No of Features: "));
        panel.add(noOfFeatureTextField);
        panel.add(filterApplyButton);

        return panel;
    }


    DefaultComboBoxModel refinableForestsNames = new DefaultComboBoxModel();
    final DefaultComboBoxModel forestNames = new DefaultComboBoxModel();
    final JCheckBox providePreTrainedModelCheckBox = new JCheckBox();
    final JTextField noOfRandomFeaturesTextField = new JTextField();
    final JTextField minLeafSizeTextField = new JTextField();
    final JTextField minInfoGainTextField = new JTextField();
    final JTextField noOfTreesTextField = new JTextField();
    final JTextField maxDepthTextField = new JTextField();
    final JFileChooser  preTrainedFileChooser = new JFileChooser();
    private JPanel getClassificationPanel(){

        // File column separator

        forestNames.addElement("Random Forest");
        forestNames.addElement("Refined Forest");
        forestNames.addElement("Extra Tree Forest");
        forestNames.addElement("Perfect Random Tree Ensemble");
        forestNames.addElement("Weighted Forest");
        forestNames.addElement("Improved Random Forest");
        final JComboBox forests = new JComboBox(forestNames);
        forests.setSelectedIndex(0);


        refinableForestsNames.addElement("Random Forest");
        refinableForestsNames.addElement("Extra Tree Forest");
        refinableForestsNames.addElement("Perfect Random Tree Ensemble");
        final JComboBox refinableForests = new JComboBox(refinableForestsNames);
        refinableForests.setSelectedIndex(0);
        refinableForests.setEnabled(false);

        // Input File chooser
        final JButton selectModelFileButton = new JButton("Browse..");
        selectModelFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = preTrainedFileChooser .showOpenDialog(new JFrame());
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    selectModelFileButton.setText(preTrainedFileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
        selectModelFileButton.setEnabled(false);


        JPanel preTrainedModelPanel = new JPanel();
        preTrainedModelPanel.setLayout(new FlowLayout());
        preTrainedModelPanel.add(new JLabel("Pre-trained Model : "));
        preTrainedModelPanel.add(providePreTrainedModelCheckBox);


        providePreTrainedModelCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBox checkBox = (JCheckBox)e.getSource();
                if(checkBox.isSelected()){
                    selectModelFileButton.setEnabled(true);
                    noOfRandomFeaturesTextField.setEnabled(false);
                    minLeafSizeTextField.setEnabled(false);
                    minInfoGainTextField.setEnabled(false);
                    noOfTreesTextField.setEnabled(false);
                    maxDepthTextField.setEnabled(false);
                    refinableForests.setEnabled(false);

                    evaluationOptionNames.removeElementAt(0);
                    evaluationOptionNames.removeElementAt(0);
                }else {
                    evaluationOptionNames.insertElementAt("Cross Validation", 0);
                    evaluationOptionNames.insertElementAt("Holdout Set", 1);
                    evaluationOptions.setSelectedIndex(0);
                    selectModelFileButton.setEnabled(false);

                    minLeafSizeTextField.setEnabled(true);

                    noOfTreesTextField.setEnabled(true);
                    maxDepthTextField.setEnabled(true);
                    if(forestNames.getSelectedItem().equals("Refined Forest")){
                        refinableForests.setEnabled(true);
                    }
                    if(!forestNames.getSelectedItem().equals("Perfect Random Tree Ensemble") &&
                            !refinableForestsNames.getSelectedItem().equals("Perfect Random Tree Ensemble")){
                        noOfRandomFeaturesTextField.setEnabled(true);
                        minInfoGainTextField.setEnabled(true);
                    }
                }
            }
        });

        forests.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String forestName = (String) forestNames.getSelectedItem();
                if(!providePreTrainedModelCheckBox.isSelected() && forestName.equals("Random Forest")){
                    noOfRandomFeaturesTextField.setEnabled(true);
                    minLeafSizeTextField.setEnabled(true);
                    minInfoGainTextField.setEnabled(true);
                    noOfTreesTextField.setEnabled(true);
                    maxDepthTextField.setEnabled(true);
                    refinableForests.setEnabled(false);
                }else if(!providePreTrainedModelCheckBox.isSelected() && forestName.equals("Refined Forest")){
                    noOfRandomFeaturesTextField.setEnabled(true);
                    minLeafSizeTextField.setEnabled(true);
                    minInfoGainTextField.setEnabled(true);
                    noOfTreesTextField.setEnabled(true);
                    maxDepthTextField.setEnabled(true);
                    refinableForests.setEnabled(true);
                }else if(!providePreTrainedModelCheckBox.isSelected() && forestName.equals("Extra Tree Forest")){
                    noOfRandomFeaturesTextField.setEnabled(true);
                    minLeafSizeTextField.setEnabled(true);
                    minInfoGainTextField.setEnabled(true);
                    noOfTreesTextField.setEnabled(true);
                    maxDepthTextField.setEnabled(true);
                    refinableForests.setEnabled(false);
                }else if(!providePreTrainedModelCheckBox.isSelected() && forestName.equals("Perfect Random Tree Ensemble")){
                    noOfRandomFeaturesTextField.setEnabled(false);
                    minLeafSizeTextField.setEnabled(true);
                    minInfoGainTextField.setEnabled(false);
                    noOfTreesTextField.setEnabled(true);
                    maxDepthTextField.setEnabled(true);
                    refinableForests.setEnabled(false);
                }else if(!providePreTrainedModelCheckBox.isSelected() && forestName.equals("Weighted Forest")){
                    noOfRandomFeaturesTextField.setEnabled(true);
                    minLeafSizeTextField.setEnabled(true);
                    minInfoGainTextField.setEnabled(true);
                    noOfTreesTextField.setEnabled(true);
                    maxDepthTextField.setEnabled(true);
                    refinableForests.setEnabled(false);
                }else if(!providePreTrainedModelCheckBox.isSelected() && forestName.equals("Improved Random Forest")){
                    noOfRandomFeaturesTextField.setEnabled(true);
                    minLeafSizeTextField.setEnabled(true);
                    minInfoGainTextField.setEnabled(true);
                    noOfTreesTextField.setEnabled(true);
                    maxDepthTextField.setEnabled(true);
                    refinableForests.setEnabled(false);
                }
            }
        });

        refinableForests.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String refineableForestName = (String) refinableForestsNames.getSelectedItem();
                if(!providePreTrainedModelCheckBox.isSelected() && refineableForestName.equals("Random Forest")){
                    noOfRandomFeaturesTextField.setEnabled(true);
                    minLeafSizeTextField.setEnabled(true);
                    minInfoGainTextField.setEnabled(true);
                    noOfTreesTextField.setEnabled(true);
                    maxDepthTextField.setEnabled(true);
                }else if(!providePreTrainedModelCheckBox.isSelected() && refineableForestName.equals("Extra Tree Forest")){
                    noOfRandomFeaturesTextField.setEnabled(true);
                    minLeafSizeTextField.setEnabled(true);
                    minInfoGainTextField.setEnabled(true);
                    noOfTreesTextField.setEnabled(true);
                    maxDepthTextField.setEnabled(true);
                }else if(!providePreTrainedModelCheckBox.isSelected() && refineableForestName.equals("Perfect Random Tree Ensemble")){
                    noOfRandomFeaturesTextField.setEnabled(false);
                    minLeafSizeTextField.setEnabled(true);
                    minInfoGainTextField.setEnabled(false);
                    noOfTreesTextField.setEnabled(true);
                    maxDepthTextField.setEnabled(true);
                }
            }
        });

        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(300, 280));
        panel.setBorder(BorderFactory.createTitledBorder("Classification"));
        GridLayout layout = new GridLayout(0, 2);
        layout.setHgap(5);
        layout.setVgap(5);
        panel.setLayout(layout);
        panel.add(new JLabel("Select Variation : "));
        panel.add(forests);
        panel.add(preTrainedModelPanel);
        panel.add(selectModelFileButton);
        panel.add(new JLabel("Refineable Forest : "));
        panel.add(refinableForests);
        panel.add(new JLabel("No of random Features : "));
        panel.add(noOfRandomFeaturesTextField);
        panel.add(new JLabel("Min leaf Size : "));
        panel.add(minLeafSizeTextField);
        panel.add(new JLabel("No of trees : "));
        panel.add(noOfTreesTextField);
        panel.add(new JLabel("Min information gain: "));
        panel.add(minInfoGainTextField);
        panel.add(new JLabel("Max depth : "));
        panel.add(maxDepthTextField);

        return panel;
    }

    DefaultComboBoxModel evaluationOptionNames = new DefaultComboBoxModel();
    JTextField trainSetPercentageTextField = new JTextField();
    JTextField noOfFoldsTextField = new JTextField();
    final JCheckBox hasLabelCheckBox = new JCheckBox("Has Label");
    final JCheckBox hasIdCheckBox = new JCheckBox("Has Id");

    final JFileChooser testSetResultFileChooser = new JFileChooser();
    final JFileChooser testSetInputFileChooser = new JFileChooser();
    final JFileChooser saveClassifierFileChooser = new JFileChooser();
    JComboBox evaluationOptions;
    private JPanel getEvaluationOption(){
        final JButton testSetInputFileBrowseButton = new JButton("Browse...");
        final JButton testSetResultFileBrowseButton = new JButton("Browse...");
        // File column separator

        evaluationOptionNames.addElement("Cross Validation");
        evaluationOptionNames.addElement("Holdout Set");
        evaluationOptionNames.addElement("Using Train Set");
        evaluationOptionNames.addElement("Using Test Set");

        evaluationOptions = new JComboBox(evaluationOptionNames);
        evaluationOptions.setSelectedIndex(0);
        trainSetPercentageTextField.setEnabled(false);
        testSetInputFileBrowseButton.setEnabled(false);
        hasLabelCheckBox.setEnabled(false);
        hasIdCheckBox.setEnabled(false);
        testSetResultFileBrowseButton.setEnabled(false);

        evaluationOptions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String evaluationName = (String) evaluationOptionNames.getSelectedItem();
                if(evaluationName.equals("Using Train Set")){
                    hasLabelCheckBox.setEnabled(false);
                    hasIdCheckBox.setEnabled(false);
                    testSetResultFileBrowseButton.setEnabled(false);
                    noOfFoldsTextField.setEnabled(false);
                    trainSetPercentageTextField.setEnabled(false);
                    testSetInputFileBrowseButton.setEnabled(false);
                }else if(evaluationName.equals("Cross Validation")){
                    hasLabelCheckBox.setEnabled(false);
                    hasIdCheckBox.setEnabled(false);
                    testSetResultFileBrowseButton.setEnabled(false);
                    noOfFoldsTextField.setEnabled(true);
                    trainSetPercentageTextField.setEnabled(false);
                    testSetInputFileBrowseButton.setEnabled(false);
                }else if(evaluationName.equals("Using Test Set")){
                    hasLabelCheckBox.setEnabled(true);
                    hasIdCheckBox.setEnabled(true);
                    testSetResultFileBrowseButton.setEnabled(true);
                    noOfFoldsTextField.setEnabled(false);
                    trainSetPercentageTextField.setEnabled(false);
                    testSetInputFileBrowseButton.setEnabled(true);
                }else if(evaluationName.equals("Holdout Set")){
                    hasLabelCheckBox.setEnabled(false);
                    hasIdCheckBox.setEnabled(false);
                    testSetResultFileBrowseButton.setEnabled(false);
                    noOfFoldsTextField.setEnabled(false);
                    trainSetPercentageTextField.setEnabled(true);
                    testSetInputFileBrowseButton.setEnabled(false);
                }
            }
        });

        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(300, 200));
        panel.setBorder(BorderFactory.createTitledBorder("Evaluation Option"));
        GridLayout layout = new GridLayout(0, 2);
        layout.setHgap(5);
        layout.setVgap(5);
        panel.setLayout(layout);
        panel.add(new JLabel("Selection Option : "));
        panel.add(evaluationOptions);
        panel.add(new JLabel("No of Folds : "));
        panel.add(noOfFoldsTextField);
        panel.add(new JLabel("Train set (%) : "));
        panel.add(trainSetPercentageTextField);

        // Input File chooser
        testSetInputFileBrowseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = testSetInputFileChooser.showOpenDialog(new JFrame("Save Result File"));
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    testSetInputFileBrowseButton.setText(testSetInputFileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        });

        // Input File chooser
        saveClassifierFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        saveClassifierButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = saveClassifierFileChooser.showOpenDialog(new JFrame("Save trained classifier"));
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    System.out.println();
                    String filePath = saveClassifierFileChooser.getSelectedFile().getPath() + "\\" + forestName+".model";
                    if(classifierModel != null){
                        classifierModel.setPredictedLabels(null);
                        try {
                            ObjectSerializer.serialize(classifierModel, filePath);
                        } catch (IOException e1) {
                            JOptionPane.showMessageDialog(new JFrame(), "Unable to save classifier model!!!!", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });
        // Output File chooser
        testSetResultFileBrowseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = testSetResultFileChooser.showOpenDialog(new JFrame());
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    testSetResultFileBrowseButton.setText(testSetResultFileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
        panel.add(new JLabel("Open test data file : "));
        panel.add(testSetInputFileBrowseButton);
        panel.add(hasLabelCheckBox);
        panel.add(hasIdCheckBox);
        panel.add(new JLabel("Save test result : "));
        panel.add(testSetResultFileBrowseButton);

        return panel;
    }

    private JPanel getResultPanel(){
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(360, 510));
        panel.setBorder(BorderFactory.createTitledBorder("Result"));

        resultTextArea = new JTextArea(29, 30);
        //resultTextArea.setPreferredSize(3new Dimension(340, 470));

        JScrollPane scrollPane = new JScrollPane(resultTextArea);
        panel.add(scrollPane);

        return panel;
    }
}
