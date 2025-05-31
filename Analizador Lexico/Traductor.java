import java.util.*;

public class Traductor {
    private final List<Token> tokens;
    private int current = 0;
    private final StringBuilder xml = new StringBuilder();
    private boolean hayError = false;

    public Traductor(List<Token> tokens) {
        this.tokens = tokens;
    }

    public String traducir() {
        xml.append("<root>\n");
        json();
        xml.append("</root>\n");
        return hayError ? null : xml.toString();
    }

    private Token lookAhead() {
        return tokens.get(current);
    }

    private Token match(TokenType expected) {
        Token token = lookAhead();
        if (token.type == expected) {
            current++;
            return token;
        } else {
            hayError = true;
            System.err.println("Error de traducción: se esperaba " + expected + " pero se encontró " + token.type);
            panic(expected);
            return null;
        }
    }

    private void panic(TokenType... sincronizadores) {
        while (current < tokens.size()) {
            Token t = lookAhead();
            for (TokenType sync : sincronizadores) {
                if (t.type == sync) {
                    return;
                }
            }
            current++;
        }
    }

    private void json() {
        element();
        match(TokenType.EOF);
    }

    private void element() {
        Token t = lookAhead();
        if (t.type == TokenType.L_LLAVE) {
            object();
        } else if (t.type == TokenType.L_CORCHETE) {
            array("item");
        } else {
            hayError = true;
            System.err.println("Se esperaba un objeto o arreglo.");
            panic(TokenType.R_LLAVE, TokenType.R_CORCHETE, TokenType.EOF);
        }
    }

    private void object() {
        match(TokenType.L_LLAVE);
        if (lookAhead().type != TokenType.R_LLAVE) {
            attributesList();
        }
        match(TokenType.R_LLAVE);
    }

    private void attributesList() {
        attribute();
        while (lookAhead().type == TokenType.COMA) {
            match(TokenType.COMA);
            attribute();
        }
    }

    private void attribute() {
        Token nameToken = match(TokenType.LITERAL_CADENA);
        String tag = nameToken != null ? nameToken.lexeme.replaceAll("\"", "") : "unknown";
        match(TokenType.DOS_PUNTOS);
        xml.append("<").append(tag).append(">");
        attributeValue(tag);
        xml.append("</").append(tag).append(">\n");
    }

    private void attributeValue(String tag) {
        TokenType tipo = lookAhead().type;
        switch (tipo) {
            case L_LLAVE:
                object();
                break;
            case L_CORCHETE:
                array(tag);
                break;
            case LITERAL_CADENA:
            case LITERAL_NUM:
            case PR_TRUE:
            case PR_FALSE:
            case PR_NULL:
                xml.append(lookAhead().lexeme);
                match(tipo);
                break;
            default:
                hayError = true;
                System.err.println("Valor de atributo inválido en: " + tag);
                panic(TokenType.COMA, TokenType.R_LLAVE);
        }
    }

    private void array(String tagName) {
        match(TokenType.L_CORCHETE);
        xml.append("\n");
        if (lookAhead().type != TokenType.R_CORCHETE) {
            element();
            while (lookAhead().type == TokenType.COMA) {
                match(TokenType.COMA);
                element();
            }
        }
        match(TokenType.R_CORCHETE);
    }

    public boolean hayErrores() {
        return hayError;
    }
}
