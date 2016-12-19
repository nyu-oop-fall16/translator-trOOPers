package edu.nyu.oop;

import xtc.tree.GNode;

public class FieldDeclarationMutator {
    String[] fieldMember;
    String[] declators;

    private FieldDeclarationMutator(String[] fieldMember, String[] declators) {
        this.fieldMember = fieldMember;
        this.declators = declators;
    }

    // visit FieldDeclaration node for regular classes
    public static FieldDeclarationMutator classFieldMembers(GNode n) {
        String[] field = new String[2];
        field[0] = n.getNode(1).getNode(0).getString(0); // gets the type
        field[1] = n.getNode(2).getNode(0).getString(0); // gets the name of the field

        // store the declarators of the field
        String[] declarator = new String[2];
        if(n.getNode(2).size() != 0) {
            if(n.getNode(2).getNode(0).getNode(2) != null) {
                declarator[0] = n.getNode(2).getNode(0).getString(0); // gets the name of the field
                declarator[1] = n.getNode(2).getNode(0).getNode(2).getString(0); // gets the value of the field
            }
        }
        FieldDeclarationMutator newField = new FieldDeclarationMutator(field, declarator);
        return newField;
    }

    // visit FieldDeclaration node for main class
    public static FieldDeclarationMutator mainFieldMembers(GNode n) {
        String[] field = new String[3];
        field[0] = n.getNode(0).getNode(0).getString(0); // gets modifier
        field[1] = n.getNode(1).getNode(0).getString(0); // gets the type
        field[2] = n.getNode(2).getNode(0).getString(0); // gets the name of the field
        String[] classField = new String[2];

        // store the declarators of the field
        String[] declarator = new String[2];
        if(n.getNode(2).size() != 0) {
            if(n.getNode(2).getNode(0).getNode(2) != null) {
                declarator[0] = n.getNode(2).getNode(0).getString(0); // gets the name of the field
                declarator[1] = n.getNode(2).getNode(0).getNode(2).getString(0); // gets the value of the field
            }
        }
        FieldDeclarationMutator newField = new FieldDeclarationMutator(field, declarator);
        return newField;
    }
}
