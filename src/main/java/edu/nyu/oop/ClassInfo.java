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

    // This method provides the class with the VTable pointer and object. Every class has these. As they are added
    // right after the class object is created and its name is set, the method is called "initialize."
    public void initialize() {
        String vTname = "__" + name + "_VT";

        addField(GNode.create("Modifiers"), (vTname + "*"), "__vptr");

        GNode vTmod = GNode.create("Modifiers");
        vTmod.add("static");
        addField(vTmod, vTname, "__vtable");
    }

    //Set and get methods for the information held within a ClassInfo object.
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

    // This method takes in information about a field and adds it to the ClassInfo's list of fields as a field
    // declaration node that encapsulates said information.
    public void addField(GNode modifiers, String type, String name) {
        GNode field = GNode.create("FieldDeclaration");

        field.add(modifiers);

        GNode fieldType = GNode.create("FieldType");
        fieldType.add(type);
        field.add(fieldType);

        GNode fieldName = GNode.create("FieldName");
        fieldName.add(name);
        field.add(fieldName);

        fields.add(field);

    }

    // This method returns a list of the fields in the ClassInfo object.
    public ArrayList<GNode> getFields() {
        return fields;
    }

    // This method adds a new MethodInfo object to the ClassInfo's list of methods.
    public void addMethod(MethodInfo method) {
        methods.add(method);
    }

    // This method returns the list of MethodInfo objects in the ClassInfo object.
    public ArrayList<MethodInfo> getMethods() {
        return methods;
    }

    // This method returns a MethodInfo object belonging to the class, referenced by name.
    public MethodInfo getMethod(String name) {
        MethodInfo method = new MethodInfo();
        for(MethodInfo m: methods) {
            if (m.getName().equals(name)) {
                method = m;
            }
        }
        return method;
    }

    // This method checks to see if the ClassInfo object contains a particular method that is referenced by name.
    public boolean hasMethod(String name) {
        boolean has = false;
        for(MethodInfo m: methods) {
            if (m.getName().equals(name)) {
                has = true;
            }
        }
        return has;
    }

    // This method adds a constructor to the ClassInfo object. This is useful for overloaded constructors.
    public void addConstructor(Node constructor) {
        if (!constructors.contains(constructor)) {
            constructors.add((GNode) constructor);
        }
    }

    // This method returns the list of the ClassInfo's constructors.
    public ArrayList<GNode> getConstructors() {
        return this.constructors;
    }

    // This class returns a ClassInfo object representing the java.lang.Object class. Used for inheritance.
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
