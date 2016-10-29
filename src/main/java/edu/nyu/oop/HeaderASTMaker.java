package edu.nyu.oop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;

public class HeaderASTMaker {
    // BuildInfo holds a list of classes that were defined in the Java file.
    private List<Node> packages = new ArrayList<Node>();
    public HashMap<String,ClassInfo> classes = new HashMap<String,ClassInfo>();

    // Include Import Statements!

    public GNode makeAST() {
        GNode completeAST = GNode.create("AST");
        GNode headerDec = GNode.create("HeaderDeclaration");
        GNode namespaceDec = GNode.create("NamespaceDeclaration");
        // package imports?
        headerDec.add(namespaceDec);

        for (String s: classes.keySet()) {
            ClassInfo c = classes.get(s);

            GNode thisClass = GNode.create("ClassDeclaration");
            GNode className = GNode.create("ClassName");
            className.add("__"+ c.getName().getString(0));
            thisClass.add(className);

            GNode dataLayout = createDataLayout(c);
            GNode vTable = createVTable(c);

            thisClass.add(dataLayout);
            thisClass.add(vTable);
            headerDec.add(thisClass);
        }
        completeAST.add(headerDec);
        return completeAST;
    }

    public void addPackage(Node n) {
        packages.add(n);
    }

    private GNode createDataLayout(ClassInfo c) {
        GNode dataLayout = GNode.create("Data Layout");
        GNode fields = GNode.create("Fields");

        // Make the field declarations
        for (Node f: c.getFields()) {
            fields.add(f);
        }

        dataLayout.add(fields);

        // Make the constructor
        GNode constructorDec = GNode.create("Constructor Declaration");
        constructorDec.add(c.getName());
        constructorDec.add(c.getConstructorParams());
        dataLayout.add(constructorDec);

        // Add Method Nodes to Data Layout
        GNode methodDecs = GNode.create("Method Declarations");
        for (String str: c.getMethods().keySet()) {
            GNode newMethod = GNode.create("D.L. Method Declaration");
            MethodInfo method = c.getMethods().get(str);

            newMethod.add(method.getReturnType());
            newMethod.add(method.getName().getString(0));

            GNode mod = GNode.create("Modifier");
            mod.add("static");
            newMethod.add(mod);

            for (Node n: method.getParameter()) {
                newMethod.add(n);
            }

            methodDecs.add(newMethod);
        }
        dataLayout.add(methodDecs);
        return dataLayout;
    }

    private GNode createVTable(ClassInfo c) {
        GNode vTable = GNode.create("VTable");
        for (String str: c.getMethods().keySet()) {
            GNode newMethod = GNode.create("V.T. Method Declaration");
            MethodInfo method = c.getMethods().get(str);

            newMethod.add(method.getReturnType());
            newMethod.add(method.getName().getString(0));

            GNode mod = GNode.create("Modifier");
            mod.add("static");
            newMethod.add(mod);

            for (Node n: method.getParameter()) {
                newMethod.add(n);
            }

            vTable.add(newMethod);
        }
        return vTable;


    }

}
