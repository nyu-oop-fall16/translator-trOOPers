package edu.nyu.oop;

import xtc.tree.Node;
import xtc.tree.GNode;
import xtc.tree.Visitor;
import java.util.ArrayList;
import edu.nyu.oop.util.ChildToParentMap;

public class ClassBodyMutator extends Visitor {
    String className;
    String classExtension;
    String constructor; // the single constructor line to get initialize __vptr
    ArrayList<ConstructorDeclarationMutator> initConstructors;
    ArrayList<MethodDeclarationMutator> methods;
    ArrayList<FieldDeclarationMutator> fields;
    boolean isMain;

    /**
     * Constructs an object of ClassBodyMutator which holds the class's name and its class body.
     */
    private ClassBodyMutator(boolean isMainClass) {
        this.initConstructors = new ArrayList<ConstructorDeclarationMutator>();
        this.methods = new ArrayList<MethodDeclarationMutator>();
        this.fields = new ArrayList<FieldDeclarationMutator>();
        this.isMain = isMainClass;
    }

    // getting class body of classes that don't contain main
    public static ClassBodyMutator regularClass(GNode n, String className, String classExtension, ChildToParentMap map, boolean isMain) {
        ClassBodyMutator newClass = new ClassBodyMutator(isMain);

        newClass.className = className;
        newClass.classExtension = classExtension;
        newClass.handleMethods(n); // handle methods
        newClass.handleConstructors(n); // handle constructors
        newClass.handleFields(n, map); // handle fields
        newClass.constructor = "__" + className + "::__" + className + "() : __vptr(&__vtable) {}";

        return newClass;
    }

    // getting class body of main class
    public static ClassBodyMutator mainClass(GNode n, ChildToParentMap map, boolean isMain){
        ClassBodyMutator mainClass = new ClassBodyMutator(isMain);

        mainClass.handleFields(n, map);
        mainClass.handleMethods(n);

        return mainClass;
    }

    public void handleMethods(GNode n) {
        new Visitor() {
            public void visitMethodDeclaration(GNode n) {
                methods.add(MethodDeclarationMutator.methodSignatureInfo(n, className)); // add each new method signature to method list
                methods.get(methods.size()-1).addImplicitThis(); // add implicit this to parameter list of the method
//                MethodDeclarationMutator.mutateMethod(n); // call method to mutate the node to reflect C++
                visit(n);
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
    }

    public void handleConstructors(GNode n) {
        new Visitor() {
            public void visitConstructorDeclaration(GNode n) {
                // add each new constructor to constructor list
                initConstructors.add(ConstructorDeclarationMutator.initSignatureInfo(n, className));
                initConstructors.get(initConstructors.size()-1).addImplicitThis();
                visit(n);
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
    }

    public void handleFields(GNode n, final ChildToParentMap map) {
        new Visitor() {
            public void visitFieldDeclaration(GNode n) {
                // add each new field to fields list if parent is class body
                GNode parentOfField = (GNode)map.fetchParentFor(n);
                if(parentOfField.getName().equals("ClassBody")) {
                    if(isMain) {
                        fields.add(FieldDeclarationMutator.mainFieldMembers(n)); // regular class fields
                    }
                    else{
                        fields.add(FieldDeclarationMutator.classFieldMembers(n)); // main's class fields
                    }
                }
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
    }
}
