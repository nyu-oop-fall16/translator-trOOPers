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

    //Set and get methods for the information held within a headerClassMethod object.
    public void addModifier(String modifier) {
        modifiers.add(modifier);
    }
    public List<String> getModifiers() {
        return modifiers;
    }

    public void setReturnType(String rType) {
        returnType = GNode.create("ReturnType");
        returnType.add(rType);
    }
    public GNode getReturnType() {
        return returnType;
    }

    public void setName(String n) {
        name = n;
    }
    public String getName() {
        return name;
    }

    public void addParameter(String mParameter) {
        this.mParameters.add(mParameter);
    }
    public ArrayList<String> getParameters() {
        return mParameters;
    }

}
