package nl.han.ica.icss.parser;

import java.util.Stack;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
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
        this.ast.setRoot((Stylesheet) currentContainer.pop());
    }

    @Override
    public void enterDeclarationRule(ICSSParser.DeclarationRuleContext ctx) {
        currentContainer.push(new Stylerule());
    }

    @Override
    public void exitDeclarationRule(ICSSParser.DeclarationRuleContext ctx) {
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
        currentContainer.peek().addChild(new BoolLiteral(ctx.getText()));
    }

    @Override
    public void enterColorLiteral(ICSSParser.ColorLiteralContext ctx) {
        currentContainer.peek().addChild(new ColorLiteral(ctx.getText()));
    }

    @Override
    public void enterPercentageLiteral(ICSSParser.PercentageLiteralContext ctx) {
        currentContainer.peek().addChild(new PercentageLiteral(ctx.getText()));
    }

    @Override
    public void enterPixelLiteral(ICSSParser.PixelLiteralContext ctx) {
        currentContainer.peek().addChild(new PixelLiteral(ctx.getText()));
    }

    @Override
    public void enterScalarLiteral(ICSSParser.ScalarLiteralContext ctx) {
        currentContainer.peek().addChild(new ScalarLiteral(ctx.getText()));
    }
}
