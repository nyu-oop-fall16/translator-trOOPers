package edu.nyu.oop.util;

import xtc.tree.Node;
import xtc.tree.GNode;
import xtc.tree.Visitor;
import edu.nyu.oop.util.ChildToParentMap;


/**
 * Created by AnnaChiu on 10/26/16.
 */
public class MutateJavaAst extends Visitor {
    GNode astGNode;
//    Runtime runtime;

//    public MutateJavaAst(GNode node){
//       this.astGNode = node;
//    }

    //    public MutateJavaAst(Runtime runtime){
//        this.runtime = runtime;
//    }

    public static GNode mutate(GNode n) {

        final ChildToParentMap map = new ChildToParentMap(n);

        new Visitor() {
            public void visitClassDeclaration(GNode n) {
                // if Class name starts with "Test" then MethodDeclaration must be main....
                if(n.getString(1).startsWith("Test")) {
                    System.out.println("entering class declaration of main");
                }
//                if (!n.getNode(0).isEmpty()) {//if it's main class
//                    System.out.println("entering class declaration of main");
                    // don't have to make it namespace
//                    if (n.getNode(0).getNode(0).getString(0).equals("public")) {
//                        n.getNode(0).getNode(0).set(0, "namespace");
//
//                    }
//                }
                else{ //if it's not in main


//                    int sizeOfClassDeclaration = n.size();
                    GNode classBody = (GNode)n.getNode(5);
                    int sizeOfClassBody = classBody.size();
                    boolean addedDefault = false;
                    boolean foundConstructor = false;
//                    for(int i=0;i<sizeOfClassDeclaration;i++){
                    for(int i=0;i<sizeOfClassBody;i++){
//                        System.out.println(sizeOfClassBody);
//                        System.out.println("Class Declaration Child Name: " + n.getNode(5).getNode(i).getName());
                        if(n.getNode(5).getNode(i).getName().equals("ConstructorDeclaration")){
                            //if constructor declaration is found, mutate that one
                            foundConstructor = true;
                        }
                        //if constructor declaration is not found, create a default constructor declaration
//                        else{
//                            // make ClassBody a variable size instead of fixed
//                            if(!classBody.hasVariable()){
//                                classBody = GNode.ensureVariable(classBody);
//                                n.set(5,classBody);
//                            }
//
//                            GNode constructorDeclaration = GNode.create("DefaultConstructorDeclaration");
//
//                            //put constructor declaration after field declaration
////                            for(int j=0; j<sizeOfClassBody;j++){
//                                if (classBody.getNode(i).getName().equals("FieldDeclaration") && !addedDefault) {
//                                    classBody.add(i + 1, constructorDeclaration);
//                                    addedDefault = true;
//                                }
//
//                                if (!classBody.getNode(i).getName().equals("FieldDeclaration") && !classBody.getNode(i).getName().equals("DefaultConstructorDeclaration"))
//                                        classBody.add(0, constructorDeclaration);
//
//                                System.out.println("Size of class body: " + addedDefault);
////                            }
//                        }
                    }
                    if(!foundConstructor){
                        // make ClassBody a variable size instead of fixed
                        if(!classBody.hasVariable()){
                            classBody = GNode.ensureVariable(classBody);
                            n.set(5,classBody);
                        }

                        // create default constructor node
                        GNode constructorDeclaration = GNode.create("DefaultConstructorDeclaration");

                        //put constructor declaration after field declaration
                        for(int j=0; j<sizeOfClassBody;j++){
                            if (classBody.getNode(j).getName().equals("FieldDeclaration") && !addedDefault) {
                                classBody.add(j + 1, constructorDeclaration);
                                addedDefault = true;
                            }
                            if (!classBody.getNode(0).getName().equals("FieldDeclaration") && !classBody.getNode(0).getName().equals("DefaultConstructorDeclaration"))
                                classBody.add(0, constructorDeclaration);
                            }
                    }

//                    //create ptr constructor
//                    String classname = n.getString(1);
//                    GNode constructor = GNode.create("ConstructorDeclaration", "__" + classname, "__vptr", "&__vtable");
//
//                    //create class method declaration
//                    GNode para1 = GNode.create("para1", "__rt::literal", "java.lang." + classname);
//                    GNode para2 = GNode.create("para2", "(Class)__Object::__class()");
//                    GNode newclass = GNode.create("__Class", para1, para2);
//                    GNode newclassdecla = GNode.create("Return", "new", "__Class", newclass);
//                    GNode classmethoddecla = GNode.create("GetClass", GNode.create("Modifier", "static"), GNode.create("ReturnType", "Class"), GNode.create("ReturnVariable", "k"), newclassdecla);
//                    GNode returnblock = GNode.create("Block", classmethoddecla, GNode.create("ReturnStatement", "k"));
//                    GNode classmethod = GNode.create("ClassMethodDeclaration", returnblock);
//
//                    //vtable
//                    GNode typequalified = GNode.create("QualifiedIdentifier", "__" + classname + "__VT");
//                    GNode classmethodtype = GNode.create("Type", typequalified, null);
//                    GNode declarator = GNode.create("Declarator", "_vtable", null, GNode.create("PrimaryIdentifier"));
//                    GNode declarators = GNode.create("Declarators", declarator);
//                    GNode vtabledeclaration = GNode.create("VtableDeclaration", GNode.create("Modifiers"), classmethodtype, declarators);
//
//
//                    GNode classbody = GNode.create("ClassBody", constructor, methoddeclaration, classmethod, vtabledeclaration);
//                    n.set(5, classbody);

                }
                visit(n);
            }

            //no need to use parent to child in order to see if it is main class
            public void visitMethodDeclaration(GNode n) {
                //visit method declaration in main
                if (n.getString(3).equals("main")) {
                    //changes main to int type and takes out String[] args by creating new node and setting it
                    System.out.println("visiting method declarations in main");
                    GNode modifiers = GNode.create("Modifiers");
                    n.set(0, modifiers);
//                    GNode mainType = GNode.create("IntType");
                    GNode mainType = methodTypes("int");
                    n.set(2, mainType);
                    GNode formalParameters = GNode.create("FormalParameters");
                    n.set(4, formalParameters);
                }
                //visiting method declaration not in main
//                else{
//                    System.out.println("visiting method declarations not in main");
//                }
                //visiting method declaration not in main
                else {
                    System.out.println("visiting method declarations not in main");
                    //empty modifiers
                    GNode emptyModifiers = GNode.create("Modifiers");
                    n.set(0,emptyModifiers);

                    //parent is Class Body
                    GNode parentOfMethod = (GNode)map.fetchParentFor(n);
                    //grandparent is Class Declaration
                    GNode grandparentOfMethod = (GNode)map.fetchParentFor(parentOfMethod);
                    String classname = grandparentOfMethod.getString(1);
                        GNode methoddeclaration = n;
                        //change method to __classname :: method
                        try {
                            String original = methoddeclaration.getString(3);
                            String mutated = "__" + classname + "::" + original;
                            methoddeclaration.set(3, mutated);
                        }
                        catch(ClassCastException e){
                            //method node is not a string
                            //should not happen
                        }

                        //add in FormalParameters classname __this
                        if (methoddeclaration.getNode(4).getName().equals("FormalParameters") && methoddeclaration.getNode(4).isEmpty()) {
                            GNode newparameter = GNode.create("FormalParameters", classname + " __this");
                            methoddeclaration.set(4, newparameter);
                        }
                        //append __String to String in String Literal
                        if(methoddeclaration.getNode(7).getName().equals("Block")){
                            GNode Block = (GNode)methoddeclaration.getNode(7);
                            if(Block.getNode(0).getName().equals("ReturnStatement")){
                                GNode returnStatement = (GNode)Block.getNode(0);
                                if(returnStatement.getNode(0).getName().equals("StringLiteral")){
                                    GNode stringLiteral = (GNode)returnStatement.getNode(0);
                                    String original = stringLiteral.getString(0);
                                    String mutated = "__String";
                                    GNode arguments = GNode.create("Arguments", classname);
                                    GNode cString = GNode.create("cString", mutated, arguments);
                                    GNode NewClassExpression = GNode.create("NewClassExpression",cString);
                                    returnStatement.set(0,NewClassExpression);
                                }
                            }
                        }

//                        //ptr constructor
//                        GNode constructor = GNode.create("ConstructorDeclaration", "__" + classname, "__vptr", "&__vtable");
//
//                        //class method declartion
//                        GNode para1 = GNode.create("para1", "__rt::literal", "java.lang." + classname);
//                        GNode para2 = GNode.create("para2", "(Class)__Object::__class()");
//                        GNode newcla = GNode.create("__Class", para1, para2);
//                        GNode newclassdecla = GNode.create("Return", "new", "__Class", newcla);
//                        GNode classmethoddecla = GNode.create("GetClass", GNode.create("Modifier", "static"), GNode.create("ReturnType", "Class"), GNode.create("ReturnVariable", "k"), newclassdecla);
//                        GNode returnblock = GNode.create("Block", classmethoddecla, GNode.create("ReturnStatement", "k"));
//                        //                    GNode classmethod=GNode.create("ClassMethodDeclaration",GNode.create("Modifiers","static"),class_classmethodtype,class_declarators);
//                        GNode classmethod = GNode.create("ClassMethodDeclaration", returnblock);
//
//                        //vtable
//                        GNode typequalified = GNode.create("QualifiedIdentifier", "__" + classname + "__VT");
//                        GNode classmethodtype = GNode.create("Type", typequalified, null);
//                        GNode declarator = GNode.create("Declarator", "_vtable", null, GNode.create("PrimaryIdentifier"));
//                        GNode declarators = GNode.create("Declarators", declarator);
//                        GNode vtabledeclaration = GNode.create("VtableDeclaration", GNode.create("Modifiers"), classmethodtype, declarators);
//
//
//                        GNode classbody = GNode.create("ClassBody", constructor, methoddeclaration, classmethod, vtabledeclaration);
//                        n.set(5, classbody);
                    }

                visit(n);
            }

            public void visitFieldDeclaration(GNode n) {

                //use child to parent map for that
                GNode parentOfField = (GNode)map.fetchParentFor(n);
                GNode grandparentOfField = (GNode)map.fetchParentFor(parentOfField);
                //visit field declaration in main
                //System.out.println("hiiii" + grandparentOfField);
                try {
                    if (grandparentOfField.getString(3).equals("main")) {
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
                //If field declartion is not in main but also gives a null pointer exception when checking for main
                catch(NullPointerException e){
                    System.out.println("visiting field declarations not in main");
                }

                visit(n);
            }

            public void visitExpressionStatement(GNode n) {
                //if first node of Expression statement is call expression
                if(n.getNode(0).getName().equals("CallExpression")) {
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

//            public void visitModifiers(GNode n) {
////                while (!n.isEmpty()) {
////                    n.get(0);
////                }
////                for(int i = 0; i < n.size(); i++) {
//                    System.out.println("HIIIIIIIIIIIIIIIII Modifiers");
////                }
//                visit(n);
//            }

            public void visit(Node n) {
                for (Object o : n) {
                    if (o instanceof Node) dispatch((Node) o);
                }
            }

        }.dispatch(n);

        return n;
    }

    // method used whenever something has to be printed
    private static GNode printExpressionNode(Object o) {
        GNode primaryIdentifier = GNode.create("PrimaryIdentifier","std");
        GNode selectionExpressionStart = GNode.create("SelectionExpression",primaryIdentifier,"cout");
        GNode selectionExpressionEnd = GNode.create("SelectionExpression",primaryIdentifier,"endl");
        GNode printLine = NodeUtil.deepCopyNode((GNode)o);
        GNode callExpression = GNode.create("CallExpression",selectionExpressionStart,printLine,selectionExpressionEnd);
        return callExpression;
    }

    private static GNode methodTypes(Object o){
        GNode qualifiedIdentifier = GNode.create("QualifiedIdentifier", o);
        GNode type = GNode.create("Type", qualifiedIdentifier);
        return type;
    }
}
