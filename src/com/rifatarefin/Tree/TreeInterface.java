package com.rifatarefin.Tree;

import com.rifatarefin.CrossValidation.ForestSettings;

import java.util.ArrayList;

/**
 * Created by ROBIN on 10/23/2016.
 */
public interface TreeInterface {
    public void createTree(Node root, ArrayList<Double>[] features, ArrayList<Double> labels, ForestSettings forestSettings, int noOfClasses);

    int getLeafIndex();
}