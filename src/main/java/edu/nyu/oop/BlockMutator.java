
package edu.nyu.oop;

import xtc.tree.Node;
import xtc.tree.Visitor;
import xtc.tree.GNode;
import java.util.ArrayList;

public class BlockMutator extends Visitor {
    String returnStatement; // if the method returns anything
    ArrayList<String> expressionStatement = new ArrayList<String>(); // expression
    ArrayList<FieldDeclarationMutator> fields = new ArrayList<FieldDeclarationMutator>(); // fields
    ArrayList<String[]> parameters;
    String className;
    ArrayList<String> declarators = new ArrayList<String>();
    ArrayList<String> callExpression = new ArrayList<String>();

    private BlockMutator(GNode n, ArrayList<String[]> parameters, String className){
        this.parameters = parameters;
        this.className = className;
        super.dispatch(n);
    }

    public static BlockMutator getBlock(GNode n, ArrayList<String[]> parameters, String className){
        BlockMutator block = new BlockMutator(n, parameters, className);
        return block;
    }

    public void visitFieldDeclaration(GNode n){
        FieldDeclarationMutator newField = FieldDeclarationMutator.classFieldMembers(n);
        declarators.add(newField.fieldMember[1]);
        fields.add(newField);
    }

    public void visitExpressionStatement(GNode n){
        visit(n);
    }
    public void visitExpression(GNode n){
        String newExpression = "";
        String firstPI = n.getNode(0).getString(0);
        String operation = n.getString(1);
        String secondPI = "";
        if(n.getNode(2).getName().equals("StringLiteral")){
            secondPI = secondPI + "new __String(";
            secondPI = secondPI + n.getNode(2).getString(0);
            secondPI = secondPI + ")";
        }
        else{
            secondPI = secondPI + n.getNode(2).getString(0);
        }

        if(declarators.size()==0){
            newExpression = newExpression + "__this->";
        }
        for(int i=0; i<declarators.size();i++){
            if(firstPI.equals(declarators.get(i))){
                //do nothing
            }
            else{
                newExpression = newExpression + "__this->";
            }
        }
        newExpression = newExpression + firstPI + operation + secondPI;
        expressionStatement.add("\t" + newExpression);
        expressionStatement.add(";");
        visit(n);
    }

    public void visitCallExpression(GNode n){
//        //prints out to console, go to arugments
//        //can have mulitple nested Call Expression and arguments (test 008 and test 012)
//        if(n.getNode(0).getName().equals("SelectionExpression")){
//            callExpression.add("\tcout<<");
//        }
//        //not a method??
//        //assume primary Identifier
//        if(n.getNode(0).getName().equals("PrimaryIdentifier")){
//            String firstPI = n.getNode(0).getString(0);
//            //check if PI is in parameters and check for type
//            //if type is class do not use __this
//            for(int i=0;i<parameters.size();i++){
//                if(firstPI.equals(parameters.get(i)[1])){
//                    if(parameters.get(i).equals(className)){
//                        // do not append __this
//                    }
//                    else{
//                        callExpression.add("__this->");
//                    }
//                }
//            }
//
//            callExpression.add(firstPI + "->");
//            String dataField = n.getNode(0).get
//
//        }


        //***************
        //Ignore all other Call Expression inside Call Expression
        //if call expression's parent is arugments, do nothing



        //if call expression's child node is selection expression - check System out println
        if(n.getNode(0).getName().equals("SelectionExpression")) {
            if (n.getNode(0).getNode(0).getName().equals("PrimaryIdentifier")) {
                if (n.getNode(0).getNode(0).getString(0).equals("System")) {
                    if (n.getNode(0).getString(1).equals("out")) {
                        if (n.getString(2).equals("println")) {
                            //append cout<<
                            callExpression.add("\t cout<<");
                            //go to arguments(huge variety)
                            if(n.getNode(3).getName().equals("Arguments")) {
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
                                        String primId = Arguments.getNode(0).getNode(0).getString(0);
                                        String methodName = Arguments.getNode(0).getString(2);

                                        callExpression.add(primId + "-> __vptr ->" + methodName + "(" + primId + ")");

                                    }
                                    //SelectionExpression primId -> datafield
                                    else if (Arguments.getNode(0).getName().equals("SelectionExpression")) {
                                        if (Arguments.getNode(0).getNode(0).getName().equals("PrimaryIdentifier")) {
                                            String primId = Arguments.getNode(0).getNode(0).getString(0);
                                            String dataField = Arguments.getNode(0).getString(1);
                                            callExpression.add(primId + "->" + dataField);
                                        }
                                    }
                                }
                            }
                            callExpression.add("<<endl;");

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



    public void visitArguments(GNode n ){
        //method- look at test 011 and 012
        if(n.size()>0){
            if(n.getNode(0).getName().equals("SelectionExpression")){

            }
        }
    }


    public void visitReturnStatement(GNode n){
        //visit primaryIdentifier

        String returnValue;
        String methodName;
        String newReturn = "__this ->";
        if(n.getNode(0).getName().equals("StringLiteral")){
            returnValue = n.getNode(0).getString(0);
            newReturn = newReturn + "new __String(" + returnValue + ")";
        }
        else if(n.getNode(0).getName().equals("IntegerLiteral")){
            returnValue = n.getNode(0).getString(0);
            newReturn = returnValue;
        }
        //method
        else if(n.getNode(0).getName().equals("CallExpression")){
            returnValue = n.getNode(0).getNode(0).getString(0);
            methodName = n.getNode(0).getString(2);
            newReturn = newReturn + returnValue + "->__vptr->" + methodName + "(__this->" + returnValue + ")";
        }
        else{
            returnValue = n.getNode(0).getString(0);
            newReturn = newReturn + returnValue;
        }
        returnStatement ="return " + newReturn;
    }

    /**
     * Dispatch to the children of a given root node.
     * @param n the root node given
     */
    public void visit(Node n){
        for (Object o : n) {
            if (o instanceof Node) dispatch((Node) o);
        }
    }
}
