package edu.nyu.oop;

import xtc.tree.Node;
import xtc.tree.GNode;
import xtc.tree.Visitor;

public class ClassDeclarationMutator extends Visitor {
    String className;
    ClassBodyMutator classBody;

    /**
     * Constructs an object of ClassDeclarationMutator which holds the class's name and its class body.
     * @param n a ClassDeclaration node
     */
    private ClassDeclarationMutator(GNode n){
        this.className = n.getString(1);
        super.dispatch(n); // dispatch to run visit to ClassBody node and set classBody to results
    }

    // create a new instance of class
    public static ClassDeclarationMutator getClassDeclarationMutator(GNode n){
        ClassDeclarationMutator newClass = new ClassDeclarationMutator(n);
        return newClass;
    }

    // visit class body and create a new instance of class body
    public void visitClassBody(GNode n){
        // call ClassBodyMutator
        this.classBody = ClassBodyMutator.classMethodAndConstructor(n, this.className);
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
}
