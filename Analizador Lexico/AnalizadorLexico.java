import java.util.*;
import java.util.regex.*;

public class AnalizadorLexico {
    private static final Map<TokenType, Pattern> tokenPatterns = new LinkedHashMap<>();

    static {
        tokenPatterns.put(TokenType.L_CORCHETE, Pattern.compile("^\\["));
        tokenPatterns.put(TokenType.R_CORCHETE, Pattern.compile("^\\]"));
        tokenPatterns.put(TokenType.L_LLAVE, Pattern.compile("^\\{"));
        tokenPatterns.put(TokenType.R_LLAVE, Pattern.compile("^\\}"));
        tokenPatterns.put(TokenType.COMA, Pattern.compile("^,"));
        tokenPatterns.put(TokenType.DOS_PUNTOS, Pattern.compile("^:"));
        tokenPatterns.put(TokenType.LITERAL_CADENA, Pattern.compile("^\"(\\\\.|[^\"])*\""));
        tokenPatterns.put(TokenType.LITERAL_NUM, Pattern.compile("^[0-9]+(\\.[0-9]+)?([eE][+-]?[0-9]+)?"));
        tokenPatterns.put(TokenType.PR_TRUE, Pattern.compile("^(?i)true"));
        tokenPatterns.put(TokenType.PR_FALSE, Pattern.compile("^(?i)false"));
        tokenPatterns.put(TokenType.PR_NULL, Pattern.compile("^(?i)null"));
    }

    public List<Token> tokenize(String line) {
        List<Token> tokens = new ArrayList<>();
        String input = line.trim();

        while (!input.isEmpty()) {
            boolean matched = false;

            input = input.stripLeading();

            for (Map.Entry<TokenType, Pattern> entry : tokenPatterns.entrySet()) {
                Matcher matcher = entry.getValue().matcher(input);
                if (matcher.find()) {
                    String lexeme = matcher.group();
                    tokens.add(new Token(entry.getKey(), lexeme));
                    input = input.substring(lexeme.length());
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                tokens.add(new Token(TokenType.ERROR, String.valueOf(input.charAt(0))));
                break;
            }
        }

        tokens.add(new Token(TokenType.EOF, "EOF"));
        return tokens;
    }
}
