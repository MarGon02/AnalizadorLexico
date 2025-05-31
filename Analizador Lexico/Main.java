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

            StringBuilder contenido = new StringBuilder();
            String linea;
            while ((linea = reader.readLine()) != null) {
                contenido.append(linea).append("\n");
            }

            AnalizadorLexico lexer = new AnalizadorLexico();
            List<Token> tokens = lexer.tokenize(contenido.toString());

            boolean tieneErrorLexico = false;
            for (Token token : tokens) {
                if (token.type == TokenType.ERROR) {
                    writer.println("Error léxico: " + token.lexeme);
                    tieneErrorLexico = true;
                } else if (token.type != TokenType.EOF) {
                    writer.print(token.type + " ");
                }
            }
            writer.println();

            if (!tieneErrorLexico) {
                Traductor traductor = new Traductor(tokens);
                String xml = traductor.traducir();
                if (!traductor.hayErrores()) {
                    writer.println("\n--- Traducción a XML ---\n");
                    writer.println(xml);
                } else {
                    writer.println("Se encontraron errores durante la traducción a XML.");
                }
            } else {
                System.err.println("Se encontraron errores léxicos. No se ejecutará el parser ni la traducción.");
            }

            System.out.println("Análisis completado. Ver resultado en: " + outputFile);
        } catch (IOException e) {
            System.err.println("Error al leer/escribir archivo: " + e.getMessage());
        }
    }
}
