/*
Lexeme class
dpl
Stephen Bowles
CS 403 Fall 2017
This class stores type, value, and structure information for the source file artifacts. It also contains the needed functionality for
environments and evaluation.
*/
package dpl;

import java.lang.reflect.Type;

public class Lexeme implements Type{

	String type = "NULL";
	String string;
	int integer;
	Lexeme left = null;
	Lexeme right = null;
	Lexeme[] array;
	
	Lexeme(String type){
		this.type = type;
		this.string = "";
		this.integer = 0;
	}

	Lexeme(String type, String string){
		this.type = type;
		this.string = string;
		this.integer = 0;
	}

	Lexeme(String type, int num){
		this.type = type;
		this.integer = num;
		this.string = "";
	}
	
	Lexeme(String type, int num, String string){
		this.type = type;
		this.string = string;
		this.integer = num;
	}

	void display(){ 
		System.out.print(this.type);
		if (!this.string.equals("")){
			System.out.print(" " + this.string);
		}
		System.out.print("\n");
	}
	
	Lexeme cons(String type, Lexeme carVal, Lexeme cdrVal)
	{
		Lexeme temp = new Lexeme(type);
		temp.left = carVal;
		temp.right = cdrVal;
		return temp;
	}
	
	Lexeme car(Lexeme cell)
	{
		return cell.left;
	}
	
	Lexeme cdr(Lexeme cell)
	{
		return cell.right;
	}
	
	void setCar(Lexeme cell, Lexeme val)
	{
		cell.left = val;
	}
	
	void setCdr(Lexeme cell, Lexeme val)
	{
		cell.right = val;
	}
	
	Lexeme createEnv()
	{
		Lexeme global = extendEnv(null,null,null);
		addBuiltIn(global, "println", "println");
		addBuiltIn(global, "createArray", "createArray");
		addBuiltIn(global, "arrayInsert", "arrayInsert");
		addBuiltIn(global, "arrayLookup", "arrayLookup");
		return global;
	}
	
	String type(Lexeme cell)
	{
		return cell.type;
	}
	
	Lexeme lookupEnv(Lexeme env, String variable)
	{
		while(env != null)
		{
			Lexeme table = car(env);
			Lexeme variables = car(table);
			Lexeme values = cdr(table);
			while(variables != null) {
			if(car(variables).string.equals(variable))
			{
				return car(values);
			}
			variables = cdr(variables);
			values = cdr(values);
			}
			env = cdr(env);
		}
		
		System.out.println("Undecleared variable: " + variable);
		System.exit(0);
		return null; //Redundant return to satisfy IDE
	}
	
	int updateEnv(Lexeme env, String variable, Lexeme value)
	{
		while(env != null)
		{
			Lexeme table = car(env);
			Lexeme variables = car(table);
			Lexeme values = cdr(table);
			while(variables != null) {
			if(car(variables).string.equals(variable))
			{
				setCar(values, value);
				return 0;
			}
			variables = cdr(variables);
			values = cdr(values);
			}
			env = cdr(env);
		}
		
		System.out.println("Undecleared variable: " + variable);
		System.exit(0);
		return 1; //Redundant return to satisfy IDE
	}
	
	void insert(Lexeme variable, Lexeme value, Lexeme env)
	{
		Lexeme table = car(env);
		setCar(table, cons("GLUE", variable,car(table)));
		setCdr(table, cons("GLUE", value, cdr(table)));
	}
	
	Lexeme extendEnv(Lexeme env, Lexeme variables, Lexeme values)
	{
		return cons("ENV",makeTable(variables,values),env);
	}
	
	Lexeme makeTable(Lexeme variables, Lexeme values)
	{
		return cons("TABLE", variables, values);
	}
	
