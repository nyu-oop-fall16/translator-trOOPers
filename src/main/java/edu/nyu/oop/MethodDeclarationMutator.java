package edu.nyu.oop;

import java.io.PrintWriter;
import java.util.*;
import xtc.tree.Node;
import xtc.tree.Visitor;
import xtc.tree.GNode;

public class MethodDeclarationMutator extends Visitor {
    String returnType;
    String methodName;
    ArrayList<String[]> parameters;
    String className;
    BlockMutator block;
    MainBlockMutator mainBlock;
    boolean isMain;
    ArrayList<FieldDeclarationMutator> fields = new ArrayList<FieldDeclarationMutator>();
    static ArrayList<FieldDeclarationMutator> classFields = new ArrayList<FieldDeclarationMutator>();

    private MethodDeclarationMutator(GNode n, String returnType, String methodName, ArrayList<String[]> parameters, String className, boolean isMain, ArrayList<FieldDeclarationMutator> fields) {
        this.returnType = returnType;
        this.methodName = methodName;
        this.parameters = parameters;
        this.className = className;
        this.isMain = isMain;
        this.classFields = fields;
        super.dispatch(n);
    }

    /*
        mainly going to be used later when the implicit "this" of the class is needed. won't have the class type access here though
     */
    public void addImplicitThis() {
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
    public static MethodDeclarationMutator methodSignatureInfo(GNode n, String className, boolean isMain, ArrayList<FieldDeclarationMutator> fields) {
        // if VoidType then return "void", otherwise get the name of the return type
        String returnType = (n.getNode(2).getName() == "VoidType") ? "void":n.getNode(2).getNode(0).getString(0);
        String methodName = n.getString(3);
        ArrayList<String[]> parameters = new ArrayList<String[]>();
        parameters = Mutator.getFormalParameters(n);

        MethodDeclarationMutator newMethod = new MethodDeclarationMutator(n, returnType, methodName, parameters, className, isMain, fields);
        return newMethod;
    }

    public void printMethodImplementation(PrintWriter writer) {
        StringBuffer methodImplementation = new StringBuffer();
        if(isMain) {
            if(mainBlock != null) {
                methodImplementation.append("{\n\t");
                String beginBrace = mainBlock.beginBrace;
                methodImplementation.append(beginBrace);
                for(int j = 0; j < fields.size(); j++) {
                    methodImplementation.append(fields.get(j).fieldMember[0] + " " + fields.get(j).fieldMember[1] + " " + fields.get(j).fieldMember[2] + ";\n");
                }
                ArrayList<String> listOfFields = mainBlock.ListOfFields;
                if(listOfFields.size() > 0) {
                    for(int i = 0; i<listOfFields.size(); i++) {
                        if(!listOfFields.get(i).isEmpty()) {
                            methodImplementation.append(listOfFields.get(i) + ";\n");
                        }
                    }
                }

                ArrayList<String> expressions = mainBlock.expressionStatement;
                for (int e = 0; e < expressions.size(); e++) {
                    methodImplementation.append(expressions.get(e));
                }

                ArrayList<String> callExpressions = mainBlock.callExpression;
                for (int e = 0; e < callExpressions.size(); e++) {
                    methodImplementation.append(callExpressions.get(e));
                }

                String returnExpression = mainBlock.returnStatement;
                if (returnExpression != null) {
                    methodImplementation.append(returnExpression + ";\n");
                }
                methodImplementation.append("\nreturn 0;");
                methodImplementation.append("\n}");
                writer.println(methodImplementation);

                String endBrace = mainBlock.endBrace;
                methodImplementation.append(endBrace);
            }
        } else {
            methodImplementation.append(returnType + " ");
            String classSignature = "__" + className + "::" + methodName + "(";
            methodImplementation.append(classSignature);
            String parametersList = "";
            for (int p = 0; p < parameters.size(); p++) {
                parametersList += (parameters.get(p)[0] + " " + parameters.get(p)[1]);
                if (p != parameters.size() - 1) {
                    parametersList += ", ";
                }
            }
            if(block != null) {
                methodImplementation.append(parametersList + "){\n\t");
                ArrayList<FieldDeclarationMutator> fields = block.fields;
                for (int e = 0; e < fields.size(); e++) {
                    methodImplementation.append(fields.get(e).fieldMember[0] + " " + fields.get(e).fieldMember[1] + ";\n");

                }
                ArrayList<String> expressions = block.expressionStatement;
                for (int e = 0; e < expressions.size(); e++) {
                    methodImplementation.append(expressions.get(e));
                }

                ArrayList<String> callExpressions = block.callExpression;
                for (int e = 0; e < callExpressions.size(); e++) {
                    methodImplementation.append(callExpressions.get(e));
                }
//        methodImplementation.append(";\n");

                String returnExpression = block.returnStatement;
                if (returnExpression != null) {
                    methodImplementation.append(returnExpression + ";\n");
                }
            }
            methodImplementation.append("\n}");
            writer.println(methodImplementation);
        }
    }

    public void visitBlock(GNode n) {
        if(!isMain) {
            BlockMutator newBlock = BlockMutator.getBlock(n, parameters, className, isMain);
            this.block = newBlock;
        } else {
            MainBlockMutator newBlock = MainBlockMutator.getBlock(n, parameters, className, isMain);
            this.mainBlock = newBlock;
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
}
