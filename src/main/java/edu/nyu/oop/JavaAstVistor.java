//package edu.nyu.oop;
//
//import edu.nyu.oop.util.NodeUtil;
//import org.slf4j.Logger;
//
//import xtc.tree.GNode;
//import xtc.tree.Node;
//import xtc.tree.Visitor;
//
//import java.util.List;
//import java.util.ArrayList;
//
//public class JavaAstVistor extends Visitor {
//    private Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
//
//    private BuildInfo information = new BuildInfo();
//
//    //The following visit_____ methods will visit the nodes of the Java AST and fill out the BuildInfo object defined above.
//    //This information held in the BuildInfo class will then be used to create the AST for the header file and the cpp file.
//
//    public String currentClass;
//    public String currentMethod;
//
//    public void visitPackageDeclaration(GNode n) {
//        information.addPackage(n);
//    }
//
//    public void visitClassDeclaration(GNode n) {
//        if (!n.hasName("main")) {
//            currentClass = n.getName();
//            ClassInfo thisClass = new ClassInfo();
//            thisClass.setName(currentClass);
//            thisClass.setModifier(NodeUtil.dfs(n, "Modifiers"));
//            thisClass.addFields(NodeUtil.dfs(n, "FieldDeclaration"));
//
//            Node constructorDec = NodeUtil.dfs(n, "ConstructorDeclaration");
//            thisClass.setConstructor(NodeUtil.dfs(constructorDec, "FormalParameters"));
//
//            information.classes.put(currentClass,thisClass);
//
//            visit(n);
//        }
//
//    }
//
//    public void visitMethodDeclaration(GNode n) {
//        currentMethod = n.getName();
//        MethodInfo thisMethod  = new MethodInfo();
//        thisMethod.setName(currentMethod);
//
//        // Find modifiers and add them to the ClassInfo Object
//        for (Node m: NodeUtil.dfsAll(n, "Modifiers")) {
//            thisMethod.addModifier(m);
//        }
//
//        // Find return type of method and add it to the MethodInfo object (if void, then add VoidType)
//        Node typeTest = NodeUtil.dfs(n, "Type");
//        if (typeTest.equals(null)) {
//            thisMethod.setReturnType(NodeUtil.dfs(n, "VoidType"));
//        }
//        else {
//            thisMethod.setReturnType(NodeUtil.dfs(n, "Type"));
//        }
//
//        // Set parameters of the method
//        for (Node p: NodeUtil.dfsAll(n, "FormalParameters")) {
//            thisMethod.addParameter(p);
//        }
//
//        information.classes.get(currentClass).addMethod(thisMethod);
//
//    }
//
//
//
//
//
//    public void visit(Node n) {
//        for (Object o : n) if (o instanceof Node) dispatch((Node) o);
//    }
//
//
//}
