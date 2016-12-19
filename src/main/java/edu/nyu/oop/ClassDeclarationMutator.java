package edu.nyu.oop;

import edu.nyu.oop.util.ChildToParentMap;
import xtc.tree.Node;
import xtc.tree.GNode;
import xtc.tree.Visitor;

import java.io.PrintWriter;

public class ClassDeclarationMutator extends Visitor {
    String className;
    String classExtension;
    ClassBodyMutator classBody;
    ChildToParentMap map;
    boolean isMain;

    /**
     * Constructs an object of ClassDeclarationMutator which holds the class's name and its class body.
     * @param n a ClassDeclaration node
     */
    private ClassDeclarationMutator(GNode n, ChildToParentMap map, boolean isMainClass) {
        this.map = map;
        this.className = n.getString(1);
        this.classExtension = (n.get(3) == null) ? null:n.getNode(3).getNode(0).getNode(0).getString(0);
        this.isMain = isMainClass; //  set true if class is main class
        super.dispatch(n); // dispatch to run visit to ClassBody node and set classBody to result
    }

    // create a new instance of class
    public static ClassDeclarationMutator getClassDeclarationMutator(GNode n, ChildToParentMap map, boolean isMain) {
        ClassDeclarationMutator newClass = new ClassDeclarationMutator(n, map, isMain);
        return newClass;
    }

    // visit class body and create a new instance of class body
    public void visitClassBody(GNode n) {
        if (!isMain) {
            this.classBody = ClassBodyMutator.regularClass(n, this.className, this.classExtension, map, isMain);
        } else {
            this.classBody = ClassBodyMutator.mainClass(n, map, isMain);
        }
    }

    /**
     * Dispatch to the children of a given root node.
     * @param n the root node given
     */
    public void visit(Node n) {
        for (Object o : n) {
            if (o instanceof Node) dispatch((Node) o);
        }
    }

    // prints the constructor for the class
    public void printConstructor(PrintWriter outputWriter) {
        outputWriter.println(classBody.constructor);
    }

    // prints the implementation of the __class() of each class
    public void printClassMethod(PrintWriter outputWriter) {
        StringBuffer classImplementation = new StringBuffer();
        classImplementation.append("Class ");
        String methodCall = "__" + className + "::__class(){";
        classImplementation.append(methodCall);
        String classType = "\n\tstatic Class k = new __Class(__rt::literal(\"java.lang.";
        classImplementation.append(classType);
        String classTypePrint = className;
        classImplementation.append(classTypePrint + "\"), (Class) ");
        String superType = (classExtension == null) ? "__Object" : "__" + classExtension;
        classImplementation.append(superType + "::__class());\n\treturn k;\n}");
        outputWriter.println(classImplementation);
    }

    // prints the implementation of vtable of each class
    public void printVTable(PrintWriter outputWriter) {
        StringBuffer vtable = new StringBuffer();
        String vt = "__" + className + "_VT ";
        vtable.append(vt);
        String vtCall = "__" + className + "::__vtable;";
        vtable.append(vtCall);
        outputWriter.println(vtable);
    }

}
