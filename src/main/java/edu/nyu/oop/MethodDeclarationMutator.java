package edu.nyu.oop;

import java.util.*;
import xtc.tree.Node;
import xtc.tree.Visitor;
import xtc.tree.GNode;

public class MethodDeclarationMutator extends Visitor{
    String returnType;
    String methodName;
    ArrayList<String[]> parameters;
    String className;

    private MethodDeclarationMutator(String returnType, String methodName, ArrayList<String[]> parameters, String className){
        this.returnType = returnType;
        this.methodName = methodName;
        this.parameters = parameters;
        this.className = className;
    }

    /*
        mainly going to be used later when the implicit "this" of the class is needed. won't have the class type access here though
     */
    public void addImplicitThis(){
        String[] thisParameter = new String[2];
        thisParameter[0] = className;
        thisParameter[1] = "__this";
        parameters.add(0, thisParameter);
    }

    /**
     * Creates a MethodDeclarationMutator object with the signature info of the
     * @param n
     * @return
     */
    public static MethodDeclarationMutator methodSignatureInfo(GNode n, String className){
        // if VoidType then return "void", otherwise get the name of the return type
        String returnType = (n.getNode(2).getName() == "VoidType") ? "void":n.getNode(2).getNode(0).getString(0);
        String methodName = n.getString(3);
        ArrayList<String[]> parameters = new ArrayList<String[]>();
        parameters = Mutator.getFormalParameters(n);

        MethodDeclarationMutator newMethod = new MethodDeclarationMutator(returnType, methodName, parameters, className);
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

    public void printMethodImplementation(){
        StringBuffer methodImplementation = new StringBuffer();
        methodImplementation.append(returnType + " ");
        String classSignature = "__" + className + "::" + methodName + "(";
        methodImplementation.append(classSignature);
        String parametersList = "";
        for(int p = 0; p < parameters.size(); p++){
            parametersList += (parameters.get(p)[0] + " " + parameters.get(p)[1]);
            if(p != parameters.size()-1){
                parametersList += ", ";
            }
        }
        methodImplementation.append(parametersList + "){\n");
        methodImplementation.append("\tBLIOCK STUFF \n}");
        System.out.println(methodImplementation);
    }
}
