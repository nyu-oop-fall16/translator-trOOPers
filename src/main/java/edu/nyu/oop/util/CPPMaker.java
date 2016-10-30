package edu.nyu.oop.util;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

import xtc.tree.GNode;

import org.slf4j.Logger;

public class CPPMaker {
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(JavaFiveImportParser.class);


    // given an ast, generate the main.cpp and output.cpp files
    public static void printToCpp(GNode n){
        File output = loadOutputCpp();
        try{
            PrintWriter outputWriter = new PrintWriter(output);
        }
        catch(FileNotFoundException e){
            logger.warn("Invalid path for file " + output);
        }

        File main = loadMainCpp();
        try{
            PrintWriter outputWriter = new PrintWriter(main);
        }
        catch(FileNotFoundException e){
            logger.warn("Invalid path for file " + main);
        }

        System.out.println(output);
        System.out.println(main);
    }


    private static File loadOutputCpp() {
        File outputCPP = new File(XtcProps.get("output.location") + "/output.cpp");
        return outputCPP;
    }

    private static File loadMainCpp() {
        File mainCPP = new File(XtcProps.get("output.location") + "/main.cpp");
        return mainCPP;
    }

}
