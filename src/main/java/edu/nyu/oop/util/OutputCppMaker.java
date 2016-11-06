package edu.nyu.oop.util;

import org.slf4j.Logger;
import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class OutputCppMaker extends Visitor{
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(JavaFiveImportParser.class);

    // holds what needs to be printed to output file
    private ToBePrinted outputPrint = new ToBePrinted();
    private List<String> content = new ArrayList<String>();
    // Constructor - uses super class's constructor
    public OutputCppMaker(){}

    // visits the node
    public void visit(Node n){
        for (Object o : n) {
            if (o instanceof Node) dispatch((Node) o);
        }
    }

    // relative to CompilationUnit
    // visit qualified identifier nodes - for testing purposes
    public void visitPackageDeclaration(GNode n){
        GNode namespaces = (GNode) n.getNode(1);
//        String name = "namespace ";
//        String openBracket = "{\n";
        if(namespaces.size()==2) {
            content.add("namespace");
            content.add(namespaces.getString(0));
            content.add("{\n");
            content.add("namespace");
            content.add(namespaces.getString(1));
            content.add("{\n");
            visit(n);
        }
        visit(n);
    }

    public void visitClassDeclaration(GNode n){
        //only traverse not main class for output,cpp
//        if(!n.getString(1).startsWith("T")){
            visit(n);
//        }
    }

    public void visitDefaultConstructorDeclaration(GNode n){
        content.add(n.getString(0));
        content.add("::");
        content.add(n.getString(1));
        content.add("():");
        content.add(n.getString(2));
        content.add("{");
        content.add("}");
        visit(n);
    }

    public void MethodDeclaration(GNode n){
        visit(n);
    }

    public void visitQualifiedIdentifier(GNode n) {
        if(n.size()==1) {
            content.add(n.getString(0));
        }
        visit(n);
    }

    public void visitMethodName(GNode n){
        content.add(n.getString(0));

        visit(n);

    }

    public void visitFormalParameters(GNode n){
        content.add("(");
        try {
            content.add(n.getString(0));
        }catch (Exception e){

        }
        visit(n);
        content.add(")");

    }

    public void visitBlock(GNode n){
        content.add("(\n");
        visit(n);
        content.add(")\n");
    }

    public void visitReturnStatement(GNode n){
        content.add("return");
        visit(n);
        content.add(";");

    }

    public void visitNewClassExpression(GNode n){
        content.add("new");
        visit(n);
    }

    public void visitcString(GNode n){
        content.add(n.getString(0));
        visit(n);
    }

    public void visitArguments(GNode n){
        content.add("(");
        try{
            content.add(n.getString(0));
        }catch (Exception e){
        }
        visit(n);
        content.add(")");
    }

    // add output string and it's index to list of IndexOrderedOutputs
    // ************Maybe add syntax here/ the open and closing braces?********
    private void outputPrintString(int index, String str){
        OutputCppMaker.IndexOrderedOutputs out = new OutputCppMaker.IndexOrderedOutputs(index, str);
        outputPrint.addIndexOrderedOutput(out);
    }

    // prints given string to output.cpp
    public void printToOutputCpp(String s) {
        // Create a printwriter for output.cpp file
        File output = loadOutputCpp();
        PrintWriter cppWriter = getWriter(output);
        cppWriter.print(s);
        cppWriter.close();
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
    public String getOutputToBePrinted(GNode n){
        content.add("#include output.h\n");
        content.add("#include javalang.h\n");
        super.dispatch(n);
        content.add("}\n");
        content.add("}\n");
        String output="";
        for(String s:content) {
            output+=s;
            output+=" ";
        }

        if(output.endsWith(" ")){
            output=output.substring(0,output.length()-1);
        }

//        System.out.println(content.toString());
        System.out.println(output);
        return output;
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
