package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.HashMap;
import java.util.LinkedList;

public class EvalExpressions implements Transform {

    private LinkedList<HashMap<String, Literal>> variableValues;

    public EvalExpressions() {
        variableValues = new LinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        variableValues = new LinkedList<>();
        variableValues.push(new HashMap<String, Literal>());

        for (var node: ast.root.getChildren()) {

            if(node instanceof VariableAssignment) {
                // fill hashmap in linked list
                variableValues.getLast().put(getVariableReferenceName(((VariableAssignment) node).name), (Literal) getVariableReferenceLiteral((((VariableAssignment) node).expression)));
            }
            if(node instanceof Stylerule) {

            }
        }
    }

    private String getVariableReferenceName(VariableReference variableReference) {
        return variableReference.name;
    }

    private Expression getVariableReferenceLiteral(Expression expression) {

        if(expression instanceof Operation) {
            return getOperationValue((Operation) expression);
        }
        if(expression instanceof VariableReference) {
            return variableValues.getLast().get(getVariableReferenceName((VariableReference) expression));
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

    private Literal getOperationValue(Operation operation) {
        Literal lhs = getValueFromExpression(operation.lhs);
        Literal rhs = getValueFromExpression(operation.rhs);
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

    private Literal getValueFromExpression(Expression expression) {
        if(expression instanceof VariableReference) {
            return (Literal) getVariableReferenceLiteral(expression);
        }
        if(expression instanceof Operation) {
            return getOperationValue((Operation) expression);
        }
        // in this case we can always assume it's a literal
        return (Literal) expression;
    }

}
