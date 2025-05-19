import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;
    private boolean hayError = false;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public void parse() {
        json();
        if (!hayError) {
            System.out.println("El archivo es sintácticamente correcto.");
        }
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
            error("Se esperaba: " + expected + " pero se encontró: " + token.type);
            panic(expected);
            return null;
        }
    }

    private void error(String mensaje) {
        hayError = true;
        System.err.println("Error sintáctico: " + mensaje);
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

    // json => element EOF
    private void json() {
        element();
        match(TokenType.EOF);
    }

    // element => object | array
    private void element() {
        Token t = lookAhead();
        if (t.type == TokenType.L_LLAVE) {
            object();
        } else if (t.type == TokenType.L_CORCHETE) {
            array();
        } else {
            error("Se esperaba un objeto o un arreglo.");
            panic(TokenType.R_LLAVE, TokenType.R_CORCHETE, TokenType.EOF);
        }
    }

    // array => [element-list] | []
    private void array() {
        match(TokenType.L_CORCHETE);
        if (lookAhead().type == TokenType.R_CORCHETE) {
            match(TokenType.R_CORCHETE);
        } else {
            elementList();
            match(TokenType.R_CORCHETE);
        }
    }

    // element-list => element-list , element | element
    private void elementList() {
        element();
        while (lookAhead().type == TokenType.COMA) {
            match(TokenType.COMA);
            element();
        }
    }

    // object => {attributes-list} | {}
    private void object() {
        match(TokenType.L_LLAVE);
        if (lookAhead().type == TokenType.R_LLAVE) {
            match(TokenType.R_LLAVE);
        } else {
            attributesList();
            match(TokenType.R_LLAVE);
        }
    }

    // attributes-list => attributes-list , attribute | attribute
    private void attributesList() {
        attribute();
        while (lookAhead().type == TokenType.COMA) {
            match(TokenType.COMA);
            attribute();
        }
    }

    // attribute => attribute-name : attribute-value
    private void attribute() {
        attributeName();
        match(TokenType.DOS_PUNTOS);
        attributeValue();
    }

    // attribute-name => string
    private void attributeName() {
        match(TokenType.LITERAL_CADENA);
    }

    // attribute-value => element | string | number | true | false | null
    private void attributeValue() {
        TokenType tipo = lookAhead().type;
        switch (tipo) {
            case L_CORCHETE:
            case L_LLAVE:
                element();
                break;
            case LITERAL_CADENA:
            case LITERAL_NUM:
            case PR_TRUE:
            case PR_FALSE:
            case PR_NULL:
                match(tipo);
                break;
            default:
                error("Valor de atributo inválido.");
                panic(TokenType.COMA, TokenType.R_LLAVE);
        }
    }
}
