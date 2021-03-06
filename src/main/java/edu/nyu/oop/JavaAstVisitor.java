package edu.nyu.oop;

import edu.nyu.oop.util.NodeUtil;
import org.slf4j.Logger;

import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class JavaAstVisitor extends Visitor {
    private Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    private HeaderASTMaker information = new HeaderASTMaker();

    //The following visit_____ methods will visit the nodes of the Java AST and fill out the BuildInfo object defined above.
    //This information held in the BuildInfo class will then be used to create the AST for the header file and the cpp file.

    public String currentClass;
    public String currentMethod;

    //visitPackageDeclaration will visit each package declaration node and get the file name from the test being used. This is a
    //placeholder and will change when we incorporate dependencies
    public void visitPackageDeclaration(GNode n) {
        information.addPackage(n);
        information.setFileName(n.getNode(1).getString(1));
    }

    public void visitClassDeclaration(GNode n) {
        currentClass = n.getString(1);
        if (!currentClass.equals("T" + information.getFileName().substring(1))) {

            ClassInfo thisClass = new ClassInfo();
            thisClass.setName(currentClass);
            thisClass.addVTptr();

            //Accessing the classBody node, iterating through it children, and processing the FieldDeclaration Nodes.
            Node classBody = NodeUtil.dfs(n, "ClassBody");
            if (classBody != null) {
                Iterator<Object> fieldCheck = classBody.iterator();
                while (fieldCheck.hasNext()) {
                    Object child = fieldCheck.next();
                    if (child instanceof  Node) {
                        Node nodeChild = (Node)child;
                        if (nodeChild.getName().equals("FieldDeclaration")) {
                            GNode modifiers = GNode.create("Modifiers");
                            String type;
                            String name;

                            Node mods = NodeUtil.dfs(nodeChild, "Modifiers");
                            for(Node modifier: NodeUtil.dfsAll(mods, "Modifier")) {
                                modifiers.add(modifier.getString(0));
                            }

                            Node typeNode = NodeUtil.dfs(nodeChild, "Type");
                            type = typeNode.getNode(0).getString(0);
                            Node decs = NodeUtil.dfs(nodeChild, "Declarators");
                            name = decs.getNode(0).getString(0);

                            thisClass.addField(modifiers, type, name);
                        }

                    }
                }
            }

            thisClass.addVT();

            //Accessing the constructor node of the class and extracting the parameters.
            List<Node> constructors = NodeUtil.dfsAll(n, "ConstructorDeclaration");
            if (constructors.isEmpty()) {
                thisClass.addConstructor(GNode.create("Parameters"));
            }

            else {
                for (Object constructor: constructors) {
                    Node constructorDec = (Node) constructor;
                    Node oneConstructorParams = GNode.create("Parameters");
                    if (constructorDec != null) {
                        Node formalParams = NodeUtil.dfs(constructorDec, "FormalParameters");
                        for (Node formalParam : NodeUtil.dfsAll(formalParams, "FormalParameter")) {
                            Node param = GNode.create("Parameter");
                            Node pType = (Node) NodeUtil.dfs(formalParam, "Type").get(0);
                            Node type = GNode.create("Type");
                            type.add(pType.getString(0));
                            param.add(type);
                            Node pName = GNode.create("Name");
                            pName.add(formalParam.getString(3));
                            param.add(pName);
                            oneConstructorParams.add(param);
                        }
                    }
                    thisClass.addConstructor(oneConstructorParams);
                }
            }

            //Accessing the extends node of the class
            Node extension = NodeUtil.dfs(n, "Extension");
            if (extension != null) {
                String parentClass = NodeUtil.dfs(extension, "Type").getNode(0).getString(0);
                thisClass.setParent(parentClass);
            } else {
                thisClass.setParent("Object");
            }

            information.addClass(currentClass, thisClass);
            visit(n);
        }
    }

    public void visitMethodDeclaration(GNode n) {
        currentMethod = n.getString(3);

        MethodInfo thisMethod = new MethodInfo();
        thisMethod.setName(currentMethod);

        // Find modifiers and add them to the MethodInfo Object
        Node modifiers = NodeUtil.dfs(n, "Modifiers");
        for (Node m : NodeUtil.dfsAll(modifiers, "Modifier")) {
            thisMethod.addModifier(m.getString(0));
        }

        // Find return type of method and add it to the MethodInfo object (if void, then add VoidType)
        Node typeTest = (Node) n.get(2);
        if (typeTest.getName().equals("VoidType")) {
            thisMethod.setReturnType("void");
        } else {
            Node identifier = NodeUtil.dfs(typeTest, "QualifiedIdentifier");
            if (identifier == null) {
                identifier = NodeUtil.dfs(typeTest, "PrimitiveType");
            }
            thisMethod.setReturnType(identifier.getString(0));
        }

        // Set parameters of the method
        Node parameter = NodeUtil.dfs(n, "FormalParameters");
        for (Node p : NodeUtil.dfsAll(parameter, "FormalParameter")) {
            Node type = NodeUtil.dfs(p, "Type");
            if (type == null) {
                thisMethod.addParameter("void");
            } else {
                Node identifier = (Node) type.get(0);
                thisMethod.addParameter(identifier.getString(0));
            }
        }

        information.addMethodToClass(currentClass, thisMethod);
    }

    public void visit(Node n) {
        for (Object o : n) if (o instanceof Node) dispatch((Node) o);
    }

    public HeaderASTMaker getBuildInfo(Node n) {
        super.dispatch(n);
        return information;
    }

    public HeaderASTMaker getASTInfo() {
        return information;
    }

}
