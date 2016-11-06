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
    ChildToParentMap map;
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(JavaFiveImportParser.class);

    // holds what needs to be printed to main file
    private ToBePrinted mainPrint = new ToBePrinted();
    private List<String> content = new ArrayList<String>();
    boolean additionalParameters = false;
    // constructor - uses super class's constructor
    public MainCppMaker() {}

    public void visit(Node n) {
        for (Object o : n) {
            if (o instanceof Node) dispatch((Node) o);
        }
    }

    //adding namespace inputs::testxxx
    public void visitPackageDeclaration(GNode n) {
        if(n.getNode(1).getName().equals("QualifiedIdentifier")) {
            GNode qualifiedIdentifier = (GNode)n.getNode(1);
            content.add(qualifiedIdentifier.getString(0) + "::" + qualifiedIdentifier.getString(1) + ";\n");
        }
        visit(n);
    }
//    public void visitQualifiedIdentifier(GNode n) {
//        content.add(n.getString(0));
//        visit(n);
//    }

    public void visitMethodDeclaration(GNode n) {
        String methodname = n.getNode(3).getString(0); // get "main" (name of the method)
        assert methodname=="main";
//        if (main.equals("main")) {
//            mainPrint.addString(3, main); // 3 - method name is the 4th child of MethodDeclaration
//        }
        visit(n);
    }

    public void visitMethodName(GNode n) {
        if(n.getString(0).equals("main")) {
            content.add(n.getString(0));
        }
        visit(n);
    }

    public void visitFormalParameters(GNode n) {
        GNode parentOfFormalParameters = (GNode)map.fetchParentFor(n);
        if (parentOfFormalParameters.getName().equals("MethodDeclaration")) {
            GNode methodDeclaration = parentOfFormalParameters;
            int sizeOfMethodD = methodDeclaration.size();
            for (int i = 0; i < sizeOfMethodD; i++) {
                try {
                    if (methodDeclaration.getNode(i).getName().equals("MethodName")) {
                        GNode methodName = (GNode) methodDeclaration.getNode(i);
                        String methodString = methodName.getString(0);
                        if (methodString.equals("main")) {
                            content.add("(");
                            visit(n);
                            content.add(")");
                        }
                    }
                } catch (NullPointerException e) {

                }
            }
        }

        //visit(n);
    }

    public void visitBlock(GNode n) {
        GNode parentOfFormalParameters = (GNode)map.fetchParentFor(n);
        if (parentOfFormalParameters.getName().equals("MethodDeclaration")) {
            GNode methodDeclaration = parentOfFormalParameters;
            int sizeOfMethodD = methodDeclaration.size();
            for (int i = 0; i < sizeOfMethodD; i++) {
                try {
                    if (methodDeclaration.getNode(i).getName().equals("MethodName")) {
                        GNode methodName = (GNode) methodDeclaration.getNode(i);
                        String methodString = methodName.getString(0);
                        if (methodString.equals("main")) {
                            content.add("{");
                            visit(n);
                            content.add("}");
                        }
                    }
                } catch (NullPointerException e) {

                }
            }
        }

    }

    public void visitFieldDeclaration(GNode n) {
        if(n.getNode(0).isEmpty()) {
            if(n.getNode(1).getName().equals("Type")) {
                GNode type = (GNode)n.getNode(1);
                if(type.getNode(0).getName().equals("QualifiedIdentifier")) {
                    GNode qualifiedIdentifier = (GNode)type.getNode(0);
                    String classtype = qualifiedIdentifier.getString(0);
                    content.add(classtype);
                    visit(n);
                    content.add(";");
                }
            }
        }
        //visit(n); <<to prevent double
        int sizeOfList = content.size();

        if(!content.get(sizeOfList-1).endsWith(";\n") && !content.get(sizeOfList-1).endsWith(";")) { //before was "endswith ";\n"
            content.add(";");
        }

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
        GNode declarators = (GNode)map.fetchParentFor(n);
        GNode fieldDeclaration = (GNode)map.fetchParentFor(declarators);
        GNode check = (GNode)map.fetchParentFor(fieldDeclaration);
        if(check.getName().equals("Block")) {
            content.add(n.getString(0));
            content.add("=");

            if (n.getString(1) != null) {
                content.add(n.getString(1));
            }
            try {
                if (n.getNode(2).getName().equals("PrimaryIdentifier")) {
                    GNode primaryIdentifier = (GNode) n.getNode(2);
                    String castedInstance = primaryIdentifier.getString(0);
                    content.add(castedInstance);
                }
            } catch (NullPointerException npe) {

            }
        }
        visit(n);
    }

    public void visitArguments(GNode n) {
        if (!n.isEmpty()) {
            try {
                if (n.getNode(0).hasName("StringLiteral")) {
                    content.add("(");
                    visit(n);
                    content.add(")");
                }
                if(n.getNode(0).hasName("newCString")) {
                    content.add("(");
                    visit(n);
                    if(!additionalParameters) {
                        content.add(")");
                    }
                }

                else {
                    visit(n);
                }
            } catch(ClassCastException e) {

            }
        } else if(n.isEmpty()) {
            content.add("(");
            content.add(")");
            visit(n);
        }
    }

    public void visitCallName(GNode n) {
        content.add("->");
        content.add("_vptr");
        content.add("->");
        content.add(n.getString(0));
        visit(n);
    }

    public void visitCallExpression(GNode n) {
        boolean visitAgain = true;
        GNode parentOfCallExpression = (GNode)map.fetchParentFor(n);
        if(parentOfCallExpression.getName().equals("Arguments")) {
            if(n.getNode(0).getName().equals("PrimaryIdentifier")) {
                GNode primaryIdentifier = (GNode) n.getNode(0);
                String instance = primaryIdentifier.getString(0);
                content.add(instance);
                content.add("->__vptr->");
            }
            String check = n.getString(2);
            if (check.equals("toString")||check.equals("getFld")) {
                content.add(check);
                if(n.getNode(3).getName().equals("Arguments")) {
                    GNode arguments = (GNode) n.getNode(3);
                    String argument = arguments.getString(0);
                    content.add("(" + argument + ")");
                }
                content.add("->");
                content.add("data");
            }
        }
        if(parentOfCallExpression.getName().equals("ExpressionStatement")) {
            if(n.getNode(0).getName().equals("PrimaryIdentifier")) {
                String primId = n.getNode(0).getString(0);
                content.add(primId);
                content.add("->__vptr->");
                content.add(n.getString(2));
                additionalParameters = true;
                visit(n);
                content.add("," + primId + ")");
                visitAgain = false;
            }
        }
//        String c1 = n.getNode(0).getName();
//        if (c1.equals("PrimaryIdentifier")) {
//            visit(n);
//            content.add(n.getNode(0).getString(0));
//            content.add("->");
//            content.add("_vptr");
//            content.add("->");
//            content.add(n.getString(2));

//            System.out.println("node3"+n.getNode(3).toString());

//        } else {

//        }
//        if (n.size() > 3) {
//            Node c4 = n.getNode(3);
//            if (c4.hasName("Arguments") ) {
//                content.add("(");
//                content.add(")");
//            }
//        }
        if(visitAgain) {
            visit(n);
        }
    }

    public void visitPrimaryIdentifier(GNode n) {
        GNode parentOfPrimaryIdentifier = (GNode)map.fetchParentFor(n);
        if(parentOfPrimaryIdentifier.getName().equals("SelectionExpression")) {
            content.add(n.getString(0));
        }
        visit(n);
    }

    public void visitNewClassExpression(GNode n) {
        content.add("new");
        try {
            if (n.getNode(2).getName().equals("QualifiedIdentifier")) {
                GNode qualifiedIdentifier = (GNode) n.getNode(2);
                String constructor = qualifiedIdentifier.getString(0);
                content.add(constructor);
            }
        } catch(Exception e) {

        }
        visit(n);
//        if(n.getNode(3).hasName("Arguments")){
//            if(n.getNode(3).isEmpty()){
//                content.add("(");
//                content.add(")");
//            }
//        }
    }

    public void visitStringLiteral(GNode n) {
        content.add(n.getString(0));
        visit(n);
    }

    public void visitnewCString(GNode n) {
        content.add("new __String(" + n.getString(0) + ")");
    }

    //get first string in qualified identifiers without getting 'inputs'
    public void visitClassDeclaration(GNode n) {
        map = new ChildToParentMap(n);
        if(n.getNode(5).getName().equals("ClassBody")) {
            GNode classBody = (GNode)n.getNode(5);
            if(classBody.getNode(0).getName().equals("MethodDeclaration")) {
                GNode methodDeclaration = (GNode)classBody.getNode(0);
                if(methodDeclaration.getNode(2).getName().equals("Type")) {
                    GNode type = (GNode)methodDeclaration.getNode(2);
                    if(type.getNode(0).getName().equals("QualifiedIdentifier")) {
                        String qid = type.getNode(0).getString(0);
                        content.add(qid);
                    }
                }
            }
        }
        visit(n);
    }

    public void visitModifier(GNode n) {
        String s=n.getString(0);
        if(s.equals("namespace")) {
            content.add("using namespace");
        }
        visit(n);
    }

