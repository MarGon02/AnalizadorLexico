import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Uso: java Main <archivo_entrada.json> <archivo_salida.txt>");
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {

            AnalizadorLexico lexer = new AnalizadorLexico();
            String line;
            int lineNum = 1;

            while ((line = reader.readLine()) != null) {
                List<Token> tokens = lexer.tokenize(line);

                boolean hasError = tokens.stream().anyMatch(t -> t.type == TokenType.ERROR);

                if (hasError) {
                    writer.println("Error léxico en línea " + lineNum);
                } else {
                    for (Token token : tokens) {
                        if (token.type != TokenType.EOF)
                            writer.print(token.type + " ");
                    }
                    writer.println();
                }

                lineNum++;
            }

            System.out.println("Análisis completado. Ver resultado en: " + outputFile);

        } catch (IOException e) {
            System.err.println("Error al leer/escribir archivo: " + e.getMessage());
        }
    }
}
