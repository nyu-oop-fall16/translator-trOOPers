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

            // Loop through classes and print data layout for each one
            for (Node cl: classes) {
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

            }


            /* #include statements
            namespace stuff
            struct declarations
            typedefs
            Data Layout:
                VTable pointer, field declaration
                constructor
                method declarations (all begin with static)
                static methods
                VTable initialization
            VTable:
                Class __isa
                method declarations ReturnType(name*)(Parameters)
                VTable () :
                    methodName(&__ClassName::methodName) */


            writer.close();

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }


    }

}
