package edu.nyu.oop;

import java.util.List;
import java.util.ArrayList;

import edu.nyu.oop.util.JavaFiveImportParser;
import edu.nyu.oop.util.NodeUtil;

import xtc.tree.GNode;
import xtc.tree.Node;

// This class implements the delegator pattern. Boot.java does one thing -- it gets the command from the command line
// and calls the method from the translator. It delegates the work of actually making the ASTs and files to this class.
// Boot.java is the delegator and Translator.java is the delegate.

public class Translator {

    private Node rootNode;
    private List<GNode> listGNodes = new ArrayList<GNode>();
    private GNode headerAst;
    private GNode mutatedAst;

    public Translator(Node n) {
        rootNode = n;
    }

    // translates a java file into an AST
    public void generateGNodes() {
        listGNodes.add((GNode) rootNode);

        // should be a list of all dependencies and their dependencies recursively gotten
        List<GNode> nodes = JavaFiveImportParser.parse((GNode) rootNode);

        // add the dependencies to the list
        for(int i = 0; i < nodes.size(); i++) {
            // makes sure that a GNode isn't added to list multiple times during cyclic imports
            if(!listGNodes.contains(nodes.get(i))) {
                listGNodes.add(nodes.get(i));
            }
        }
    }

    // runs all of the 5 phases at once
    public void run() {
        makeHeaderAst();
        makeHeaderFile();
        makeImplementationFiles();
    }

    // makes a C++ Header AST and returns its root node
    public GNode makeHeaderAst() {
        JavaAstVisitor v = new JavaAstVisitor();
        HeaderASTMaker build;
        build = v.getBuildInfo(rootNode);
        headerAst = build.makeAST();
        return headerAst;
    }

    // makes a C++ Header file based on the header AST
    public void makeHeaderFile() {
        if (null == headerAst) {
            makeHeaderAst();
        }
        HeaderFileMaker maker = new HeaderFileMaker();
        maker.runVisitor(headerAst);
    }

    public void makeImplementationFiles() {
        RunMutator run = new RunMutator();
        run.printOutput(listGNodes.get(0));
        run.printMain(listGNodes.get(0));
    }

}
