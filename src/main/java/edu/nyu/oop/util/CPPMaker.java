package edu.nyu.oop.util;

import org.slf4j.Logger;
import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class CPPMaker {
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(JavaFiveImportParser.class);


    public static void printToMainCpp(GNode n) {
//        File main = loadMainCpp();
//        PrintWriter mainWriter = null;
//        try {
//            mainWriter = new PrintWriter(main);
//        } catch (FileNotFoundException e) {
//            logger.warn("Invalid path for file " + main);
//        }

        new Visitor() {
            File main = loadMainCpp();
            PrintWriter mainWriter = null;
            String content="";

            public void init() {
                try {
                    mainWriter = new PrintWriter(main);
                } catch (FileNotFoundException e) {
                    logger.warn("Invalid path for file " + main);
                }
            }


            public void visitPackageDeclaration(GNode n) {
//                mainWriter.println();
                content+="5";
                visit(n);
            }


            // implement method for visiting the nodes
            public void visit(Node n) {
                for (Object o : n) {
                    if (o instanceof Node) dispatch((Node) o);
                }
            }

            public void done(){
                mainWriter.println(content);
                mainWriter.close();

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
