package edu.nyu.oop;

import xtc.tree.GNode;
import xtc.tree.Node;

import java.util.ArrayList;
import java.util.List;

public class MethodInfo {
   // private boolean ifStatic;
    private Node returnType;
    private GNode name;
    private List<Node> mParameters = new ArrayList<Node>();
    private List<Node> modifiers = new ArrayList<Node>();

    //Constructor


    //Set and get methods for the information held within a headerClassMethod object.

    /*public void setIfStatic(boolean b) {
        ifStatic = b;
    }
    public boolean getIfStatic() {
        return ifStatic;
    } */

    public void addModifier(Node modifier) {
        modifiers.add(modifier);
    }

    public List<Node> getModifiers() {
        return modifiers;
    }

    public void setReturnType(Node returnType) {
        this.returnType = returnType;
    }

    public Node getReturnType() {
        return returnType;
    }

    public void setName(String n) {
        name = GNode.create(n);
    }
    public GNode getName() {
        return name;
    }

    public void addParameter(Node mParameter) {
        this.mParameters.add(mParameter);
    }
    public List<Node> getParameter() {
        return mParameters;
    }




}
