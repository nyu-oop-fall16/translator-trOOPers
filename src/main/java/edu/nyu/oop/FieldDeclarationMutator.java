package edu.nyu.oop;

import xtc.tree.GNode;

public class FieldDeclarationMutator {
    static String[] fieldMember;

    private FieldDeclarationMutator(String[] fieldMember) {
        this.fieldMember = fieldMember;
    }

    // visit FieldDeclaration node for regular classes
    public static FieldDeclarationMutator classFieldMembers(GNode n) {
        System.out.println("CLASS FIELD MEMBER");
        String[] field = new String[2];
        field[0] = n.getNode(1).getNode(0).getString(0); // gets the type
        field[1] = n.getNode(2).getNode(0).getString(0); // gets the name of the field

        FieldDeclarationMutator newField = new FieldDeclarationMutator(field);
        return newField;
    }

    // visit FieldDeclaration node for main class
    public static FieldDeclarationMutator mainFieldMembers(GNode n) {
        System.out.println("MAIN FIELD MEMBER");
        String[] field = new String[3];
        field[0] = n.getNode(0).getNode(0).getString(0); // gets modifier
        field[1] = n.getNode(1).getNode(0).getString(0); // gets the type
        field[2] = n.getNode(2).getNode(0).getString(0); // gets the name of the field
        String[] classField = new String[2];

        FieldDeclarationMutator newField = new FieldDeclarationMutator(classField);
        return newField;
    }
}
