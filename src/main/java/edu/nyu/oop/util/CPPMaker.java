package edu.nyu.oop.util;

import java.io.*;

import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;

import org.slf4j.Logger;

public class CPPMaker {
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(JavaFiveImportParser.class);

    public static void printToCpp(GNode n) {
        new Visitor() {
            public void visitPackageDeclaration(GNode n){
                printToOutputCpp();
            }

            // given an ast, generate the main.cpp and output.cpp files
            public void printToOutputCpp(GNode n) {
                // Create a printwriter for output.cpp file
                File output = loadOutputCpp();
                PrintWriter outputWriter = null;
                try {
                    outputWriter = new PrintWriter(output);
                } catch (FileNotFoundException e) {
                    logger.warn("Invalid path for file " + output);
                }

//                // Traverse nodes and print the implementation into cpp files
//                new Visitor() {
//
//                    // TODO: Generate output.cpp file implementations
//
//                    // implement method for visiting the nodes
//                    public void visit(Node n) {
//                        for (Object o : n) {
//                            if (o instanceof Node) dispatch((Node) o);
//                        }
//                    }
//                }.dispatch(n);

                // closes outputwriter and flushes to main.cpp file
                outputWriter.close();
            }

            public void printToMainCpp(GNode n) {
                // create a printwriter for main.cpp file
                File main = loadMainCpp();
                PrintWriter mainWriter = null;
                try {
                    mainWriter = new PrintWriter(main);
                } catch (FileNotFoundException e) {
                    logger.warn("Invalid path for file " + main);
                }

                mainWriter.close();
            }
            // implement method for visiting the nodes
            public void visit(Node n) {
                for (Object o : n) {
                    if (o instanceof Node) dispatch((Node) o);
                }
            }
        }.dispatch(n);

}

    // returns the output.cpp file
    private static File loadOutputCpp() {
        File outputCPP = new File(XtcProps.get("output.location") + "/output.cpp");
        return outputCPP;
    }

    // returns the main.cpp file
    private static File loadMainCpp() {
        File mainCPP = new File(XtcProps.get("output.location") + "/main.cpp");
        return mainCPP;
    }


}
