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
    private ArrayList<GNode> constructors = new ArrayList<GNode>();
    private ArrayList<GNode> fields = new ArrayList<GNode>();
    private ArrayList<MethodInfo> methods = new ArrayList<MethodInfo>();

    public void initialize() {
        String vTname = "__" + name + "_VT";

        addField(GNode.create("Modifiers"), (vTname + "*"), "__vptr");

        GNode vTmod = GNode.create("Modifiers");
        vTmod.add("static");
        addField(vTmod, vTname, "__vtable");
    }

    //Set and get methods for the information held within an headerClass object.

    public void setName(String n) {
        name = n;
    }
    public String getName() {
        return name;
    }

    public void setParent(String p) {
        parent = p;
    }
    public String getParent() {
        return parent;
    }

    public void addField(GNode modifiers, String type, String name) {
        GNode field = GNode.create("FieldDeclaration");

        field.add(modifiers);

        GNode fieldType = GNode.create("FieldType");
        fieldType.add(type);
        field.add(fieldType);

        GNode fieldName = GNode.create("FieldName");
        fieldName.add(name);
        field.add(fieldName);

//        GNode fieldInitialization = GNode.create("FieldInitialization");
//        fieldInitialization.add(initialization);
//        field.add(fieldInitialization);

        fields.add(field);

    }
    public ArrayList<GNode> getFields() {
        return fields;
    }

    public void addMethod(MethodInfo method) {
        methods.add(method);
    }
    public ArrayList<MethodInfo> getMethods() {
        return methods;
    }
    public MethodInfo getMethod(String name) {
        MethodInfo method = new MethodInfo();
        for(MethodInfo m: methods) {
            if (m.getName().equals(name)) {
                method = m;
            }
        }
        return method;
    }
    public boolean hasMethod(String name) {
        boolean has = false;
        for(MethodInfo m: methods) {
            if (m.getName().equals(name)) {
                has = true;
            }
        }
        return has;
    }

    public void addConstructor(Node constructor) {
        if (!constructors.contains(constructor)) {
            constructors.add((GNode) constructor);
        }
    }

    public ArrayList<GNode> getConstructors() {
        return this.constructors;
    }

    public static ClassInfo buildObject() {
        ClassInfo object = new ClassInfo();
        object.name = "Object";
        object.parent = null;

        MethodInfo isa = new MethodInfo();
        isa.setReturnType("Class");
        isa.setName("isa");

        MethodInfo hashCode = new MethodInfo();
        hashCode.setReturnType("int");
        hashCode.setName("hashCode");

        MethodInfo equals = new MethodInfo();
        equals.setReturnType("bool");
        equals.setName("equals");
        equals.addParameter("Object");

        MethodInfo getClass = new MethodInfo();
        getClass.setReturnType("Class");
        getClass.setName("getClass");

        MethodInfo toString = new MethodInfo();
        toString.setReturnType("String");
        toString.setName("toString");

        object.addMethod(isa);
        object.addMethod(hashCode);
        object.addMethod(equals);
        object.addMethod(getClass);
        object.addMethod(toString);

        return object;
    }
}
