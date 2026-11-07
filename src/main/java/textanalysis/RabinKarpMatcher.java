package textanalysis;

import java.util.ArrayList;
import java.util.List;

public final class RabinKarpMatcher implements PatternMatcher {
    private static final long MOD = 1_000_000_007L;
    private static final long BASE = 131L;

    private long comparisons;
    private long collisions;

    @Override
    public String name() {
        return "Rabin-Karp";
    }

    public long collisions() {
        return collisions;
    }

    @Override
    public List<Integer> search(String text, String pattern) {
        comparisons = 0;
        collisions = 0;
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
        long patternHash = 0;
        long windowHash = 0;
        long highPower = 1;
        for (int i = 0; i < m; i++) {
            patternHash = (patternHash * BASE + pattern.charAt(i)) % MOD;
            windowHash = (windowHash * BASE + text.charAt(i)) % MOD;
            if (i > 0) {
                highPower = highPower * BASE % MOD;
            }
        }
        for (int i = 0; i <= n - m; i++) {
            if (patternHash == windowHash) {
                boolean equal = true;
                for (int j = 0; j < m; j++) {
                    comparisons++;
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        equal = false;
                        break;
                    }
                }
                if (equal) {
                    result.add(i);
                } else {
                    collisions++;
                }
            }
            if (i < n - m) {
                long removed = text.charAt(i) * highPower % MOD;
                windowHash = ((windowHash - removed + MOD) % MOD * BASE + text.charAt(i + m)) % MOD;
            }
        }
        return result;
    }

    @Override
    public long comparisons() {
        return comparisons;
    }
}