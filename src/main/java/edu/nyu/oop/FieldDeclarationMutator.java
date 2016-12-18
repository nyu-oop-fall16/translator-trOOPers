package edu.nyu.oop;

import edu.nyu.oop.util.RecursiveVisitor;
import xtc.tree.Node;
import xtc.tree.Visitor;
import xtc.tree.GNode;
import java.util.ArrayList;

public class FieldDeclarationMutator{
    ArrayList<String[]> fieldMembers = new ArrayList<String[]>();
    ArrayList<String[]> mainFieldMembers = new ArrayList<String[]>();


    // visit FieldDeclaration node
    public void classFieldMembers(GNode n){
        new Visitor(){
            public void visitFieldDeclaration(GNode n) {
                String[] field = new String[2];
                field[0] = n.getNode(1).getNode(0).getString(0); // gets the type
                field[1] = n.getNode(2).getNode(0).getString(0); // gets the name of the field
                fieldMembers.add(field);
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

   public void mainFieldMembers(GNode n){
        new Visitor(){
            public void visitFieldDeclaration(GNode n) {
                String[] field = new String[3];
                field[0] = n.getNode(0).getNode(0).getString(0); // gets modifier
                field[1] = n.getNode(1).getNode(0).getString(0); // gets the type
                field[2] = n.getNode(2).getNode(0).getString(0); // gets the name of the field
                mainFieldMembers.add(field);
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
}
