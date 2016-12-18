package edu.nyu.oop;

import xtc.tree.Node;
import xtc.tree.Visitor;
import xtc.tree.GNode;
import java.util.ArrayList;

public class RunMutator extends Visitor{
    ArrayList<ClassDeclarationMutator> classes = new ArrayList<ClassDeclarationMutator>();
    ClassDeclarationMutator main;
    ArrayList<String> content=new ArrayList<String>();

    public RunMutator(GNode n){
        String s = getString(n);
    }


    public String getString(GNode n){
        new Visitor(){
            public void visitClassDeclaration(GNode n){

                // if not main class
                if(!n.getString(1).startsWith("Test")){
//                    System.out.println("visitClassDeclaration");
//                    ClassDeclarationMutator newClass = ClassDeclarationMutator.getClassDeclarationMutator(n); // create an object
                    ClassDeclarationMutator newClass=new ClassDeclarationMutator(n);
                    classes.add(newClass);
                    String s=newClass.getString(n);
                    content.add(s);
                }
                visit(n);
                // else if main class
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

        String output="";
        for(String s:content){
            output+=s;
        }
        return output;
    }


    // return the list of classes and
//    public ArrayList<ClassDeclarationMutator> getClasses(Node n){
//        super.dispatch(n);
//        return classes;
//    }
//
//    public ClassDeclarationMutator getMainClass(Node n) {
//        super.dispatch(n);
//        return main;
//    }
}
