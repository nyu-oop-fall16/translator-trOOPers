package edu.nyu.oop.util;

import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;

import java.util.Map;
import java.util.Set;

public class MutateJavaAst {

    // mutates the given java ast to reflect a c++ ast
    public static GNode mutate(GNode n) {
        new Visitor() {

            ChildToParentMap m;

            // visit ClassDeclaration and make following changes
            public void visitClassDeclaration(GNode n) {
                //put "test000"->ClassName("test000")
                String classname = n.getString(1);
                n.set(1,GNode.create("ClassName",classname));
                //
                if (!n.getNode(0).isEmpty()) { //if it's the testXXX class
                    if (n.getNode(0).getNode(0).getString(0).equals("public")) {
                        n.getNode(0).getNode(0).set(0, "namespace");
                    } else { //if it's class declaration that's not testXXX
                        //pass "__this" as parameter
                        Node methoddeclaration = n.getNode(5).getNode(0);
                        if (methoddeclaration.getNode(4).getName().equals("FormalParameters")) {
//                        System.out.println("formal parameters");
                            if (methoddeclaration.getNode(4).isEmpty()) {
                                GNode newparameter = GNode.create("FormalParameters", classname, "__this");
                                methoddeclaration.set(4, newparameter);
                            }
                        }

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
                //UPDATED
                // change MethodName from String type into GNode type e.g. "main"->MethodName("main")
                //so that it's accessible to the visitot in phase5 and could be printed in the right order
                String methodName=n.getString(3);
                GNode method=GNode.create("MethodName",methodName);
                n.set(3,method);


                // if main method, do adjustments to modifiers, type, and parameters to refect C++ main method signature
                if (n.getNode(3).getString(0).equals("main")) {
//                    System.out.println("HIIIIIIIIIIIIIIIII Main");
                    GNode modifiers = GNode.create("Modifiers");
                    n.set(0, modifiers);
                    GNode mainType = methodTypes("int");
                    n.set(2, mainType);
                    GNode formalParameters = GNode.create("FormalParameters");
                    n.set(4, formalParameters);
                }
                visit(n);
            }

            public void visitBlock(GNode n){
                try {
                    if (n.getNode(0).hasName("FieldDeclaration")) {
                        m = new ChildToParentMap(n);
                    }
                }catch (Exception e){

                }
                visit(n);
            }

            public void visitFieldDeclaration(GNode n) {
                Node newclass = n.getNode(2).getNode(0).getNode(2);
                if(newclass!=null){
                    if (newclass.getName().equals("NewClassExpression")) {
                    String classname = newclass.getNode(2).getString(0);
                    newclass.getNode(2).set(0, "__" + classname);

                    }
                }
                visit(n);
            }

            public void visitDeclarators(GNode n) {
                //e.g. B b=new __B();
                //A a2=b; -->> A a2=(A) b;
                //compares b's new declared class(A) with its original class(B)
                //if it doesn't match, cast B to A.
                Node declaratorsType = n.getNode(0).getNode(2);
                try{
                if (declaratorsType.hasName("PrimaryIdentifier")) {
                    String RHS = declaratorsType.getString(0);
                    System.out.println("RHS= " + RHS);
                    Node field = m.fetchParentFor(n);
                    String newclasstype = field.getNode(1).getNode(0).getString(0);
                    assert m != null;
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
                }catch (Exception e){
                }
                visit(n);
            }

            public void visitExpressionStatement(GNode n) {
                GNode callExpression=(GNode) n.getNode(0);
                if(callExpression.size()>3) {
                    GNode ncallExpression = printExpressionNode(callExpression.getNode(3));
                    n.set(0, ncallExpression);
                }

                visit(n);
            }

            public void visitCallExpression(GNode n){
                try {
                    if (n.getNode(3).hasName("Arguments")) {
                        String callname = n.getString(2);
                        GNode g = GNode.create("CallName", callname);
                        n.set(2, g);
                    }
                }catch (Exception e){

                }
                visit(n);
            }


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
