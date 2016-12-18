package edu.nyu.oop;

import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;

public class ConstructorDeclarationMutator {
    String constructorCall; // ex __A::__A() for call to constructor A

    /*
        Assume given the node is the MethodDeclaration node
    */
    public static void mutateConstructor(GNode n){
        new Visitor(){
            public void visitConstructorDeclaration(GNode n){
                //TODO: constructor info collected
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
        }.dispatch(n);
    }

    /**
     * Replace current node with one we create - simple nodes
     */
    public static void recreateConstructorNode(){

    }

    //TODO: implement init() methods
}
