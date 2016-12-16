package edu.nyu.oop;

import edu.nyu.oop.util.XtcProps;
import org.slf4j.Logger;
import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;

import java.io.*;

// This class visits all the nodes in a CPP AST. It saves relevant information into an AggregatedHeaderPrinter as it
// traverses the tree.

public class HeaderFileMaker extends Visitor {
    private File header;
    private PrintWriter writer;

    private int numNameSpaces;
    private String className;
    private String vTableName;

    private AggregatedHeaderPrinter printer;

    private Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    // This is the method that accesses the header file in order to overwrite it, creates a PrintWriter and an Aggregated
    // Header Printer, and calls the methods that print the information to the file. This is the method that is to be called
    // from Boot.java.
    public void runVisitor(GNode n) {
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

    // This method visits and saves the namespace declarations in the AST.
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

    // This method visits and saves the struct declarations and typedefs that are found at the beginning of every header file.
    public void visitDeclarationsAndTypedef(GNode n) {
        for (int i = 0; i < 3; i++) {
            printer.addToHeader(n.getString(i) + ";\n");
        }
        printer.addToHeader("\n");
        visit(n);
    }

    // This method visits the class declaration node, saves the class name to a list of classes, and saves the beginning of the
    // struct.
    public void visitClassDeclaration(GNode n) {
        className = n.getNode(0).getString(0); // get Class Name
        printer.addClass(className);
        HeaderClassPrinter hcp = printer.getPrinter(className);

        // prints beginning of class struct
        hcp.addToDL("struct " + className + " {\n");
        visit(n);
    }

    // This method visits and saves all of the fields in a given class to that class' HeaderClassPrinter.
    public void visitFieldDeclaration(GNode n) {
        HeaderClassPrinter hcp = printer.getPrinter(className);
        for (int i = 1; i < n.size(); i++) {
            Node fieldInfo = n.getNode(i);
            for (int j = 0; j < fieldInfo.size(); j++) {
                if (!fieldInfo.isEmpty() && !fieldInfo.getName().equals("FieldInitialization") && !fieldInfo.getString(j).equals("private")) {
                    hcp.addToDL(fieldInfo.getString(j));
                }
                if (fieldInfo.getName().equals("FieldInitialization") && fieldInfo.get(0) != null) {
                    hcp.addToDL("= " + fieldInfo.getString(j));
                }
            }
            if (i < n.size()-1) {
                hcp.addToDL(" ");
            } else {
                hcp.addToDL(";\n");
            }
        }
        hcp.addToDL("\n");
        visit(n);
    }

    // This method visits and saves the constructor of a given class to that class' HeaderClassPrinter.
    public void visitConstructorDeclarations(GNode n) {
        visit(n);
    }

    public void visitConstructorDeclaration(GNode n) {
        HeaderClassPrinter hcp = printer.getPrinter(className);
        hcp.addToDL(n.getNode(0).getString(0)+"("); // prints name
        Node constParams = n.getNode(1); // get constructorParameters node
        for (int i = 0; i < constParams.size(); i++) {
            Node constP = constParams.getNode(i);
            hcp.addToDL(constP.getNode(0).getString(0) + " " + constP.getNode(1).getString(0));
        }
        hcp.addToDL(");\n\n");
        visit(n);
    }

    // This method visits and saves all of the method declarations in a given class to that class' HeaderClassPrinter's
    // data layout.
    public void visitDLMethodDeclaration(GNode n) {
        HeaderClassPrinter hcp = printer.getPrinter(className);
        hcp.addToDL(n.getNode(0).getString(0) + " " + n.getNode(2).getString(0) + " " + n.getNode(1).getString(0) + "("); // print modifier "static", return type, and method name
        Node methodParameters = n.getNode(3);
        StringBuffer s = addParamsToBuffer(methodParameters);
        hcp.addToDL(s + ";\n");
        visit(n);
    }

    // This method visits and saves the beginning of the VTable struct to that class' HeaderClassPrinter's vTable declaration buffer.
    public void visitVTDeclaration(GNode n) {
        HeaderClassPrinter hcp = printer.getPrinter(className);
        hcp.addToDL("};\n\n"); // end DataLayout
        vTableName = className + "_VT";
        hcp.setVTableName(vTableName);
        hcp.addToMethodDecs("struct " +  vTableName + " {\n"); // begin VTable
        visit(n);
    }

    // This method visits every VTable Method and saves information relevant both to declaring it and to putting it in the VTable.
    public void visitVTMethod(GNode n) {
        buildMethodDecs(n);
        buildVTable(n);
        visit(n);
    }

    // This helper method saves the information needed for the VTable declaration of a given node to the correct class'
    // HeaderClassPrinter's method declarations.
    private void buildMethodDecs(GNode n) {
        HeaderClassPrinter hcp = printer.getPrinter(className);
        if (n.getNode(0).getString(0).equals("isa")) {
            hcp.addToMethodDecs(n.getNode(1).getString(0) + " " + n.getNode(0).getString(0) + ";\n");
        } else {
            hcp.addToMethodDecs(n.getNode(1).getString(0) + " (*" + n.getNode(0).getString(0) + ")(");
            StringBuffer s = addParamsToBuffer(n.getNode(3));
            hcp.addToMethodDecs(s + ";\n");
        }
    }

    // This helper method saves the information needed for the VTable listing of a given node to the correct class'
    // HeaderClassPrinter's vTable. It accounts for whether methods are inherited or not.
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

    // This helper method saves the correct VTable listing for an inherited method to the current class' HeaderClassPrinter's
    // vTable.
    private void addInheritedMethod(GNode n) {
        HeaderClassPrinter hcp = printer.getPrinter(className);
        hcp.addToVTable(n.getNode(0).getString(0) + "((" + n.getNode(1).getString(0) + "(*)(");
        StringBuffer s = addParamsToBuffer(n.getNode(3));
        hcp.addToVTable(s + ") &__" + n.getNode(2).getString(0) + "::" + n.getNode(0).getString(0) + "),\n");
    }

    // This helper method saves the correct VTable listing for a noninherited method to the current class' HeaderClassPrinter's
    // vTable.
    private void addOwnMethod(GNode n) {
        HeaderClassPrinter hcp = printer.getPrinter(className);
        hcp.addToVTable(n.getNode(0).getString(0) + "(&" + className + "::" + n.getNode(0).getString(0) + "),\n");
    }

    // This helper method saves the parameters from a given Node representing a method's parameters to a StringBuffer. The
    // method then returns this StringBuffer, where it will be added to the correct class' HeaderClassPrinter in the right place.
    private StringBuffer addParamsToBuffer(Node params) {
        StringBuffer s = new StringBuffer();
        if (params.isEmpty()) {
            s.append(")");
        } else {
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
