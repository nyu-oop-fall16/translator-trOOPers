package edu.nyu.oop;

import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;

import java.util.ArrayList;

public class FieldDeclarationMutator{
    String[] fieldMember;
    String[] mainFieldMember;
    ArrayList<String> content=new ArrayList<>();

    public FieldDeclarationMutator(GNode n){
        //TODO: visit Nodes and store info to this class's fields;
    }

//    private FieldDeclarationMutator(String[] fieldMember, String[] mainFieldMember){
//        this.fieldMember = fieldMember;
//        this.mainFieldMember = mainFieldMember;
//    }


    public String getString(GNode n){

        new Visitor(){
            public void visitPrimaryIdentifier(GNode n) {
                System.out.println("FieldDeclarationMutator visitPrimaryIdentifier");
            }

            public void visit(Node n){
                for (Object o : n) {
                    if (o instanceof Node) dispatch((Node) o);
                }
            }
        }.dispatch(n);

        //TODO: put FieldDeclaration info into String;

        String contentString="";
        for(String s:content){
            contentString+=s;
        }
        return contentString;
    }


//    public static FieldDeclarationMutator getClassField(GNode n){
//        FieldDeclarationMutator newField = classFieldMembers(n);
//        return newField;
//    }
//
//    public static FieldDeclarationMutator getMainField(GNode n){
//        FieldDeclarationMutator newField = mainFieldMembers(n);
//        return newField;
//    }


    // visit FieldDeclaration node
//    public static FieldDeclarationMutator classFieldMembers(GNode n){
//        String[] field = new String[2];
//        field[0] = n.getNode(1).getNode(0).getString(0); // gets the type
//        field[1] = n.getNode(2).getNode(0).getString(0); // gets the name of the field
//        String[] mainField = new String[3];

//        FieldDeclarationMutator newField = new FieldDeclarationMutator(field, mainField);
//        return newField;
//    }

//    public static FieldDeclarationMutator mainFieldMembers(GNode n){
//        String[] field = new String[3];
//        field[0] = n.getNode(0).getNode(0).getString(0); // gets modifier
//        field[1] = n.getNode(1).getNode(0).getString(0); // gets the type
//        field[2] = n.getNode(2).getNode(0).getString(0); // gets the name of the field
//        String[] classField = new String[2];

//        FieldDeclarationMutator newField = new FieldDeclarationMutator(classField, field);
//        return newField;
//    }
}
