package edu.nyu.oop.util;

import xtc.tree.Node;
import xtc.tree.GNode;
import xtc.tree.Visitor;
import xtc.util.Runtime;

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

        new Visitor() {
            public void visitClassDeclaration(GNode n){
                if(n.getNode(0) != null){
                    if(n.getNode(0).getNode(0).getString(0).equals("public")){
                        n.getNode(0).getNode(0).set(0,"namespace");
                    }
                }
            }

            public void visitMethodDeclaration(GNode n) {
                System.out.println("HIIIIIIIIIIIIIIIII Method Declarations");
//                System.out.println(n.getString(3));
                if (n.getString(3).equals("main")) {
                    System.out.println("HIIIIIIIIIIIIIIIII Main");
                    GNode modifiers = GNode.create("Modifiers");
                    n.set(0, modifiers);
                    GNode mainType = GNode.create("IntType");
                    n.set(2, mainType);
                    GNode formalParameters = GNode.create("FormalParameters");
                    n.set(4, formalParameters);
                }
                visit(n);
            }

            public void visitExpressionStatement(GNode n){
                GNode primaryIdentifier = GNode.create("PrimaryIdentifier","std");
                GNode selectionExpressionStart = GNode.create("SelectionExpression",primaryIdentifier,"cout");
                GNode selectionExpressionEnd = GNode.create("SelectionExpression",primaryIdentifier,"endl");
                GNode stringLiteral = GNode.create("StringLiteral", "\"Hello.\"");
                GNode callExpression = GNode.create("CallExpression",selectionExpressionStart,stringLiteral,selectionExpressionEnd);
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

            public void visit (Node n){
                for (Object o : n) {
                    if (o instanceof Node) dispatch((Node) o);
                }
            }

        }.dispatch(n);

        return n;
    }
}
