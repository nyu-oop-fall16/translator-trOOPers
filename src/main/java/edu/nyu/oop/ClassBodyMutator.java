package edu.nyu.oop;

import xtc.tree.Node;
import xtc.tree.GNode;
import xtc.tree.Visitor;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

public class ClassBodyMutator extends Visitor {
    String className;
    ArrayList<ConstructorDeclarationMutator> constructors;
    ArrayList<MethodDeclarationMutator> methods;
    ArrayList<FieldDeclarationMutator> fields;

    /**
     * Constructs an object of ClassBodyMutator which holds the class's name and its class body.
     */
    private ClassBodyMutator(){
        this.constructors = new ArrayList<ConstructorDeclarationMutator>();
        this.methods = new ArrayList<MethodDeclarationMutator>();
        this.fields = new ArrayList<FieldDeclarationMutator>();
    }

    public static ClassBodyMutator classMethodAndConstructor(GNode n, String className){
        ClassBodyMutator newClass = new ClassBodyMutator();

        newClass.className = className;
        newClass.handleMethods(n); // handle methods
        newClass.handleConstructors(n); // handle constructors
        newClass.handleFields(n); // handle fields

        return newClass;
    }

    public void handleMethods(GNode n){
        new Visitor(){
            public void visitMethodDeclaration(GNode n) {
                methods.add(MethodDeclarationMutator.methodSignatureInfo(n)); // add each new method signature to method list
                MethodDeclarationMutator.mutateMethod(n); // call method to mutate the node to reflect C++

                visit(n);
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

    public void handleConstructors(GNode n){
        new Visitor(){
            public void visitConstructorDeclaration(GNode n) {
                // add each new constructor to constructor list

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

    public void handleFields(GNode n){
        new Visitor(){
            public void visitFieldDeclaration(GNode n) {
                // add each new field to feilds list

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
}
