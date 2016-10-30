package edu.nyu.oop.util;

import java.io.*;
import xtc.tree.GNode;

import org.slf4j.Logger;

public class CPPMaker {
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(JavaFiveImportParser.class);


    // given an ast, generate the main.cpp and output.cpp files
    public static void printToCpp(GNode n){
        String absPath = "";
        File output = loadOutputCppFile(absPath);
        try{
            PrintWriter writer = new PrintWriter(output);
        }
        catch(FileNotFoundException e){
            logger.warn("Invalid path for file " + absPath);
        }
    }


    private static File loadOutputCppFile(String path){
        File outputCpp = new File(path);
        return outputCpp;
    }


}
