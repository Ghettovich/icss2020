package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.HashMap;
import java.util.LinkedList;

public class EvalExpressions implements Transform {

    private HashMap<String, Literal> globalVariables;
    private LinkedList<HashMap<String, Literal>> variableValues;

    public EvalExpressions() {
        variableValues = new LinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        variableValues = new LinkedList<>();
        variableValues.push(new HashMap<String, Literal>());
        globalVariables = new HashMap<>();

        for (int i = 0; i < ast.root.getChildren().size(); i++) {
            ASTNode node = ast.root.getChildren().get(i);

            if(node instanceof VariableAssignment) {
                // fill hashmap in linked list
                globalVariables.put(getVariableReferenceName(((VariableAssignment) node).name),
                        (Literal) getVariableReferenceLiteral((((VariableAssignment) node).expression), null));

                ast.root.removeChild(node);
                i--;
            }
            if(node instanceof Stylerule) {
                checkStyleRule((Stylerule)node);
            }
        }
    }

    private String getVariableReferenceName(VariableReference variableReference) {
        return variableReference.name;
    }

    private void checkStyleRule(Stylerule stylerule) {
        HashMap<String, Literal> scopeVariables = new HashMap<>();

        for (int i = 0; i < stylerule.body.size(); i++) {

            ASTNode node = stylerule.body.get(i);

            if(node instanceof VariableAssignment) {
                scopeVariables.put(getVariableReferenceName(((VariableAssignment) node).name),
                        (Literal) getVariableReferenceLiteral((((VariableAssignment) node).expression), scopeVariables));

                stylerule.body.remove(i);
                i--;
            }

            if(node instanceof Declaration) {
                ((Declaration) node).expression = getVariableReferenceLiteral(((Declaration) node).expression, scopeVariables);
                stylerule.body.set(i, node);
            }
        }
    }

    // Most of the times it will return a literal
    private Expression getVariableReferenceLiteral(Expression expression, HashMap<String, Literal> scopeVariables) {

        if(expression instanceof Operation) {
            return getOperationValue((Operation) expression, scopeVariables);
        }
        if(expression instanceof VariableReference) {
            // check if exists within his OWN scope
            if(scopeVariables != null && scopeVariables.containsKey(getVariableReferenceName((VariableReference) expression))) {
                return scopeVariables.get(getVariableReferenceName((VariableReference) expression));
            }
            // if not in local scope it should be in global scope
            else {
                return globalVariables.get(getVariableReferenceName((VariableReference) expression));
            }
        }
        if(expression instanceof PixelLiteral) {
            return (PixelLiteral) expression;
        }
        if(expression instanceof PercentageLiteral) {
            return (PercentageLiteral) expression;
        }
        if(expression instanceof ScalarLiteral) {
            return (ScalarLiteral) expression;
        }
        if(expression instanceof ColorLiteral) {
            return (ColorLiteral) expression;
        }
        // Support for boolean values not implemented
        // if(expression instanceof BoolLiteral)
        return null;
    }

    private Literal getOperationValue(Operation operation, HashMap<String, Literal> scopeVariables) {
        Literal lhs = getValueFromExpression(operation.lhs, scopeVariables);
        Literal rhs = getValueFromExpression(operation.rhs, scopeVariables);
        Literal literal = calculateValue(lhs, rhs, operation);
        return literal;
    }

    private Literal calculateValue(Literal lhs, Literal rhs, Operation operation) {

        if(operation instanceof SubtractOperation) {
            if(lhs instanceof PixelLiteral) {
                return new PixelLiteral(((PixelLiteral) lhs).value - ((PixelLiteral) rhs).value);
            }
            if(lhs instanceof PercentageLiteral) {
                return new PercentageLiteral(((PercentageLiteral) lhs).value - ((PercentageLiteral) rhs).value);
            }
        }
        if(operation instanceof AddOperation) {
            if(lhs instanceof PixelLiteral) {
                return new PixelLiteral(((PixelLiteral) lhs).value + ((PixelLiteral) rhs).value);
            }
            if(lhs instanceof PercentageLiteral) {
                return new PercentageLiteral(((PercentageLiteral) lhs).value + ((PercentageLiteral) rhs).value);
            }
        }
        if(operation instanceof MultiplyOperation) {

            if(lhs instanceof PixelLiteral) {
                return new PixelLiteral(((PixelLiteral) lhs).value * ((ScalarLiteral) rhs).value);
            }
            if(lhs instanceof PercentageLiteral) {
                return new PercentageLiteral(((PercentageLiteral) lhs).value * ((ScalarLiteral) rhs).value);
            }
            if(rhs instanceof PixelLiteral) {
                return new PixelLiteral(((ScalarLiteral) lhs).value * ((PixelLiteral) rhs).value);
            }
            if(rhs instanceof PercentageLiteral) {
                return new PixelLiteral(((ScalarLiteral) lhs).value * ((PercentageLiteral) rhs).value);
            }
            else {
                return new ScalarLiteral(((ScalarLiteral) lhs).value * ((ScalarLiteral) rhs).value);
            }
        }

        return null;
    }

    private Literal getValueFromExpression(Expression expression, HashMap<String, Literal> scopeVariables) {
        if(expression instanceof VariableReference) {
            return (Literal) getVariableReferenceLiteral(expression, scopeVariables);
        }
        if(expression instanceof Operation) {
            return getOperationValue((Operation) expression, scopeVariables);
        }
        // in this case we can always assume it's a literal
        return (Literal) expression;
    }

}
