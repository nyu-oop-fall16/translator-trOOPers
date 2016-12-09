package edu.nyu.oop;

import xtc.tree.GNode;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by gina on 12/8/16.
 */
public class VTable {
    ClassInfo c;
    GNode root;
    private static ClassInfo object = ClassInfo.buildObject();
    private static HashMap<String,ClassInfo> classes = new HashMap<String,ClassInfo>();
    private static LinkedHashMap<String,GNode> methodMap = new LinkedHashMap<String,GNode>();

    public VTable(ClassInfo c, HashMap<String, ClassInfo> classes) {
        this.c = c;
        this.classes = classes;
        root = GNode.create("VTDeclaration");
        createVTable();
    }

    public GNode getRoot() {
        return root;
    }

    private void createVTable() {
        GNode vT = GNode.create("VTMethodDeclarations");

        //Fill the VTMethodDeclarations node
        makeVTMethod(c,c.getName());

        //System.out.println("The completed memory map looks as follows: " + methodMap.keySet());

        for(String s: methodMap.keySet()) {
            vT.add(methodMap.get(s));
        }
        methodMap.clear();

        root.add(vT);
    }

    // Creates and returns the node representing a single method in the VTable
    private void makeVTMethod(ClassInfo c, String className) {
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

}
