package edu.nyu.oop;

import edu.nyu.oop.util.NodeUtil;
import xtc.tree.GNode;
import xtc.tree.Node;

import java.io.*;
import java.util.List;


// this class takes in a node representing a CPP AST and translates it into a CPP Header File
public class HeaderFileMaker {
    static File header;
    static PrintWriter writer;

    public static void makeHeaderFile(GNode n) {
        try {
            header = new File("output.h");
            writer = new PrintWriter(header);

            Node head = NodeUtil.dfs(n, "Head");

            // Prints namespace declarations and saves number of namespaces
            int numNameSpaces = printNamespaceDecs(head);

            // Prints Declarations and TypeDefs for all Classes
            printDecsAndTypeDefs(head);

            List<Node> classes = NodeUtil.dfsAll(n, "ClassDeclaration");

            // Loop through classes and print data layout and vTable for each one
            for (Node cl: classes) {
                String className = NodeUtil.dfs(cl, "ClassName").getString(0);

                // prints beginning of class struct
                writer.println("struct " + className + "{");

                Node dataLayout = NodeUtil.dfs(cl, "DataLayoutDeclaration");

                // prints data layout
                printDataLayout(dataLayout);
                writer.println("};");

                Node vTableDec = NodeUtil.dfs(cl, "VTDeclaration");

                // prints beginning of VTable struct
                String vTableName = className + "_VT";
                writer.println("struct " + vTableName + " {");

                // prints VTable method declarations
                printVTMethodDecs(vTableDec);

                // print VTable itself
                writer.println(vTableName + "() :");

               //Node vTable = NodeUtil.dfs(vTableDec, "VTable");

                //printVTable(vTable, className);
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

    private static int printNamespaceDecs(Node n) {
        List<Node> namespaces = NodeUtil.dfsAll(n, "NameSpaceDeclaration");
        int numNameSpaces = namespaces.size();
        for (Node ns: namespaces) {
            writer.println("namespace " + ns.getString(0) + " {");
        }
        return namespaces.size();
    }

    private static void printDecsAndTypeDefs(Node n) {
        for (Node dec: NodeUtil.dfsAll(n, "DeclarationsAndTypedef")) {
            for (int i = 0; i < 3; i++) {
                writer.println(dec.getString(i) + ";");
            }
        }
    }

    private static void printDataLayout(Node dataLayout) {
        // Prints all class field declarations
        Node fields = NodeUtil.dfs(dataLayout, "Fields");
        if (fields != null) {
            printClassFieldDecs(fields);
        }

        // Prints constructor
        Node constructor = NodeUtil.dfs(dataLayout, "ConstructorDeclaration");
        if (constructor != null) {
            printConstructor(constructor);
        }

        // Method Declarations-- all declarations are modifiers returnType Name Parameters
        Node dlMethodDecs = NodeUtil.dfs(dataLayout, "DLMethodDeclarations");
        if (dlMethodDecs != null) {
            printDLMethodDecs(dlMethodDecs);
        }
    }

    private static void printClassFieldDecs(Node fields) {
        for (Node field: NodeUtil.dfsAll(fields, "FieldDeclaration")) {
            for (int i = 0; i < field.size(); i++) {
                writer.print(field.getString(i));
                if (i == field.size()-1) { writer.print(";\n"); }
                else { writer.print(" "); }
            }
        }
    }

    private static void printConstructor(Node constructor) {
        writer.print(constructor.getNode(0).getString(0)+"("); // prints name
        for (Node p: NodeUtil.dfsAll(constructor.getNode(1), "ConstructorParameter")) {
            writer.print(p.getString(0) + " " + p.getString(1));
        }
        writer.print(");\n");
    }

    private static void printDLMethodDecs(Node dlMethodDecs) {
        List<Node> methods = NodeUtil.dfsAll(dlMethodDecs, "DLMethodDeclaration");
        for (Node method: methods) {
            writer.print(method.getNode(0).getString(0) + " " + method.getNode(1).getString(0) + " " + method.getNode(2).getString(0) + "("); // print modifier "static", return type, and method name
            Node methodParameters = method.getNode(3);
            for (int i = 0; i < methodParameters.size(); i++) {
                writer.print(methodParameters.getString(i));
                if (i < methodParameters.size()-1) { writer.print(" ,"); }
                else { writer.print(");\n"); }
            }
        }
    }

    private static void printVTMethodDecs(Node vTableDec) {
        Node vtMethodDecs = NodeUtil.dfs(vTableDec, "VTMethodDeclarations");
        List <Node> vTableMethodDecs = NodeUtil.dfsAll(vtMethodDecs, "VTMethodDeclaration");
        for (Node method: vTableMethodDecs) {
            writer.print(method.getNode(0).getString(0) + " (*" + method.getNode(1).getString(0) + ")("); // prints returntype and pointer with name
            Node parameters = NodeUtil.dfs(method, "Parameters");
            for (int i = 0; i < parameters.size(); i++) {
                writer.print(parameters.getString(i));
                if (i < parameters.size()-1) { writer.print(", "); }
                else { writer.print(");"); }
            }
            writer.println();
        }
    }

    private static void printVTable(Node vTable, String className) {
        List<Node> vTableMethods = NodeUtil.dfsAll(vTable, "VTMethod");

        for (int i = 0; i < vTableMethods.size(); i++) {
            Node method = vTableMethods.get(i); // each one is: MethodName - Parameters - Implementation Class - ReturnType
            String methodName = NodeUtil.dfs(method, "MethodName").getString(0);
            writer.print(methodName + "("); // print method name

            String superClassName = NodeUtil.dfs(method, "ImplementationClass").getString(0);
            if (!superClassName.equals(className)) { // for inherited methods
                writer.print("(" + NodeUtil.dfs(method, "ReturnType") + "(*)("); // print casting information
                Node methodParams = NodeUtil.dfs(method, "Parameters");
                for (int j = 0; j < methodParams.size(); j++) {
                    if (j < methodParams.size() - 1) {
                        writer.print(methodParams.getString(j) + ", ");
                    } else {
                        writer.print(methodParams.getString(j) + "))");
                    }
                }
                writer.print(" &__" + superClassName);
            } else {
                writer.print(" &__" + className);
            }

            writer.print(":: " + methodName + ")");

            if (i < vTableMethods.size() - 1) {
                writer.print(",\n");
            } else {
                writer.print("{\n}\n");
            }

        }
    }
}
