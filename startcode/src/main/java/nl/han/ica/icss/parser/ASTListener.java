package nl.han.ica.icss.parser;

import java.util.Stack;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {

    //Accumulator attributes:
    private AST ast;

    //Use this to keep track of the parent nodes when recursively traversing the ast
    private Stack<ASTNode> currentContainer;

    public ASTListener() {
        ast = new AST();
        currentContainer = new Stack<>();
    }

    public AST getAST() {
        return ast;
    }

    @Override
    public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
        currentContainer.push(new Stylesheet());
    }

    @Override
    public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
        ast.setRoot((Stylesheet) currentContainer.pop());
    }

    @Override
    public void enterStylerule(ICSSParser.StyleruleContext ctx) {
        currentContainer.push(new Stylerule());
    }

    @Override
    public void exitStylerule(ICSSParser.StyleruleContext ctx) {
        Stylerule stylerule = ((Stylerule) currentContainer.pop());
        currentContainer.peek().addChild(stylerule);
    }

    @Override
    public void enterSelectortag(ICSSParser.SelectortagContext ctx) {
        currentContainer.peek().addChild(new TagSelector(ctx.getText()));
    }

    @Override
    public void enterSelectorid(ICSSParser.SelectoridContext ctx) {
        currentContainer.peek().addChild(new IdSelector(ctx.getText()));
    }

    @Override
    public void enterSelectorclass(ICSSParser.SelectorclassContext ctx) {
        currentContainer.peek().addChild(new ClassSelector(ctx.getText()));
    }

    @Override
    public void enterStyledeclaration(ICSSParser.StyledeclarationContext ctx) {
        currentContainer.push(new Declaration());
    }

    @Override
    public void exitStyledeclaration(ICSSParser.StyledeclarationContext ctx) {
        Declaration d = (Declaration)currentContainer.pop();
        currentContainer.peek().addChild(d);
    }

    @Override
    public void enterPropertyname(ICSSParser.PropertynameContext ctx) {
        currentContainer.peek().addChild(new PropertyName(ctx.getText()));
    }

    @Override
    public void enterBoolLiteral(ICSSParser.BoolLiteralContext ctx) {
        currentContainer.push(new BoolLiteral(ctx.getText()));
    }

    @Override
    public void enterColorLiteral(ICSSParser.ColorLiteralContext ctx) {
        currentContainer.push(new ColorLiteral(ctx.getText()));
    }

    @Override
    public void enterPercentageLiteral(ICSSParser.PercentageLiteralContext ctx) {
        currentContainer.push(new PercentageLiteral(ctx.getText()));
    }

    @Override
    public void enterPixelLiteral(ICSSParser.PixelLiteralContext ctx) {
        currentContainer.push(new PixelLiteral(ctx.getText()));
    }

    @Override
    public void enterScalarLiteral(ICSSParser.ScalarLiteralContext ctx) {
        currentContainer.push(new ScalarLiteral(ctx.getText()));
    }

    @Override
    public void enterVariableassignment(ICSSParser.VariableassignmentContext ctx) {
        currentContainer.push(new VariableAssignment());
    }

    @Override
    public void enterAddOperator(ICSSParser.AddOperatorContext ctx) {
        currentContainer.push(new AddOperation());
    }

    @Override
    public void enterSubstractOperator(ICSSParser.SubstractOperatorContext ctx) {
        currentContainer.push(new SubtractOperation());
    }

    @Override
    public void enterMultiplyOperator(ICSSParser.MultiplyOperatorContext ctx) {
        currentContainer.push(new MultiplyOperation());
    }

    @Override
    public void exitVariableassignment(ICSSParser.VariableassignmentContext ctx) {
        ASTNode varAssignment = currentContainer.pop();
        currentContainer.peek().addChild(varAssignment);
    }

    @Override
    public void enterVariablereference(ICSSParser.VariablereferenceContext ctx) {
        if (ctx.parent.getChildCount() != 1){
            currentContainer.peek().addChild(new VariableReference(ctx.getText()));
        }
        else {
            currentContainer.push(new VariableReference(ctx.getText()));
        }
    }

    @Override
    public void exitExpression(ICSSParser.ExpressionContext ctx) {
        if(ctx.getChildCount() == 1) {
            ASTNode l = currentContainer.pop();
            currentContainer.peek().addChild(l);

        } else {
            for (int i = 2; i < ctx.getChildCount(); i+=2) {
                ASTNode rightValue = this.currentContainer.pop();
                ASTNode operator = this.currentContainer.pop();
                ASTNode leftValue = this.currentContainer.pop();

                ((Operation)operator).rhs = (Expression) rightValue;
                ((Operation)operator).lhs = (Expression) leftValue;

                if (i + 2 < ctx.getChildCount()){
                    currentContainer.push(operator);
                }else {
                    currentContainer.peek().addChild(operator);
                }
            }
        }
    }


}
