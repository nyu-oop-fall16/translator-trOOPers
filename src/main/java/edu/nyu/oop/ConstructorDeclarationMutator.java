package edu.nyu.oop;

import edu.nyu.oop.util.NodeUtil;
import javafx.util.Pair;
import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConstructorDeclarationMutator {
    String constructorCall; // ex __A::__A() for call to constructor A
    String classname;
    ArrayList<Pair<String, String>>fields;
    HashMap<String, String>parameters;
    HashMap<String, String> match;
    GNode theNode;
    HashMap<String, String>init;



    private ConstructorDeclarationMutator(GNode n, ArrayList fields){
        this.theNode= NodeUtil.deepCopyNode(n);
        this.fields=fields;
        this.classname=n.getString(2);
        new Visitor(){
            public void visitFieldDeclaration(GNode n){
                //TODO: constructor info collected


            }

            public void visitThisExpression(GNode n){
                if(n.get(0)==null){
                    n.set(0,"__this");
                }
                visit(n);
            }

            public void visitStringLiteral(GNode n){
                n.set(0,"new __String("+n.getString(0)+")");
                visit(n);
            }


            public void visitExpression(GNode n){
                visit(n);
                init.put(n.getNode(0).getNode(0).getString(0),n.getNode(0).getNode(0).getString(2));
            }

//            public void visitCallExpression(GNode n){
//
//            }


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


    /*
        Assume given the node is the ConstructorDeclaration node
    */
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

    public GNode addDefaultConstructor(String classname){
        String defaultConstructor="__"+ classname + "::__" + classname + "():__vptr(&__vtable){}";
        return GNode.create("DefaultConstructor",defaultConstructor);
    }

    public GNode initConstructor(){
//        GNode methodDeclaration=GNode.create("MethodDeclaration",GNode.create("Modifiers"),null,GNode.create("VoidType"),"__init",GNode.create("FormalParameters"));
//        methodDeclaration.add(GNode.create("FormalParameter",GNode.create("Modifiers"),GNode.create("Type")));
//        this.theNode=GNode.ensureVariable(this.theNode);
//        theNode.set(2,)

        String initWord="void __"+classname+"::__init(";

        for(Map.Entry<String,String>p:parameters.entrySet()){
            initWord+=p.getKey()+" "+p.getValue()+",";
        }
        if(initWord.lastIndexOf(",")==initWord.length()-1){
            initWord=initWord.substring(0,initWord.length()-1);
        }
        initWord+="){\n";

        for(Map.Entry<String,String>e:init.entrySet()){
            String word=e.getKey()+" = "+e.getValue()+";";
            initWord+=word;
        }

        initWord="}";
        return GNode.create("Constructor",initWord);

    }





    /**
     * Replace current node with one we create - simple nodes
     */
    public static void recreateConstructorNode(){

    }

    //TODO: implement init() methods
}
