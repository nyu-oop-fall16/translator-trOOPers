package edu.nyu.oop;

import xtc.tree.GNode;
import xtc.tree.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

/*This class object is designed to hold information about a particular class including:
    1) the name of the class
    2) the modifiers of the class
    3) the types of the parameters
    4) the field types and names
    5) a list of the class' methods
*/

public class ClassInfo {
    private String name;
    private String parent;
    private Node constructorParams = GNode.create("ConstructorParameters");
    private List<String> fields = new ArrayList<String>();
    private ArrayList<MethodInfo> methods = new ArrayList<MethodInfo>();

    //Set and get methods for the information held within an headerClass object.

    public void setName(String n) {
        name = n;
    }

    public String getName() {
        return name;
    }

    public void setParent(String p) { parent = p;}

    public String getParent() {return parent;}

    public void addFields(String name) {
        fields.add(name);
    }

    public List<String> getFields() {
        return fields;
    }

    public void addMethod(MethodInfo method) {
        methods.add(method);
    }
    public ArrayList<MethodInfo> getMethods() {
        return methods;
    }


    public void addConstructorParams(String r, String n) {
        Node param = GNode.create("ConstructorParameter");
        param.add(r);
        param.add(n);
        constructorParams.add(param);
    }

    public Node getConstructorParams() {
        return this.constructorParams;
    }



}
