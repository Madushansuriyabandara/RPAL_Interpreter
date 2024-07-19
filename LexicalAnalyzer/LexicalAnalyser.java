package LexicalAnalyzer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import Exception.CustomException;

public class LexicalAnalyser {
    private String inputFileName;
    private List<Token> tokens;	

    public LexicalAnalyser(String inputFileName) {
        this.inputFileName = inputFileName;
        tokens = new ArrayList<>();
    }
    
    // method to scan the input file and tokenize the input
    public List<Token> scan() throws CustomException {
        try (BufferedReader reader = new BufferedReader(new FileReader(this.inputFileName))) {
            String line;
            int lineCount = 0;
            // reading through each line of the input file
            while ((line = reader.readLine()) != null) {
            	lineCount++;
                try {
					tokenizeLine(line);
				} catch (CustomException e) {
                    // custom exception to handle errors in the lexical analyzer
					throw new CustomException(e.getMessage() + " in line " + lineCount + "\nError in LexicalAnalyzer");
				}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this.tokens;
    }

    private void tokenizeLine(String line) throws CustomException {

        /*
            Identifier      ->  Letter (Letter | Digit | ’_’)*              => ’<IDENTIFIER>’;

            Integer         ->  Digit+                                      =>’<INTEGER>’;

            Operator        ->  Operator_symbol+                            => ’<OPERATOR>’;

            String          ->  ’’’’
                                ( ’\’ ’t’ | ’\’ ’n’ | ’\’ ’\’ | ’\’ ’’’’
                                | ’(’ | ’)’ | ’;’ | ’,’
                                | ’ ’
                                | Letter | Digit | Operator_symbol
                                )* ’’’’                                     => ’<STRING>’;

            Spaces          ->  ( ’ ’ | ht | Eol )+                         => ’<DELETE>’;

            Comment         ->  ’//’
                                ( ’’’’ | ’(’ | ’)’ | ’;’ | ’,’ | ’\’ | ’ ’
                                | ht | Letter | Digit | Operator_symbol
                                )* Eol                                      => ’<DELETE>’;
                                
            Punction        ->  ’(’                                         =>’(’
                            ->  ’)’                                         => ’)’
                            ->  ’;’                                         => ’;’
                            ->  ’,’                                         => ’,’;

            Letter          ->  ’A’..’Z’ | ’a’..’z’;

            Digit           ->  ’0’..’9’;

            Operator_symbol ->  ’+’ | ’-’ | ’*’ | ’<’ | ’>’ | ’&’ | ’.’
                                | ’@’ | ’/’ | ’:’ | ’=’ | ’˜’ | ’|’ | ’$’
                                | ’!’ | ’#’ | ’%’ | ’ˆ’ | ’_’ | ’[’ | ’]’
                                | ’{’ | ’}’ | ’"’ | ’‘’ | ’?’;
        */

        // regex patterns for different types of tokens
    	String digit = "[0-9]";
    	String letter = "[a-zA-Z]";
    	Pattern operatorSymbol = Pattern.compile("[+\\-*/<>&.@/:=~|$!#%^_\\[\\]{}\"`\\?]");
    	Pattern escape = Pattern.compile("(\\\\'|\\\\t|\\\\n|\\\\\\\\)");
        Pattern identifierPattern = Pattern.compile(letter+"("+letter+"|"+digit+"|"+"_)*");
        Pattern integerPattern = Pattern.compile(digit + "+");
        Pattern operatorPattern = Pattern.compile(operatorSymbol + "+");
        Pattern punctuationPattern = Pattern.compile("[(),;]");
        Pattern spacesPattern = Pattern.compile("(\s|\t)+");
        Pattern stringPattern = Pattern.compile("'("+letter+"|"+digit+"|"+operatorSymbol+"|"+escape+"|"+punctuationPattern+"|"+spacesPattern+")*'");
        Pattern commentPattern = Pattern.compile("//.*");

        Matcher matcher;
        
        int currentIndex = 0;
        // reading through each character of the line
        while (currentIndex < line.length()) {
            char currentChar = line.charAt(currentIndex);
            Matcher spaceMatcher = spacesPattern.matcher(line.substring(currentIndex));
            Matcher commentMatcher = commentPattern.matcher(line.substring(currentIndex));
            
            // checking for spaces and comments
            if(commentMatcher.lookingAt()) {
            	String comment = commentMatcher.group();
            	currentIndex += comment.length();
            	continue;
            }
            if (spaceMatcher.lookingAt()) {
            	String space = spaceMatcher.group();
          		currentIndex += space.length();
          		continue;
            }
                       
            matcher = identifierPattern.matcher(line.substring(currentIndex));

            if (matcher.lookingAt()) {
                String identifier = matcher.group();
                // checking for keywords
                List<String> keywords = List.of("let", "in", "fn", "where", "aug", "or", "not", "gr", "ge", "ls", "le", "eq", "ne", "true", "false", "nil", "dummy", "within", "and", "rec");
                if(keywords.contains(identifier)) 
                	this.tokens.add(new Token(TokenType.KEYWORD, identifier));
                else
                	this.tokens.add(new Token(TokenType.IDENTIFIER, identifier));
                currentIndex += identifier.length();
                continue;
            }

            matcher = integerPattern.matcher(line.substring(currentIndex));
            // checking for integers
            if (matcher.lookingAt()) {
                String integer = matcher.group();
                this.tokens.add(new Token(TokenType.INTEGER, integer));
                currentIndex += integer.length();
                continue;
            }

            matcher = operatorPattern.matcher(line.substring(currentIndex));
            // checking for operators
            if (matcher.lookingAt()) {
                String operator = matcher.group();
                this.tokens.add(new Token(TokenType.OPERATOR, operator));
                currentIndex += operator.length();
                continue;
            }

            matcher = stringPattern.matcher(line.substring(currentIndex));
            // checking for strings
            if (matcher.lookingAt()) {
                String string = matcher.group();
                this.tokens.add(new Token(TokenType.STRING, string));
                currentIndex += string.length();
                continue;
            }

            matcher = punctuationPattern.matcher(Character.toString(currentChar));
            // checking for punctuations
            if (matcher.matches()) {
                this.tokens.add(new Token(TokenType.PUNCTUATION, Character.toString(currentChar)));
                currentIndex++;
                continue;
            }
            
            // if none of the above cases are satisfied, throws an exception
            throw new CustomException("Error tokenizing the character " + currentChar + " at index " + currentIndex);
        }    
    }
}