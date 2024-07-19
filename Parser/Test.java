package Parser;

import java.util.List;
import Exception.CustomException;
import LexicalAnalyzer.LexicalAnalyser;
import LexicalAnalyzer.Token;

// used for validating the Parser
public class Test {
    public static void main(String[] args) {
        String inputFileName = "t1.txt";
        LexicalAnalyser scanner = new LexicalAnalyser(inputFileName);
        List<Token> tokens;
        List<Node> AST;
        try {
            tokens = scanner.scan();
            Parser parser = new Parser(tokens);
            AST = parser.Parse();
            if (AST == null) return;
            List<String> stringAST = parser.AST2StringAST();
            for (String string : stringAST) {
                System.out.println(string);
            }
        } catch (CustomException e) {
            System.out.println(e.getMessage());
        }
    }
}