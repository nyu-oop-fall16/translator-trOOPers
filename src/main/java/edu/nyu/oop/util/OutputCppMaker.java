package edu.nyu.oop.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;

import org.slf4j.Logger;

public class OutputCppMaker extends Visitor{
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(JavaFiveImportParser.class);

    // holds what needs to be printed to output file
    private ToBePrinted outputPrint = new ToBePrinted();

    // Constructor - uses super class's constructor
    public OutputCppMaker(){}

    // visits the node
    public void visit(Node n){
        for (Object o : n) {
            if (o instanceof Node) dispatch((Node) o);
        }
    }

    // add output string and it's index to list of IndexOrderedOutputs
    // ************Maybe add syntax here/ the open and closing braces?********
    private void outputPrintString(int index, String str){
        OutputCppMaker.IndexOrderedOutputs out = new OutputCppMaker.IndexOrderedOutputs(index, str);
        outputPrint.addIndexOrderedOutput(out);
    }

    // relative to CompilationUnit
    // visit qualified identifier nodes - for testing purposes
    public void visitPackageDeclaration(GNode n){
        GNode namespaces = (GNode) n.getNode(1);
        String name = "namespace ";
        String openBracket = "{\n";

        outputPrintString(1,name + namespaces.getString(0) + openBracket);
        outputPrintString(1,name + namespaces.getString(1) + openBracket);
        visit(n);
    }

    // prints given string to output.cpp
    public void printToOutputCpp(ToBePrinted printThis) {
        // Create a printwriter for output.cpp file
        File output = loadOutputCpp();
        PrintWriter outputWriter = getWriter(output);

        // sorts the impelementation code by index (print according to the order they appear in ast)
        printThis.sortByIndex(printThis.implementationCode);

        // go through the string list and print to output file
        for(int i = 0; i < printThis.getList().size(); i++){
            outputWriter.print(printThis.getList().get(i).getOutputString() + " ");
        }

        // close writer when done
        outputWriter.close();
    }

    // returns a printwriter given a file to write to -- could put in printToOutputCpp method
    // returns null if a non valid file is given
    private static PrintWriter getWriter(File file){
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            logger.warn("Invalid path for file " + file);
        }
        return writer;
    }

    // returns the output.cpp file
    private static File loadOutputCpp() {
        File outputCPP = new File(XtcProps.get("output.location") + "/output.cpp");
        return outputCPP;
    }

    // dispatch through the mutated Ast and returns the list of strings being generated/added to in each visit
    public ToBePrinted getOutputToBePrinted(GNode n){
        super.dispatch(n);
        return outputPrint;
    }


    // *****************put in separate class file?************
    // An instance of this class will be mutated as the Ast is traversed.
    public static class ToBePrinted {

        // list of IndexOrderedOutputto be printed
        private List<IndexOrderedOutputs> implementationCode = new ArrayList<IndexOrderedOutputs>();

        // add to list of IndexOrderedOutput
        public void addIndexOrderedOutput(IndexOrderedOutputs s) {
            this.implementationCode.add(s);
        }

        // get list of IndexOrderedOutput
        public List<IndexOrderedOutputs> getList() {
            return this.implementationCode;
        }

        // insertion sort by index
        public List<IndexOrderedOutputs> sortByIndex(List<IndexOrderedOutputs> unordered) {
            // sort using insertion sort
            for (int i = 1; i < unordered.size(); i++) {
                for (int j = i; j > 0; j--) {
                    if (unordered.get(j).getIndex() < unordered.get(j - 1).getIndex()) {
                        IndexOrderedOutputs temp = unordered.remove(j);
                        unordered.add(j - 1, temp);
                    }
                }
            }
            return unordered;
        }
    }

            // get the index of the node of the string (string is a child of a parent - the index is i and string is the ith child)
    public class IndexOrderedOutputs{
        private int index;
        private String outputString;

        // constructor
        public IndexOrderedOutputs(int index, String outputString){
            this.index = index;
            this.outputString = outputString;
        }

        // get inddex
        public int getIndex(){
            return this.index;
        }

        // get output string
        public String getOutputString(){
            return this.outputString;
        }
    }

}
