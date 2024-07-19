package Parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import LexicalAnalyzer.Token;
import LexicalAnalyzer.TokenType;

public class Parser {
	private List<Token> tokens;
	private List<Node> AST; // Abstract Syntax Tree
	private ArrayList<String> stringAST; // Abstract Syntax Tree in string format
	
	public Parser(List<Token> tokens) {
		this.tokens = tokens;
		AST = new ArrayList<>();
		stringAST = new ArrayList<>();
	}
	
	// method to parse the tokens and generate the Abstract Syntax Tree
	public List<Node> Parse() {
		tokens.add(new Token(TokenType.EndOfTokens,""));
		E();
		if (tokens.get(0).type.equals(TokenType.EndOfTokens)) {
			return AST; // return the Abstract Syntax Tree
		}
		else {
			// print the remaining unparsed tokens in case of error
			System.out.println("Parsing error: Remaining unparsed tokens : ");
			for (Token token : tokens) {
	            System.out.println("<" + token.type + ", " + token.value + ">");
	        }
			return null;
		}
	}
	
	// method to filter out identifiers integers and strings to add them to the AST, the rest will be simply removed
	public void eat(Token token) {
		if (token.getType() == TokenType.IDENTIFIER) {
			AST.add(new Node(NodeType.identifier, tokens.get(0).value, 0));
			tokens.remove(0);
		}
		else if (token.getType() == TokenType.INTEGER) {
			AST.add(new Node(NodeType.integer, tokens.get(0).value, 0));
			tokens.remove(0);
		}
		else if (token.getType() == TokenType.STRING) {
			AST.add(new Node(NodeType.string, tokens.get(0).value, 0));
			tokens.remove(0);
		}
		else {
			tokens.remove(0);
		}
	}

	// method to convert the Abstract Syntax Tree to a string format
	public ArrayList<String> AST2StringAST() {		
		String dots = "";
		List<Node> stack= new ArrayList<Node>();
		
		while (!AST.isEmpty()) {
			if (stack.isEmpty()) {
				if (AST.get(AST.size()-1).noOfChildren == 0) {
					addStrings(dots,AST.remove(AST.size()-1));
				}
				else {
					Node node = AST.remove(AST.size()-1);
					stack.add(node);
				}
			}
			else {
				if (AST.get(AST.size()-1).noOfChildren>0) {
					Node node = AST.remove(AST.size()-1);
					stack.add(node);
					dots += ".";
				}
				else {
					stack.add(AST.remove(AST.size()-1));
					dots += ".";
					while (stack.get(stack.size()-1).noOfChildren==0) {
						addStrings(dots,stack.remove(stack.size()-1));
						if (stack.isEmpty()) break;
						dots = dots.substring(0, dots.length() - 1);
						Node node =stack.remove(stack.size()-1);
						node.noOfChildren--;
						stack.add(node);
					}
				}
			}
		}
        Collections.reverse(stringAST);
		return stringAST;
	}
	
	// method to add the nodes to the stringAST
	void addStrings(String dots, Node node) {
		switch(node.type) {
			case identifier:
				stringAST.add(dots + "<ID:" + node.value + ">");
				break;
			case integer:
				stringAST.add(dots + "<INT:" + node.value + ">");
				break;
			case string: 
				stringAST.add(dots + "<STR:" + node.value + ">");
				break;	
			case true_value:
				stringAST.add(dots + "<" + node.value + ">");
				break;
			case false_value:
				stringAST.add(dots + "<" + node.value + ">");
				break;
			case nil:
				stringAST.add(dots + "<" + node.value + ">");
				break;
			case dummy:
				stringAST.add(dots + "<" + node.value + ">");
				break;
			case fcn_form:
				stringAST.add(dots + "function_form");
				break;
			default :
				stringAST.add(dots + node.value);
		}		
	}
	
	// --------------------- Expressions -----------------------------------------------------

	/*
		E	->	’let’ D ’in’ E					=> ’let’
			->	’fn’ Vb+ ’.’ E					=> ’lambda’
			->	Ew
	*/

