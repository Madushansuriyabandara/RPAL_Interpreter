package CSEMachine;

import java.util.ArrayList;
import java.util.List;
import Exception.CustomException;
import Parser.Parser;
import Standardizer.AST;
import Standardizer.ASTFactory;
import LexicalAnalyzer.LexicalAnalyser;
import LexicalAnalyzer.Token;

// Runner class executes the program and returns the answer
public class Runner {
    public static String Run(String filename, boolean isPrintAST) {
		// calls the lexicalAnalyzer
	    LexicalAnalyser scanner = new LexicalAnalyser(filename);
	    List<Token> tokens;  
		try {
			tokens = scanner.scan();
			if (tokens.isEmpty()) {
				System.out.println("The program is empty");
				return "";
			}
			// calls the parser
			Parser parser = new Parser(tokens);
			parser.Parse();
			ArrayList<String> stringAST = parser.AST2StringAST();
			// if the AST switch is on
			if (isPrintAST) {
				// output the AST
				for(String string: stringAST) { 
					System.out.println(string);
				}
			}
			// if the AST switch is off
			else {
				// calls the standardizer
				ASTFactory astf = new  ASTFactory();
				AST ast = astf.getAST(stringAST);
				ast.standardize();
				// calls the CSEMachine
				CSEMachineFactory csemfac = new CSEMachineFactory();
				CSEMachine csemachine = csemfac.getCSEMachine(ast);
				// output the answer
				return csemachine.getAnswer();
			}
		} catch (CustomException e) {
			System.out.println(e.getMessage());
		}
        return null;
    }
}