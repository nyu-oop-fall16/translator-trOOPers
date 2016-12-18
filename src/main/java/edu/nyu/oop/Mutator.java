package edu.nyu.oop;

import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;
import java.util.ArrayList;

/*
   Each method should be a call to a visit method BUT, in each visit, don't call visit() so that the action is
   done locally in the current node alone. In the classes that call methods of this class, a call to visit is made.
 */
public class Mutator {
    /**
     * Method to visit and mutate ClassDeclaration nodes. Assume it receives a ClassDeclaration node.
     * @param n
     */
    public static void mutateClassDeclaration(GNode n) {
        new Visitor() {

        } .dispatch(n);
    }

    /**
     * Method to visit and mutate ClassBody nodes. Assume it receives a ClassBody node.
     * @param n
     */
    public static void mutateClassBody(GNode n) {
        new Visitor() {

        } .dispatch(n);
    }

    /**
     * Assuming there is only one node FormalParameters, visit the children FormalParameters and get the type and name of the parameters.
     * @param n the given GNode (usually the MethodDeclaration)
     * @return an array list of String arrays containing the type and name of a parameter
     */
    public static ArrayList<String[]> getFormalParameters(GNode n) {
        final ArrayList<String[]> parameters = new ArrayList<String[]>();

        new Visitor() {
            String[] parameter = new String[2];
            /**
             * Visit FormalParameters and call visit() on the node to have other visit methods called.
             * @param n the root node given to be visited
             */
            public void visitFormalParameters(GNode n) {
                visit(n);
            }

            public void visitFormalParameter(GNode n) {
                parameter = new String[2]; // reset
                visit(n); // call visit to get the type first
                parameter[1] = n.getString(3);
                parameters.add(parameter);
            }

            public void visitType(GNode n) {
                visit(n);
            }

            public void visitQualifiedIdentifier(GNode n) {
                parameter[0] = n.getString(0);
            }

            /**
             * Dispatch to the children of a given root node.
             * @param n the root node given
             */
            public void visit(Node n) {
                for (Object o : n) {
                    if (o instanceof Node) dispatch((Node) o);
                }
            }
        } .dispatch(n);

        return parameters;
    }
}