	Lexeme eval(Lexeme tree, Lexeme env)
	{
		switch (tree.type)
		{
		case "PROGRAM":
		{
			Lexeme left = null;
			if(tree.left != null)
				left = eval(tree.left, env);
			if(tree.right != null)
				return eval(tree.right, env);
			else
				return left;
		}
		case "NUMBER": return tree;
		case "STRING": return tree;
		case "ARRAY": return tree;
		case "CLOSURE": return tree;
		case "ID": return lookupEnv(env, tree.string);
		case "OPAREN": return eval(tree.right, env);
		case "PLUS":
		case "MINUS":
		case "MULTIPLY":
		case "DIVIDE":
		case "MOD":
		case "LTHAN":
		case "EQUIVALENT":
		case "GTHAN": return evalSimpleOp(tree, env);
		case "UMINUS": return evalUMinus(tree, env);
		case "AND":
		case "OR": return evalSCOP(tree, env);
		case "ASSIGN": return evalAssign(tree, env);
		case "VAR": return evalVarDef(tree, env);
		case "DEF": return evalFuncDef(tree, env);
		case "IF": return evalIf(tree, env);
		case "WHILE": return evalWhile(tree, env);
		case "ANON": return evalAnon(tree, env);
		case "FUNC_CALL": return evalFuncCall(tree, env);
		case "BLOCK": return evalBlock(tree, env);
		default: 
			{
				System.out.println("Bad expression!");
				System.exit(0);
				return null; //Redundant return to satisfy IDE
			}
		}
	}
	
	Lexeme evalSimpleOp(Lexeme tree, Lexeme env)
	{
		if (tree.type.equals("PLUS")) return evalPlus(tree, env);
		if (tree.type.equals("MINUS")) return evalMinus(tree, env);
		if (tree.type.equals("MULTIPLY")) return evalTimes(tree, env);
		if (tree.type.equals("DIVIDE")) return evalDivide(tree, env);
		if (tree.type.equals("MOD")) return evalMod(tree, env);
		if (tree.type.equals("GTHAN")) return evalGthan(tree, env);
		if (tree.type.equals("LTHAN")) return evalLthan(tree, env);
		if (tree.type.equals("EQUIVALENT")) return evalEQ(tree, env);
		return null;
		
	}
	
	Lexeme evalPlus(Lexeme tree, Lexeme env)
	{
		Lexeme left = eval(tree.left, env);
		Lexeme right = eval(tree.right, env);
		if(left.type.equals("NUMBER") && right.type.equals("NUMBER"))
			return new Lexeme("NUMBER", Integer.toString(Integer.parseInt(left.string) + Integer.parseInt(right.string)));
		else
		{
			System.out.println("Invalid argument types for operation: +");
			System.exit(0);
			return null; //Redundant return to satisfy IDE
		}
	}
	
	Lexeme evalMinus(Lexeme tree, Lexeme env)
	{
		Lexeme left = eval(tree.left, env);
		Lexeme right = eval(tree.right, env);
		if(left.type.equals("NUMBER") && right.type.equals("NUMBER"))
			return new Lexeme("NUMBER", Integer.toString(Integer.parseInt(left.string) - Integer.parseInt(right.string)));
		else
		{
			System.out.println("Invalid argument types for operation: -");
			System.exit(0);
			return null; //Redundant return to satisfy IDE
		}
	}
	
	Lexeme evalTimes(Lexeme tree, Lexeme env)
	{
		Lexeme left = eval(tree.left, env);
		Lexeme right = eval(tree.right, env);
		if(left.type.equals("NUMBER") && right.type.equals("NUMBER"))
			return new Lexeme("NUMBER", Integer.toString(Integer.parseInt(left.string) * Integer.parseInt(right.string)));
		else
		{
			System.out.println("Invalid argument types for operation: *");
			System.exit(0);
			return null; //Redundant return to satisfy IDE
		}
	}
	
	Lexeme evalDivide(Lexeme tree, Lexeme env)
	{
		Lexeme left = eval(tree.left, env);
		Lexeme right = eval(tree.right, env);
		if(left.type.equals("NUMBER") && right.type.equals("NUMBER"))
			return new Lexeme("NUMBER", Integer.toString(Integer.parseInt(left.string) / Integer.parseInt(right.string)));
		else
		{
			System.out.println("Invalid argument types for operation: /");
			System.exit(0);
			return null; //Redundant return to satisfy IDE
		}
	}
	
