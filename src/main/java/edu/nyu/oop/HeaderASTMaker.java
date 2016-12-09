package edu.nyu.oop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import edu.nyu.oop.util.NodeUtil;

import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;

public class HeaderASTMaker {
    // HeaderASTMaker holds a list of classes that were defined in the Java file.
    private List<Node> packages = new ArrayList<Node>();
    private static HashMap<String,ClassInfo> classes = new HashMap<String,ClassInfo>();
    private String fileName;
    private static ClassInfo object = ClassInfo.buildObject();
    private static LinkedHashMap <String,GNode> methodMap = new LinkedHashMap<String,GNode>();

    // Makes the AST and returns the root node
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

    // Adds a given package node to the list of packages belonging to this class
    public void addPackage(Node n) {
        packages.add(n);
    }


    // Gets the HashMap of classes belonging to this AST
    public HashMap<String, ClassInfo> getClasses() {
        return classes;
    }


    // Gets a class belonging to this AST
    public ClassInfo getClass(String className) {
        return classes.get(className);
    }

    // Adds a class to this AST
    public void addClass(String className, ClassInfo c) {
        classes.put(className, c);
    }

    // Adds a method to a given class belonging to the AST
    public void addMethodToClass(String className, MethodInfo m) {
        ClassInfo c = classes.get(className);
        c.addMethod(m);
    }

    // Gets the name of the file currently being worked on
    public String getFileName() {
        return fileName;
    }

    // Sets the name of the file currently being worked on
    public void setFileName(String s) {
        fileName = s;
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
            GNode decs = GNode.create("DeclarationsAndTypedef");
            decs.add("struct __" + s);
            decs.add("struct __" + s + "_VT");
            decs.add("typedef __" + s + "* " + s);
            head.add(decs);
        }
        return head;
    }

    // Creates and returns the node representing the Data Layout for a particular ClassInfo object c.
    private GNode createDataLayout(ClassInfo c) {
        GNode dataLayout = GNode.create("DLDeclaration");

        GNode fields = GNode.create("Fields");
        GNode constructorDecs = GNode.create("ConstructorDeclarations");

        // Fill the field declarations
        dataLayout.add(makeAstFieldNodes(c));

        // Add constructors with parameters
        dataLayout.add(makeConstructorNodes(c));

        // Fill the method declarations node
        dataLayout.add(makeAstDLMethodNodes(c));

        return dataLayout;
    }

    // Creates and returns the node encapsulating all of the fields of the class
    private GNode makeAstFieldNodes(ClassInfo c) {
        GNode fields = GNode.create("Fields");
        for (GNode f: c.getFields()) {
            GNode fieldDec = GNode.create("FieldDeclaration");
            fieldDec = f;
            fields.add(fieldDec);
        }
        return fields;

    }

    // Creates and returns the node encapsulating all of the constructors of the class
    private GNode makeConstructorNodes(ClassInfo c) {
        GNode constructorDecs = GNode.create("ConstructorDeclarations");

        for (GNode constructorParam: c.getConstructors())  {
            constructorDecs.add(makeConstructor(c, constructorParam));
        }

        return constructorDecs;
    }

    // Creates and returns the node representing a single constructor of the given class
    private GNode makeConstructor(ClassInfo c, GNode constructorParam) {
        GNode constDec = GNode.create("ConstructorDeclaration");
        GNode constName = GNode.create("ConstructorName");
        constName.add("__" + c.getName());
        constDec.add(constName);
        constDec.add(constructorParam);
        return constDec;
    }

    // Creates and returns the node encapsulating all of the methods in the Data Layout of the class
    private GNode makeAstDLMethodNodes(ClassInfo c) {
        GNode methodDecs = GNode.create("DLMethodDeclarations");

        for (GNode constructor: c.getConstructors()) {
            GNode initMethod = makeInitMethod(c.getName(), "static", "__init", "void", constructor);
            methodDecs.add(initMethod);
        }

        for (MethodInfo method: c.getMethods()) {
            GNode newMethod = makeDLMethod(c.getName(),"static", method.getName(), method.getReturnType().getString(0), method.getParameters());
            methodDecs.add(newMethod);
        }

        GNode classObject = makeDLMethod(null, "static", "__class", "Class", null);
        methodDecs.add(classObject);
        return methodDecs;
    }

    // Creates and returns the node representing a single init method
    static GNode makeInitMethod(String className, String modifier, String name, String returnType, GNode parameters) {
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
            for (Node parameterType : NodeUtil.dfsAll(parameters, "Type")) {
                params.add(parameterType);
            }
        }

        newMethod.add(mod);
        newMethod.add(methodName);
        newMethod.add(rType);
        newMethod.add(params);

        return newMethod;
    }

    // Creates and returns the node representing a single method in the Data Layout
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
            for (String parameter:parameters) {
                params.add(parameter);
            }
        }

        newMethod.add(mod);
        newMethod.add(methodName);
        newMethod.add(rType);
        newMethod.add(params);

        return newMethod;
    }

    //Creates and returns the node representing the VTable for a particular ClassInfo object c.
    private GNode createVTable(ClassInfo c) {
        GNode vTable = GNode.create("VTDeclaration");
        GNode vT = GNode.create("VTMethodDeclarations");

        //Fill the VTMethodDeclarations node
        makeVTMethod(c,c.getName());

        //System.out.println("The completed memory map looks as follows: " + methodMap.keySet());

        for(String s: methodMap.keySet()) {
            vT.add(methodMap.get(s));
        }
        methodMap.clear();

        vTable.add(vT);

        return vTable;
    }

    // Creates and returns the node representing a single method in the VTable
    static void makeVTMethod(ClassInfo c, String className) {
        if (c.getParent() == null) {}
        else if(c.getParent().equals("Object")) {
            makeVTMethod(object, className);
        }
        else {
            makeVTMethod(classes.get(c.getParent()), className);
        }
        for(MethodInfo m: c.getMethods()) {
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

//            System.out.println("The method " + m.getName() + " has been added to " + className + " by " + c.getName());
//            System.out.println("Now methodMap looks like: " + methodMap.keySet());
    }

    private void nameMangler(ClassInfo c) {
        HashMap<String,Boolean> nameCheck = new HashMap<String,Boolean>();
        for(MethodInfo m: c.getMethods()) {
            if(!nameCheck.containsKey(m.getName())) {
                nameCheck.put(m.getName(),false);
            } else {
                nameCheck.put(m.getName(),true);
            }

        }
        for(String s: nameCheck.keySet()) {
            if(nameCheck.get(s) == true) {
                for(MethodInfo m: c.getMethods()) {
                    String mangledName = m.getName();
                    for(String st: m.getParameters()) {
                        mangledName+= st;
                    }
                    m.setName(mangledName);
                }
            }
        }
    }
}