	void E() {		
	    int n=0;
		Token token=tokens.get(0);
		if (token.type.equals(TokenType.KEYWORD) && Arrays.asList("let", "fn").contains(token.value)) { 
			if (token.value.equals("let")) {
				eat(tokens.get(0));
				D();
				if (!tokens.get(0).value.equals("in")) {
					System.out.println("Parsing error at E : 'in' Expected");
				}
				eat(tokens.get(0));
				E();
				AST.add(new Node(NodeType.let, "let", 2));
				
			}
			else {
				eat(tokens.get(0));
				do {
					Vb();
					n++;
				}
				while (tokens.get(0).type.equals(TokenType.IDENTIFIER) || tokens.get(0).value.equals("(")); 
				if (!tokens.get(0).value.equals(".")) {
					System.out.println("Parsing error at E : '.' Expected");
				}
				eat(tokens.get(0));
				E();
				AST.add(new Node(NodeType.lambda, "lambda", n+1));							
			}
		}	
		else
			Ew();	
	}
	
	/*
		Ew	->	T ’where’ Dr					=> ’where’
			->	T
	*/

	void Ew() {
		T();
		if (tokens.get(0).value.equals("where")) {
			eat(tokens.get(0));
			Dr();
			AST.add(new Node(NodeType.where, "where", 2));
	    }
		
	}

	// --------------------- Tuple Expressions -----------------------------------------------

	/*
		T	->	Ta ( ’,’ Ta )+					=> ’tau’
			->	Ta
	*/

	void T() {
		Ta();
		int n = 1;
		while (tokens.get(0).value.equals(",")) {
			eat(tokens.get(0));
			Ta();
			++n;
	    }
	    if (n > 1) {
			AST.add(new Node(NodeType.tau, "tau", n));		
	    }
	}

	/*
		Ta	->	Ta ’aug’ Tc						=> ’aug’
			->	Tc
	*/

	void Ta() {
		Tc();
		while (tokens.get(0).value.equals("aug")) {
			eat(tokens.get(0));
			Tc();
			AST.add(new Node(NodeType.aug, "aug", 2));		
		} 
	}	

	/*
		Tc	->	B ’->’ Tc ’|’ Tc				=> ’->’
			->	B
	*/

	void Tc() {
		B();
		if (tokens.get(0).value.equals("->")) {
			eat(tokens.get(0));
			Tc();
			if (!tokens.get(0).value.equals("|")) {
				System.out.println("Parsing error at Tc : '|' expected");
			}
			eat(tokens.get(0));
			Tc();
			AST.add(new Node(NodeType.conditional, "->", 3));		
		}
	}

	// --------------------- Boolean Expressions ---------------------------------------------

	/*
		B	->	B ’or’ Bt						=> ’or’
			->	Bt
	*/

	void B() {
		Bt();
		while (tokens.get(0).value.equals("or")) {
			eat(tokens.get(0));
			Bt();
			AST.add(new Node(NodeType.op_or, "or", 2));		
		} 
	}

	/*
		Bt	->	Bt ’&’ Bs						=> ’&’
			->	Bs
	*/

	void Bt() {
		Bs();
		while (tokens.get(0).value.equals("&")) {
			eat(tokens.get(0));
			Bs();
			AST.add(new Node(NodeType.op_and, "&", 2));		
		}
	}

	/*
		Bs	->	’not’ Bp						=> ’not’
			->	Bp
	*/

	void Bs() {
		if (tokens.get(0).value.equals("not")) {
			eat(tokens.get(0));
			Bp();
			AST.add(new Node(NodeType.op_not, "not", 1));		
		}
		else Bp();
	}

	/*
		E	->	’let’ D ’in’ E					=> ’let’
			->	’fn’ Vb+ ’.’ E					=> ’lambda’
			->	Ew;
		Bp	->	A (’gr’ | ’>’ ) A				=> ’gr’
			->	A (’ge’ | ’>=’) A				=> ’ge’
			->	A (’ls’ | ’<’ ) A				=> ’ls’
			->	A (’le’ | ’<=’) A				=> ’le’
			->	A ’eq’ A						=> ’eq’
			->	A ’ne’ A						=> ’ne’
			-> 	A
	*/

	void Bp() {
		A();
		Token token = tokens.get(0);
		if (Arrays.asList(">", ">=", "<", "<=").contains(token.value) || Arrays.asList("gr", "ge", "ls", "le", "eq", "ne").contains(token.value)) {
			eat(tokens.get(0));
			A();
			switch(token.value) {
				case ">":
					AST.add(new Node(NodeType.op_compare, "gr", 2));		
					break;
				case ">=":
					AST.add(new Node(NodeType.op_compare, "ge", 2));		
					break;
				case "<":
					AST.add(new Node(NodeType.op_compare, "ls", 2));		
					break;
				case "<=":
					AST.add(new Node(NodeType.op_compare, "le", 2));		
					break;
				default:
					AST.add(new Node(NodeType.op_compare, token.value, 2));	
					break;
			}
		}
	}

