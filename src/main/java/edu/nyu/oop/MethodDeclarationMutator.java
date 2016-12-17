package edu.nyu.oop;

import java.util.*;
import xtc.tree.Node;
import xtc.tree.Visitor;
import xtc.tree.GNode;

public class MethodDeclarationMutator extends Visitor{
    String returnType;
    String methodName;
    ArrayList<String[]> parameters;

    private MethodDeclarationMutator(String returnType, String methodName, ArrayList<String[]> parameters){
        this.returnType = returnType;
        this.methodName = methodName;
        this.parameters = parameters;
    }

    /*
        mainly going to be used later when the implicit "this" of the class is needed. won't have the class type access here though
     */
    public void addToParameters(String[] newParameter){
        this.parameters.add(newParameter);
    }

    /**
     * Creates a MethodDeclarationMutator object with the signature info of the
     * @param n
     * @return
     */
    public static MethodDeclarationMutator methodSignatureInfo(GNode n){
        // if VoidType then return "void", otherwise get the name of the return type
        String returnType = (n.getTokenText() == "VoidType") ? "void":n.getNode(2).getNode(0).getString(0);
        String methodName = n.getString(3);
        ArrayList<String[]> parameters = new ArrayList<String[]>();
        parameters = Mutator.getFormalParameters(n);

        MethodDeclarationMutator newMethod = new MethodDeclarationMutator(returnType, methodName, parameters);
        return newMethod;
    }

    /*
        Assume given the node is the MethodDeclaration node
     */
    public static void mutateMethod(GNode n){
        new Visitor(){
            public void visitMethodDeclaration(GNode n){
                //TODO: mutate the method declaration node
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
