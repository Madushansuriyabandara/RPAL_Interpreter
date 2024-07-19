package LexicalAnalyzer;

import java.util.List;
import Exception.CustomException;

// used for validating the Lexical Analyzer
public class Test {
    public static void main(String[] args) {
        String inputFileName = "t1.txt";
        LexicalAnalyser scanner = new LexicalAnalyser(inputFileName);
        List<Token> tokens;
        try {
            tokens = scanner.scan();
            for (Token token : tokens) {
                System.out.println("<" + token.getType() + ", " + token.getValue() + ">");
            }
        } catch (CustomException e) {
            System.out.println(e.getMessage());
        }
    }
}