	// --------------------- Arithmetic Expressions ------------------------------------------

	/*
		A 	->	A ’+’ At						=> ’+’
			->	A ’-’ At						=> ’-’
			->	’+’ At
			->	’-’At							=>’neg’
			->	At
	*/

	void A() {
		if (tokens.get(0).value.equals("+")) {
			eat(tokens.get(0));
			At();
	    }
		else if (tokens.get(0).value.equals("-")) {
			eat(tokens.get(0));
			At();
			AST.add(new Node(NodeType.op_neg, "neg", 1));	
	    }
		else {
	        At();
	    }
	    while (Arrays.asList("+", "-").contains(tokens.get(0).value)) {
	    	Token currentToken = tokens.get(0);
			eat(tokens.get(0));
			At();
			if (currentToken.value.equals("+")) AST.add(new Node(NodeType.op_plus, "+", 2));
			else AST.add(new Node(NodeType.op_minus, "-", 2));
	    }
		
	}

	/*
		At	->	At ’*’ Af						=> ’*’
			->	At ’/’ Af						=> ’/’
			->	Af
	*/

	void At() {
		Af();
		while (Arrays.asList("*", "/").contains(tokens.get(0).value)) {
			Token currentToken = tokens.get(0);
			eat(tokens.get(0));
			Af();
			if (currentToken.value.equals("*")) AST.add(new Node(NodeType.op_mul, "*", 2));
			else AST.add(new Node(NodeType.op_div, "/", 2));
		}		
	}

	/*
		Af	->	Ap ’**’ Af						=> ’**’
			->	Ap
	*/

	void Af() {
		Ap();
		if (tokens.get(0).value.equals("**")) {
			eat(tokens.get(0));
			Af();
			AST.add(new Node(NodeType.op_pow, "**", 2));	
		}
	}

	/*
		Ap	->	Ap ’@’ ’<IDENTIFIER>’ R			=> ’@’
			->	R
	*/

	void Ap() {
		R();
		while (tokens.get(0).value.equals("@")) {
			eat(tokens.get(0));
			if (!tokens.get(0).type.equals(TokenType.IDENTIFIER)) {
				System.out.println("Parsing error at Ap : an identifier expected");
			}
			eat(tokens.get(0));
			R();
			AST.add(new Node(NodeType.at, "@", 3));	
		}
	}

	// --------------------- Rators and Rands ------------------------------------------------

	/*
		R	->	R Rn							=> ’gamma’
			->	Rn
	*/

	void R() {
		Rn();
		while ((Arrays.asList(TokenType.IDENTIFIER, TokenType.INTEGER, TokenType.STRING).contains(tokens.get(0).type)) || (Arrays.asList("true", "false", "nil", "dummy").contains(tokens.get(0).value)) || (tokens.get(0).value.equals("("))) {
			Rn();
			AST.add(new Node(NodeType.gamma, "gamma", 2));
		}
	}

	/*
		Rn	->	’<IDENTIFIER>’
			->	’<INTEGER>’
			->	’<STRING>’
			->	’true’							=> ’true’
			->	’false’							=> ’false’
			->	’nil’							=> ’nil’
			->	’(’ E ’)’
			->	’dummy’							=> ’dummy’
	*/

	void Rn() {
		switch(tokens.get(0).type) {
			case IDENTIFIER:
				eat(tokens.get(0));
				break;
			case INTEGER:
				eat(tokens.get(0));
				break;
			case STRING:
				eat(tokens.get(0));
				break;
			case KEYWORD:
				switch(tokens.get(0).value) {
					case "true":
						AST.add(new Node(NodeType.true_value, tokens.get(0).value, 0));
						eat(tokens.get(0));
						break;
					case "false":
						AST.add(new Node(NodeType.false_value, tokens.get(0).value, 0));	
						eat(tokens.get(0));
						break;
					case "nil":
						AST.add(new Node(NodeType.nil, tokens.get(0).value, 0));
						eat(tokens.get(0));
						break;
					case "dummy":
						AST.add(new Node(NodeType.dummy, tokens.get(0).value, 0));
						eat(tokens.get(0));
						break;
					default:
						System.out.println("Parsing Error at Rn : invalid keyword");
						break;
				}
				break;
			case PUNCTUATION:
				if (tokens.get(0).value.equals("(")) {
					eat(tokens.get(0));
					E();
					if (!tokens.get(0).value.equals(")")) {
						System.out.println("Parsing error at Rn : ')' expected ");
					}
					eat(tokens.get(0));
				}
				else System.out.println("Parsing error at Rn : invalid punctuation");
				break;
			default:
				System.out.println("Parsing error at Rn : Rn expected");
				break;
		}			
	}

