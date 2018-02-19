/*
Parser class
dpl
Stephen Bowles
CS 403 Fall 2017
This class parses the source file, catching any syntax errors and building the parse tree needed for evaluation
printTree() is an incomplete pretty printer used during early debugging
*/
package dpl;

public class Parser{

	Lexer lexer;
	Lexeme CurrentLexeme;

	public Parser(Lexer lexee){
		this.lexer = lexee;
		CurrentLexeme = lexer.lex();
	}

	public Lexeme program(){
		Lexeme tree = new Lexeme("PROGRAM");
		if (definitionPending()){
			tree.left = definition();
		}
		else if(expressionPending()) 
		{
			tree.left = expression();
		}
		if (definitionPending()){
			tree.right = program();
		}
		else if(expressionPending()) 
		{
			tree.right = program();
		}		
		return tree;
	}

	public Lexeme definition(){
		Lexeme tree;
		if (check("VAR")){
			tree = varDef();
		}
		else
			tree  = funcDef();
		return tree;
	}

	public Lexeme varDef(){
		Lexeme tree;
		tree = match("VAR");
		tree.left = match("ID");
		if (check("ASSIGN")){
			match("ASSIGN");
			tree.right = expression();
		}
		return tree;
	}

	public Lexeme expression(){
		if(check("IF"))
		{
			return conditional();
		}
		else if(check("WHILE"))
		{
			return loop();
		}
		else if(check("ANON"))
		{
			return anon();
		}
		else if(check("STRING"))
		{
			return match("STRING");
		}
		else{
			Lexeme u = unary();
			if(operationPending()){ 
				Lexeme tree = operator();
				tree.left = u;
				tree.right = expression();
				if(scOpPending())
				{
					Lexeme temp = tree;
					tree = operator();
					tree.left = temp;
					tree.right = expression();
				}
				return tree;
			}
			/*else if(scOpPending())
			{
				Lexeme tree = operator();
				tree.left = u;
				tree.right = expression();
				return tree;
			}*/
			return u;
		}
	}

	public Lexeme unary(){
		Lexeme tree = null;
		if (check("NUMBER")){
			tree = advance();
		}
		else if(check("STRING"))
			tree = advance();
		else if(check("ID")){
			tree = advance();
		}
		else if(check("FUNC_CALL"))
		{
			tree = funcCall();
		}
		else if(check("ANON"))
		{
			tree = anon();
		}
		else if (check("OPAREN"))
        {
        tree = match("OPAREN"); 
        tree.left = null;
        tree.right = expression();
        match("CPAREN");
        }
    else if(check("MINUS"))
        {
        match("MINUS");
        tree = new Lexeme("UMINUS"); 
        tree.left = null;
        tree.right = unary();
        }
		return tree;
	}
	
	public Lexeme funcCall()
	{
		Lexeme tree = match("FUNC_CALL");
		if(check("ID")) {
		tree.left = match("ID");
		tree.right = new Lexeme("GLUE");
		match("OPAREN");
		tree.right.left = optArgs();
		match("CPAREN");
		return tree;
		}
		else if(check("FUNC_CALL"))
		{
			tree.left = funcCall();
			tree.right = new Lexeme("GLUE");
			match("OPAREN");
			tree.right.left = optArgs();
			match("CPAREN");
			return tree;
		}
		else
		{
			tree.left = anon();
			tree.right = new Lexeme("GLUE");
			match("OPAREN");
			tree.right.left = optArgs();
			match("CPAREN");
			return tree;
		}
	}

	public Lexeme funcDef(){
		Lexeme tree;
		tree = match("DEF");
		tree.left = match("ID");
		match("OPAREN");
		tree.right = new Lexeme("GLUE");
		tree.right.left = optParam();
		match("CPAREN");
		tree.right.right = block();
		return tree;
	}
	
	public Lexeme anon()
	{
		Lexeme tree= match("ANON");
		tree.right = new Lexeme("GLUE");
		match("OPAREN");
		tree.right.left = optParam();
		match("CPAREN");
		tree.right.right = block();
		return tree;
	}

