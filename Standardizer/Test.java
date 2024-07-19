package Standardizer;

import java.util.ArrayList;
import java.util.List;
import Exception.CustomException;
import LexicalAnalyzer.LexicalAnalyser;
import LexicalAnalyzer.Token;
import Parser.Parser;

// used for validating the Standardizer
public class Test {
    public static void main(String[] args) {
        String inputFileName = "t1.txt";
        LexicalAnalyser scanner = new LexicalAnalyser(inputFileName);
        List<Token> tokens;
        try {
            tokens = scanner.scan();
			Parser parser = new Parser(tokens);
			parser.Parse();
			ArrayList<String> stringAST = parser.AST2StringAST();
			ASTFactory astf = new  ASTFactory();
			AST ast = astf.getAST(stringAST);
			ast.standardize();
            ast.printAst();
        } catch (CustomException e) {
            System.out.println(e.getMessage());
        }
    }
}