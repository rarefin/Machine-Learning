package com.rifatarefin.Datamanager;

import com.rifatarefin.Data.DataSet;
import com.rifatarefin.Exception.DataFormatException;

import java.io.IOException;

/**
 * Created by ROBIN on 11/20/2016.
 */
public class DataReader {
    public DataSet readData(String fileName, String separator, boolean hasHeader, String classIndexName, String fileType) throws NumberFormatException, IOException, DataFormatException{
        DataSet dataSet = null;
        if(fileType.equals("CSV")){
            dataSet = new CSVReader().readCSV(fileName, separator, hasHeader, classIndexName);
        }else if(fileType.equals("ARFF")){
            dataSet = new ARFFReader().readArff(fileName);
        }

        return dataSet;
    }
}