	public Lexeme optParam(){
		if(check("ID")){
			Lexeme tree;
			tree = paramList();
			return tree;
		}
		else{
			return null;
		}
	}
	
	public Lexeme optArgs(){
		Lexeme tree = null;
		if(expressionPending() || functionPending())
			tree = paramList();
		return tree;
	}
	
	public Lexeme paramList(){
		Lexeme tree = null;
		if(expressionPending() || functionPending())
		{
		Lexeme u = unary();
		tree = new Lexeme("GLUE");
		if(operationPending()){ 
			tree.left = operator();
			tree.left.left = u;
			tree.left.right = expression();
		}
		else
			tree.left = u;
		}
		if(check("COMMA")){
			advance();
			tree.right = paramList();
		}
		return tree;
	}

	public Lexeme item()
	{
		Lexeme tree;
		if (check("STRING")){
			tree = match("STRING");
			
		}
		else{
			tree = unary();
		}
		return tree;
	}

	public Lexeme operator(){
		Lexeme tree;
		if (check("PLUS")){
			tree = advance();
		}
		else if (check("MINUS")){
			tree = advance();
		}
		else if (check("ASSIGN")){
			tree = advance();
		}
		else if (check("MULTIPLY")){
			tree = advance();
		}
		else if (check("DIVIDE")){
			tree = advance();
		}
		else if (check("MOD")){
			tree = advance();
		}
		else if (check("GTHAN")){
			tree = advance();
		}
		else if (check("OR")){
			tree = advance();
		}
		else if (check("AND")){
			tree = advance();
		}
		else if (check("LTHAN")){
			tree = advance();
		}
		else{
			tree = match("EQUIVALENT");
		}
		return tree;

	}

	public Lexeme conditional(){
		Lexeme tree = match("IF");
		match("OPAREN");
		tree.left = expression();
		match("CPAREN");
		tree.right = new Lexeme("GLUE");
		tree.right.left = block();
		if(check("ELSE"))
		{
			tree.right.right = alt();
		}
		return tree;
	}
	
	public Lexeme alt()
	{
		match("ELSE");
		Lexeme tree = block();
		return tree;
	}

	public Lexeme loop(){
		Lexeme tree = match("WHILE");
		match("OPAREN");
		tree.left = expression();
		match("CPAREN");
		tree.right = new Lexeme("GLUE");
		tree.right.left = block();
		return tree;
	}
	
	public Lexeme block(){
		match("OBRACKET");
		Lexeme tree = new Lexeme("BLOCK");
		tree.right = commands();
		match("CBRACKET");
		return tree;
	}

	public Lexeme commands(){
		Lexeme tree = null;
		if (!check("CBRACKET")){
			tree = new Lexeme("GLUE");
			if (expressionPending()){
				tree.left = expression();
				tree.right = commands();
			}
			else if (definitionPending()){
				tree.left = definition();
				tree.right = commands();
			}		
		}
		return tree;
		}

	public boolean check(String type){
		return CurrentLexeme.type.equals(type);
	}

	public Lexeme advance(){
		Lexeme newLex = new Lexeme(CurrentLexeme.type, CurrentLexeme.integer, CurrentLexeme.string);
		this.CurrentLexeme = lexer.lex();
		return newLex;
	}

	public Lexeme match(String type){
		matchNoAdvance(type);
		return advance();
	}

	public void matchNoAdvance(String type){
		if (!check(type)){
			System.out.println("Syntax Error: Expected: " + type + " Found: " + CurrentLexeme.type); //Use proper error output here
			System.exit(0);
		}
	}

	public boolean functionPending(){
		return check("FUNC_CALL");
	}

	public boolean definitionPending(){
		return check("VAR") || check("DEF");
	}

	public boolean expressionPending(){
		return check("NUMBER") || check("ID") || check("IF") || check("WHILE") || check("ANON") || check("FUNC_CALL") || check("STRING") || check("MINUS");
	}

