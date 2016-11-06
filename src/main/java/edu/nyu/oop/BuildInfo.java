//package edu.nyu.oop;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//import xtc.tree.GNode;
//import xtc.tree.Node;
//import xtc.tree.Visitor;
//
//public class BuildInfo {
//    // BuildInfo holds a list of classes that were defined int he Java file.
//    List<Node> packages = new ArrayList<Node>();
//    HashMap<String,ClassInfo> classes = new HashMap<String,ClassInfo>();
//
//    // Include Import Statements!
//
//    public GNode makeAST() {
//        // Do package import somehow
//
//        // Create base node, one for each class (Node's name is name of class)
//            // HeaderDeclaration node to encapsulate all of it
//                // DataLayout node
//                    // add Field Declarations
//                    // Create Constructor Node
//                        // Write in className
//                        // Put in constructor node saved in ClassInfo
//                    // add Method Declarations
//                        // add everything from the MethodInfo object
//
//
//
//        GNode g;
//        return g;
//    }
//
//
//    public void addPackage(Node n) {
//        packages.add(n);
//    }
//
//
//}