	Lexeme evalMod(Lexeme tree, Lexeme env)
	{
		Lexeme left = eval(tree.left, env);
		Lexeme right = eval(tree.right, env);
		if(left.type.equals("NUMBER") && right.type.equals("NUMBER"))
			return new Lexeme("NUMBER", Integer.toString(Integer.parseInt(left.string) % Integer.parseInt(right.string)));
		else
		{
			System.out.println("Invalid argument types for operation: %");
			System.exit(0);
			return null; //Redundant return to satisfy IDE
		}
	}
	
	Lexeme evalGthan(Lexeme tree, Lexeme env)
	{
		Lexeme left = eval(tree.left, env);
		Lexeme right = eval(tree.right, env);
		if(left.type.equals("NUMBER") && right.type.equals("NUMBER"))
			if(Integer.parseInt(left.string) > Integer.parseInt(right.string))
				return new Lexeme("BOOL", "TRUE");
			else
				return  new Lexeme("BOOL", "FALSE");
		else
		{
			System.out.println("Invalid argument types for operation: >");
			System.exit(0);
			return null; //Redundant return to satisfy IDE
		}
	}
	
	Lexeme evalLthan(Lexeme tree, Lexeme env)
	{
		Lexeme left = eval(tree.left, env);
		Lexeme right = eval(tree.right, env);
		if(left.type.equals("NUMBER") && right.type.equals("NUMBER"))
			if(Integer.parseInt(left.string) < Integer.parseInt(right.string))
				return new Lexeme("BOOL", "TRUE");
			else
				return  new Lexeme("BOOL", "FALSE");
		else
		{
			System.out.println("Invalid arguments for operation: <");
			System.exit(0);
			return null; //Redundant return to satisfy IDE
		}
	}

	Lexeme evalUMinus(Lexeme tree, Lexeme env)
	{
		Lexeme val = eval(tree.right, env);
		if(val.type.equals("NUMBER") && Integer.parseInt(val.string) > 0)
			val.string = "-" + val.string;
		return val;
	}
	
	Lexeme evalEQ(Lexeme tree, Lexeme env)
	{
		Lexeme left = eval(tree.left, env);
		Lexeme right = eval(tree.right, env);
		if(left.string.equals(right.string) && left.type.equals(right.type))
			return new Lexeme("BOOL", "TRUE");
		else
			return  new Lexeme("BOOL", "FALSE");
		
	}
	
	Lexeme evalAssign(Lexeme tree, Lexeme env)
	{
		Lexeme val = eval(tree.right, env);
		updateEnv(env, tree.left.string, val);
		return val;
	}

	Lexeme evalSCOP(Lexeme tree, Lexeme env)
	{
		Lexeme left = eval(tree.left, env);
		if(tree.type == "AND")
		{
			if(left.string.equals("FALSE"))
				return left;
			else
			{
				return eval(tree.right, env);
			}
		}
		if(tree.type == "OR")
		{
			if(left.string.equals("TRUE"))
				return left;
			else
			{
				return eval(tree.right, env);
			}
		}
		return null;
	}

	Lexeme evalFuncDef(Lexeme tree, Lexeme env)
	{
		Lexeme closure = cons("CLOSURE", env, tree);
		insert(tree.left, closure, env);
		return closure;
	}
	
	Lexeme evalAnon(Lexeme tree, Lexeme env)
	{
		return cons("CLOSURE", env, tree);
	}

	Lexeme evalFuncCall(Lexeme tree, Lexeme env)
	{
		Lexeme closure = eval(tree.left, env);
		Lexeme args = tree.right.left;
		if(closure.type.equals("BUILTIN"))
		{
			return lookupBuiltIn(args, env, closure.string);
		}
		Lexeme params = closure.right.right.left;
		Lexeme body = closure.right.right.right;
		Lexeme senv = closure.left;
		Lexeme eargs = evalArgs(args, env);
		Lexeme xenv = extendEnv(senv, params, eargs);
		
		return eval(body, xenv);
	}

	Lexeme evalArgs(Lexeme tree, Lexeme env)
	{
		if(tree == null)
			return null;
		else
		{	
			return cons("GLUE", eval(tree.left, env), evalArgs(tree.right, env));
		}
	}

