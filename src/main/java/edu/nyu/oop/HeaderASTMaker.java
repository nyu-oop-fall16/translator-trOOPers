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

    // Makes the AST and returns the root node
    public GNode makeAST() {
        GNode completeAST = GNode.create("AST");
        GNode head = createHead();
        completeAST.add(head);

        for (String s: classes.keySet()) {
            ClassInfo c = classes.get(s);
            // mangleNames(c);

            GNode thisClass = GNode.create("ClassDeclaration");
            GNode className = GNode.create("ClassName");
            className.add("__" + s);
            thisClass.add(className);

            DataLayout dl = new DataLayout(c);
            VTable vt = new VTable(c, classes);

            thisClass.add(dl.getRoot());
            thisClass.add(vt.getRoot());

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

    private void mangleMethodNames(ClassInfo c) {
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