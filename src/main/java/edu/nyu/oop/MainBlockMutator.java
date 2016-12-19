package edu.nyu.oop;

import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainBlockMutator extends Visitor {

    String returnStatement; // if the method returns anything
    ArrayList<String> expressionStatement = new ArrayList<String>(); // expression
    ArrayList<String> ListOfFields = new ArrayList();
    String fields; // fields
    String inits;
    ArrayList<String[]> parameters;
    String className;
    ArrayList<String[]> declarators = new ArrayList<String[]>();
    ArrayList<String> callExpression = new ArrayList<String>();
    boolean isMain;
    String isString = "false";
    String beginBrace = "";
    String endBrace = "";

    private MainBlockMutator(GNode n, ArrayList<String[]> parameters, String className, boolean isMain) {
        this.parameters = parameters;
        this.className = className;
        this.isMain = isMain;
        super.dispatch(n);
    }

    public static MainBlockMutator getBlock(GNode n, ArrayList<String[]> parameters, String className, boolean isMain) {
        MainBlockMutator block = new MainBlockMutator(n, parameters, className, isMain);
        return block;
    }

     public void visitBlock(GNode n) {
        if(n.getNode(0).getName().equals("Block")){
            beginBrace = "{";
            endBrace = "}";
        }

        visit(n);

    }

    public void visitFieldDeclaration(GNode n) {
        if(isMain) {
            if(n.getNode(1).getName().equals("Type")) {
                //Qualified Id (usually class?)
                if(n.getNode(1).getNode(0).getName().equals("QualifiedIdentifier")) {
                    String qualId = n.getNode(1).getNode(0).getString(0);
                    String declarator = n.getNode(2).getNode(0).getString(0);
                    String isString = "false";
                    try {
                        String hasStringLiteral = n.getNode(2).getNode(0).getNode(2).getNode(3).getNode(0).getName();
                        if(hasStringLiteral.equals("StringLiteral")) {
                            isString = "true";
                        }
                    } catch(Exception e) {
                        isString = "false";
                    }
                    String[] newDeclarator = {qualId, declarator,isString};
                    declarators.add(newDeclarator);
                    fields = qualId + " " + declarator;

                    //Maybe multiple different types??
                    //Primary Identifier
                    if(n.getNode(2).getNode(0).getNode(2).getName().equals("PrimaryIdentifier")) {
                        fields = fields + "=" + "(" + qualId + ")" + n.getNode(2).getNode(0).getNode(2).getString(0);
                    }
                    //newClassExpression
                    if(n.getNode(2).getNode(0).getNode(2).getName().equals("NewClassExpression")) {
                        String classQualId = n.getNode(2).getNode(0).getNode(2).getNode(2).getString(0);
                        if(n.getNode(2).getNode(0).getNode(2).getNode(3).getName().equals("Arguments")) {
                            GNode arguments = (GNode) n.getNode(2).getNode(0).getNode(2).getNode(3);
                            fields = fields + "= new __" + classQualId + "()";
                            if(arguments.size() == 0) {
                                inits = "__" + classQualId + "::" + "__init(" + declarator + ")";
                            } else {
                                inits = "__" + classQualId + "::" + "__init(" + declarator;
                                for(int i =0; i<arguments.size(); i++) {
                                    if (arguments.getNode(i).getName().equals("StringLiteral")) {
                                        inits = inits + "," + "new __String(" + arguments.getNode(i).getString(0) + ")";

                                    }
                                    if(arguments.getNode(i).getName().equals("IntegerLiteral")) {
                                        inits = inits + "," + arguments.getNode(i).getString(0);
                                    }
                                }
                                inits = inits + ")";
                            }
                        }


                    }
                    ListOfFields.add(fields);
                    ListOfFields.add(inits);
                    inits = "";


                }
                //Primitive Type(usually int x)
                else if(n.getNode(1).getNode(0).getName().equals("PrimitiveType")) {
                    String qualId = n.getNode(1).getNode(0).getString(0);
                    String declarator = n.getNode(2).getNode(0).getString(0);
                    String[] newDeclarator = {qualId, declarator};
                    declarators.add(newDeclarator);
                    fields = qualId + " " + declarator;
                    ListOfFields.add(fields);
                } else {
                    //is there any other type?

                }

            }

        }

    }

    public void visitExpressionStatement(GNode n) {
        visit(n);
    }

    public void visitExpression(GNode n) {
        if(isMain) {

            String newExpression = "";
            String firstPI = "";
            String operation = n.getString(1);
            String secondPI = "";
            String selector = "";

            if(n.getNode(0).getName().equals("SelectionExpression")) {
                firstPI = firstPI + n.getNode(0).getNode(0).getString(0);
                selector = selector + n.getNode(0).getString(1);
                if(n.getNode(2).getName().equals("CastExpression")) {
                    String qualId = n.getNode(2).getNode(0).getNode(0).getString(0);
                    secondPI = secondPI + n.getNode(2).getNode(1).getString(0);
                    newExpression = newExpression + firstPI + "->some" + operation + "(" + qualId + ")" + secondPI;
                }
                if(n.getNode(2).getName().equals("PrimaryIdentifier")) {
                    secondPI = secondPI + n.getNode(2).getString(0);
                    newExpression = newExpression + firstPI + "->" + selector + operation +secondPI;
                }


            } else {
                firstPI = firstPI + n.getNode(0).getString(0);
            }
            if(n.getNode(2).getName().equals("SelectionExpression")) {
                secondPI = n.getNode(2).getNode(0).getString(0);
                selector = n.getNode(2).getString(1);
                newExpression = newExpression + selector + operation + "__" + secondPI + "::" + selector + secondPI;
            } else if (n.getNode(2).getName().equals("StringLiteral")) {
                secondPI = secondPI + "new __String(";
                secondPI = secondPI + n.getNode(2).getString(0);
                secondPI = secondPI + ")";
                newExpression = newExpression + firstPI + operation + secondPI;
            } else if (n.getNode(2).getName().equals("IntegerLiteral")) {
                secondPI = secondPI + n.getNode(2).getString(0);
                newExpression = newExpression + firstPI + operation + secondPI;
            }


            expressionStatement.add("\t" + newExpression);
            expressionStatement.add(";\n");
            visit(n);
        }
    }

    public void visitCallExpression(GNode n) {

        //***************
        //Ignore all other Call Expression inside Call Expression
        //if call expression's parent is arugments, do nothing

        if(isMain) {
            //if call expression's child node is selection expression - check System out println
            if (n.getNode(0).getName().equals("SelectionExpression")) {
                if (n.getNode(0).getNode(0).getName().equals("PrimaryIdentifier")) {
                    if (n.getNode(0).getNode(0).getString(0).equals("System")) {
                        if (n.getNode(0).getString(1).equals("out")) {
                            if (n.getString(2).equals("println")) {
                                //append cout<<
                                callExpression.add("\t cout<<");
                                //go to arguments(huge variety)
                                if (n.getNode(3).getName().equals("Arguments")) {
                                    GNode Arguments = (GNode) n.getNode(3);
                                    //single primary identifier(means printing single datafield)
                                    //go to primary identifier in argument and print out string
                                    if (Arguments.size() > 0) {
                                        if (Arguments.getNode(0).getName().equals("PrimaryIdentifier")) {
                                            String primId = Arguments.getNode(0).getString(0);
                                            callExpression.add(primId);
                                            //(test 008) cout<<a<<endl;
                                        }
                                        //Call Expression (means method, you can double check by seeing if there is an argument node)
                                        else if (Arguments.getNode(0).getName().equals("CallExpression")) {
                                            //**can consider doing this via visit, look below
                                            if(Arguments.getNode(0).getNode(0).getName().equals("PrimaryIdentifier")) {
                                                String primId = Arguments.getNode(0).getNode(0).getString(0);
                                                String methodName = Arguments.getNode(0).getString(2);
                                                String enterNull = "";
                                                for(int i=0; i<ListOfFields.size(); i++) {
                                                    if(!ListOfFields.get(i).startsWith("__" + primId)) {
                                                        enterNull = "__rt::null()";
                                                    }
                                                }
//                                                callExpression.add("__"+ primId + "::" + methodName + "((" + primId + ")" + enterNull + ")");
                                                callExpression.add(primId + "-> __vptr ->" + methodName + "(" + primId + ")");
                                                for(int i=0; i<declarators.size(); i++) {
                                                    if(declarators.get(i)[1].equals(primId)) {
                                                        String checkIfString = declarators.get(i)[2];
                                                        if(checkIfString.equals("true")) {
                                                            callExpression.add("->data");
                                                        }
                                                    }
                                                }
                                                if(methodName.equals("toString")) {
                                                    callExpression.add("->data");
                                                }
                                            }
                                            //Test009
                                            else if(Arguments.getNode(0).getNode(0).getName().equals("SelectionExpression")) {
                                                String primId = Arguments.getNode(0).getNode(0).getNode(0).getString(0);
                                                String selection = Arguments.getNode(0).getNode(0).getString(1);
                                                String methodName = Arguments.getNode(0).getString(2);

                                                callExpression.add(primId + "->" + selection + "->__vptr->" + methodName +"(" + primId + "->" + selection +")");

                                            } else if(Arguments.getNode(0).getNode(0).getName().equals("CallExpression")) {
                                                String primId = Arguments.getNode(0).getNode(0).getNode(0).getString(0);
                                                String callExp = Arguments.getNode(0).getNode(0).getString(2);
                                                String methodName = Arguments.getNode(0).getString(2);

                                                callExpression.add(primId + "->__vptr->" + callExp + "(" + primId + ")->__vptr->" + methodName + "(" + primId + "->__vptr->" + callExp + "(" + primId + "))" );
                                            }

                                        }
                                        //SelectionExpression primId -> datafield
                                        else if (Arguments.getNode(0).getName().equals("SelectionExpression")) {
                                            if (Arguments.getNode(0).getNode(0).getName().equals("PrimaryIdentifier")) {
                                                String primId = Arguments.getNode(0).getNode(0).getString(0);
                                                String dataField = Arguments.getNode(0).getString(1);
                                                callExpression.add(primId + "->" + dataField);
                                            }
                                        } else if(Arguments.getNode(0).getName().equals("StringLiteral")) {
                                            callExpression.add(Arguments.getNode(0).getString(0));
                                        }
                                    }
                                }
                                callExpression.add("<<endl;\n");

                            }
                        }
                    }
                }
            }

            if (n.getNode(0).getName().equals("PrimaryIdentifier")) {
                String primId = n.getNode(0).getString(0);
                String methodName = n.getString(2);
                //go to arguments (huge variety?)
                callExpression.add(primId + "->__vptr->" + methodName);
                if (n.getNode(3).getName().equals("Arguments")) {
                    GNode arguments = (GNode) n.getNode(3);
                    if (arguments.size() > 0) {
                        if (arguments.getNode(0).getName().equals("StringLiteral")) {
                            String stringLit = arguments.getNode(0).getString(0);
                            callExpression.add("(" + primId + ",new __String(" + stringLit + "));\n");
                        }
                        if(arguments.getNode(0).getName().equals("PrimaryIdentifier")) {
                            String argPrimId = arguments.getNode(0).getString(0);
                            callExpression.add("(" + primId + "," + "(" + primId.toUpperCase() + ")" + argPrimId + ");\n");

                        }
                    }

                }
            }
        }

        //Nested call expression, method
//        else{
//            System.out.println("entering??");
////        if(!n.getNode(0).getName().equals("SelectionExpression")){
//            if(n.getNode(0).getName().equals("PrimaryIdentifier")){
//                String primId = n.getNode(0).getString(0);
//                callExpression.add(primId + "-> __vptr ->");
//                String methodName = n.getString(3);
//                callExpression.add(methodName + "(" + primId + ")");
//            }
//        }

    }



    public void visitReturnStatement(GNode n) {
        if(isMain) {
//            //visit primaryIdentifier
//            String returnValue;
//            String methodName;
//            String newReturn = "__this ->";
//            if (n.getNode(0).getName().equals("StringLiteral")) {
//                returnValue = n.getNode(0).getString(0);
//                newReturn = newReturn + "new __String(" + returnValue + ")";
//            } else if (n.getNode(0).getName().equals("IntegerLiteral")) {
//                returnValue = n.getNode(0).getString(0);
//                newReturn = returnValue;
//            }
//            //method
//            else if (n.getNode(0).getName().equals("CallExpression")) {
//                returnValue = n.getNode(0).getNode(0).getString(0);
//                methodName = n.getNode(0).getString(2);
//                newReturn = newReturn + returnValue + "->__vptr->" + methodName + "(__this->" + returnValue + ")";
//            } else {
//                returnValue = n.getNode(0).getString(0);
//                newReturn = newReturn + returnValue;
//            }
//            returnStatement = "return " + newReturn;
        }
    }

    /**
     * Dispatch to the children of a given root node.
     * @param n the root node given
     */
    public void visit(Node n) {
        for (Object o : n) {
            if (o instanceof Node) dispatch((Node) o);
        }
    }
}
