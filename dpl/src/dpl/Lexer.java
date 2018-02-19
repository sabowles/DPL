/*
Lexer class
dpl
Stephen Bowles
CS 403 Fall 2017
This class scans the source file and breaks it up into lexemes as demanded by the parser.
*/
package dpl;


import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PushbackReader;

public class Lexer{
	String Input;
	File program;
	PushbackReader buffer;
	public Lexer(String programin){
		this.Input = programin;
		this.program = new File(Input);
		Charset encoding = Charset.defaultCharset();
		InputStream in = null;
		try {
			in = new FileInputStream(program);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	    Reader reader = new InputStreamReader(in, encoding);
	    this.buffer = new PushbackReader(reader);
	}
	
	private char charRead() {
		int r;
        try {
			if ((r = this.buffer.read()) != -1) {
			    return (char) r;
			}
			else {
				return '|'; //EoF symbol
			}
		} catch (IOException e) {
			e.printStackTrace();
			return '|';
		}
	}
	
	private void pushback(char c) {
		try {
			buffer.unread((int)c);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Lexeme lex()
	{
		char ch;
		SkipWhiteSpace();
		ch = charRead();

		if (ch == ':'){
			skipComment();
			SkipWhiteSpace();
			ch = charRead();
		}
		if (ch == '|') return new Lexeme("ENDOFINPUT");
		
		switch(ch){
			case '(': return new Lexeme("OPAREN");
			case ')': return new Lexeme("CPAREN");
			case ',': return new Lexeme("COMMA");
			case '+': return new Lexeme("PLUS");
			case '-': return new Lexeme("MINUS");
			case '*': return new Lexeme("MULTIPLY");
			case '/': return new Lexeme("DIVIDE");
			case '>': return new Lexeme("GTHAN");
			case '<': return new Lexeme("LTHAN");
			case '=': return new Lexeme("ASSIGN");
			case ';': return new Lexeme("OR");
			case '?': return new Lexeme("EQUIVALENT");
			case '&': return new Lexeme("AND");
			case '%': return new Lexeme("MOD");
			case '{': return new Lexeme("OBRACKET");
			case '}': return new Lexeme("CBRACKET");
			case '#': return new Lexeme("ANON");

			default:
				if(Character.isDigit(ch)){
					pushback(ch);
					return lexNumber();
				}
				else if (Character.isLetter(ch)){
					pushback(ch);
					return lexVarOrKey();
				}
				else if (ch == '\"'){
					return lexString();
				}
				else
					return new Lexeme("UNKNOWN", ch);
		}
	}

	public void SkipWhiteSpace()
	{
		char ch = charRead();;
		while (Character.isWhitespace(ch)){
			ch = charRead();
		}
		pushback(ch);
	}

	public Lexeme lexVarOrKey(){
		char ch;
		String token = "";

		ch = charRead();
		while (Character.isLetter(ch) || Character.isDigit(ch))
		{
			token = token + ch;
			ch = charRead();
		}
		pushback(ch);
		if (token.equals("if")){
			return new Lexeme("IF");
		}
		else if (token.equals("else")){
			return new Lexeme("ELSE");
		}
		else if (token.equals("while")){
			return new Lexeme("WHILE");
		}
		else if (token.equals("call")){
			return new Lexeme("FUNC_CALL");
		}
		else if (token.equals("for")){
			return new Lexeme("FOR");
		}
		else if (token.equals("return")){
			return new Lexeme("RETURN");
		}
		else if (token.equals("var")){
			return new Lexeme("VAR");
		}
		else if (token.equals("def")){
			return new Lexeme("DEF");
		}
		else{
			return new Lexeme("ID", token);
		}
	}

	public Lexeme lexString(){
		char ch;
		String token = "";

		ch = charRead();
		while (ch != '\"')
		{
			token = token + ch;
			ch = (char) charRead();
			if(ch == '|')
			{
				System.out.println("Syntax Error: Unterminated string");
				System.exit(0);
			}
		}
		return new Lexeme("STRING", token);
		}
	
	public void skipComment(){
		char ch;
		

		ch = charRead();
		if(ch == ':')
		{
			while(ch != '\n')
			{
				ch = (char) charRead();
			}
			pushback(ch);
		}
		else if(ch == '$') {
			while (ch != '|')
			{
				ch = (char) charRead();
			}
			pushback(ch);
		}
		else
		{
			System.out.println("Syntax Error: Invalid comment. Expected ':' or '$' after ':'");
			System.exit(0);
		}
		}
	

	public Lexeme lexNumber(){
		char ch;
		String token = "";

		ch = charRead();
		while (Character.isDigit(ch))
		{
			token = token + ch;
			ch = charRead();
		}
		pushback(ch);
		return new Lexeme("NUMBER", token);
		}
	}



