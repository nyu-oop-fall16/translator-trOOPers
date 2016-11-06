package edu.nyu.oop.util;

import xtc.tree.Node;
import xtc.tree.GNode;
import xtc.tree.Visitor;
import java.util.ArrayList;
import java.util.Set;
import java.util.Map;

public class MutateJavaAst extends Visitor {
    public static GNode mutate(GNode n) {

        // map of children given the root node of an ast
        final ChildToParentMap map = new ChildToParentMap(n);

        new Visitor() {
            public void visitClassDeclaration(GNode n) {
                System.out.println("In class declaration");
                // if Class name starts with "Test" then MethodDeclaration must have main method
                if(n.getString(1).startsWith("Test")) {
                    System.out.println("entering class declaration of main");
                }
                //if it's not Testxxx class
                else{
                    String classname = n.getString(1);
                    GNode classBody = (GNode)n.getNode(5); // go to the ClassBody of the class that doesn't have main
                    boolean addedDefault = false; // true when a DefaultConstructorDeclaration is added
                    boolean foundConstructor = false; // true when ConstructorDeclaration found in ClassBody of class that doesn't contain main method
                    int sizeOfClassBody = classBody.size();

                    // loop through children of ClassBody and check if there is a ConstructorDeclaration
                    for(int i=0;i<sizeOfClassBody;i++){
                        if(n.getNode(5).getNode(i).getName().equals("ConstructorDeclaration")) {
                            //if constructor declaration is found, mutate that one
                            foundConstructor = true;
                        }
                    }

                    //if constructor declaration is not found, create a default constructor declaration
                    if(!foundConstructor){
                        // make ClassBody a variable size instead of fixed so we can add children nodes
                        if(!classBody.hasVariable()){
                            classBody = GNode.ensureVariable(classBody);
                            n.set(5,classBody);
                        }

                        // create default constructor node and make it variable size
                        GNode constructorDeclaration = GNode.create("DefaultConstructorDeclaration", n.get(1));
                        if(!constructorDeclaration.hasVariable()){
                            constructorDeclaration = GNode.ensureVariable(constructorDeclaration);
                        }

                        for(int j=0; j<sizeOfClassBody;j++){
                            // put constructor declaration after field declaration if not already added
                            if (classBody.getNode(j).getName().equals("FieldDeclaration")){
                                if(!addedDefault){
                                    classBody.add(j + 1, constructorDeclaration);
                                    classBody.set(j + 1,constructorDeclaration);
                                    addedDefault = true;
                                }
                            }
                            // if there is no FieldDeclaration and DefaultConstructor not already added then add it
                            else{
                                if(!classBody.getNode(0).getName().equals("DefaultConstructorDeclaration")){
                                    classBody.add(0, constructorDeclaration);
                                    classBody.set(0,constructorDeclaration);
                                }
                            }
                        }
                    }

                    GNode modifiers = GNode.create("Modifiers");
                    GNode qualifiedIdentifier = GNode.create("QualifiedIdentifier","Class");
                    GNode type = GNode.create("Type", qualifiedIdentifier,null);
                    GNode methodName = GNode.create("MethodName","__" + classname + "::__class");
                    GNode formalParamaters = GNode.create("FormalParameters");
                    String javalang = "java.lang.";
                    GNode contents = GNode.create("Contents", "static Class k = new __Class(__rt::literal(" + javalang + classname + "), (Class)__" + "Object" + "::__class()");

                    boolean isExtended = false;

                    int sizeOfClassDeclaration = n.size();
                    for(int e=0;e<sizeOfClassDeclaration;e++){
                        try {
                            if (n.getNode(e) != null && n.getNode(e).getName().equals("Extension")) {
                                isExtended = true;
                            }
                        }
                        catch(ClassCastException exception){

                        }
                    }

                    if(isExtended) {
                        GNode extension = (GNode) n.getNode(3);
                        if (extension.getNode(0).getName().equals("Type")) {
                            GNode extensiontype = (GNode) extension.getNode(0);
                            if (extensiontype.getNode(0).getName().equals("QualifiedIdentifier")) {
                                String extendsFrom = extensiontype.getNode(0).getString(0);
                                contents = GNode.create("Contents", "static Class k = new __Class(__rt::literal(" + javalang + classname + "), (Class)__" + extendsFrom + "::__class()");
                            }
                        }

                    }

                    GNode returnstatement = GNode.create("ReturnStatement", "k");
                    GNode block = GNode.create("Block",contents, returnstatement);
                    GNode cInheritance = GNode.create("cInheritance", modifiers, null, type, methodName,formalParamaters,null,null,block);
                    classBody.add(cInheritance);
                    String vptr = "__" + classname + "_VT" + " __" + classname +"::" + " __vtable";
                    GNode vptrString = GNode.create("vptrString", vptr);
                    classBody.add(vptrString);

                }
                visit(n);
            }

            //no need to use parent to child in order to see if it is main class
            public void visitMethodDeclaration(GNode n) {
                System.out.println("In method declaration");

                // puts the method name into a MethodName node
                String methodNameNode = n.getString(3);
                GNode method = GNode.create("MethodName", methodNameNode);
                n.set(3,method);

                //visit method declaration in main
                if (n.getNode(3).getString(0).equals("main")) {
                    // get rid of modifiers by creating new empty node and replacing it
                    n.set(0, emptyNode("Modifiers"));
                    // changes main to int return type
                    GNode mainType = methodType("int");
                    n.set(2, mainType);
                    // takes out parameters by creating new empty node and replacing it
                    n.set(4, emptyNode("FormalParameters"));
                }
                //visiting method declaration that's not main
                else {
                    // empty modifiers
                    n.set(0, emptyNode("Modifiers"));

                    // parent is Class Body
                    GNode parentOfMethod = (GNode) map.fetchParentFor(n);

                    // grandparent is Class Declaration
                    GNode grandparentOfMethod = (GNode) map.fetchParentFor(parentOfMethod);

                    // get name of the class
                    String className = grandparentOfMethod.getString(1);

                    // rename for easier reading
                    GNode methodDeclaration = n;

                    //change method to __className :: methodName
                    try {
                        String methodName = methodDeclaration.getNode(3).getString(0);
                        String cppMethodName = "__" + className + "::" + methodName;
                        methodDeclaration.getNode(3).set(0, cppMethodName);
                    } catch (ClassCastException e) {
                        //method name is not a string
                        //should not happen
                    }

                    //add in FormalParameters classname __this
                    if (methodDeclaration.getNode(4).getName().equals("FormalParameters") && methodDeclaration.getNode(4).isEmpty()) {
                        GNode newParameter = GNode.create("FormalParameters", className + " __this");
                        methodDeclaration.set(4, newParameter);
                    }
                    //append __String to String in String Literal
                    if (methodDeclaration.getNode(7).getName().equals("Block")) {
                        GNode Block = (GNode) methodDeclaration.getNode(7);
                        if (Block.getNode(0).getName().equals("ReturnStatement")) {
                            GNode returnStatement = (GNode) Block.getNode(0);
                            if (returnStatement.getNode(0).getName().equals("StringLiteral")) {
                                String cppString = "__String";
                                GNode arguments = GNode.create("Arguments", className);
                                GNode cString = GNode.create("cString", cppString, arguments);
                                GNode NewClassExpression = GNode.create("NewClassExpression", cString);
                                returnStatement.set(0, NewClassExpression);
                            }
                        }
                    }
                }
                visit(n);
            }

            public void visitFieldDeclaration(GNode n) {
                System.out.println("In field declaration");
                //use child to parent map for that
                GNode parentOfField = (GNode)map.fetchParentFor(n);
                GNode grandparentOfField = (GNode)map.fetchParentFor(parentOfField);
                //visit field declaration in main
                try {
                    if (grandparentOfField.getNode(3).getString(0).equals("main")) {
                        System.out.println("visiting field declarations in main");
                        Node newclass = n.getNode(2).getNode(0).getNode(2);
                        if (newclass.getName().equals("NewClassExpression")) {
                            String classname = newclass.getNode(2).getString(0);
                            //if first node modifiers is empty change qualified identifier to append __
                            newclass.getNode(2).set(0, "__" + classname);

                        }
                    }
                    //visit field declaration not in main
                    else {
                        System.out.println("visiting field declarations not in main");
                    }
                }
                //If field declaration is not in main but also gives a null pointer exception when checking for main
                catch(NullPointerException e){
                    System.out.println("visiting field declarations not in main");
                }

                visit(n);
            }

            public void visitExpressionStatement(GNode n) {
                System.out.println("In expression statement");
                //if first node of Expression statement is call expression
                if(n.getNode(0).getName().equals("CallExpression")) {
                    System.out.println("In call expression");
                    GNode callExpression = printExpressionNode(n.getNode(0).getNode(3));
                    n.set(0, callExpression);
                }
                //if first node of Expression statement is not call expression
                //In test 003 and 006, first node of expression statement is Expression
                else{
                    //do nothing for now
                }
                visit(n);
            }

            public void visitConstructorDeclaration(GNode n){
                String constructorname = n.getString(2);
                System.out.println("In constructor declaration");
                //empty modifiers
                n.set(0, emptyNode("Modifiers"));

                // puts the constructors name into a MethodName node
                String methodNameNode = n.getString(2);
                GNode method = GNode.create("MethodName", methodNameNode);
                n.set(2,method);

                GNode classBody = (GNode) map.fetchParentFor(n);
                GNode classDeclaration = (GNode) map.fetchParentFor(classBody);

                String renamedConstructor = "__" + classDeclaration.getString(1) + "::" + "__" + classDeclaration.getString(1);
                n.set(2,renamedConstructor);

                GNode block = (GNode)n.getNode(5);
                if(!block.isEmpty()) {
                    if (block.getNode(0).getName().equals("ExpressionStatement")) {
                        GNode expressionStatement = (GNode) block.getNode(0);
                        if (expressionStatement.getNode(0).getName().equals("Expression")) {
                            GNode expression = (GNode) expressionStatement.getNode(0);

                            ArrayList<String> primaryIdentifierList = new ArrayList<String>();
                            //get size of expression and add primary identifiers to arraylist
                            int sizeOfExpression = expression.size();
                            for (int i = 0; i < sizeOfExpression; i++) {
                                try {
                                    if (expression.getNode(i).getName().equals("PrimaryIdentifier")) {
                                        GNode primaryIdentifier = (GNode) expression.getNode(i);
                                        String identifier = primaryIdentifier.getString(0);
                                        primaryIdentifierList.add(identifier);
                                    }
                                } catch (ClassCastException e) {
                                    //string cannot getName
                                }
                            }
                            if (expression.getNode(0).getName().equals("SelectionExpression")) {
                                //check if there is a this expression as first node
                                GNode selectionExpression = (GNode) expression.getNode(0);
                                if (selectionExpression.getNode(0).getName().equals("ThisExpression")) {
                                    String argument = selectionExpression.getString(1);
                                    String newArgument = primaryIdentifierList.get(0) + "(__this->" + argument + ")";
                                    GNode newExpression = GNode.create("Expression", newArgument, "__vptr(&__vtable)");
                                    expressionStatement.set(0, newExpression);
                                    //since there is a this expression, you also have to ad A__this to formal parameters
                                    if (n.getNode(3).getName().equals("FormalParameters")) {
                                        GNode formalParameters = (GNode) n.getNode(3);
                                        if (!formalParameters.isEmpty()) {
                                            //add a formal parameter node
                                            GNode modifiers = GNode.create("Modifiers");
                                            GNode qualifiedIdentifier = GNode.create("QualifiedIdentifier", constructorname);
                                            GNode type = GNode.create("Type", qualifiedIdentifier, null);
                                            GNode newFormalParameter = GNode.create("FormalParameter", modifiers, type, null, "__this", null);
                                            formalParameters.add(0, newFormalParameter);
                                        }
                                    }
                                }
                            }

                            if (primaryIdentifierList.size() == 2) {
                                GNode newExpression = GNode.create("Expression", primaryIdentifierList.get(0) + "(" + primaryIdentifierList.get(1) + ")", "__vptr(&__vtable)");
                                expressionStatement.set(0, newExpression);
                            }


                        }
                    }
                }


                visit(n);
            }

            public void visitDefaultConstructorDeclaration(GNode n){
                System.out.println("In default constructor declaration");
                String className = "__" + n.getString(0);
                String vptr = "__vptr(&__vtable)";
                n.set(0,className);
                n.add(className);
                n.add(vptr);
                visit(n);
            }

            ChildToParentMap m;
            public void visitBlock(GNode n) {
                if (!n.isEmpty()){
                    if (n.getNode(0).hasName("FieldDeclaration")) {
                        m = new ChildToParentMap(n);
                    }
                }
                visit(n);
            }

            public void visitDeclarators(GNode n) {
                //e.g. B b=new __B();
                //A a2=b; -->> A a2=(A) b;
                //compares b's new declared class(A) with its original class(B)
                //if it doesn't match, cast B to A.
                try {
                    Node declaratorsType = n.getNode(0).getNode(2);
                    if (declaratorsType.hasName("PrimaryIdentifier")) {
                        String RHS = declaratorsType.getString(0);
                        System.out.println("RHS= " + RHS);
                        Node field = m.fetchParentFor(n);
                        String newclasstype = field.getNode(1).getNode(0).getString(0);
                        assert (m != null);
                        Map<Node, Node> blockmap = m.getMap();
                        Set<Node> keys = blockmap.keySet();
                        for (Node i : keys) {
                            if (i.hasName("FieldDeclaration")) {
                                String LHS = i.getNode(2).getNode(0).getString(0);
                                if (LHS.equals(RHS)) {
                                    String originalclasstype = i.getNode(1).getNode(0).getString(0);
                                    if (!originalclasstype.equals(newclasstype)) {
                                        n.getNode(0).set(1, "(" + newclasstype + ")");
                                    }
                                    System.out.println("originalClasstype= " + originalclasstype);
                                }
                            }
                        }
                    }
                }
                catch (NullPointerException e){

                }
                visit(n);
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

        }.dispatch(n);

        return n;
    }

    // Below are methods to help shorten above code

    /**
     * Given the argument to be printed, returns a CallExpression node reformatted to reflect how things are printed in C++.
     * @param o the argument node describing what needs to be printed
     * @return node containing the information for printing
     */
    private static GNode printExpressionNode(Object o) {
        GNode primaryIdentifier = GNode.create("PrimaryIdentifier","std");
        GNode selectionExpressionStart = GNode.create("SelectionExpression",primaryIdentifier,"cout");
        GNode selectionExpressionEnd = GNode.create("SelectionExpression",primaryIdentifier,"endl");
        GNode printLine = NodeUtil.deepCopyNode((GNode)o);
        GNode callExpression = GNode.create("CallExpression",selectionExpressionStart,printLine,selectionExpressionEnd);
        return callExpression;
    }

    /**
     * Given a return type as a string, a node housing the return type of a method is returned.
     * @param str the return type of a method as a String
     * @return a node housing the return type of a method
     */
    private static GNode methodType(String str){
        GNode qualifiedIdentifier = GNode.create("QualifiedIdentifier", str);
        GNode type = GNode.create("Type", qualifiedIdentifier);
        return type;
    }

    /**
     * Returns an empty node given the name of the empty node.
     * @param str name of the node
     * @return and empty (childless) node with the name given by str
     */
    private static GNode emptyNode(String str){
        GNode empty = GNode.create(str);
        return empty;
    }
}