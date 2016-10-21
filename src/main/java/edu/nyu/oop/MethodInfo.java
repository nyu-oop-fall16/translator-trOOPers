package edu.nyu.oop;

import java.util.ArrayList;

public class MethodInfo {
    private boolean ifStatic;
    private String modifier;
    private String returnType;
    private String name;
    private ArrayList<String> mParameters = new ArrayList<String>();

    //Constructor


    //Set and get methods for the information held within a headerClassMethod object.

    public void setIfStatic(boolean b) {
        ifStatic = b;
    }
    public boolean getIfStatic() {
        return ifStatic;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public String getModifier() {
        return modifier;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void addParameter(String mParameter) {
        this.mParameters.add(mParameter);
    }
    public ArrayList<String> getParameter() {
        return mParameters;
    }




}
