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
    // private Node parent = whatever this class extends ("Object" if nothing else)
    private Node name;
    private Node modifier;
    private Node constructorParams;
    private List<Node> fields = new ArrayList<Node>();
    private HashMap<String,MethodInfo> methods = new HashMap<String,MethodInfo>();

    //Set and get methods for the information held within an headerClass object.

    public void setName(String n) {
        name = GNode.create(n);
    }
    public Node getName() {
        return name;
    }

    public void setModifier(Node modifier) {
        this.modifier = modifier;
    }
    public Node getModifier() {
        return modifier;
    }

    public void addFields(Node name) {
        fields.add(name);
    }

    public List<Node> getFields() {
        return fields;
    }

    public void addMethod(MethodInfo method) {
        methods.put(method.getName().getString(0),method);
    }
    public HashMap<String,MethodInfo> getMethods() {
        return methods;
    }


    public void setConstructorParams(Node c) {
        this.constructorParams = c;
    }

    public Node getConstructorParams() {
        return this.constructorParams;
    }



}
