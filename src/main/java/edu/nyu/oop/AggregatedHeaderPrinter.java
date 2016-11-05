package edu.nyu.oop;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

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
        header = new StringBuffer();
    }


    public void setNumNamespaces(int ns) {
        numNamespaces = ns;
    }

    public void addClass(String className) {
        classesInOrder.add(className);
        printers.put(className, new HeaderClassPrinter(writer));
    }

    public void addToHeader(String s) {
        header.append(s);
    }

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

    public HeaderClassPrinter getPrinter(String className) {
        return printers.get(className);
    }



}
