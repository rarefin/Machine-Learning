package com.rifatarefin.Datamanager;

import java.util.ArrayList;

/**
 * Created by ROBIN on 8/25/2016.
 */
public class DataViewer {
    public void viewHead(ArrayList<Double>[] instances){
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < instances.length; j++) {
                System.out.print(instances[j].get(i)+",");
            }
            System.out.println();
        }
    }
}
