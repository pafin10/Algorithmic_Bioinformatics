package changLawler;

import java.io.*;
import java.util.*;

import static java.util.Collections.sort;

public class PatternMatcher {
    private int k;
    private String text;
    private String pattern;
    private String msOutputFile;
    private String survivingRegionsOutputFile;

    public PatternMatcher(String[] args) throws IOException {
        if (args.length < 5) {
            throw new IllegalArgumentException("Insufficient arguments. Expected 5 arguments.");
        }

        this.k = parseIntArg(args[0]);
        this.text = readFile(args[1]);
        this.pattern = readFile(args[2]);
        this.msOutputFile = args[3];
        this.survivingRegionsOutputFile = args[4];
    }

    private static int parseIntArg(String arg) {
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("First argument must be an integer.");
        }
    }
    public static void main(String[] args) {
        if (args.length != 5) {
            System.err.println("Usage: java -jar gruppennname_ex4.jar <k> <in:text-file> <in:pattern-file> <out:ms(j).tsv> <out:surviving_regions.txt>");
            System.exit(1);
        }

        try {
            PatternMatcher p = new PatternMatcher(args);
            int k = p.k;
            String text = p.text;
            String pattern = p.pattern;
            String msOutputFile = p.msOutputFile;
            String survivingRegionsOutputFile = p.survivingRegionsOutputFile;


            Map<Integer, Integer> ms = new HashMap<>();
            List<Integer> survivingRegions = new ArrayList<>();

            check_regions(survivingRegions, pattern.length() / 2, k, text, pattern, ms);
            Map<Integer, Integer> sortedMs = new TreeMap<>(ms);

            // Write mismatch scores to the specified file
            try (BufferedWriter msWriter = new BufferedWriter(new FileWriter(msOutputFile))) {
                msWriter.write("j\tms(j)\n");
                for (Map.Entry<Integer, Integer> entry : sortedMs.entrySet()) {
                    msWriter.write(entry.getKey() + "\t" + entry.getValue() + "\n");
                }
            }

            sort(survivingRegions);


            // Write surviving regions to the specified file
            try (BufferedWriter regionsWriter = new BufferedWriter(new FileWriter(survivingRegionsOutputFile))) {
                regionsWriter.write("surviving regions " + "(k=" + k + ")" + "\n");
                regionsWriter.write("[");
                int cnt = 0;
                for (int position : survivingRegions) {
                    cnt++;
                    if (cnt < survivingRegions.size())
                        regionsWriter.write(position + ", ");
                    else regionsWriter.write(position + "");
                }
                regionsWriter.write("]");
            }

            System.out.println("Output written to " + msOutputFile + " and " + survivingRegionsOutputFile);

        } catch (NumberFormatException e) {
            System.err.println("Error: The number of allowed mismatches (k) must be an integer.");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void check_regions(List<Integer> survivingRegions, int r, int k, String text, String pattern, Map<Integer, Integer> ms) {
        int i = 0, j = 0;
        int cnt = 0;

        while (i < text.length()) {
            while (cnt <= k && i - j <= r && i < text.length()) {
                int tmp = compute_ms(text, pattern, i);
                if (i + tmp < text.length())
                    ms.put(i, tmp);
                else
                    ms.put(i, text.length() - i);

                i += tmp + 1;
                cnt++;
            }

            if (i - j > r || i > text.length() || (i < text.length() && (i - j) == r && cnt < k)) survivingRegions.add(j);
            cnt = 0;
            j += r;
            i = j;
        }
    }


    private static int compute_ms(String text, String pattern, int j) {
        int n = text.length(), m = pattern.length(), ms = 0;
        if (j == n) return 0;

        int a = 0;
        while (a < m) {
            for (int l = a; l < m; l++) {
                if (j + (l - a) < n && text.charAt(j + (l - a)) != pattern.charAt(l)) {
                    ms = Math.max(ms, l - a);
                    break;
                }
                if (l == m - 1) ms = Math.max(ms, l - a + 1);
            }
            a++;
        }

        return ms;
    }

    private static String readFile(String filename) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        }
        return content.toString();
    }
}
