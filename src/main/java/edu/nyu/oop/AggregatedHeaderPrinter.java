package edu.nyu.oop;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

// This class keeps a list of classes and a hashmap that connects each class to its HeaderClassPrinter. It contains
// the method that is called from HeaderFileMaker.java to print the final header file.

public class AggregatedHeaderPrinter {
    private ArrayList<String> classesInOrder;
    private HashMap<String, HeaderClassPrinter> printers;
    private PrintWriter writer;
    private StringBuffer header;
    private int numNamespaces;

    public AggregatedHeaderPrinter(PrintWriter w) {
        writer = w;
        classesInOrder = new ArrayList();
        printers = new HashMap<String, HeaderClassPrinter>();
        header = new StringBuffer("#include \"java_lang.h\"\nusing namespace java::lang;");
    }

    // This method saves the number of namespaces in the AST for use in closing the curly braces at the end of the file.
    public void setNumNamespaces(int ns) {
        numNamespaces = ns;
    }

    // This method adds a class to the ArrayList of classes and creates a HeaderClassPrinter for that class. It links
    // the className with the HeaderClassPrinter in the Hashmap.
    public void addClass(String className) {
        classesInOrder.add(className);
        printers.put(className, new HeaderClassPrinter(writer));
    }

    // This method saves all the information that goes at the beginning of the file (the namespaces, struct declarations,
    // and typedefs).
    public void addToHeader(String s) {
        header.append(s);
    }

    // This method prints the namespace declarations, the struct declarations, the typedefs, the header information for each
    // class, and the closing namespace braces to the output file. In short, it prints the final header file.
    public void writeToOutputFile() {
        writer.print(header);
        for (String cl: classesInOrder) {
            HeaderClassPrinter hcp = printers.get(cl);
            hcp.writeToOutputFile();;
        }
        for (int i = 0; i <= numNamespaces; i++) {
            writer.print("}\n");
        }
        writer.close();
    }

    // This class returns a HeaderClassPrinter for a given class.
    public HeaderClassPrinter getPrinter(String className) {
        return printers.get(className);
    }



}
