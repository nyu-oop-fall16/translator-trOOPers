package edu.nyu.oop;

import edu.nyu.oop.util.ChildToParentMap;
import xtc.tree.Node;
import xtc.tree.GNode;
import xtc.tree.Visitor;

public class ClassDeclarationMutator extends Visitor {
    String className;
    String classExtension;
    ClassBodyMutator classBody;
    ChildToParentMap map;

    /**
     * Constructs an object of ClassDeclarationMutator which holds the class's name and its class body.
     * @param n a ClassDeclaration node
     */
    private ClassDeclarationMutator(GNode n, ChildToParentMap map){
        this.map = map;
        this.className = n.getString(1);
        this.classExtension = (n.get(3) == null) ? null:n.getNode(3).getNode(0).getNode(0).getString(0);
        super.dispatch(n); // dispatch to run visit to ClassBody node and set classBody to results
    }

    // create a new instance of class
    public static ClassDeclarationMutator getClassDeclarationMutator(GNode n, ChildToParentMap map){
        ClassDeclarationMutator newClass = new ClassDeclarationMutator(n,map);
        return newClass;
    }

    // visit class body and create a new instance of class body
    public void visitClassBody(GNode n){
        // call ClassBodyMutator
        this.classBody = ClassBodyMutator.classMethodAndConstructor(n, this.className, this.classExtension, map);
    }

    /**
     * Dispatch to the children of a given root node.
     * @param n the root node given
     */
    public void visit(Node n){
        for (Object o : n) {
            if (o instanceof Node) dispatch((Node) o);
        }
    }

    public void printConstructor(){
        System.out.println(classBody.constructor);
    }

    // prints the implementation of the __class() of each class
    public void printClassMethod(){
        StringBuffer classImplementation = new StringBuffer();
        classImplementation.append("Class ");
        String methodCall = "__" + className + "::__class(){";
        classImplementation.append(methodCall);
        String classType = "\n\tstatic Class k = new __Class(__rt::literal(\"java.lang.";
        classImplementation.append(classType);
        String superTypePrint = (classExtension == null) ? "Object":className;
        classImplementation.append(superTypePrint + "\"), (Class) ");
        String superType = (classExtension == null) ? "__Object" : "__" + classExtension;
        classImplementation.append(superType + "::__class();\n\treturn k;\n}");
        System.out.println(classImplementation);
    }

    // prints the implementation of vtable of each class
    public void printVTable(){
        StringBuffer vtable = new StringBuffer();
        String vt = "__" + className + "_VT ";
        vtable.append(vt);
        String vtCall = "__" + className + "::__vtable;";
        vtable.append(vtCall);
        System.out.println(vtable);
    }

}
