package ahoCorasick;
import utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AhoCorasick {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java -jar gruppennname_blatt02_1.jar <in:text-file> <in:pattern-file>");
            System.exit(1);
        }

        try {
            String text = Utils.readFile(args[0]);
            String [] patterns = Utils.readPatterns(args[1]);
            Trie T = new Trie();
            int k = 0;

            for (String pattern : patterns) {
                T.insert(pattern);
                k += pattern.length();
            }
            T.setFailureLinks();
            Map<String, List<Integer>> output = new HashMap<>();
            search(T, text, output);
            writeOutput(output);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeOutput(Map<String, List<Integer>> output) {
        System.out.println("p_i\tindex"); // Header row

        for (Map.Entry<String, List<Integer>> entry : output.entrySet()) {
            String pattern = entry.getKey();
            List<Integer> positions = entry.getValue();

            // Print pattern, followed by a tab
            System.out.print(pattern + "\t[");

            // Print all positions, separated by commas
            for (int i = 0; i < positions.size(); i++) {
                System.out.print(positions.get(i));
                if (i < positions.size() - 1) {
                    System.out.print(", ");
                }
            }

            // Close the bracket and go to the next line
            System.out.println("]");
        }
    }


    private static void search(Trie T, String text, Map<String, List<Integer>> output) {
        TrieNode curr = T.root;
        int i = 0;
        TrieNode temp;

        while (i < text.length()) {
            char c = text.charAt(i);

            // Follow failure links until we find a match or reach the root
            while (curr != T.root && curr.children.get(c) == null) {
                curr = curr.failureLink;
            }

            // Move to the matched child or root if no match found
            curr = curr.children.getOrDefault(c, T.root);

            // If we reach a pattern, record all patterns that end here
            temp = curr;
            while (temp != T.root) {
                if (temp.isLeaf && temp.pattern.equals(text.substring(i - temp.pattern.length() + 1, i + 1))) {
                    String pattern = temp.pattern;
                    output.computeIfAbsent(pattern, k -> new ArrayList<>())
                            .add(i - pattern.length() + 1);
                }
                temp = temp.failureLink;
            }

            i++;
        }
    }

}
