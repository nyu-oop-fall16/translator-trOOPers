package edu.nyu.oop;

import org.slf4j.Logger;

import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;

import java.util.List;
import java.util.ArrayList;

public class JavaAstVistor extends Visitor {
    private Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    private BuildInfo information = new BuildInfo();

    //The following visit_____ methods will visit the nodes of the Java AST and fill out the BuildInfo object defined above.
    //This information held in the BuildInfo class will then be used to create the AST for the header file and the cpp file.

    public String currentClass;
    public String currentMethod;

    public void visitClassDeclaration(GNode n) {
        if (!n.hasName("main")) {
            currentClass = n.getName();
            //Use dfs method in NodeUtil to access the Modifier Child node

            ClassInfo thisClass = new ClassInfo();
            thisClass.setName(currentClass);
            information.classes.put(currentClass,thisClass);

            visit(n);


        }

    }
    public void visitMethodDeclaration(GNode n) {
        currentMethod = n.getName();
        MethodInfo m  = new MethodInfo();
        information.classes.get(currentClass).addMethod(m);

    }





    public void visit(Node n) {
        for (Object o : n) if (o instanceof Node) dispatch((Node) o);
    }


}
