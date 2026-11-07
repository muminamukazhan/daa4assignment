package textanalysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public final class TestRunner {
    private static int passed;
    private static int failed;

    private TestRunner() {
    }

    private static void check(String name, boolean condition) {
        if (condition) {
            passed++;
        } else {
            failed++;
            System.out.println("FAIL: " + name);
        }
    }

    private static List<Integer> list(Integer... values) {
        return new ArrayList<>(Arrays.asList(values));
    }

    private static void expectMatches(String text, String pattern, Integer... expected) {
        PatternMatcher[] matchers = {new NaiveMatcher(), new KmpMatcher(), new RabinKarpMatcher()};
        List<Integer> want = list(expected);
        List<Integer> first = null;
        for (PatternMatcher matcher : matchers) {
            List<Integer> got = matcher.search(text, pattern);
            check(matcher.name() + " '" + pattern + "' in '" + text + "'", got.equals(want));
            if (first == null) {
                first = got;
            } else {
                check("agreement " + matcher.name() + " '" + pattern + "'", first.equals(got));
            }
        }
    }

    private static void testLcs() {
        check("lcs identical", Lcs.length("algorithm", "algorithm") == 9);
        check("lcs different", Lcs.length("abc", "xyz") == 0);
        check("lcs empty left", Lcs.length("", "abc") == 0);
        check("lcs empty right", Lcs.length("abc", "") == 0);
        check("lcs both empty", Lcs.length("", "") == 0);
        check("lcs classic", Lcs.length("ABCBDAB", "BDCABA") == 4);
        check("lcs subsequence", Lcs.length("AGGTAB", "GXTXAYB") == 4);
    }

    private static void testEditDistance() {
        check("ed identical", EditDistance.distance("same", "same") == 0);
        check("ed empty left", EditDistance.distance("", "abc") == 3);
        check("ed empty right", EditDistance.distance("abc", "") == 3);
        check("ed substitution", EditDistance.distance("cat", "cut") == 1);
        check("ed insertion", EditDistance.distance("cat", "cart") == 1);
        check("ed deletion", EditDistance.distance("cart", "cat") == 1);
        check("ed classic", EditDistance.distance("kitten", "sitting") == 3);
        check("ed symmetric", EditDistance.distance("sunday", "saturday")
                == EditDistance.distance("saturday", "sunday"));
    }

    private static void testMatching() {
        expectMatches("abcdef", "abc", 0);
        expectMatches("xxabcxx", "abc", 2);
        expectMatches("xxxabc", "abc", 3);
        expectMatches("aaaa", "aa", 0, 1, 2);
        expectMatches("abcabcabc", "abc", 0, 3, 6);
        expectMatches("abcdef", "xyz");
        expectMatches("ab", "abc");
        expectMatches("abc", "", 0, 1, 2, 3);
        expectMatches("", "", 0);
        expectMatches("", "a");
        expectMatches("abc", "abc", 0);
        int[] lps = new KmpMatcher().buildLps("ababaca");
        check("kmp lps", Arrays.equals(lps, new int[]{0, 0, 1, 2, 3, 0, 1}));
        Random rnd = new Random(7);
        for (int trial = 0; trial < 300; trial++) {
            StringBuilder text = new StringBuilder();
            int n = rnd.nextInt(40);
            for (int i = 0; i < n; i++) {
                text.append((char) ('a' + rnd.nextInt(2)));
            }
            StringBuilder pattern = new StringBuilder();
            int m = rnd.nextInt(5);
            for (int i = 0; i < m; i++) {
                pattern.append((char) ('a' + rnd.nextInt(2)));
            }
            List<Integer> naive = new NaiveMatcher().search(text.toString(), pattern.toString());
            check("random kmp", naive.equals(new KmpMatcher().search(text.toString(), pattern.toString())));
            check("random rk", naive.equals(new RabinKarpMatcher().search(text.toString(), pattern.toString())));
        }
    }

    private static int[] naiveSuffixArray(String s) {
        int n = s.length();
        Integer[] idx = new Integer[n];
        for (int i = 0; i < n; i++) {
            idx[i] = i;
        }
        Arrays.sort(idx, (x, y) -> s.substring(x).compareTo(s.substring(y)));
        int[] sa = new int[n];
        for (int i = 0; i < n; i++) {
            sa[i] = idx[i];
        }
        return sa;
    }

    private static void testSuffixArray() {
        check("sa empty", SuffixArrays.build("").length == 0);
        check("sa single", Arrays.equals(SuffixArrays.build("z"), new int[]{0}));
        check("sa banana", Arrays.equals(SuffixArrays.build("banana"), new int[]{5, 3, 1, 0, 4, 2}));
        check("sa all same", Arrays.equals(SuffixArrays.build("aaaaa"), new int[]{4, 3, 2, 1, 0}));
        check("sa mississippi",
                Arrays.equals(SuffixArrays.build("mississippi"), new int[]{10, 7, 4, 1, 0, 9, 8, 6, 3, 5, 2}));
        Random rnd = new Random(23);
        for (int trial = 0; trial < 300; trial++) {
            int n = 1 + rnd.nextInt(80);
            int sigma = 1 + rnd.nextInt(4);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < n; i++) {
                sb.append((char) ('a' + rnd.nextInt(sigma)));
            }
            String text = sb.toString();
            check("sa random", Arrays.equals(SuffixArrays.build(text), naiveSuffixArray(text)));
        }
        String periodic = "ab".repeat(500);
        check("sa periodic", Arrays.equals(SuffixArrays.build(periodic), naiveSuffixArray(periodic)));
    }

    private static int bruteLcp(String s, int a, int b) {
        int h = 0;
        while (a + h < s.length() && b + h < s.length() && s.charAt(a + h) == s.charAt(b + h)) {
            h++;
        }
        return h;
    }

    private static void testKasai() {
        String s = "banana";
        int[] sa = {5, 3, 1, 0, 4, 2};
        check("lcp banana", Arrays.equals(Kasai.buildLcp(s, sa), new int[]{0, 1, 3, 0, 0, 2}));
        check("lcp empty", Kasai.buildLcp("", new int[0]).length == 0);
        check("lcp single", Arrays.equals(Kasai.buildLcp("a", new int[]{0}), new int[]{0}));
        Random rnd = new Random(11);
        for (int trial = 0; trial < 100; trial++) {
            int n = 1 + rnd.nextInt(60);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < n; i++) {
                sb.append((char) ('a' + rnd.nextInt(3)));
            }
            String text = sb.toString();
            int[] suffixArray = SuffixArrays.build(text);
            int[] lcp = Kasai.buildLcp(text, suffixArray);
            boolean ok = lcp[0] == 0;
            for (int i = 1; i < n; i++) {
                ok &= lcp[i] == bruteLcp(text, suffixArray[i - 1], suffixArray[i]);
            }
            check("random lcp", ok);
        }
    }

    public static void main(String[] args) {
        testLcs();
        testEditDistance();
        testMatching();
        testSuffixArray();
        testKasai();
        System.out.println("passed=" + passed + " failed=" + failed);
        if (failed > 0) {
            System.exit(1);
        }
    }
}