//package edu.nyu.oop;
//
//import xtc.tree.Node;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.HashMap;
//
///*This class object is designed to hold information about a particular class including:
//    1) the name of the class
//    2) the modifiers of the class
//    3) the types of the parameters
//    4) the field types and names
//    5) a list of the class' methods
//
//*/
//
//public class ClassInfo {
//    private String name;
//    private Node modifier;
//    private Node constructor;
//    private List<Node> cParameters = new ArrayList<Node>();
//    private List<Node> fields = new ArrayList<Node>();
//    private HashMap<String,MethodInfo> methods = new HashMap<String,MethodInfo>();
//
//    //Constructor
//
//
//    //Set and get methods for the information held within an headerClass object.
//
//    public void setName(String name) {
//        this.name = name;
//    }
//    public String getName() {
//        return name;
//    }
//
//    public void setModifier(Node modifier) {
//        this.modifier = modifier;
//    }
//    public Node getModifier() {
//        return modifier;
//    }
//
//    public void addFields(Node name) {
//        fields.add(name);
//    }
//
//    public List<Node> getFields() {
//        return fields;
//    }
//
//    public void addMethod(MethodInfo method) {
//        methods.put(method.getName(),method);
//    }
//    public HashMap<String,MethodInfo> getMethods() {
//        return methods;
//    }
//
//
//    public void setConstructor(Node c) {
//        this.constructor = c;
//    }
//
//    public Node getConstructor() {
//        return this.constructor;
//    }
//
//
//
//}
