package nl.han.ica.icss.checker;

import java.util.*;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.MultiplyOperation;

public class Checker {

    private HashSet<String> pixelDeclarations;
    private HashSet<String> percentageDeclarations;
    private HashSet<String> colorDeclarations;
    private HashMap<VariableReference, VariableAssignment> globalVariables = new HashMap<>();

    // Decided not to use the linkedlist, since we only go 1 level deep
    //private LinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        initializeDeclarationHashMaps();
        for (ASTNode node : ast.root.getChildren()) {

            if (node instanceof VariableAssignment) {
                globalVariables.put(((VariableAssignment) node).name, (VariableAssignment)node);
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
            node = getOperationReference(o, scopeVariables);
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
        if (globalVariables.containsKey(variableReference)) {
            if(globalVariables.get(variableReference).expression instanceof Operation) {
                 return getOperationReference((Operation) globalVariables.get(variableReference).expression, scopeVariables);
            }
            else {
                return globalVariables.get(variableReference).expression;
            }
        }
        if (scopeVariables.containsKey(variableReference)) {
            if(scopeVariables.get(variableReference).expression instanceof Operation) {
                return getOperationReference((Operation)scopeVariables.get(variableReference).expression, scopeVariables);
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

    private ASTNode getOperationReference(Operation operation, HashMap<VariableReference, VariableAssignment> scopeVariables) {
        Expression lhs = operation.lhs;
        Expression rhs = operation.rhs;

        if (lhs instanceof VariableReference) {
            lhs = (Expression) getVariableReference((VariableReference) lhs, scopeVariables);
        }
        if (lhs instanceof Operation) {
            Operation o = (Operation) lhs;
            lhs = (Expression) getOperationReference(o, scopeVariables);
        }

        if (rhs instanceof VariableReference) {
            rhs = (Expression) getVariableReference((VariableReference) rhs, scopeVariables);
        }
        if (rhs instanceof Operation) {
            Operation o = (Operation) rhs;
            rhs = (Expression) getOperationReference(o, scopeVariables);
        }

        if(lhs instanceof ColorLiteral || rhs instanceof ColorLiteral) {
            operation.setError("Invalid variable types used in operation.");
            return null;
        }

        if((lhs instanceof PixelLiteral && rhs instanceof PixelLiteral)
            || (lhs instanceof PercentageLiteral && rhs instanceof PercentageLiteral)) {
            return rhs;
        }
        if((lhs instanceof PixelLiteral && rhs instanceof PercentageLiteral)
            ||(lhs instanceof PercentageLiteral && rhs instanceof PixelLiteral)) {
            operation.setError("Do not mix percentage literals with pixel literals in sum.");
        }

        if(operation instanceof MultiplyOperation) {

            if((lhs instanceof PixelLiteral && rhs instanceof ScalarLiteral)
                || (lhs instanceof PercentageLiteral && rhs instanceof ScalarLiteral)) {
                return lhs;
            }
            if((lhs instanceof ScalarLiteral && rhs instanceof PercentageLiteral)
                || (lhs instanceof ScalarLiteral && rhs instanceof PixelLiteral)) {
                return rhs;
            }
            operation.setError("Multiplier should atleast contain one scalar variable");
        }

        return null;
    }

    // This function defines all the allowed properties and their assigned literals.
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
