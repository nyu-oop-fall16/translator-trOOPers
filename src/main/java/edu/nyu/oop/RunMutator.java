package edu.nyu.oop;

import xtc.tree.Node;
import xtc.tree.Visitor;
import xtc.tree.GNode;
import java.util.ArrayList;
import edu.nyu.oop.util.ChildToParentMap;

public class RunMutator extends Visitor{
    ArrayList<ClassDeclarationMutator> classes = new ArrayList<ClassDeclarationMutator>();
    ClassDeclarationMutator main;
//    String testName;

    public void visit(Node n) {
        for (Object o : n) if (o instanceof Node) dispatch((Node) o);
    }

    public void visitClassDeclaration(GNode n){
        final ChildToParentMap map = new ChildToParentMap(n);
        // if not main class
        if(!n.getString(1).startsWith("Test")){
            ClassDeclarationMutator newClass = ClassDeclarationMutator.getClassDeclarationMutator(n,map); // create an object
            classes.add(newClass);
        }
        // else if main class

        visit(n);
    }

//    public void visitCompilationUnit(GNode n){
//        this.testName = n.getNode(0).getNode(1).getString(1);
//        visit(n);
//    }

    // return the list of classes and
    public ArrayList<ClassDeclarationMutator> getClasses(Node n){
        super.dispatch(n);
        return classes;
    }

    public ClassDeclarationMutator getMainClass(Node n) {
        super.dispatch(n);
        return main;
    }

    public void printBeginning(Node n){

        StringBuffer beginning = new StringBuffer();
        beginning.append("#include \"output.h\"\n");
        beginning.append("#include \"java_lang.h\"\n");
        beginning.append("#include <iostream>\n");
        beginning.append("using namespace std;\n");
        beginning.append("namespace inputs{ \n");
        String testName = n.getNode(0).getNode(1).getString(1);
        beginning.append("\tnamespace " + testName + "{\n");
        System.out.println(beginning);
        super.dispatch(n);
        for(int i = 0; i < classes.size(); i++){
            String className = classes.get(i).className;
            String classExtension = classes.get(i).classExtension;

            ArrayList<FieldDeclarationMutator> fieldsList = classes.get(i).classBody.fields;
            for(int j = 0; j < fieldsList.size(); j++){
                System.out.println(fieldsList.get(j).fieldMember[0] + " " + fieldsList.get(j).fieldMember[1]);
            }

            classes.get(i).printConstructor();

            ArrayList<ConstructorDeclarationMutator> constructorsList = classes.get(i).classBody.initConstructors;
            for(int j = 0; j < constructorsList.size(); j++){
                constructorsList.get(j).printInitMethod();
            }

            ArrayList<MethodDeclarationMutator> methodList = classes.get(i).classBody.methods;
            for(int j = 0; j < methodList.size(); j++){
                methodList.get(j).printMethodImplementation();
            }

            classes.get(i).printClassMethod();
            classes.get(i).printVTable();
        }
        System.out.println("\t}\n}");
    }
}
