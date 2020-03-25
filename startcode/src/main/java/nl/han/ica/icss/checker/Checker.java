package nl.han.ica.icss.checker;

import java.util.*;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

public class Checker {

    private HashSet<String> pixelDeclarations;
    private HashSet<String> percentageDeclarations;
    private HashSet<String> colorDeclarations;
    private HashMap<VariableReference, VariableAssignment> variableAssignmentHashMap = new HashMap<>();

    // Decided not to use the linkedlist, since we only go 1 level deep
    //private LinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        initializeDeclarationHashMaps();
        for (ASTNode node : ast.root.getChildren()) {

            if (node instanceof VariableAssignment) {
                variableAssignmentHashMap.put(((VariableAssignment) node).name, (VariableAssignment)node);
            }
            if (node instanceof Stylerule) {
                checkStyleRule((Stylerule) node);
            }

        }
    }

    private void checkStyleRule(Stylerule stylerule) {
        HashMap<VariableReference, VariableAssignment> scopeVariables = new HashMap<VariableReference, VariableAssignment>();
        if (!stylerule.body.isEmpty()) {
            for (ASTNode node : stylerule.body) {
                if (node instanceof VariableAssignment) {
                    scopeVariables.put(((VariableAssignment) node).name, (VariableAssignment)node);
                }
                if (node instanceof Declaration) {
                    checkDeclaration((Declaration) node, scopeVariables);
                }
            }
        }
    }

    private void checkDeclaration(Declaration declaration, HashMap<VariableReference, VariableAssignment> scopeVariables) {
        ASTNode node = declaration.expression;

        // This if must always be the first IF statement, so it can check if the return node is a valid literal.
        if (node instanceof VariableReference) {
            node = getVariableReference((VariableReference) declaration.expression, scopeVariables);
        }
        if (node instanceof Operation) {
            Operation o = (Operation) node;
            node = getOperationReference(o);
        }
        if (node instanceof ColorLiteral
                && !colorDeclarations.contains(declaration.property.name)) {
            node.setError("Invalid declaration Color Literal");
        }
        if (node instanceof PercentageLiteral
                && !percentageDeclarations.contains(declaration.property.name)) {
            node.setError("Invalid declaration Percentage Literal");
        }
        if (node instanceof PixelLiteral
                && !pixelDeclarations.contains(declaration.property.name)) {
            node.setError("Invalid declaration Pixel Literal");
        }
    }

    private ASTNode getVariableReference(VariableReference variableReference, HashMap<VariableReference, VariableAssignment> scopeVariables) {
        if (variableAssignmentHashMap.containsKey(variableReference)) {
            if(variableAssignmentHashMap.get(variableReference).expression instanceof Operation) {
                 return getOperationReference((Operation)variableAssignmentHashMap.get(variableReference).expression);
            }
            else {
                return variableAssignmentHashMap.get(variableReference).expression;
            }
        }
        if (scopeVariables.containsKey(variableReference)) {
            if(scopeVariables.get(variableReference).expression instanceof Operation) {
                return getOperationReference((Operation)scopeVariables.get(variableReference).expression);
            }
            else {
                return scopeVariables.get(variableReference).expression;
            }
        }
        else{
            variableReference.setError("Variable undefined");
            return null;
        }
    }

    private ASTNode getOperationReference(Operation operation) {
        // Operations will also be checked here
        if(operation instanceof AddOperation) {

        }
        if(operation instanceof SubtractOperation) {

        }
        if(operation instanceof MultiplyOperation) {

        }

        return null;

//        if(variableAssignmentHashMap.get(variableReference).expression instanceof AddOperation) {
//
//        }
//        if(variableAssignmentHashMap.get(variableReference).expression instanceof AddOperation) {
//
//        }
    }

    private void initializeDeclarationHashMaps() {
        pixelDeclarations = new HashSet<>();
        percentageDeclarations = new HashSet<>();
        colorDeclarations = new HashSet<>();

        pixelDeclarations.add("height");
        pixelDeclarations.add("width");

        percentageDeclarations.add("height");
        percentageDeclarations.add("width");

        colorDeclarations.add("background-color");
        colorDeclarations.add("color");
    }
}
