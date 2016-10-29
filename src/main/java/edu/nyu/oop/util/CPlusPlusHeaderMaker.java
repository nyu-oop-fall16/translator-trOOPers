package edu.nyu.oop.util;

import xtc.tree.GNode;
import xtc.tree.Visitor;

/**
 *
 */
public class CPlusPlusHeaderMaker {
    public static void printCHeaderFile(final GNode n){
        // Class Declaration, Data Layout, VTable
        new Visitor(){
            public void visitHeaderDeclaration(){
                System.out.println("struct " + n.get(1) + "{");
            }
        }.dispatch(n);
    }

}
