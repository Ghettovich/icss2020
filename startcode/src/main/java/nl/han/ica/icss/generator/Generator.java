package nl.han.ica.icss.generator;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;

public class Generator {

	public String generate(AST ast) {
		StringBuilder stringBuilder = new StringBuilder();

		for (ASTNode node : ast.root.getChildren()) {
			stringBuilder.append(printStylerule((Stylerule) node));
		}

		return stringBuilder.toString();
	}

	private String printStylerule(Stylerule stylerule) {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(printTag(stylerule));
		stringBuilder.append(" {");
		stringBuilder.append(printDivider());
		for (ASTNode node: stylerule.body) {
			stringBuilder.append(printDeclaration((Declaration) node));
		}
		stringBuilder.append("} ");

		return stringBuilder.toString();
	}

	private String printDeclaration(Declaration declaration) {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(declaration.property.name);
		stringBuilder.append(printColon());
		stringBuilder.append(printLiteral(declaration.expression));
		stringBuilder.append(printSemiColon());
		stringBuilder.append(" ");

		return stringBuilder.toString();
	}

	private String printLiteral(ASTNode node){
		if (node instanceof ColorLiteral){
			return ((ColorLiteral) node).value;
		}
		if (node instanceof PercentageLiteral){
			return ((PercentageLiteral) node).value + "%";
		}
		if (node instanceof PixelLiteral){
			return ((PixelLiteral) node).value + "px";
		}
		return "";
	}

	private String printTag(Stylerule stylerule) {
		return stylerule.selectors.get(0).toString();
	}

	private String printDivider() {
		return "  ";
	}

	private String printColon() {
		return ": ";
	}

	private String printSemiColon() {
		return ";";
	}

	private String addNewLine() {
		return "\n";
	}
}
