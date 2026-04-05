package main;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class StartApp {
    public static void main(String[] args) throws IOException {
        VeloxLexer lexer = new VeloxLexer();

        if (args.length == 0) {
            System.err.println("Usage: java VeloxLexer <input_file.velox>");
            System.exit(1);
        }

        String filename = args[0];
        List<String> lines = Files.readAllLines(Paths.get(filename));

        // Tokenize all lines
        List<Token> all = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++)
            all.addAll(lexer.tokenizeLine(lines.get(i), i + 1));

        // Print token table
        System.out.println("--- Token List ---");
        System.out.printf("%-12s %-24s  %s%n", "TYPE", "VALUE", "LINE");
        System.out.println("-".repeat(55));
        for (Token t : all)
            System.out.println(t);

        // Structural check
        lexer.validateStructure(all);

    }
}