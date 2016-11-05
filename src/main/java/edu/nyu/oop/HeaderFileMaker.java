package edu.nyu.oop;

import edu.nyu.oop.util.XtcProps;
import org.slf4j.Logger;
import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;

import java.io.*;

// This class visits all the nodes in a CPP AST and prints a file from it

public class HeaderFileMaker extends Visitor {
    private File header;
    private PrintWriter writer;

    private int numNameSpaces;
    private String className;
    private String vTableName;

    private AggregatedHeaderPrinter printer;

    private Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    public void runVisitor(GNode n) { // call this from Boot.java
        try {
            header = new File(XtcProps.get("output.location") + "/output.h");
            writer = new PrintWriter(header);
            printer = new AggregatedHeaderPrinter(writer);
            visit(n);
            printer.writeToOutputFile();
        } catch (FileNotFoundException e) {
            System.out.println("Header AST file was not found.");
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void visit(GNode n) {
        for (Object o : n) {
            if (o instanceof Node) {
                dispatch((Node) o);
            }
        }
    }

    public void visitNameSpaceDeclaration(GNode n) {
        numNameSpaces = n.size();
        printer.setNumNamespaces(numNameSpaces);
        for (int nodeIdx = 0; nodeIdx < numNameSpaces; nodeIdx++) {
            Object nodeItem = n.get(nodeIdx);
            if (nodeItem instanceof String) {
                printer.addToHeader("namespace " + nodeItem + " {\n");
            }
        }
        visit(n);
    }

    public void visitDeclarationsAndTypedef(GNode n) {
        for (int i = 0; i < 3; i++) {
            printer.addToHeader(n.getString(i) + ";\n");
        }
        printer.addToHeader("\n");
        visit(n);
    }

    public void visitClassDeclaration(GNode n) {
        className = n.getNode(0).getString(0); // get Class Name
        printer.addClass(className);
        HeaderClassPrinter hcp = printer.getPrinter(className);

        // prints beginning of class struct
        hcp.addToDL("struct " + className + " {\n");
        visit(n);
    }

    public void visitFieldDeclaration(GNode n) {
        HeaderClassPrinter hcp = printer.getPrinter(className);
        for (int i = 0; i < n.size(); i++) {
            hcp.addToDL(n.getString(i));
            if (i < n.size()-1) {
                hcp.addToDL(" ");
            }
            else {
                hcp.addToDL(";\n");
            }
        }
        hcp.addToDL("\n");
        visit(n);
    }

    public void visitConstructorDeclaration(GNode n) {
        HeaderClassPrinter hcp = printer.getPrinter(className);
        hcp.addToDL(n.getNode(0).getString(0)+"("); // prints name
        Node constParams = n.getNode(1); // get constructorParameters node
        for (int i = 0; i < constParams.size(); i++) {
            Node constP = constParams.getNode(i);
            hcp.addToDL(constP.getString(0) + " " + constP.getString(1));
        }
        hcp.addToDL(");\n\n");
        visit(n);
    }

    public void visitDLMethodDeclaration(GNode n) {
        HeaderClassPrinter hcp = printer.getPrinter(className);
        hcp.addToDL(n.getNode(0).getString(0) + " " + n.getNode(1).getString(0) + " " + n.getNode(2).getString(0) + "("); // print modifier "static", return type, and method name
        Node methodParameters = n.getNode(3);
        StringBuffer s = addParamsToBuffer(methodParameters);
        hcp.addToDL(s + ";\n");
        visit(n);
    }

    public void visitVTDeclaration(GNode n) {
        HeaderClassPrinter hcp = printer.getPrinter(className);
        hcp.addToDL("};\n\n"); // end DataLayout
        vTableName = className + "_VT";
        hcp.setVTableName(vTableName);
        hcp.addToMethodDecs("struct " +  vTableName + " {\n"); // begin VTable
        visit(n);
    }

    public void visitVTMethod(GNode n) {
        buildMethodDecs(n);
        buildVTable(n);
        visit(n);
    }

    private void buildMethodDecs(GNode n) {
        HeaderClassPrinter hcp = printer.getPrinter(className);
        if (n.getNode(0).getString(0).equals("isa")) {
            hcp.addToMethodDecs(n.getNode(1).getString(0) + " " + n.getNode(0).getString(0) + ";\n");
        }
        else {
            hcp.addToMethodDecs(n.getNode(1).getString(0) + " (*" + n.getNode(0).getString(0) + ")(");
            StringBuffer s = addParamsToBuffer(n.getNode(3));
            hcp.addToMethodDecs(s + ";\n");
        }
    }

    private void buildVTable(GNode n) {
        HeaderClassPrinter hcp = printer.getPrinter(className);

        // if isa --> isa(__ClassName::__class())
        if (n.getNode(0).getString(0).equals("isa")) {
            hcp.addToVTable(n.getNode(0).getString(0) + "(" + className + "::__class()),\n");
        }

        else {
            // if method is inherited --> methodName((ReturnType(*)(Parameters)) &__ParentObject::methodName)
            if (!n.getNode(2).getString(0).equals(className.substring((2)))) {
                addInheritedMethod(n);
            }

            // if not inherited --> methodName(&__thisClass::methodName)
            else {
                addOwnMethod(n);
            }
        }
    }

    private void addInheritedMethod(GNode n) {
        HeaderClassPrinter hcp = printer.getPrinter(className);
        hcp.addToVTable(n.getNode(0).getString(0) + "((" + n.getNode(1).getString(0) + "(*)(");
        StringBuffer s = addParamsToBuffer(n.getNode(3));
        hcp.addToVTable(s + ") &__" + n.getNode(2).getString(0) + "::" + n.getNode(0).getString(0) + "),\n");
    }

    private void addOwnMethod(GNode n) {
        HeaderClassPrinter hcp = printer.getPrinter(className);
        hcp.addToVTable(n.getNode(0).getString(0) + "(&" + className + "::" + n.getNode(0).getString(0) + "),\n");
    }

    private StringBuffer addParamsToBuffer(Node params) {
        StringBuffer s = new StringBuffer();
        if (params.isEmpty()) {
            s.append(")");
        }
        else {
            for (int i = 0; i < params.size(); i++) {
                s.append(params.getString(i));
                if (i < params.size() - 1) {
                    s.append(", ");
                } else {
                    s.append(")");
                }
            }
        }
        return s;
    }

}
