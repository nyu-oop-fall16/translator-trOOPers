package edu.nyu.oop;

import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;

import java.io.PrintWriter;
import java.util.ArrayList;

public class ConstructorDeclarationMutator extends Visitor {
    String className;
    String classExtension;
    static String returnType = "void";
    String initName;
    ArrayList<String[]> parameters = new ArrayList<String[]>();
    BlockMutator block;
    ArrayList<FieldDeclarationMutator> classFields;
    boolean isMain;

    private ConstructorDeclarationMutator(GNode n, String initName, ArrayList<String[]> parameters, String className, ArrayList<FieldDeclarationMutator> classFields,String classExtension,boolean isMain) {
        this.className = className;
        this.initName = initName;
        this.parameters = parameters;
        this.classFields = classFields;
        this.classExtension = classExtension;
        this.isMain = isMain;
        super.dispatch(n);
    }

    private ConstructorDeclarationMutator(String initName, ArrayList<String[]> parameters, String className, ArrayList<FieldDeclarationMutator> classFields, String classExtension, boolean isMain) {
        this.className = className;
        this.initName = initName;
        this.parameters = parameters;
        this.classFields = classFields;
        this.classExtension = classExtension;
        this.isMain = isMain;
    }

    /**
     * Creates a ConstructionDeclarationMutator object with the signature info of the
     *
     * @param n
     * @return
     */
    public static ConstructorDeclarationMutator initSignatureInfo(GNode n, String className, ArrayList<FieldDeclarationMutator> classFields, String classExtension, boolean isMain) {
        String initName = "__" + className + "::__init";
        ArrayList<String[]> parameters = new ArrayList<String[]>();
        parameters = Mutator.getFormalParameters(n);

        ConstructorDeclarationMutator newInit = new ConstructorDeclarationMutator(n, initName, parameters, className, classFields, classExtension,isMain);
        return newInit;
    }

    public static ConstructorDeclarationMutator defaultInitSignatureInfo(String className, ArrayList<FieldDeclarationMutator> classFields, String classExtension,boolean isMain) {
        String initName = "__" + className + "::__init";
        ArrayList<String[]> parameters = new ArrayList<String[]>();

        ConstructorDeclarationMutator newInit = new ConstructorDeclarationMutator(initName, parameters, className, classFields, classExtension,isMain);
        return newInit;
    }

    /* mainly going to be used later when the implicit "this" of the class is needed. won't have the class type access here though*/
    public void addImplicitThis() {
        String[] thisParameter = new String[2];
        thisParameter[0] = className;
        thisParameter[1] = "__this";
        parameters.add(0, thisParameter);
    }


    public void visitBlock(GNode n) {
        BlockMutator newBlock = BlockMutator.getBlock(n, parameters, className, isMain);
        this.block = newBlock;
    }

    public void printInitImplementation(PrintWriter outputWriter) {
        StringBuffer initImplementation = new StringBuffer();
        initImplementation.append(returnType + " ");
        initImplementation.append(initName);
        String parametersList = "(";
        for (int p = 0; p < parameters.size(); p++) {
            parametersList += (parameters.get(p)[0] + " " + parameters.get(p)[1]);
            if (p != parameters.size() - 1) {
                parametersList += ", ";
            }
        }
        initImplementation.append(parametersList + "){\n\t");

        initImplementation.append(callToSuperInit(className, classExtension, parameters));

        // if the class fields have declarators then set the values of those fields in the init methods
        for (int f = 0; f < classFields.size(); f++) {
            String[] declarator = classFields.get(f).declators;
            if (declarator[1] != null) {
                initImplementation.append("__this->" + declarator[0] + " = ");
                if (classFields.get(f).fieldMember[0].equals("String")) {
                    initImplementation.append("new __String(" + declarator[1] + ");");
                } else {
                    initImplementation.append(declarator[1]);
                }
            }
        }

        if (block != null) {
            ArrayList<FieldDeclarationMutator> fields = block.fields;
            for (int e = 0; e < fields.size(); e++) {
                initImplementation.append(fields.get(e).fieldMember[0] + " " + fields.get(e).fieldMember[1]);
            }

            ArrayList<String> expressions = block.expressionStatement;
            for (int e = 0; e < expressions.size(); e++) {
                initImplementation.append(expressions.get(e));
            }

            ArrayList<String> callExpressions = block.callExpression;
            for (int e = 0; e < callExpressions.size(); e++) {
                initImplementation.append(callExpressions.get(e));
            }

            String returnExpression = block.returnStatement;
            if (returnExpression != null) {
                initImplementation.append(returnExpression + ";\n");
            }
        }

        initImplementation.append("\n}");
        outputWriter.println(initImplementation);
    }

    /**
     * Dispatch to the children of a given root node.
     *
     * @param n the root node given
     */
    public void visit(Node n) {
        for (Object o : n) {
            if (o instanceof Node) dispatch((Node) o);
        }
    }

    // method to input a call to super class's init()
    public StringBuffer callToSuperInit(String className, String classExtension, ArrayList<String[]> parameters) {
        StringBuffer call = new StringBuffer();
        if (classExtension != null) {
            call.append("__" + classExtension + "::__init(");
            for (int p = 0; p < parameters.size(); p++) {
                if (parameters.get(p)[1].equals("__this")) {
                    call.append("(" + classExtension + ")");
                }
                call.append(parameters.get(p)[1]);
                if (p < parameters.size() - 1) {
                    call.append(",");
                }
            }
            call.append(");\n");
        }
        return call;
    }
}
