package edu.nyu.oop;

import edu.nyu.oop.util.NodeUtil;
import xtc.tree.GNode;
import xtc.tree.Node;

import java.util.ArrayList;

/**
 * Created by gina on 12/8/16.
 */
public class DataLayout {
    ClassInfo c;
    GNode root;

    public DataLayout(ClassInfo c) {
        this.c = c;
        root = GNode.create("DLDeclaration");
        createDataLayout(c);
    }

    public GNode getRoot() {
        return root;
    }

    private void createDataLayout(ClassInfo c) {
        GNode fields = GNode.create("Fields");
        GNode constructorDecs = GNode.create("ConstructorDeclarations");

        // Fill the field declarations
        root.add(makeAstFieldNodes(c));

        // Add constructors with parameters
        root.add(makeConstructorNodes(c));

        // Fill the method declarations node
        root.add(makeAstDLMethodNodes(c));
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
                params.add(parameterType.getString(0));
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


}
