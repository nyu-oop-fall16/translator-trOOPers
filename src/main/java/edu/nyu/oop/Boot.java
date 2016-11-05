package edu.nyu.oop;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import java.util.ArrayList;

import edu.nyu.oop.util.*;
import edu.nyu.oop.util.JavaFiveImportParser;
import edu.nyu.oop.util.NodeUtil;
import edu.nyu.oop.util.XtcProps;
import org.slf4j.Logger;

import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.util.Tool;
import xtc.lang.JavaPrinter;
import xtc.parser.ParseException;

/**
 * This is the entry point to your program. It configures the user interface, defining
 * the set of valid commands for your tool, provides feedback to the user about their inputs
 * and delegates to other classes based on the commands input by the user to classes that know
 * how to handle them. So, for example, do not put translation code in Boot. Remember the
 * Single Responsiblity Principle https://en.wikipedia.org/wiki/Single_responsibility_principle
 */
public class Boot extends Tool {
    private Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    @Override
    public String getName() {
        return XtcProps.get("app.name");
    }

    @Override
    public String getCopy() {
        return XtcProps.get("group.name");
    }

    @Override
    public void init() {
        super.init();
        // Declare command line arguments.
        runtime.
        bool("printJavaAst", "printJavaAst", false, "Print Java Ast.").
        bool("printJavaCode", "printJavaCode", false, "Print Java code.").
        bool("printJavaImportCode", "printJavaImportCode", false, "Print Java code for imports and package source.").
        bool("generateListGNodes", "generateListGNodes", false, "Generate list of GNodes of the java class and its dependencies.").
//        bool("generateHeaderOutput", "generateHeaderOutput", false, "Prints definitions into the output.h file.").
        bool("phaseFour", "phaseFour", false, "Mutates the Java Ast files to correspond with C++ files.").
        bool("phaseFive", "phaseFive", false, "Generates the output.cpp files and main.cpp files using mutated Asts.");
    }

    @Override
    public void prepare() {
        super.prepare();
        // Perform consistency checks on command line arguments.
        // (i.e. are there some commands that cannot be run together?)
        logger.debug("This is a debugging statement."); // Example logging statement, you may delete
    }

    @Override
    public File locate(String name) throws IOException {
        File file = super.locate(name);
        if (Integer.MAX_VALUE < file.length()) {
            throw new IllegalArgumentException("File too large " + file.getName());
        }
        if(!file.getAbsolutePath().startsWith(System.getProperty("user.dir"))) {
            throw new IllegalArgumentException("File must be under project root.");
        }
        return file;
    }

    @Override
    public Node parse(Reader in, File file) throws IOException, ParseException {
        return NodeUtil.parseJavaFile(file);
    }

    // create the list
    List<GNode> listGNodes = new ArrayList<GNode>();
    private GNode mutatedAst; // my additions

    @Override
    public void process(Node n) {
        if (runtime.test("printJavaAst")) {
            runtime.console().format(n).pln().flush();
        }

        if (runtime.test("printJavaCode")) {
            new JavaPrinter(runtime.console()).dispatch(n);
            runtime.console().flush();
        }

        if (runtime.test("printJavaImportCode")) {
            List<GNode> nodes = JavaFiveImportParser.parse((GNode) n);
            for(Node node : nodes) {
                runtime.console().pln();
                new JavaPrinter(runtime.console()).dispatch(node);
            }
            runtime.console().flush();
            //runtime.console().p("MEEEEE" + nodes.get(0).getName()).flush();
        }

        // if (runtime.test("Your command here.")) { ... don't forget to add it to init()

        // Generates list of GNodes with its dependencies
        if (runtime.test("generateListGNodes")){
            // add the GNode of the java class passed in
            listGNodes.add((GNode) n);

            // should be a list of all dependencies and their dependencies recursively gotten
            List<GNode> nodes = JavaFiveImportParser.parse((GNode) n);

            // add the dependencies to the list
            for(int i = 0; i < nodes.size(); i++) {
                // makes sure that a GNode isn't added to list multiple times during cyclic imports
                if(!listGNodes.contains(nodes.get(i))) {
                    listGNodes.add(nodes.get(i));
                }
            }

            // checks the nodes in list
//            runtime.console().p("Size of GNodes List: " + g.size()).pln().flush();
            for (int k = 0; k < listGNodes.size(); k++) {
                runtime.console().p("List of GNodes, GNode at index " + k + ": ").format(listGNodes.get(k)).pln().flush();
//                runtime.console().p("List of GNodes, GNode at index " + k + ": " + g.get(k)).pln().flush();
                runtime.console().pln().flush();
            }
        }

        if(runtime.test("phaseFour")) {
            //I combine phasefour and five for a moment to test.
            //And if command phase four and five respectively, the file doesn't mutatedAst and the node it generates
            //-Prudence

            // make a copy of the Java Ast of the test class and mutate it to C++ Ast
            GNode nodeCopy = NodeUtil.deepCopyNode((GNode)n);
            mutatedAst = MutateJavaAst.mutate(nodeCopy); // mutate copy

            // check the Ast in console
            runtime.console().pln("Mutate: ").format(mutatedAst).pln().flush();
        }

    }

    /**
     * Run Boot with the specified command line arguments.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        new Boot().run(args);
    }
}
