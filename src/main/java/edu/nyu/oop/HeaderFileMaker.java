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
                Node vTable = NodeUtil.dfs(cl, "VTable");
                String vTableName = className + "_VT";

                writer.println("struct " + vTableName + " {");

                List <Node> vTableMethods = NodeUtil.dfsAll(vTable, "VTableMethodDeclaration");
                for (Node method: vTableMethods) {
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

                for(Node method: vTableMethods) {
                    writer.print(method.getNode(0).getString(0) + "(&" + className + "::" + method.get(1) + ")");
                    if (m < vTableMethods.size()-1) { writer.print(","); }
                    else { writer.print("{ }"); }
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
