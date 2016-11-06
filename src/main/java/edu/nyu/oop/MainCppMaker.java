package edu.nyu.oop;

import edu.nyu.oop.util.ChildToParentMap;
import edu.nyu.oop.util.JavaFiveImportParser;
import edu.nyu.oop.util.XtcProps;
import org.slf4j.Logger;
import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Takes a GNode of the mutated Java Ast representing the C++ version and traverses it to add to a list of strings to be printed to main.cpp file.
 */
public class MainCppMaker extends Visitor {
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(JavaFiveImportParser.class);

    // holds what needs to be printed to main file
    private List<String> content = new ArrayList<String>();

    // holds the map of children to a given parent node
    ChildToParentMap map;

    // **********************************
    boolean additionalParameters = false;
    // **********************************

    // visit all the nodes given a root node
    public void visit(Node n) {
        for (Object o : n) {
            if (o instanceof Node) dispatch((Node) o);
        }
    }

    //adding namespace inputs::testxxx from package inputs.testxxx
    public void visitPackageDeclaration(GNode n) {
        if(n.getNode(1).getName().equals("QualifiedIdentifier")) {
            GNode qualifiedIdentifier = (GNode)n.getNode(1);
            content.add(qualifiedIdentifier.getString(0) + "::" + qualifiedIdentifier.getString(1) + ";\n");
        }
//        visit(n);
    }

    // constrain visit to main method node - all the information for main method should be within main's method declaration node
    public void visitMethodDeclaration(GNode n) {
        String methodName = n.getNode(3).getString(0); // get "main" (name of the method)
        if (methodName.equals("main")) {
            visit(n);
        }
    }

    // add the main method name "main" to the list of strings to be written to main.cpp file
    public void visitMethodName(GNode n) {
        if(n.getString(0).equals("main")) {
            content.add(n.getString(0));
        }
    }

    // visit the formal parameters of main method and surround them with parentheses in list of strings
    // in between adding the open and closed parentheses, visit the children of formal parameters and add to the list of strings
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
    }

    // visit the block of main method and surround them with braces in list of strings
    // in between adding the open and closed brace, visit the children of block and add to the list of strings for implementation of main
    public void visitBlock(GNode n) {
        GNode parentOfBlock = (GNode)map.fetchParentFor(n);
        if (parentOfBlock.getName().equals("MethodDeclaration")) {
            GNode methodDeclaration = parentOfBlock;
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

    // For fields, get the data type of the field and add to list of strings
    // visit node to get the rest of the information of that field declaration before adding semicolon to list of strings (end of declaration)
    public void visitFieldDeclaration(GNode n) {
        if(n.getNode(0).isEmpty()) {
            if(n.getNode(1).getName().equals("Type")) {
                GNode type = (GNode)n.getNode(1);
                if(type.getNode(0).getName().equals("QualifiedIdentifier")) {
                    GNode qualifiedIdentifier = (GNode)type.getNode(0);
                    String classType = qualifiedIdentifier.getString(0);
                    content.add(classType);
                    visit(n);
                    content.add(";");
                }
            }
        }

        // this takes care of any extra semicolons being added to the list
        int sizeOfList = content.size();
        if(!content.get(sizeOfList-1).endsWith(";\n") && !content.get(sizeOfList-1).endsWith(";")) { //before was "endswith ";\n"
            content.add(";");
        }
    }

    // visit Expression statement and run a visit on that node to let the other visit methods get the info needed and end it with a semicolon
    public void visitExpressionStatement(GNode n) {
        visit(n);
        content.add(";");
    }

    // for print statements in c++ do the format std::cout << arguments << std::endl;
    // check for "cout" and "endl" and call visit in the appropriate order to get "std" added to string list
    // if it's not for printing, call visit and handle appropriately with other visitXXX methods
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
            visit(n);
        }
    }

    // add instantiations to list of strings (i.e A a = new __A() or Object o = (Object) a etc..)
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

    // get any arguments and surround with parentheses in the list of strings
    // handle StringLiteral and newCStrings arguments differently
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
                } else {
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

    // Handles calling a method using the inheritance that utilizes vtables
    // mainly handles methods that return a String object
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
        if(visitAgain) {
            visit(n);
        }
    }

    // if the parent is a SelectionExpression then add it's child string to list
    // otherwise run visit to let other visitXXX methods handle
    public void visitPrimaryIdentifier(GNode n) {
        GNode parentOfPrimaryIdentifier = (GNode)map.fetchParentFor(n);
        if(parentOfPrimaryIdentifier.getName().equals("SelectionExpression")) {
            content.add(n.getString(0));
        }
        visit(n);
    }

    // add new keyword to list and if there is a constructor, add the constructor the the list afterwards
    // run visit to let other methods handle otherwise
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
    }

    // add string literals to list
    public void visitStringLiteral(GNode n) {
        content.add(n.getString(0));
        visit(n);
    }

    // add the string as a string object
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
        String mainOutput="";
        for(String s:content) {
            mainOutput+=s;
            mainOutput+=" ";
        }

        if(mainOutput.endsWith(" ")) {
            mainOutput=mainOutput.substring(0,mainOutput.length()-1);
        }

        System.out.println(mainOutput);
        return mainOutput;
    }
}
