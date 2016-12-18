package edu.nyu.oop;

import xtc.tree.GNode;
import xtc.tree.Node;

import java.util.ArrayList;
import java.util.List;

public class MethodInfo {
    private GNode returnType;
    private String name;
    private ArrayList<String> mParameters = new ArrayList<String>();
    private ArrayList<String> modifiers = new ArrayList<String>();

    // Adds a modifier (such as "public," "private," or "static" to the MethodInfo object's list of modifiers.
    public void addModifier(String modifier) {
        modifiers.add(modifier);
    }

    // Adds a parameter to the method's list of arguments
    public void addParameter(String mParameter) {
        this.mParameters.add(mParameter);
    }

    //Set and get methods for the information held within a MethodInfo object.
    public void setReturnType(String rType) {
        returnType = GNode.create("ReturnType");
        returnType.add(rType);
    }
    public void setName(String n) {
        name = n;
    }

    public List<String> getModifiers() {
        return modifiers;
    }
    public GNode getReturnType() {
        return returnType;
    }
    public String getName() {
        return name;
    }
    public ArrayList<String> getParameters() {
        return mParameters;
    }

}
