package edu.nyu.oop;

import javafx.util.Pair;
import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;

import java.util.ArrayList;

public class ClassBodyMutator extends Visitor {
    String className;
    ArrayList<ConstructorDeclarationMutator> constructors;
    ArrayList<MethodDeclarationMutator> methods;
    ArrayList<Pair<String,String>>fields;

    /**
     * Constructs an object of ClassBodyMutator which holds the class's name and its class body.
     */
    private ClassBodyMutator(){
        this.constructors = new ArrayList<ConstructorDeclarationMutator>();
        this.methods = new ArrayList<MethodDeclarationMutator>();
    }

    public static ClassBodyMutator classMethodAndConstructor(GNode n, String className){
        ClassBodyMutator newClass = new ClassBodyMutator();

        newClass.className = className;

        ArrayList<MethodDeclarationMutator> methodList = newClass.methods;
        newClass.handleMethods(n, methodList); // handle methods

        ArrayList<ConstructorDeclarationMutator> constructorList = newClass.constructors;
        newClass.handleConstructors(n, constructorList); // handle constructors

        return newClass;
    }

    public void handleMethods(GNode n, final ArrayList<MethodDeclarationMutator> methods){
        new Visitor(){
            public void visitMethodDeclaration(GNode n) {
                methods.add(MethodDeclarationMutator.methodSignatureInfo(n)); // add each new method signature to method list
                MethodDeclarationMutator.mutateMethod(n); // call method to mutate the node to reflect C++
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
        for(int i=0;i<n.size();i++){
            if(n.getNode(i).hasName("FieldDeclaration")){
                Pair<String, String> a= new Pair<>(n.getNode(i).getNode(1).getString(0), n.getNode(i).getNode(2).getNode(0).getString(0));
                fields.add(a);
            }

        }

    }

    public void handleConstructors(GNode n, final ArrayList<ConstructorDeclarationMutator> constructors){
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
}
