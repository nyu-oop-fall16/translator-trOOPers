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

public class MainCppMaker extends Visitor{
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(JavaFiveImportParser.class);

    // holds what needs to be printed to main file
    private ToBePrinted mainPrint = new ToBePrinted();

    // constructor - uses super class's constructor
    public MainCppMaker(){}

    // visit the node
    public void visit(Node n){
        for (Object o : n) {
            if (o instanceof Node) dispatch((Node) o);
        }
    }

    // add output string and it's index to list of IndexOrderedOutputs
    private void mainPrintString(int index, String str){
        IndexOrderedOutputs out = new IndexOrderedOutputs(index, str);
        mainPrint.addIndexOrderedOutput(out);
    }

    // TODO: write all the visit methods so that main.cpp is complete
    // indices relative to MethodDeclaration of main method

    // visit qualified identifier nodes - for testing purposes
//    public void visitType(GNode n){
//        String returnType = ""; // holds return type of main
//        if(n.getNode(0).getString(0).equals("int")){
//            returnType = n.getNode(0).getString(0);
//        }
//        // add to list of strings to be printed
//        mainPrintString(2,returnType); // 2 - Type is the 3rd child of MethodDeclaration
//        visit(n);
//    }

    // visit expression statement - for testing purposes
    public void visitMethodDeclaration(GNode n){
        String main = n.getString(3); // get "main" (name of the method)
        if(main.equals("main")) {
            mainPrintString(2, "int "+main); // 3 - method name is the 4th child of MethodDeclaration
            buildmainfunction(n);
        }
        visit(n);
    }

    public void buildmainfunction(GNode n){
        String method="";
        GNode filedDeclaration=GNode.cast(n.getNode(7).getNode(0));
        method+=buildFieldDeclaration(filedDeclaration);
        GNode callexpression=GNode.cast(n.getNode(7).getNode(1).getNode(0));

        for(int i=0;i<callexpression.size();i++){
            Node c=callexpression.getNode(i);
            if(c.getName()=="SelectionExpression"){
                if(c.getString(1)=="cout"){
                    method+=c.getNode(0).getString(0)+"::"+c.getString(1)+"<<";
                }else if(c.getString(1)=="endl"){
                    method+="<<"+c.getNode(0).getString(0)+"::"+c.getString(1);
                }
            }else if(c.getName().equals("Arguments")){
                GNode c1=GNode.cast(c.getNode(0));
                if(c1.getName().equals("StringLiteral")){
                    method+=c1.getString(0);
                }else if(c1.getName().equals("CallExpression")){
                    int c1size=c1.getNode(3).size();
                    String parameter="";
                    if(c1size==0){
                        parameter="";
                    }
                    method+=c1.getNode(0).getString(0)+"."+c1.getString(2)+"("+parameter+")";
                }
            }

        }
        method="{"+method+"\n}";
        mainPrintString(3,method);

        visit(n);
    }


    public String buildFieldDeclaration(GNode n){
        String s="\n";
        s+=n.getNode(1).getNode(0).getString(0)+" ";
        s+=n.getNode(2).getNode(0).getString(0)+" = ";
        s+="new "+n.getNode(2).getNode(0).getNode(2).getNode(2).getString(0);
        s+="();\n";
        return s;
    }


    // prints implementation to output.cpp
    public void printToMainCpp(ToBePrinted printThis) {
        // Create a printwriter to write into output.cpp file
        File main = loadMainCpp();
        PrintWriter mainWriter = getWriter(main);

        // sorts the impelementation code by index (print according to the order they appear in ast)
        printThis.sortByIndex(printThis.implementationCode);

        // print to output file
        for(int i = 0; i < printThis.getList().size(); i++){
            System.out.println(i+"elemnt of the list is "+printThis.getList().get(i).getOutputString());
            mainWriter.print(printThis.getList().get(i).getOutputString() + " ");
        }

        // close writer when done
        mainWriter.close();
    }

    // returns a printwriter given a file to write to - could just add to printToMainCpp method
    private static PrintWriter getWriter(File file){
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            logger.warn("Invalid path for file " + file);
        }
        return writer;
    }

    // returns the main.cpp file
    private static File loadMainCpp() {
        File mainCPP = new File(XtcProps.get("output.location") + "/main.cpp");
        return mainCPP;
    }

    // visits the whole ast and returns the list of what needs to be printed in main.cpp
    public ToBePrinted getMainToBePrinted(GNode n){
        super.dispatch(n);
        return mainPrint;
    }

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
        public List<IndexOrderedOutputs> sortByIndex(List<IndexOrderedOutputs> unordered){
            // sort using insertion sort
            for(int i = 1; i < unordered.size(); i++){
                for(int j = i; j > 0; j--){
                    if(unordered.get(j).getIndex() < unordered.get(j-1).getIndex()){
                        IndexOrderedOutputs temp = unordered.remove(j);
                        unordered.add(j-1, temp);
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
