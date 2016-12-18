package edu.nyu.oop;

import xtc.tree.Node;
import xtc.tree.Visitor;
import xtc.tree.GNode;
import java.util.ArrayList;

public class RunMutator extends Visitor{
    ArrayList<ClassDeclarationMutator> classes = new ArrayList<ClassDeclarationMutator>();
    ClassDeclarationMutator main;

    public void visit(Node n) {
        for (Object o : n) if (o instanceof Node) dispatch((Node) o);
    }

    public void visitClassDeclaration(GNode n){
        // if not main class
        if(!n.getString(1).startsWith("Test")){
            ClassDeclarationMutator newClass = ClassDeclarationMutator.getClassDeclarationMutator(n); // create an object
            classes.add(newClass);
        }
        // else if main class


        visit(n);
    }

    // return the list of classes and
    public ArrayList<ClassDeclarationMutator> getClasses(Node n){
        super.dispatch(n);
        return classes;
    }

    public ClassDeclarationMutator getMainClass(Node n) {
        super.dispatch(n);
        return main;
    }
}
