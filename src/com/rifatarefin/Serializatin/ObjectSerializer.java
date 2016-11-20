package com.rifatarefin.Serializatin;

import java.io.*;

/**
 * Created by ROBIN on 10/21/2016.
 */
public class ObjectSerializer {
    public static void serialize(ClassifierModel classifierModel, String fileName) throws IOException{
        System.out.println(fileName);
        FileOutputStream fileOut = new FileOutputStream(fileName);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(classifierModel);
        out.close();
        fileOut.close();
    }

    public static ClassifierModel deserialize(String fileName) throws IOException, ClassNotFoundException {
        ClassifierModel object = null;
            FileInputStream fileIn = new FileInputStream(fileName);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            object =  (ClassifierModel)in.readObject();
            in.close();
            fileIn.close();

        return object;
    }
}
