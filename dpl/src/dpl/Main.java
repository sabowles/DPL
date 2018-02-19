/*
Main class
dpl
Stephen Bowles
CS 403 Fall 2017
This class executes the source file
*/
package dpl;

public class Main {
	public static void main(String[] args) {
		Lexer lexer = new Lexer(args[0]);
		Parser parse = new Parser(lexer);
		Lexeme nlex = parse.program();
		nlex.eval(nlex, nlex.createEnv());
	}
}
