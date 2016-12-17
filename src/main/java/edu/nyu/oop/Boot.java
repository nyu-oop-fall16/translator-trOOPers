package edu.nyu.oop;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import java.util.ArrayList;

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
        bool("printHeaderAst", "printHeaderAst", false, "Generates and prints the AST for the header file.").
        bool("printHeaderFile", "printHeaderFile", false, "Writes a header file from the C++ AST.").
        bool("printMutatedAst", "printMutatedAst", false, "Mutates the Java Ast files to correspond with C++ files.").
        bool("printImplementationFiles", "printImplementationFiles", false, "Generates the output.cpp files and main.cpp files using mutated Asts.").
        bool("runTranslator", "runTranslator", false, "Translates a Java file into C++ files.");
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

    @Override
    public void process(Node n) {
        Translator translator = new Translator(n);

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
        }

        // Generates list of GNodes with its dependencies
        if (runtime.test("generateListGNodes")) {
            translator.generateGNodes();
        }

        if (runtime.test("printHeaderAst")) {
            GNode rootNode = translator.makeHeaderAst();
            runtime.console().format(rootNode).pln().flush();

        }

        if (runtime.test("printHeaderFile")) {
            translator.makeHeaderFile();
        }


        if (runtime.test("printMutatedAst")) {
            GNode rootNode = translator.getMutatedAst();
            // check the Ast in console
            runtime.console().pln("Mutate: ").format(rootNode).pln().flush();
        }


        // passes the mutated Ast to be used to create the implementation files
        if (runtime.test("printImplementationFiles")) {
           translator.makeImplementationFiles();
        }

        if (runtime.test("runTranslator")) {
            translator.run();
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
