package edu.nyu.oop;

import edu.nyu.oop.util.JavaFiveImportParser;
import edu.nyu.oop.util.XtcProps;
import org.slf4j.Logger;

import xtc.tree.Node;
import xtc.tree.Visitor;
import xtc.tree.GNode;
import java.util.ArrayList;
import edu.nyu.oop.util.ChildToParentMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class RunMutator extends Visitor {

    private static Logger logger = org.slf4j.LoggerFactory.getLogger(JavaFiveImportParser.class);

    ArrayList<ClassDeclarationMutator> classes = new ArrayList<ClassDeclarationMutator>();
    ClassDeclarationMutator main = null;
    String testName = "";

    public void visit(Node n) {
        for (Object o : n) if (o instanceof Node) dispatch((Node) o);
    }

    public void visitPackageDeclaration(GNode n) {
        testName = n.getNode(1).getString(1);
    }

    public void visitClassDeclaration(GNode n) {
        final ChildToParentMap map = new ChildToParentMap(n);
        // if not main class
        if(!(n.getString(1).contains("Test"))) {
            ClassDeclarationMutator newClass = ClassDeclarationMutator.getClassDeclarationMutator(n,map,false); // create an object
            newClass.setTestName(testName);
            classes.add(newClass);
        }
        // else if main class
        else {
            ClassDeclarationMutator newClass = ClassDeclarationMutator.getClassDeclarationMutator(n,map,true); // create an object
            newClass.setTestName(testName);
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
        PrintWriter outputWriter = printToOutputCpp();
        StringBuffer beginning = new StringBuffer();
        beginning.append("#include \"output.h\"\n");
        beginning.append("#include \"java_lang.h\"\n");
        beginning.append("#include <iostream>\n");
        beginning.append("using namespace std;\n");
        beginning.append("using namespace java::lang;\n");
        beginning.append("namespace inputs{ \n");
        String testName = n.getNode(0).getNode(1).getString(1);
        beginning.append("\tnamespace " + testName + "{\n");
        outputWriter.println(beginning);

        ArrayList<ClassDeclarationMutator> classes = getClasses(n); // dispatch called
        for(int i = 0; i < classes.size(); i++) {
            classes.get(i).printConstructor(outputWriter);

            ArrayList<ConstructorDeclarationMutator> constructorsList = classes.get(i).classBody.initConstructors;
            for(int j = 0; j < constructorsList.size(); j++) {
                constructorsList.get(j).printInitImplementation(outputWriter);
            }

            ArrayList<MethodDeclarationMutator> methodList = classes.get(i).classBody.methods;
            for(int j = 0; j < methodList.size(); j++) {
                methodList.get(j).printMethodImplementation(outputWriter);
            }

            classes.get(i).printClassMethod(outputWriter);
            classes.get(i).printVTable(outputWriter);
        }
        outputWriter.println("\t}\n}");

        outputWriter.println("namespace __rt { ");
        for (int i = 0; i < classes.size(); i++) {
            classes.get(i).printArraySpecialization(outputWriter);
        }

        outputWriter.println("\n}");

        outputWriter.close();
    }

    public void printMain(Node n) {
        PrintWriter mainWriter = printToMainCpp();
        StringBuffer beginning = new StringBuffer();
        beginning.append("#include \"output.h\"\n");
        beginning.append("#include \"java_lang.h\"\n");
        beginning.append("#include <iostream>\n");
        beginning.append("using namespace std;\n");
        beginning.append("using namespace java::lang;\n");
        String testName = n.getNode(0).getNode(1).getString(1);
        beginning.append("using namespace inputs::" + testName + ";\n");
        beginning.append("int main() \n");
        mainWriter.println(beginning);

        ClassDeclarationMutator mainClass = getMainClass(n); // dispatch called
//        ArrayList<FieldDeclarationMutator> fields = mainClass.classBody.fields;
//        for(int j = 0; j < fields.size(); j++) {
//            mainWriter.println(fields.get(j).fieldMember[0] + " " + fields.get(j).fieldMember[1] + " " + fields.get(j).fieldMember[2] + ";\n");
//        }

        ArrayList<MethodDeclarationMutator> method = mainClass.classBody.methods;
        MethodDeclarationMutator mainMethod = method.get(0);
        mainMethod.printMethodImplementation(mainWriter);
        mainWriter.close();
    }

    // prints given string to output.cpp
    public PrintWriter printToOutputCpp() {
        // Create a printwriter for output.cpp file
        File output = loadOutputCpp();
        PrintWriter cppWriter = getWriter(output);
        return cppWriter;
    }

    // prints given string to output.cpp
    public PrintWriter printToMainCpp() {
        // Create a printwriter for output.cpp file
        File main = loadMainCpp();
        PrintWriter cppWriter = getWriter(main);
        return cppWriter;
    }

    // returns a printwriter given a file to write to -- could put in printToOutputCpp method
    // returns null if a non valid file is given
    private static PrintWriter getWriter(File file) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            logger.warn("Invalid path for file " + file);
        }
        return writer;
    }

    // returns the output.cpp file
    private static File loadOutputCpp() {
        File outputCPP = new File(XtcProps.get("output.location") + "/output.cpp");
        return outputCPP;
    }

    // returns the main.cpp file
    private static File loadMainCpp() {
        File mainCPP = new File(XtcProps.get("output.location") + "/main.cpp");
        return mainCPP;
    }
}