	Lexeme evalVarDef(Lexeme tree, Lexeme env)
	{
		if(tree.right != null)
			insert(tree.left, eval(tree.right, env), env);
		else
			insert(tree.left, null, env);
		return tree;
	}
	
	Lexeme evalBlock(Lexeme tree, Lexeme env)
	{
		Lexeme result = null;
		tree = tree.right; //move to first glue node
		while (tree != null)
		{
			result = eval(tree.left, env);
			tree = tree.right;
		}
		
		return result;
	}

	Lexeme evalIf(Lexeme tree, Lexeme env)
	{
		Lexeme exprResult = eval(tree.left, env);
		if(exprResult.string.equals("TRUE"))
			return evalBlock(tree.right.left, env);
		else
			if(tree.right.right != null)
			{
				return evalBlock(tree.right.right, env);
			}
			return exprResult;
	}
	
	Lexeme evalWhile(Lexeme tree, Lexeme env)
	{
		Lexeme exprResult = eval(tree.left, env);
		Lexeme currentBlock = null;
		while(exprResult.string.equals("TRUE"))
		{
			currentBlock = evalBlock(tree.right.left, env);
			exprResult = eval(tree.left, env);
		}
		return currentBlock;
	}
	
	void addBuiltIn(Lexeme env, String name, String type)
	{
		Lexeme bi = new Lexeme("BUILTIN");
		bi.string = type; //Will lookup later
		Lexeme var = new Lexeme("ID");
		var.string = name;
		insert(var, bi, env);
	}
	
	Lexeme lookupBuiltIn(Lexeme args, Lexeme env, String type)
	{
		if(type.equals("println"))
			return evalPrintln(env, args);
		else if(type.equals("createArray"))
			return evalArrayInit(env, args);
		else if(type.equals("arrayInsert"))
			return evalArrayInsert(env, args);
		else if(type.equals("arrayLookup"))
			return evalArrayRetrieve(env, args);
		else
		{
			System.out.println("Error: Unable to locate built in function");
			System.exit(0);
			return null; //Redundant return to satisfy IDE
		}
	}
	
	Lexeme evalPrintln(Lexeme env, Lexeme args)
	{
		if(args == null)
		{
			System.out.println();
			return null;
		}
		Lexeme arg = evalArgs(args, env);
		if(arg.left.type.equals("NUMBER") || arg.left.type.equals("STRING") || arg.left.type.equals("BOOL"))
			System.out.println(arg.left.string);
		else
			System.out.println(arg.left.type);
		return arg;
	}
	
	Lexeme evalArrayInit(Lexeme env, Lexeme args)
	{
		{
			Lexeme arg = evalArgs(args, env);
			Lexeme arr = new Lexeme("ARRAY");
			if(arg.left.type.equals("NUMBER")) {
				int size = Integer.parseInt(arg.left.string);
				arr.array = new Lexeme[size];
				arr.integer = size;
			}
			else
			{
				System.out.println("Runtime Error: Invalid type for size of array. Size must be a number");
				System.exit(0);
				return null; //Redundant return to satisfy IDE
			}
			return arr;
		}
	}
	
	Lexeme evalArrayInsert(Lexeme env, Lexeme args)
	{
		Lexeme arg = evalArgs(args, env);
		Lexeme arr = arg.left;
		int index = Integer.parseInt(arg.right.left.string);
		if(index < arr.integer)
		{
			Lexeme val = arg.right.right.left;
			arr.array[index] = val;
			return arr;
		}
		else
		{
			System.out.println("Runtime Error: Array index out of bounds");
			System.exit(0);
			return null; //Redundant return to satisfy IDE
		}
	}
	
	Lexeme evalArrayRetrieve(Lexeme env, Lexeme args)
	{
		Lexeme arg = evalArgs(args, env);
		Lexeme arr = arg.left;
		int index = Integer.parseInt(arg.right.left.string);
		if(index < arr.integer)
			return eval(arr.array[index], env);
		else
		{
			System.out.println("Runtime Error: Array index out of bounds");
			System.exit(0);
			return null; //Redundant return to satisfy IDE
		}
	}
}
