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

public class MainCppMaker extends Visitor {
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(JavaFiveImportParser.class);

    // holds what needs to be printed to main file
    private ToBePrinted mainPrint = new ToBePrinted();
    private List<String> content = new ArrayList<String>();

    // constructor - uses super class's constructor
    public MainCppMaker() {
    }

    public void visit(Node n) {
        for (Object o : n) {
            if (o instanceof Node) dispatch((Node) o);
        }
    }

    public void visitQualifiedIdentifier(GNode n) {
        content.add(n.getString(0));
        visit(n);
    }

    public void visitMethodDeclaration(GNode n) {
        String methodname = n.getNode(3).getString(0); // get "main" (name of the method)
        assert methodname=="main";
//        if (main.equals("main")) {
//            mainPrint.addString(3, main); // 3 - method name is the 4th child of MethodDeclaration
//        }
        visit(n);
    }

    public void visitMethodName(GNode n){
        content.add(n.getString(0));
        visit(n);
    }

    public void visitFormalParameters(GNode n){
        content.add("(");
        visit(n);
        content.add(")");
    }

    public void visitBlock(GNode n) {
        content.add("{");
        visit(n);
        content.add("}");

    }

    public void visitFieldDeclaration(GNode n) {
        visit(n);
        content.add(";");
    }

    public void visitExpressionStatement(GNode n) {
        visit(n);
        content.add(";");
    }

    public void visitSelectionExpression(GNode n) {
        String s = "";
        s = n.getString(1);

        if (s.equals("cout")) {
            visit(n);
            content.add("::" + s);
            content.add("<<");
        } else if (s.equals("endl")) {
            content.add("<<");
            visit(n);
            content.add("::" + s);

        } else {
            System.out.println("visitSelectionExpression getString1 error");
            visit(n);
        }
    }


    public void visitDeclarator(GNode n) {
        content.add(n.getString(0));
        if (n.getNode(2) != null) {
            content.add("=");
        }
        visit(n);
    }


    public void visitArguments(GNode n) {
        if (!n.isEmpty()) {
            if (n.getNode(0).hasName("StringLiteral")) {
                content.add("(");
                visit(n);
                content.add(")");
            } else {
                visit(n);
            }
        } else {
            visit(n);
        }
    }

    public void visitCallExpression(GNode n) {
        String c1 = n.getNode(0).getName();
        if (c1.equals("PrimaryIdentifier")) {
            visit(n);
            content.add("->");
            content.add(n.getString(2));
        } else {
            visit(n);
        }
        if (n.size() > 3) {
            Node c4 = n.getNode(3);
            if (c4.hasName("Arguments") && c4.isEmpty()) {
                content.add("(");
                content.add(")");
            }
        }
    }

    public void visitPrimaryIdentifier(GNode n) {
        content.add(n.getString(0));
        visit(n);
    }


    public void visitNewClassExpression(GNode n) {
        content.add("new");
        visit(n);
    }

    public void visitStringLiteral(GNode n) {
        content.add(n.getString(0));
        visit(n);
    }











    // prints implementation to output.cpp
    public void printToMainCpp(ToBePrinted printThis) {
        // Create a printwriter to write into output.cpp file
        File main = loadMainCpp();
        PrintWriter mainWriter = getWriter(main);

        // sorts the impelementation code by index (print according to the order they appear in ast)

        printThis.sortByIndex();
//        System.out.println("sorted");
        // print to output file
        for (int i = 0; i < printThis.getList().size(); i++) {
//            System.out.println("the " + i + " element is " + printThis.getList().get(i).getOutputString());
            mainWriter.print(printThis.getList().get(i).getOutputString() + " ");
        }

        // close writer when done
        mainWriter.close();
    }

    // returns a printwriter given a file to write to - could just add to printToMainCpp method
    private static PrintWriter getWriter(File file) {
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
    public ToBePrinted getMainToBePrinted(GNode n) {
        super.dispatch(n);
        String output="";
        for(String s:content) {
            output+=s;
            output+=" ";

        }
        if(output.endsWith(" ")){
            output=output.substring(0,output.length()-1);
        }

        System.out.println(content.toString());
        System.out.println(output);
        return mainPrint;
    }

    // An instance of this class will be mutated as the Ast is traversed.
    public class ToBePrinted {

        // list of IndexOrderedOutputto be printed
        private List<IndexOrderedOutputs> implementationCode = new ArrayList<IndexOrderedOutputs>();

        // add to list of IndexOrderedOutput
        private void addIndexOrderedOutput(IndexOrderedOutputs s) {
            this.implementationCode.add(s);
        }

        // get list of IndexOrderedOutput
        public List<IndexOrderedOutputs> getList() {
            return this.implementationCode;
        }

        public String getString() {
            List<String> s = new ArrayList<String>();
            this.sortByIndex();
            for (int i = 0; i < this.implementationCode.size(); i++) {
                s.add(implementationCode.get(i).getOutputString());
            }
            return s.toString();

        }

        public void addString(int index, String str) {
            IndexOrderedOutputs out = new IndexOrderedOutputs(index, str);
            this.addIndexOrderedOutput(out);
        }

        // insertion sort by index
        public void sortByIndex() {
            // sort using insertion sort
            for (int i = 1; i < implementationCode.size(); i++) {
                for (int j = i; j > 0; j--) {
                    if (implementationCode.get(j).getIndex() < implementationCode.get(j - 1).getIndex()) {
                        IndexOrderedOutputs temp = implementationCode.remove(j);
                        implementationCode.add(j - 1, temp);
                    }
                }
            }

        }
    }

    // get the index of the node of the string (string is a child of a parent - the index is i and string is the ith child)
    public class IndexOrderedOutputs {
        private int index;
        private String outputString;

        // constructor
        public IndexOrderedOutputs(int index, String outputString) {
            this.index = index;
            this.outputString = outputString;
        }

        // get inddex
        public int getIndex() {
            return this.index;
        }

        // get output string
        public String getOutputString() {
            return this.outputString;
        }
    }
}
