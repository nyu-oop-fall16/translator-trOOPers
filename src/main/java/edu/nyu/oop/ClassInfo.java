package edu.nyu.oop;

import java.util.ArrayList;
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
    private String modifier;
    private ArrayList<String> cParameters = new ArrayList<String>();
    private HashMap<String,String> fields = new HashMap<String,String>();
    private HashMap<String,MethodInfo> methods = new HashMap<String,MethodInfo>();

    //Constructor


    //Set and get methods for the information held within an headerClass object.

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }
    public String getModifier(String modifier) {
        return modifier;
    }

    public void putField(String type, String name) {
        fields.put(name,type);
    }
    public HashMap<String,String> getFields() {
        return fields;
    }

    public void addMethod(MethodInfo method) {
        methods.put(method.getName(),method);
    }
    public HashMap<String,MethodInfo> getMethods() {
        return methods;
    }






}
