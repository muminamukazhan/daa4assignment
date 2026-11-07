package textanalysis;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public final class Benchmark {
    static final String ALPHABET = "ACGT";
    static final long SEED = 42L;
    static final int RUNS = 5;
    static final int PATTERN_LENGTH = 20;

    private static long sink;

    private interface Task {
        long run();
    }

    private Benchmark() {
    }

    static String random(int length, long seed) {
        Random rnd = new Random(seed);
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHABET.charAt(rnd.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    static final int WARMUP = 20;

    private static double timeMs(Task task) {
        for (int w = 0; w < WARMUP; w++) {
            sink += task.run();
        }
        long total = 0;
        for (int r = 0; r < RUNS; r++) {
            long start = System.nanoTime();
            long value = task.run();
            long end = System.nanoTime();
            total += end - start;
            sink += value;
        }
        return total / (double) RUNS / 1_000_000.0;
    }

    private static String fmt(double v) {
        return String.format(Locale.ROOT, "%.4f", v);
    }

    private static String[] makeInput(String scenario, int n) {
        switch (scenario) {
            case "present": {
                String text = random(n, SEED + n);
                return new String[]{text, text.substring(n / 2, n / 2 + PATTERN_LENGTH)};
            }
            case "absent": {
                String text = random(n, SEED + n);
                long seed = SEED + 7;
                String pattern = random(PATTERN_LENGTH, seed);
                while (text.indexOf(pattern) >= 0) {
                    pattern = random(PATTERN_LENGTH, ++seed);
                }
                return new String[]{text, pattern};
            }
            default: {
                String text = "A".repeat(n);
                String pattern = "A".repeat(PATTERN_LENGTH - 1) + "C";
                return new String[]{text, pattern};
            }
        }
    }

    private static void runDp(Path dir, StringBuilder md) throws IOException {
        int[] sizes = {100, 500, 1000};
        double[] xs = new double[sizes.length];
        double[] lcsTimes = new double[sizes.length];
        double[] editTimes = new double[sizes.length];
        md.append("## LCS and Edit Distance\n\n");
        md.append("| n | LCS avg ms | Edit avg ms | LCS length | Edit distance | table cells |\n");
        md.append("|---|---|---|---|---|---|\n");
        for (int k = 0; k < sizes.length; k++) {
            int n = sizes[k];
            String a = random(n, SEED + n);
            String b = random(n, SEED + n + 1);
            xs[k] = n;
            lcsTimes[k] = timeMs(() -> Lcs.length(a, b));
            editTimes[k] = timeMs(() -> EditDistance.distance(a, b));
            md.append(String.format(Locale.ROOT, "| %d | %s | %s | %d | %d | %d |%n", n,
                    fmt(lcsTimes[k]), fmt(editTimes[k]), Lcs.length(a, b),
                    EditDistance.distance(a, b), (long) (n + 1) * (n + 1)));
        }
        Map<String, double[]> series = new LinkedHashMap<>();
        series.put("LCS", lcsTimes);
        series.put("Edit Distance", editTimes);
        Plot.save("Dynamic programming: time vs input size", "string length n",
                "time (ms)", xs, series, dir.resolve("dp_time.png"));
        md.append("\n![dp](dp_time.png)\n\n");
    }

    private static void runMatching(Path dir, StringBuilder md) throws IOException {
        int[] sizes = {10_000, 50_000, 100_000};
        String[] scenarios = {"present", "absent", "worst"};
        PatternMatcher[] matchers = {new NaiveMatcher(), new KmpMatcher(), new RabinKarpMatcher()};
        double[] xs = new double[sizes.length];
        for (int k = 0; k < sizes.length; k++) {
            xs[k] = sizes[k];
        }
        for (String scenario : scenarios) {
            double[][] times = new double[matchers.length][sizes.length];
            md.append("## String matching: ").append(scenario).append("\n\n");
            md.append("| text length | algorithm | avg ms | char comparisons | occurrences |\n");
            md.append("|---|---|---|---|---|\n");
            for (int k = 0; k < sizes.length; k++) {
                String[] input = makeInput(scenario, sizes[k]);
                String text = input[0];
                String pattern = input[1];
                List<Integer> reference = null;
                for (int a = 0; a < matchers.length; a++) {
                    PatternMatcher matcher = matchers[a];
                    times[a][k] = timeMs(() -> matcher.search(text, pattern).size());
                    List<Integer> found = matcher.search(text, pattern);
                    long comparisons = matcher.comparisons();
                    if (reference == null) {
                        reference = found;
                    } else if (!reference.equals(found)) {
                        throw new IllegalStateException("Mismatch in " + matcher.name());
                    }
                    md.append(String.format(Locale.ROOT, "| %d | %s | %s | %d | %d |%n", sizes[k],
                            matcher.name(), fmt(times[a][k]), comparisons, found.size()));
                }
            }
            Map<String, double[]> series = new LinkedHashMap<>();
            for (int a = 0; a < matchers.length; a++) {
                series.put(matchers[a].name(), times[a]);
            }
            String file = "matching_" + scenario + ".png";
            Plot.save("String matching (" + scenario + "): time vs text length", "text length",
                    "time (ms)", xs, series, dir.resolve(file));
            md.append("\n![").append(scenario).append("](").append(file).append(")\n\n");
        }
    }

    private static void runKasai(Path dir, StringBuilder md) throws IOException {
        int[] sizes = {1000, 5000, 10_000};
        double[] xs = new double[sizes.length];
        double[] kasaiTimes = new double[sizes.length];
        double[] saTimes = new double[sizes.length];
        md.append("## Suffix array (doubling) and Kasai LCP\n\n");
        md.append("| n | suffix array avg ms | Kasai avg ms |\n|---|---|---|\n");
        for (int k = 0; k < sizes.length; k++) {
            int n = sizes[k];
            String s = random(n, SEED + n);
            int[] sa = SuffixArrays.build(s);
            xs[k] = n;
            saTimes[k] = timeMs(() -> {
                int[] built = SuffixArrays.build(s);
                return built[built.length / 2] + built.length;
            });
            kasaiTimes[k] = timeMs(() -> {
                int[] lcp = Kasai.buildLcp(s, sa);
                return lcp[lcp.length / 2] + lcp.length;
            });
            md.append(String.format(Locale.ROOT, "| %d | %s | %s |%n", n, fmt(saTimes[k]), fmt(kasaiTimes[k])));
        }
        Map<String, double[]> series = new LinkedHashMap<>();
        series.put("Suffix array", saTimes);
        series.put("Kasai", kasaiTimes);
        Plot.save("Suffix array and Kasai: time vs string length", "string length n", "time (ms)",
                xs, series, dir.resolve("kasai_time.png"));
        md.append("\n![kasai](kasai_time.png)\n");
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        Path dir = Path.of("results");
        Files.createDirectories(dir);
        StringBuilder md = new StringBuilder();
        runDp(dir, md);
        runMatching(dir, md);
        runKasai(dir, md);
        Files.writeString(dir.resolve("results.md"), md.toString());
        System.out.println(md);
        System.out.println("checksum=" + sink);
    }
}