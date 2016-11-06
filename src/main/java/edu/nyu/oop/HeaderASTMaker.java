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
    public static HashMap<String,ClassInfo> classes = new HashMap<String,ClassInfo>();
    public String fileName;
    public static ClassInfo object = ClassInfo.buildObject();

    public static HashMap <String,GNode> methodMap = new HashMap<String,GNode>();

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
        GNode dataLayout = GNode.create("DLDeclaration");

        GNode fields = GNode.create("Fields");
        GNode constructorDec = GNode.create("ConstructorDeclaration");
        GNode methodDecs = GNode.create("DLMethodDeclarations");

        // Fill the field declarations
        GNode fieldVPTR = GNode.create("FieldDeclaration");
        fieldVPTR.add("__" + c.getName() + "_VT*");
        fieldVPTR.add("__vptr");
        GNode fieldVTable = GNode.create("FieldDeclaration");
        fieldVTable.add("static");
        fieldVTable.add("__" + c.getName() + "_VT");
        fieldVTable.add("__vtable");

        fields.add(fieldVPTR);
        fields.add(fieldVTable);
        System.out.println(c.getFields());
        for (GNode f: c.getFields()) {
            GNode fieldDec = GNode.create("FieldDeclaration");
            fieldDec = f;
            fields.add(fieldDec);
        }

        // Fill the constructor node
        GNode constructorName = GNode.create("ConstructorName");
        constructorName.add("__" + c.getName());
        constructorDec.add(constructorName);
        constructorDec.add(c.getConstructorParams());

        // Fill the declarations node
        for (MethodInfo method: c.getMethods()) {
            GNode newMethod = makeDLMethod(c.getName(),"static", method.getName(), method.getReturnType().getString(0), method.getParameters());
            methodDecs.add(newMethod);
        }

        //Adding the class object
        GNode classObject = makeDLMethod(null, "static", "__class", "Class", null);
        methodDecs.add(classObject);

        //Adding fields, constructor, and method declaration nodes to the DL node
        dataLayout.add(fields);
        dataLayout.add(constructorDec);
        dataLayout.add(methodDecs);
        return dataLayout;
    }

    //Creates the VTable for a particular ClassInfo object c.
    private GNode createVTable(ClassInfo c) {
        GNode vTable = GNode.create("VTDeclaration");
        GNode vT = GNode.create("VTMethodDeclarations");

        //Fill the VTMethodDeclarations node
        makeVTMethod(c,c.getName());

        for(String s: methodMap.keySet()) {
            vT.add(methodMap.get(s));
        }
        methodMap.clear();

        vTable.add(vT);

        return vTable;
    }

    static void makeVTMethod(ClassInfo c, String className) {
        for(MethodInfo m: c.getMethods()) {
            if(!methodMap.containsKey(m.getName())) {
                GNode newMethod = GNode.create("VTMethod");

                GNode methodName = GNode.create("MethodName");
                GNode rType = GNode.create("ReturnType");
                GNode implementedClass = GNode.create("ImplementedClass");
                GNode params = GNode.create("MethodParameters");

                methodName.add(m.getName());
                rType.add(m.getReturnType().getString(0));
                implementedClass.add(c.getName());
                params.add(className);
                for (String parameter : m.getParameters()) {
                    params.add(parameter);
                }

                newMethod.add(methodName);
                newMethod.add(rType);
                newMethod.add(implementedClass);
                newMethod.add(params);

                methodMap.put(m.getName(), newMethod);
            }
        }
        if (c.getParent() == null) {}
        else if(c.getParent().equals("Object")) {
            makeVTMethod(object, className);
        }
        else {
            makeVTMethod(classes.get(c.getParent()), className);
        }
    }

    static GNode makeDLMethod(String className, String modifier, String name, String returnType, ArrayList<String> parameters) {
        GNode newMethod = GNode.create("DLMethodDeclaration");

        GNode mod = GNode.create("Modifier");
        GNode methodName = GNode.create("MethodName");
        GNode rType = GNode.create("ReturnType");
        GNode params = GNode.create("Parameters");


        mod.add(modifier);
        methodName.add(name);
        rType.add(returnType);
        if (className != null) {
            params.add(className);
        }
        if(parameters != null) {
            for (String parameter:parameters) {params.add(parameter);}
        }

        newMethod.add(mod);
        newMethod.add(methodName);
        newMethod.add(rType);
        newMethod.add(params);

        return newMethod;
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
            Node decs = GNode.create("DeclarationsAndTypedef");
            decs.add("struct __" + s);
            decs.add("struct __" + s + "_VT");
            decs.add("typedef __" + s + "* " + s);
            head.add(decs);
        }
        /*GNode children = GNode.create("children");
        for(Object o: childrenArray) {
            children.add(o);
        }
        head.add(children);*/

        return head;
    }
}