package edu.nyu.oop;

import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;

import java.util.ArrayList;

public class ConstructorDeclarationMutator extends Visitor{
    String className;
    static String returnType = "void";
    String initName;
    ArrayList<String[]> parameters = new ArrayList<String[]>();

    private ConstructorDeclarationMutator(String initName, ArrayList<String[]> parameters, String className){
        this.className = className;
        this.initName = initName;
        this.parameters = parameters;
    }

    /**
     * Creates a ConstructionDeclarationMutator object with the signature info of the
     * @param n
     * @return
     */
    public static ConstructorDeclarationMutator initSignatureInfo(GNode n, String className){
        String initName = "__" + className + "::__init";
        ArrayList<String[]> parameters = new ArrayList<String[]>();
        parameters = Mutator.getFormalParameters(n);

        ConstructorDeclarationMutator newInit = new ConstructorDeclarationMutator(initName, parameters, className);
        return newInit;
    }

    /* mainly going to be used later when the implicit "this" of the class is needed. won't have the class type access here though*/
    public void addImplicitThis(){
        String[] thisParameter = new String[2];
        thisParameter[0] = className;
        thisParameter[1] = "__this";
        parameters.add(0, thisParameter);
    }

    public void printInitMethod(){
        StringBuffer initImplementation = new StringBuffer();
        initImplementation.append(returnType + " " + initName + "(");
        String parametersList = "";
        for(int p = 0; p < parameters.size(); p++){
            parametersList += (parameters.get(p)[0] + " " + parameters.get(p)[1]);
            if(p != parameters.size()-1){
                parametersList += ", ";
            }
        }
        initImplementation.append(parametersList + "){\n");
        initImplementation.append("\tBLIOCK STUFF \n}");
        System.out.println(initImplementation);
    }

    /* Assume given the node is the ConstructorDeclaration node */
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

    //TODO: implement init() methods
}
