package textanalysis;

import java.util.ArrayList;
import java.util.List;

public final class KmpMatcher implements PatternMatcher {
    private long comparisons;

    @Override
    public String name() {
        return "KMP";
    }

    public int[] buildLps(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        int len = 0;
        int i = 1;
        while (i < m) {
            comparisons++;
            if (pattern.charAt(i) == pattern.charAt(len)) {
                lps[i++] = ++len;
            } else if (len > 0) {
                len = lps[len - 1];
            } else {
                lps[i++] = 0;
            }
        }
        return lps;
    }

    @Override
    public List<Integer> search(String text, String pattern) {
        comparisons = 0;
        List<Integer> result = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();
        if (m == 0) {
            for (int i = 0; i <= n; i++) {
                result.add(i);
            }
            return result;
        }
        if (m > n) {
            return result;
        }
        int[] lps = buildLps(pattern);
        int j = 0;
        for (int i = 0; i < n; i++) {
            while (j > 0) {
                comparisons++;
                if (text.charAt(i) == pattern.charAt(j)) {
                    break;
                }
                j = lps[j - 1];
            }
            if (j == 0) {
                comparisons++;
                if (text.charAt(i) == pattern.charAt(0)) {
                    j++;
                }
            } else {
                j++;
            }
            if (j == m) {
                result.add(i - m + 1);
                j = lps[j - 1];
            }
        }
        return result;
    }

    @Override
    public long comparisons() {
        return comparisons;
    }
}