	// --------------------- Definitions -----------------------------------------------------

	/*
		D	->	Da ’within’ D					=> ’within’
			->	Da
	*/

	void D() {
		Da();
		if (tokens.get(0).value.equals("within")) {
			eat(tokens.get(0));
			D();
			AST.add(new Node(NodeType.within, "within", 2));	
		}
	}

	/*
		Da	->	Dr ( ’and’ Dr )+				=> ’and’
			->	Dr
	*/

	void Da() {
		Dr();
		int n = 1;
		while (tokens.get(0).value.equals("and")) {
			eat(tokens.get(0));
			Dr();
			n++;
		}
		if (n>1) AST.add(new Node(NodeType.and, "and", n));	
	}

	/*
		Dr	->	’rec’ Db						=> ’rec’
			->	Db
	*/

	void Dr() {
		boolean isRec = false;
		if (tokens.get(0).value.equals("rec")) {
			eat(tokens.get(0));
	        isRec = true;
	    }
	    Db();
	    if (isRec) {
			AST.add(new Node(NodeType.rec, "rec", 1));	
	    }
	}

	/*
		Db	->	Vl ’=’ E						=> ’=’
			->	’<IDENTIFIER>’ Vb+ ’=’ E		=> ’fcn_form’
			->	’(’ D ’)’
	*/

	void Db() {
		if ( tokens.get(0).type.equals(TokenType.PUNCTUATION) && tokens.get(0).value.equals("(")) {
			eat(tokens.get(0));
			D();
			if (!tokens.get(0).value.equals(")")) {
				System.out.println("Parsing error at Db : ')' expected");
			}
			eat(tokens.get(0));
		}
		else if (tokens.get(0).type.equals(TokenType.IDENTIFIER)) {
			if (tokens.get(1).value.equals("(") || tokens.get(1).type.equals(TokenType.IDENTIFIER)) {
				eat(tokens.get(0));
				int n = 1;
				do {
					Vb();
					n++;
				}
				while (tokens.get(0).type.equals(TokenType.IDENTIFIER) || tokens.get(0).value.equals("("));
				if (!tokens.get(0).value.equals("=")) {
					System.out.println("Parsing error at Db : '=' expected");
				}
				eat(tokens.get(0));
				E();
				
				AST.add(new Node(NodeType.fcn_form, "fcn_form", n+1));		
				
			}
			else if (tokens.get(1).value.equals("=")) {
				eat(tokens.get(0));
				eat(tokens.get(0));
				E();
				AST.add(new Node(NodeType.equal, "=", 2));		
			}
			else if (tokens.get(1).value.equals(",")) {
				Vl();
				if (!tokens.get(0).value.equals("=")) {
					System.out.println("Parsing error at Db : '=' expected");
				}
				eat(tokens.get(0));
				E();
				AST.add(new Node(NodeType.equal, "=", 2));		
			}
		}
	}

	// --------------------- Variables -------------------------------------------------------

	/*
		Vb	->	’<IDENTIFIER>’
			->	’(’ Vl ’)’
			->	’(’ ’)’							=> ’()’
	*/

	void Vb() {
		if (tokens.get(0).type.equals(TokenType.PUNCTUATION) && tokens.get(0).value.equals("(")) {
			eat(tokens.get(0));
			boolean isVl=false;
			if (tokens.get(0).type .equals(TokenType.IDENTIFIER) ) {
				Vl();
				isVl = true;
			}
			if (!tokens.get(0).value.equals(")")) {
				System.out.println("Parsing error at Vb : ')' expected'");
			}
			eat(tokens.get(0));
			if (!isVl) AST.add(new Node(NodeType.empty_params, "()", 0));	
			
		}
		else if (tokens.get(0).type .equals(TokenType.IDENTIFIER) ) {
			eat(tokens.get(0));
	    }
		
	}

	/*
		Vl	->	’<IDENTIFIER>’ list ’,’			=> ’,’
	*/

	void Vl() {
		int n = 0;
		do {
			if (n > 0) {
				eat(tokens.get(0));
			}
			if (!tokens.get(0).type.equals(TokenType.IDENTIFIER)) {
				System.out.println("Parsing error at Vl : an identifier expected");
			}
			eat(tokens.get(0));
			n++;
		}
		while (tokens.get(0).value.equals(","));
		if (n > 1) {
		AST.add(new Node(NodeType.comma, ",", n));
		}
	}
}