package edu.nyu.oop;

import edu.nyu.oop.util.NodeUtil;
import xtc.tree.GNode;
import xtc.tree.Node;

import java.io.*;
import java.util.List;


// this class takes in a node representing a CPP AST and translates it into a CPP Header File
public class HeaderFileMaker {

    public void makeHeaderFile(GNode n) {
        try {
            File header = new File("translator-trOOPers\\output\\output.h");
            PrintWriter writer = new PrintWriter(header);

            List<Node> classes = NodeUtil.dfsAll(n, "ClassDeclaration");
            List<Node> classNames = NodeUtil.dfsAll(n, "ClassName");
            List<Node> vTableNames = NodeUtil.dfsAll(n, "VTableName");

            // Struct declarations
            for (int i = 0; i < classNames.size(); i++) {
                writer.println("struct " + classNames.get(i).getString(0));
                writer.println("struct " + vTableNames.get(i).getString(0));
            }

            // TypeDefs
            for (Node c: classNames) {
                writer.println("typedef " + c.getName() + " *" + c.getName().substring(2));
            }

            // Loop through classes and print data layout and vTable for each one
            for (int idx = 0; idx < classes.size(); idx++) {
                Node cl = classes.get(idx);
                writer.println("struct " + NodeUtil.dfs(cl, "ClassName").getString(0) + "{");
                writer.println(NodeUtil.dfs(cl, "VTableName") + " *__vptr;");

                Node clDataLayout = NodeUtil.dfs(cl, "DataLayout");

                // Print constructor
                Node constructor = NodeUtil.dfs(clDataLayout, "Constructor");
                writer.print(NodeUtil.dfs(constructor, "ClassName") + "(");
                List<Node> params = NodeUtil.dfsAll(constructor, "FormalParameter");
                for (Node p: params) {
                    writer.print(NodeUtil.dfs(p, "Qualified Identifier").getString(0) + " " + p.getString(2)); // name of parameter is not saved in name node, saved as string in FormalParameter node
                }
                writer.print(");\n");

                // Method Declarations
                List<Node> methods = NodeUtil.dfsAll(clDataLayout, "DLMethodDeclaration");
                for (Node method: methods) {
                    writer.print("static " + NodeUtil.dfs(method, "ReturnType").getString(0) + " " + method.get(1) + "(");
                    for (int i = 2; i < method.size()-1; i++) {
                        writer.print(method.getNode(i).getString(0) + ", ");
                    }
                    writer.print(method.getNode(method.size()-1).getString(0) + ");");
                    writer.println();
                }

                // Class object declaration (?) Idk if we need this
                writer.println("static Class __class();");

                // VTable declaration
                writer.println("static " + vTableNames.get(idx) + " __vtable;");

                writer.println("}");

                //Prints VTable Method Declarations
                Node vTable = NodeUtil.dfs(cl, "VTable");
                writer.println("struct " + vTableNames.get(idx) + " {");
                writer.println("Class __isa");
                List <Node> vTableMethods = NodeUtil.dfsAll(vTable, "VTableMethodDeclaration");
                for (Node method: vTableMethods) {
                    writer.print(NodeUtil.dfs(method, "ReturnType").getString(0) + " (*" + method.get(1) + ")(");
                    for (int i = 2; i < method.size()-1; i++) {
                        writer.print(method.getNode(i).getString(0) + ", ");
                    }
                    writer.print(method.getNode(method.size()-1).getString(0) + ");");
                    writer.println();
                }

                writer.println(vTableNames.get(idx) + "() :");
                writer.println("__isa(" + classNames.get(idx) + "::__class()");

                for(int m = 0; m < vTableMethods.size(); m++) {
                    Node method = vTableMethods.get(0);
                    writer.print(method.get(1) + "(&" + classNames.get(idx) + "::" + method.get(1) + ")");
                    if (m < vTableMethods.size()-1) { writer.print(","); }
                    else { writer.print("{ }"); }
                }
                writer.println("} ;");
            }
            
            writer.close();

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }


    }

}
