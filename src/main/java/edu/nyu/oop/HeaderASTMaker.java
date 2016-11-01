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
    public ArrayList<Object> childrenArray = new ArrayList<Object>();
    public String fileName;

    public GNode makeAST() {
        GNode completeAST = GNode.create("AST");
        GNode head = createHead();
        completeAST.add(head);

        for (String s: classes.keySet()) {
            ClassInfo c = classes.get(s);

            GNode thisClass = GNode.create("ClassDeclaration");
            GNode className = GNode.create("ClassName");
            className.add("__" + s);
            thisClass.add(className);

            GNode dataLayout = createDataLayout(c);
            GNode vTable = createVTable(c);

            thisClass.add(dataLayout);
            thisClass.add(vTable);
            completeAST.add(thisClass);
        }
        return completeAST;
    }

    public void addPackage(Node n) {
        packages.add(n);
    }

    //Creates the Data Layout for a particular ClassInfo object c.
    private GNode createDataLayout(ClassInfo c) {
        GNode dataLayout = GNode.create("DataLayout");
        GNode fields = GNode.create("Fields");

        //Creates the vptr and the vtable declarations in the data layout
        GNode fieldVPTR = GNode.create("FieldDeclaration");
        fieldVPTR.add("__" + c.getName() + "_VT*");
        fieldVPTR.add("__vptr");
        GNode fieldVTable = GNode.create("FieldDeclaration");
        fieldVTable.add("static");
        fieldVTable.add("__" + c.getName() + "_VT");
        fieldVTable.add("__vtable");

        fields.add(fieldVPTR);
        fields.add(fieldVTable);

        // Make the field declarations
        for (String f: c.getFields()) {
            GNode fieldDec = GNode.create("FieldDeclaration");
            fieldDec.add(f);
            fields.add(fieldDec);
        }

        dataLayout.add(fields);

        // Make the constructor
        GNode constructorDec = GNode.create("ConstructorDeclaration");
        GNode constructorName = GNode.create("ConstructorName");
        constructorName.add("__" + c.getName());
        constructorDec.add(constructorName);
        constructorDec.add(c.getConstructorParams());
        dataLayout.add(constructorDec);

        // Add Method Nodes to Data Layout
        GNode methodDecs = GNode.create("DLMethodDeclarations");
        for (MethodInfo method: c.getMethods()) {
            GNode newMethod = GNode.create("DLMethodDeclaration");

            GNode mod = GNode.create("Modifier");
            mod.add("static");
            newMethod.add(mod);

            newMethod.add(method.getReturnType());

            GNode methodName = GNode.create("MethodName");
            methodName.add(method.getName());
            newMethod.add(methodName);

            GNode parameters = GNode.create("MethodParameters");
            parameters.add(c.getName());




            for (String parameter: method.getParameter()) {
                newMethod.add(parameter);
            }

            methodDecs.add(newMethod);
        }
        dataLayout.add(methodDecs);
        return dataLayout;
    }

    //Creates the VTable for a particular ClassInfo object c.
    private GNode createVTable(ClassInfo c) {
        GNode vTable = GNode.create("VTable");
        for (MethodInfo method: c.getMethods()) {
            GNode newMethod = GNode.create("VTMethodDeclaration");

            newMethod.add(method.getReturnType());
            newMethod.add(method.getName());

            GNode mod = GNode.create("Modifier");
            mod.add("static");
            newMethod.add(mod);

            for (String parameter: method.getParameter()) {
                newMethod.add(parameter);
            }

            vTable.add(newMethod);
        }
        return vTable;
    }

    //Creates the head of the file (changing the namespace) and declaring structs for the data layouts and vtables.
    //This method will be changed for part 2 when we have to worry about dependancies.
    private GNode createHead() {
        GNode head = GNode.create("Head");
        GNode nameSpace1 = GNode.create("NameSpaceDeclaration");
        GNode nameSpace2 = GNode.create("NameSpaceDeclaration");

        nameSpace1.add("inputs");
        nameSpace2.add(fileName);

        head.add(nameSpace1);
        nameSpace1.add(nameSpace2);

        for(String s: classes.keySet()) {
            head.add("struct __" + s + ";");
            head.add("struct __" + s + "_VT");
            head.add("typedef __" + s + "* " + s);
        }
        /*GNode children = GNode.create("children");
        for(Object o: childrenArray) {
            children.add(o);
        }
        head.add(children);*/

        return head;
    }
}
