package edu.nyu.oop;

import java.io.PrintWriter;

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

    public void addToDL(String s) {
        dataLayout.append(s);
    }

    public void addToMethodDecs(String s) {
        vtMethodDecs.append(s);
    }

    public void addToVTable(String s) {
        vTable.append(s);
    }

    public void writeToOutputFile() {
        writer.print(dataLayout);
        writer.print(vtMethodDecs);
        writer.print("\n");
        writer.print(vTName + "() \n:\n" + vTable.substring(0, vTable.length() - 2) + " {\n}\n};\n\n");
    }

    public void setVTableName(String s) {
        vTName = s;
    }

}
