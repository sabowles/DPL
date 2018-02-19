dpl

Stephen Bowles

cs 403 Fall 2017

sabowles@crimson.ua.edu

Language ltcl(pronounced late call)

Contents: 

          1. [prstr] Program Structure

          2. [varbl] Variables
          
          3. [funct] Functions 
          
          4. [cmmnt] Comments 
          
          5. [lmbda] Anonymous Functions 
          
          6. [cndtl] Conditionals 
          
          7. [itern] Iteration 
          
          8. [exprs] Expressions 
          
          9. [bltin] Built In Functions

[prstr] Program Structure 

Every program in ltcl is a composed of some
combination of definitions and expressions. A definition must precede an
expression can be executed which references the defined item. No
expressions will be executed if none is referenced from the global
scope. As such, it is recommended that no expression be executed until
all definitions are complete.

    Programs are deliniated based on whitespace. Any whitespace can indicate a separation between elements of an
    expression, expressions, or definitions. 
    
    Example:
        
        var i
        
        i = 0
        
        and
        
        var i i = 0
    
    will produce the same results.

[varbl] Variables 

Variables in ltcl are dynamically typed. Any unit of
the program that can be returned can be assigned to a variable, though
it should be noted that only some values may be operated upon.

    A variable is declared with the keyword "var". A variable may be assigned at the time of its declaration or after
    but not before.

    Variables may be operated upon, passed as arguments, and assigned to other variables. When a variable is assigned
    to another variable, a copy of that variable's value is given to the new variable. This allows a variable to be 
    manipulated without compromising the variable that has received its value.

    Example code:
    
    var i
    
    var j = 0
    
    var i = 1
    
    var x = i
    
    var i = 2
    
    var y = i + x 

[funct] Functions 

Functions in ltcl are defined with the keyword "def".

    Example:
    
    def func(args)
    
    {
    
      block
    
    }

    "args" may be any alphabet character or list of characters separated by commas.
    
    "block" is any expression, definition, or set thereof surrounded by curly braces.
    
    Functions are called with the keywork "call". A function call must be preceded by this keyword.

    Example:
    
    call func(x)

    In a function execution the last command of the block is the return value. A function can be assigned to a variable
    as in the example:
    
    var b = func
    
    call b(x)

    They may also be returned from another function or passed to a function as an argument

[cmmnt] Comments

Comments in ltcl begin with the character ':' and come
in two forms. "::" Comments out a single line while ":\$" comments out
the remainder of the file.

[lmbda] Anonymous Functions 

An anonymous function is specified by the symbol '\#'. An anonymous function may be called directly, assigned to a
variable, passed to, or returned from a function. Example:

    call #(x){ x + 1 }(2)
    
    var x = #(y){y * 2}
    
    call x(2)

[cndtl] Conditionals

Conditionals in ltcl take the form of if-else structures. The keywork "if" is followed by an expression using the
operators '\>', '\<', or '?' for greater than, less than, or equal to respectively. These operations may be combined with the short cut
operators ';' and '&' representing "or" and "and" respectively.

    There are no else if branches in ltcl. In order to acheive the same functionality, one may nest an if conditional
    withing the else branch.

    It is important to note that the else branch is not required.
    
    Example
    
    if(expr)
    
    {
    
      block
    
    }
    
    else
    
    {
    
      block
    
    }

[itern] Iteration 

Iteration in ltcl is implemented with the "while" keyword. Much like the "if" keyword, it is followed by an expression
comprised of boolean and short circuit operators. 
  
    Example:
  
    while(expr)
  
    {
  
      block
  
    }

[exprs] Expressions

Expressions are comprised of variables, numbers, function calls, and operations. Operations are executed from right to
left and have no order of precedence. If a particular order of
evaluation or precedence is needed, the subexpressions should be
enclosed within parentheses.

    Because of the operation order, it is important to note that comparing the results of an expression to a specific value
    requires either the value be listed with the appropriate comparitor before the expression to be compared, or that
    the expression be enclosed in parentheses.

    Example:
    
    "Is the expression a - b greater than 0?" can be written in positive or negative form as follows.
    
    0 < a - b
    
    (a - b) > 0

    The operators supported are as follows:
    
    '+': Add two numbers
    
    '-': Subtract two numbers
    
    '*': Multiply two numbers
    
    '/': Divide two numbers
    
    '%': Mod two numbers
    
    '>': Return a boolean indicating if a number is greater than another
    
    '<': Return a boolean indicating if a number is greater than another
    
    '?': Return a boolean if two items are of the same type and value. This is the only operator that is compatible with
        any type.
    
    ';': Or operator
    
    '&': And operator

[bltin] Built In Functions

ltcl supports four built in functions:
    
    println(x): prints the value of x if x is of type NUMBER, STRING, or BOOL. Prints the type of x otherwise.

    createArray(size): returns a new array of length size. This array may hold any type of item and may hold more than one type.
    
    arrayInsert(arr, index, val): inserts or updates the value stored at slot "index" of array "arr" to "val" 
    
    arrayLookup(arr, index): returns the values stored in array "arr" at slot "index"
