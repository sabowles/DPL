package dpl;


public class Scanner {
	boolean cool;
	public Scanner() {
		cool = true;
	}
	
	public void scan(String input){
		Lexeme token;
		Lexer l = new Lexer(input);
		token = l.lex();
		while(token.type != "ENDOFINPUT"){
			token.display();
			token = l.lex();
		}
	}
	
	public void main() {
		Scanner scanner = new Scanner();
		scanner.scan("test.prime");
	}
}