	public boolean operationPending(){
		return check("PLUS") || check("MULTIPLY") || check("MINUS") || check("DIVIDE") || check("MOD") || check("GTHAN") || check("LTHAN") || check("EQUIVALENT") || check("ASSIGN");// || scOpPending();
	}
	
	public boolean scOpPending()
	{
		return check("AND") || check("OR");
	}

	public void printTree(Lexeme tree){
		if(tree != null) {
		switch(tree.type){
		case "PROGRAM": 
		{
			printTree(tree.left);
			printTree(tree.right);
		}
		break;
		case "COMMA":
		{
			System.out.print(", ");
			printTree(tree.left);
			printTree(tree.right);
		}
			break;
		case "BLOCK":
		{
			printTree(tree.right);
		}
			break;
		case "IF":
		{
			System.out.print("if(");
			printTree(tree.left);
			System.out.print(")\n{\n");
			printTree(tree.right.left);
			System.out.print("\n}\n");
			printTree(tree.right.right);
		}
		break;
			case "NUMBER": System.out.print(tree.string);
				break;
			case "ID": 
				{
					System.out.print(tree.string);
					printTree(tree.left);
					printTree(tree.right);
				}
				break;
			case "STRING": System.out.print(tree.string + '\"');
				break;
			case "OPAREN":
			{
				System.out.print("(");
				printTree(tree.right);
				System.out.print(")\n");
			}
				break;
			case "ELSE":
			{
				System.out.print("else ");
				if(tree.left.type.equals("IF"))
				{
					printTree(tree.left);
					printTree(tree.right);
				}
				else {
				System.out.print("\n{\n");
				printTree(tree.left);
				System.out.print("\n}\n");
				printTree(tree.right);}
			}
				break;
			case "BINARY":
			{
				printTree(tree.right.left);
				printTree(tree.left);
				printTree(tree.right.right);
				System.out.println();
			}
				break;
			case "GLUE":
			{
				printTree(tree.left);
				printTree(tree.right);
			}
				break;
			case "ASSIGN":
			{
				System.out.print(" = ");
				printTree(tree.left);
				printTree(tree.right);
			}
				break;
			case "PLUS":
			{
				System.out.print(" + ");
				printTree(tree.left);
				printTree(tree.right);
			}
				break;
			case "MINUS":
			{
				System.out.print(" - ");
				printTree(tree.left);
				printTree(tree.right);
			}
				break;
			case "MULTIPLY":
			{
				System.out.print(" * ");
				printTree(tree.left);
				printTree(tree.right);
			}
				break;
			case "DIVIDE":
			{
				System.out.print(" / ");
				printTree(tree.left);
				printTree(tree.right);
			}
				break;
			case "LTHAN":
			{
				System.out.print(" < ");
				printTree(tree.left);
				printTree(tree.right);
			}
				break;
			case "GTHAN":
			{
				System.out.print(" > ");
				printTree(tree.left);
				printTree(tree.right);
			}
				break;
			case "EQUIVALENT":
			{
				System.out.print(" ? ");
				printTree(tree.left);
				printTree(tree.right);
			}
				break;
			case "PARAMLIST":
			{
				System.out.print("(");
				printTree(tree.left);
				printTree(tree.right);
				System.out.print(")\n");
			}
				break;
			case "ARGLIST":
			{
				System.out.print("(");
				printTree(tree.left);
				printTree(tree.right);
				System.out.print(")\n");
			}
				break;
			case "COMMAND":
			{
				printTree(tree.left);
				printTree(tree.right);
			}
				break;
			case "DEF":
			{
				System.out.print("def ");
				printTree(tree.left);
				printTree(tree.right.left);
				System.out.print("{ \n");

				printTree(tree.right.right);
				System.out.println("}\n");
			}
				break;
			case "VAR":
			{
				System.out.print("var ");
				System.out.print(tree.left.string);
				if(tree.right != null) {
					System.out.print(" = ");
					printTree(tree.right);
					System.out.print("\n");
				}
			}
			break;
		}
	}
	}

}
