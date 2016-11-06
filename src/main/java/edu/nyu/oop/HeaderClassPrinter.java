package edu.nyu.oop;

import java.io.PrintWriter;

// This class holds StringBuffers and a PrintWriter that will be used for printing a single class' header file code.

public class HeaderClassPrinter {
    private StringBuffer dataLayout;
    private StringBuffer vtMethodDecs;
    private StringBuffer vTable;
    private String vTName;
    private PrintWriter writer;

    public HeaderClassPrinter(PrintWriter w) {
        writer = w;
        vTable = new StringBuffer();
        vtMethodDecs = new StringBuffer();
        dataLayout = new StringBuffer();
    }

    // This method adds a String to the StringBuffer representing the class' data layout.
    public void addToDL(String s) {
        dataLayout.append(s);
    }

    // This method adds a String to the StringBuffer representing the class' VTable method declarations.
    public void addToMethodDecs(String s) {
        vtMethodDecs.append(s);
    }

    // This method adds a String to the StringBuffer representing the class' vTable.
    public void addToVTable(String s) {
        vTable.append(s);
    }

    // This method prints all of the information pertaining to a particular class to the output file.
    public void writeToOutputFile() {
        writer.print(dataLayout);
        writer.print(vtMethodDecs);
        writer.print("\n");
        writer.print(vTName + "() \n:\n" + vTable.substring(0, vTable.length() - 2) + " {\n}\n};\n\n");
    }

    // This method saves the name of the VTable of the relevant class.
    public void setVTableName(String s) {
        vTName = s;
    }

}