//    public void visitClassName(GNode n) {
//        String classname=n.getString(0);
//        if(classname.startsWith("Test")) {
//            content.add("inputs::");
//            classname=classname.toLowerCase();
//            content.add(classname);
//            content.add(";");
//        } else {
//            content.add(classname);
//        }
//
//        visit(n);
//    }











    // prints implementation to output.cpp
    public void printToMainCpp(String s) {
        // Create a printwriter to write into output.cpp file
        File main = loadMainCpp();
        PrintWriter mainWriter = getWriter(main);
        mainWriter.print(s);
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
    public String getMainToBePrinted(GNode n) {
        content.add("#include <iostream>\n");
        content.add("#include \"output.h\"\n");
        content.add("#include \"java_lang.h\"\n");
        content.add("using namespace");
        content.add("std");
        content.add(";\n");
        content.add("using namespace java::lang;\n");
        content.add("using namespace");
        super.dispatch(n);
        String output="";
        for(String s:content) {
            output+=s;
            output+=" ";
        }

        if(output.endsWith(" ")) {
            output=output.substring(0,output.length()-1);
        }

//        System.out.println(content.toString());
        System.out.println(output);
        return output;
//        return mainPrint;
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
        // sorts the impelementation code by index (print according to the order they appear in ast)

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
