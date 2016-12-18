package edu.nyu.oop;

import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;

import java.util.ArrayList;

public class ClassDeclarationMutator extends Visitor {
    String className;

    ArrayList<ConstructorDeclarationMutator> constructors;
    ArrayList<MethodDeclarationMutator> methods;
    ArrayList<FieldDeclarationMutator> fields;

    ArrayList<String> content=new ArrayList<>();


    public ClassDeclarationMutator(GNode n){
//        this.className = n.getString(1);
//        this.constructors = new ArrayList<ConstructorDeclarationMutator>();
//        this.methods = new ArrayList<MethodDeclarationMutator>();
//
//        super.dispatch(n); // dispatch to run visit to ClassBody node and set classBody to results


    }

    public String getString(GNode n){

        new Visitor(){
            public void visitClassBody(GNode n){
                System.out.println("ClassDeclarationMutator visitClassBody");
                visit(n);
            }
            public void visitFieldDeclaration(GNode n){
                System.out.println("ClassDeclarationMutator visitFieldDeclaration");
                FieldDeclarationMutator m = new FieldDeclarationMutator(n);
                String s=m.getString(n);
                content.add(s);
                visit(n);
            }

            public void visitMethodDeclaration(GNode n){
                System.out.println("ClassDeclarationMutator visitMethodDeclaration");
                //TODO: constructor info collected
                MethodDeclarationMutator m = new MethodDeclarationMutator(n);
                String s=m.getString(n);
                content.add(s);
                visit(n);
            }


            public void visitConstructorDeclaration(GNode n) {
                System.out.println("ClassDeclarationMutator visitConstructorDeclaration");
                ConstructorDeclarationMutator c = new ConstructorDeclarationMutator(n);
                String s=c.getString(n);
                content.add(s);
                visit(n);
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


    /**
     * Dispatch to the children of a given root node.
     * @param n the root node given
     */
    public void visit(Node n){
        for (Object o : n) {
            if (o instanceof Node) dispatch((Node) o);
        }
    }






//    public void handleMethods(GNode n, final ArrayList<MethodDeclarationMutator> methods){
//        new Visitor(){
//            public void visitMethodDeclaration(GNode n) {
//                methods.add(MethodDeclarationMutator.methodSignatureInfo(n)); // add each new method signature to method list
//                MethodDeclarationMutator.mutateMethod(n); // call method to mutate the node to reflect C++
//            }
//
//
//
//        }.dispatch(n);
//        for(int i=0;i<n.size();i++){
//            if(n.getNode(i).hasName("FieldDeclaration")){
//                Pair<String, String> a= new Pair<>(n.getNode(i).getNode(1).getString(0), n.getNode(i).getNode(2).getNode(0).getString(0));
//                fields.add(a);
//            }
//
//        }
//
//    }
//
//    public void handleConstructors(GNode n, final ArrayList<ConstructorDeclarationMutator> constructors){
//        new Visitor(){
//            public void visitConstructorDeclaration(GNode n) {
//                // add each new constructor to constructor list
//
//            }
//
//        }.dispatch(n);
//    }






}
