package edu.nyu.oop;

import edu.nyu.oop.util.NodeUtil;
import xtc.tree.GNode;
import xtc.tree.Node;

import java.io.*;
import java.util.List;


// this class takes in a node representing a CPP AST and translates it into a CPP Header File
public class HeaderFileMaker {
    File header;
    PrintWriter writer;

    public void makeHeaderFile(GNode n) {
        try {
            header = new File("translator-trOOPers\\output\\output.h");
            writer = new PrintWriter(header);

            Node head = NodeUtil.dfs(n, "Head");

            // Print namespace declarations
            List<Node> namespaces = NodeUtil.dfsAll(head, "NameSpaceDeclaration");
            int numNameSpaces = namespaces.size();
            for (Node ns: namespaces) {
                writer.println("namespace " + ns.getString(0) + " {");
            }

            // Prints Declarations and TypeDefs for all Classes
            for (Node dec: NodeUtil.dfsAll(head, "DeclarationsAndTypedef")) {
                for (int i = 0; i < 3; i++) {
                    writer.println(head.getString(i) + ";");
                }
            }

            List<Node> classes = NodeUtil.dfsAll(n, "ClassDeclaration");

            // Loop through classes and print data layout and vTable for each one
            for (Node cl: classes) {
                String className = NodeUtil.dfs(cl, "ClassName").getString(0);
                writer.println("struct " + className + "{");

                Node dataLayout = NodeUtil.dfs(cl, "DataLayout");

                // Prints all class field declarations
                Node fields = NodeUtil.dfs(dataLayout, "Fields");
                for (Node field: NodeUtil.dfsAll(fields, "FieldDeclaration")) {
                    for (int i = 0; i < field.size(); i++) {
                        writer.print(field.getString(0));
                        if (i == field.size()-1) { writer.print(";\n"); }
                    }
                }

                // Prints constructor
                Node constructor = NodeUtil.dfs(dataLayout, "Constructor");
                writer.print(constructor.getNode(0).getString(0)+"("); // prints name
                for (Node p: NodeUtil.dfsAll(constructor.getNode(1), "ConstructorParameter")) {
                    writer.print(p.getString(0) + " " + p.getString(1));
                }
                writer.print(");\n");

                // Method Declarations-- all declarations are modifiers returnType Name Parameters
                Node dlMethodDecs = NodeUtil.dfs(dataLayout, "DLMethodDeclarations");
                List<Node> methods = NodeUtil.dfsAll(dlMethodDecs, "DLMethodDeclaration");
                for (Node method: methods) {
                    writer.print(method.getNode(0).getString(0) + " " + method.getNode(1).getString(0) + method.getNode(2).getString(0)); // print modifier "static", return type, and method name
                    Node methodParameters = method.getNode(3);
                    for (int i = 0; i < methodParameters.size(); i++) {
                        writer.print(methodParameters.getString(i));
                        if (i < methodParameters.size()-1) { writer.print(" ,"); }
                        else { writer.print(");\n"); }
                    }
                }

                writer.println("}");

                //Prints VTable Method Declarations
                Node vTableDec = NodeUtil.dfs(cl, "VTDeclaration");
                String vTableName = className + "_VT";

                writer.println("struct " + vTableName + " {");

                Node vtMethodDecs = NodeUtil.dfs(vTableDec, "VTMethodDeclarations");
                List <Node> vTableMethodDecs = NodeUtil.dfsAll(vtMethodDecs, "VMethodDeclaration");
                for (Node method: vTableMethodDecs) {
                    writer.print(method.getNode(0).getString(0) + " (*" + method.getNode(1).getString(0) + ")("); // prints returntype and pointer with name
                    for (int i = 2; i < method.getNode(2).size(); i++) {
                        writer.print(method.getNode(i).getString(0));
                        if (i < method.size()-1) { writer.print(", "); }
                        else { writer.print(");"); }
                    }
                    writer.println();
                }

                writer.println(vTableName + "() :");

                // DONE THROUGH HERE
                Node vTable = NodeUtil.dfs(vTableDec, "VTable");

                List<Node> vTableMethods = NodeUtil.dfsAll(vTable, "VTMethod");

                for(int i = 0; i < vTableMethods.size(); i++) {
                    Node method = vTableMethods.get(i);
                    String methodName = method.getNode(0).getString(0);
                    writer.print(methodName +"("); // print method name

                    String superClassName = NodeUtil.dfs(method, "ImplementationClass").getString(0);
                    if (!superClassName.equals(className)) { // for inherited methods
                        writer.print("(" + NodeUtil.dfs(method, "ReturnType") + "(*)("); // print casting information
                        Node methodParams = NodeUtil.dfs(method, "Parameters");
                        for (int i = 0; i < methodParams.size(); i++) {
                            if (i < methodParams.size()-1) { writer.print(methodParams.getString(i) + ", "); }
                            else { writer.print(methodParams.getString(i) + "))"); }
                        }
                        writer.print(" &__" + superClassName);
                    }
                    else { writer.print(" &__" + className); }

                    writer.print(":: " + methodName + ")");

                    if (i < vTableMethods.size() - 1) {
                        writer.print(",\n");
                    }

                    else {
                        writer.print("{\n}\n");
                    }
                }
                writer.println("} ;");
            }

            // Put in end brackets for namespaces
            for (int i = 0; i < numNameSpaces; i++) {
                writer.println("}");
            }
            writer.close();

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }


    }
}
