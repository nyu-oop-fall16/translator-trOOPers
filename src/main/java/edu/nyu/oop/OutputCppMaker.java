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

public class OutputCppMaker extends Visitor {
    ChildToParentMap map;
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(JavaFiveImportParser.class);

    // holds what needs to be printed to output file
    private List<String> content = new ArrayList<String>();

    // Constructor - uses super class's constructor
    public OutputCppMaker() {}

    // visits the node
    public void visit(Node n) {
        for (Object o : n) {
            if (o instanceof Node) dispatch((Node) o);
        }
    }

    // relative to CompilationUnit
    // visit qualified identifier nodes - for testing purposes
    public void visitPackageDeclaration(GNode n) {
        GNode namespaces = (GNode) n.getNode(1);
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

    public void visitClassDeclaration(GNode n) {
        //only traverse not main class for output.cpp
        map = new ChildToParentMap(n);
        if(n.getString(1).startsWith("Test")) {

        } else {
            visit(n);
        }
    }

    public void visitConstructorDeclaration(GNode n) {
        try {
            content.add(n.getString(2));
            content.add("(");
            GNode formalParameters = (GNode)n.getNode(3);
            int sizeOfFormalParameters = formalParameters.size();
            GNode formalParameter = (GNode) formalParameters.getNode(0);
            GNode qualifiedIdentifier = (GNode) formalParameter.getNode(1).getNode(0);
            content.add(qualifiedIdentifier.getString(0));
            content.add(formalParameter.getString(3));
            content.add(",");
            if(content.get(content.size()-1).equals(",")) {
                content.remove(content.size()-1);
            }
            content.add(")");
            content.add(":");
            GNode block = (GNode) n.getNode(5);
            GNode expression = (GNode) block.getNode(0).getNode(0);
            int sizeOfExpression = expression.size();
            for (int i = 0; i < sizeOfExpression; i++) {
                content.add(expression.getString(i));
                if (i != sizeOfExpression - 1) {
                    content.add(",");
                }
            }
            content.add("{}\n");
        } catch(Exception e) {

        }
        visit(n);

    }
    public void visitDefaultConstructorDeclaration(GNode n) {
        content.add(n.getString(0));
        content.add("::");
        content.add(n.getString(1));
        content.add("():");
        content.add(n.getString(2));
        content.add("{");
        content.add("}");
        visit(n);
    }

    public void MethodDeclaration(GNode n) {
        visit(n);
    }

    public void visitQualifiedIdentifier(GNode n) {
        try {
            GNode type = (GNode) map.fetchParentFor(n);
            GNode fieldDeclaration = (GNode) map.fetchParentFor(type);
            if (!fieldDeclaration.getName().equals("FieldDeclaration") && !fieldDeclaration.getName().equals("Extension")) {
                if (n.size() == 1) {
                    content.add(n.getString(0));
                }
            }
        } catch(Exception e) {
            if (n.size() == 1) {
                content.add(n.getString(0));
            }
        }
        visit(n);
    }

    public void visitMethodName(GNode n) {
        content.add(n.getString(0));

        visit(n);

    }

    public void visitFormalParameters(GNode n) {
        GNode constructorDeclaration = (GNode) map.fetchParentFor(n);
        if(!constructorDeclaration.getName().equals("ConstructorDeclaration")) {
            content.add("(");
            try {
                content.add(n.getString(0));
            } catch (Exception e) {

            }
            visit(n);
            content.add(")");
        }

    }

    public void visitBlock(GNode n) {
        GNode constructor = (GNode) map.fetchParentFor(n);
        if(!constructor.getName().equals("ConstructorDeclaration")) {
            content.add("{\n");
            if (!n.isEmpty()) {
                int sizeOfBlock = n.size();
                //changed to visitBlock
//                try {
//                    if (n.getNode(0).getName().equals("Contents")) {
//                        GNode contents = (GNode) n.getNode(0);
//                        content.add(contents.getString(0));
//                    }
//                } catch (IndexOutOfBoundsException e) {
//
//                }

            }
            visit(n);
            content.add("}\n");
        }
    }

    public void visitContent(GNode n){
        content.add(n.getString(0));
        visit(n);

    }


    public void visitPrimaryIdentifier(GNode n){
        content.add("__this->" + n.getString(0));
        visit(n);
    }

    public void visitReturnStatement(GNode n) {
        content.add("return");
        try{//if the first is string, then return k;
            String k=n.getString(0);
            content.add(k);
        }catch (Exception e){
        }
        //changed to visitPrimaryIdentifier;
//            try {
//                if(n.getNode(0).getName().equals("PrimaryIdentifier")) {
//                    GNode primaryIdentifier = (GNode)n.getNode(0);
//                    content.add("__this->" + primaryIdentifier.getString(0));
//                }
//            } catch(Exception e) {
//
//            }

        visit(n);
        content.add(";");

    }

    public void visitNewClassExpression(GNode n) {
        content.add("new");
        visit(n);
    }

    public void visitcString(GNode n) {
        content.add(n.getString(0));
        visit(n);
    }

    public void visitArguments(GNode n) {
        content.add("(");
        try {
            content.add(n.getString(0));
        } catch (Exception e) {
        }
        visit(n);
        content.add(")");
    }

    public void visitvptrString(GNode n) {
        content.add(n.getString(0));
        visit(n);
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
    private static PrintWriter getWriter(File file) {
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
    public String getOutputToBePrinted(GNode n) {
        content.add("#include \"output.h\"\n");
        content.add("#include \"java_lang.h\"\n");
        content.add("using namespace java::lang;\n");
        super.dispatch(n);
        content.add("}\n");
        content.add("}\n");
        String output = "";
        for (String s : content) {
            output += s;
            output += " ";
        }

        if (output.endsWith(" ")) {
            output = output.substring(0, output.length() - 1);
        }

        // print the output to console too
        System.out.println(output);

        return output;
    }
}
