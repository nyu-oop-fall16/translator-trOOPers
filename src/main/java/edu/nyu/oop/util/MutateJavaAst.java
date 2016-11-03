package edu.nyu.oop.util;

import xtc.tree.Node;
import xtc.tree.GNode;
import xtc.tree.Visitor;

public class MutateJavaAst {

    // mutates the given java ast to reflect a c++ ast
    public static GNode mutate(GNode n) {
        new Visitor() {
            // visit ClassDeclaration and make following changes
            public void visitClassDeclaration(GNode n) {
                if (!n.getNode(0).isEmpty()) { //if it's the testXXX class
                    if (n.getNode(0).getNode(0).getString(0).equals("public")) {
                        n.getNode(0).getNode(0).set(0, "namespace");
                    } else { //if it's class declaration that's not testXXX
                        String classname = n.getString(1);
                        //pass "__this" as parameter
                        Node methoddeclaration = n.getNode(5).getNode(0);
                        if (methoddeclaration.getNode(4).getName().equals("FormalParameters")) {
//                        System.out.println("formal parameters");
                            if (methoddeclaration.getNode(4).isEmpty()) {
                                GNode newparameter = GNode.create("FormalParameters", classname, "__this");
                                methoddeclaration.set(4, newparameter);
                            }
                        }

                        System.out.println("yayyyyyyyyyyyyyyyyyyyyy");

                        GNode constructor = GNode.create("ConstructorDeclaration", "__" + classname, "__vptr", "&__vtable");

                        GNode para1 = GNode.create("para1", "__rt::literal", "java.lang." + classname);
                        GNode para2 = GNode.create("para2", "(Class)__Object::__class()");
                        GNode newcla = GNode.create("__Class", para1, para2);
                        GNode newclassdecla = GNode.create("Return", "new", "__Class", newcla);
                        GNode classmethoddecla = GNode.create("GetClass", GNode.create("Modifier", "static"), GNode.create("ReturnType", "Class"), GNode.create("ReturnVariable", "k"), newclassdecla);
//                    Block(
//                    ReturnStatement(
//                            PrimaryIdentifier(
//                                    "fld"
//                            )
//                    )
                        GNode returnblock = GNode.create("Block", classmethoddecla, GNode.create("ReturnStatement", "k"));

//                    GNode classmethod=GNode.create("ClassMethodDeclaration",GNode.create("Modifiers","static"),class_classmethodtype,class_declarators);
                        GNode classmethod = GNode.create("ClassMethodDeclaration", returnblock);

                        GNode typequalified = GNode.create("QualifiedIdentifier", "__" + classname + "__VT");
                        GNode classmethodtype = GNode.create("Type", typequalified, null);
                        GNode declarator = GNode.create("Declarator", "_vtable", null, GNode.create("PrimaryIdentifier"));
                        GNode declarators = GNode.create("Declarators", declarator);
                        GNode vtabledeclaration = GNode.create("VtableDeclaration", GNode.create("Modifiers"), classmethodtype, declarators);


                        GNode classbody = GNode.create("ClassBody", constructor, methoddeclaration, classmethod, vtabledeclaration);
                        n.set(5, classbody);
                    }
                }
                visit(n);
            }

            public void visitMethodDeclaration(GNode n) {
                System.out.println("HIIIIIIIIIIIIIIIII Method Declarations");
//                System.out.println(n.getString(3));

                // if main method, do adjustments to modifiers, type, and parameters to refect C++ main method signature
                if (n.getString(3).equals("main")) {
                    System.out.println("HIIIIIIIIIIIIIIIII Main");
                    GNode modifiers = GNode.create("Modifiers");
                    n.set(0, modifiers);
//                    GNode mainType = GNode.create("IntType");
                    GNode mainType = methodTypes("int");
                    n.set(2, mainType);
                    GNode formalParameters = GNode.create("FormalParameters");
                    n.set(4, formalParameters);
                }
                visit(n);
            }

            public void visitFieldDeclaration(GNode n) {
                System.out.println("HIIIIIIIIIIIIIIIII Field Declarations");
                Node newclass = n.getNode(2).getNode(0).getNode(2);
                if (newclass.getName().equals("NewClassExpression")) {
                    String classname = newclass.getNode(2).getString(0);
                    newclass.getNode(2).set(0, "__" + classname);

                }

//                if(n.getNode(2).getNode(0).getNode(2).getName().equals("NewClassExpression")) {
//                    Node newclass = n.getNode(2).getNode(0).getNode(2).getNode(2);
//                    String classname = newclass.getString(0);
//                    newclass.set(0, "__" + classname);
//                }
                visit(n);
            }

            public void visitExpressionStatement(GNode n) {
                GNode callExpression = printExpressionNode(n.getNode(0).getNode(3));
                n.set(0, callExpression);
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

            // implement method for visiting the nodes
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
        GNode printLine = NodeUtil.deepCopyNode((GNode) o);
        GNode callExpression = GNode.create("CallExpression",selectionExpressionStart,printLine,selectionExpressionEnd);
        return callExpression;
    }

    // method to change the types
    private static GNode methodTypes(Object o){
        GNode qualifiedIdentifier = GNode.create("QualifiedIdentifier", o);
        GNode type = GNode.create("Type", qualifiedIdentifier);
        return type;
    }
}
