package edu.nyu.oop;

import xtc.tree.Node;
import xtc.tree.Visitor;
import xtc.tree.GNode;
import java.util.ArrayList;
import edu.nyu.oop.util.ChildToParentMap;

public class RunMutator extends Visitor {
    ArrayList<ClassDeclarationMutator> classes = new ArrayList<ClassDeclarationMutator>();
    ClassDeclarationMutator main;

    public void visit(Node n) {
        for (Object o : n) if (o instanceof Node) dispatch((Node) o);
    }

    public void visitClassDeclaration(GNode n) {
        final ChildToParentMap map = new ChildToParentMap(n);
        // if not main class
        if(!(n.getString(1).contains("Test"))) {
            ClassDeclarationMutator newClass = ClassDeclarationMutator.getClassDeclarationMutator(n,map,false); // create an object
            System.out.println(newClass.className);
            classes.add(newClass);
        }
        // else if main class
        else {
            ClassDeclarationMutator newClass = ClassDeclarationMutator.getClassDeclarationMutator(n, map,true); // create an object
            System.out.println(newClass.className);
            main = newClass;
        }
        visit(n);
    }

    // return the list of classes and dispatches the visitors
    public ArrayList<ClassDeclarationMutator> getClasses(Node n) {
        super.dispatch(n);
        return classes;
    }

    public ClassDeclarationMutator getMainClass(Node n) {
        super.dispatch(n);
        return main;
    }

    public void printOutput(Node n) {
        StringBuffer beginning = new StringBuffer();
        beginning.append("#include \"output.h\"\n");
        beginning.append("#include \"java_lang.h\"\n");
        beginning.append("#include <iostream>\n");
        beginning.append("using namespace std;\n");
        beginning.append("using namespace java::lang;\n");
        beginning.append("namespace inputs{ \n");
        String testName = n.getNode(0).getNode(1).getString(1);
        beginning.append("\tnamespace " + testName + "{\n");
        System.out.println(beginning);

        ArrayList<ClassDeclarationMutator> classes = getClasses(n); // dispatch called
        for(int i = 0; i < classes.size(); i++) {
            classes.get(i).printConstructor();

            ArrayList<ConstructorDeclarationMutator> constructorsList = classes.get(i).classBody.initConstructors;
            for(int j = 0; j < constructorsList.size(); j++) {
                constructorsList.get(j).printInitMethod();
            }

            ArrayList<MethodDeclarationMutator> methodList = classes.get(i).classBody.methods;
            for(int j = 0; j < methodList.size(); j++) {
                methodList.get(j).printMethodImplementation();
            }

            classes.get(i).printClassMethod();
            classes.get(i).printVTable();
        }
        System.out.println("\t}\n}");
    }

    public void printMain(Node n){
        StringBuffer beginning = new StringBuffer();
        beginning.append("#include \"output.h\"\n");
        beginning.append("#include \"java_lang.h\"\n");
        beginning.append("#include <iostream>\n");
        beginning.append("using namespace std;\n");
        beginning.append("using namespace java::lang;\n");
        String testName = n.getNode(0).getNode(1).getString(1);
        beginning.append("using namespace inputs::" + testName + ";\n");
        beginning.append("int main() {\n");
        System.out.println(beginning);

        // TODO: main implementation
        ClassDeclarationMutator mainClass = getMainClass(n); // dispatch called
        ArrayList<FieldDeclarationMutator> fields = mainClass.classBody.fields;
        for(int j = 0; j < fields.size(); j++) {
            System.out.println(fields.get(j).fieldMember[0] + " " + fields.get(j).fieldMember[1] + " " + ";\n");
        }

        System.out.println("\treturn 0;");
        System.out.println("}");

    }
}
