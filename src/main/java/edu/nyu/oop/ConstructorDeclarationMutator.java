package edu.nyu.oop;

import xtc.tree.GNode;
import xtc.tree.Visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public class ConstructorDeclarationMutator {
    String constructorCall; // ex __A::__A() for call to constructor A
    ArrayList<FieldDeclarationMutator> classfields;
    ArrayList<MethodDeclarationMutator> methods;
    HashMap<String, String>parameters;
    //*
    ArrayList<String> content;
    String classname;
    /*
        Assume given the node is the MethodDeclaration node
    */

    public ConstructorDeclarationMutator(GNode n) {
        //TODO: constructor info collected to fields, methods and parameters
    }

    public void setFields(ArrayList<FieldDeclarationMutator> classfiels){
        this.classfields=classfiels;
    }

    public void setClsasName(String s){
        classname=s;
    }

    public String addDefaultConstructor(String classname){
        String defaultConstructor="__"+ classname + "::__" + classname + "():__vptr(&__vtable){}";
        return defaultConstructor;
    }

    public String initConstructor(){
        String initWord="void __"+classname+"::__init(";

        for(Map.Entry<String,String>p:parameters.entrySet()){
            initWord+=p.getKey()+" "+p.getValue()+",";
        }
        if(initWord.lastIndexOf(",")==initWord.length()-1){
            initWord=initWord.substring(0,initWord.length()-1);
        }
        initWord+="){\n";

//        for(Map.Entry<String,String>e:init.entrySet()){
//            String word=e.getKey()+" = "+e.getValue()+";";
//            initWord+=word;
//        }

        initWord="}";
        return initWord;

    }

    public String getString(GNode n){

        content.add(addDefaultConstructor(classname));
        content.add(initConstructor());

        new Visitor(){
            public void visitMethodDeclaration(GNode n){
                //TODO: constructor info collected
                MethodDeclarationMutator m = new MethodDeclarationMutator(n);
                String s="";
                content.add(s);
            }

//            public void visit(Node n){
//                for (Object o : n) {
//                    if (o instanceof Node) dispatch((Node) o);
//                }
//            }
        }.dispatch(n);



        String contentString="";
        for(String s:content){
            contentString+=s;
        }
        return contentString;

    }




//    public static void mutateConstructor(GNode n){
//        new Visitor(){
//            public void visitConstructorDeclaration(GNode n){
//                //TODO: constructor info collected
//            }
//
//            /**
//             * Dispatch to the children of a given root node.
//             * @param n the root node given
//             */
//            public void visit(Node n){
//                for (Object o : n) {
//                    if (o instanceof Node) dispatch((Node) o);
//                }
//            }
//        }.dispatch(n);
//    }

    /**
     * Replace current node with one we create - simple nodes
     */
    public static void recreateConstructorNode(){

    }

    //TODO: implement init() methods
}
