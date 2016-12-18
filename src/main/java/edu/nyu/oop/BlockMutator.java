package edu.nyu.oop;

import xtc.tree.Visitor;
import java.util.ArrayList;

public class BlockMutator extends Visitor {
    String returnStatement; // if the method returns anything
    ArrayList<String> expressionStatement; // expression
    ArrayList<FieldDeclarationMutator> fields; // fields
}
