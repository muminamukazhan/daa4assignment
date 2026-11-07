package textanalysis;

public final class SuffixArrays {
    private SuffixArrays() {
    }

    public static int[] build(String s) {
        int n = s.length();
        if (n == 0) {
            return new int[0];
        }
        int maxChar = 0;
        for (int i = 0; i < n; i++) {
            maxChar = Math.max(maxChar, s.charAt(i));
        }
        int m = Math.max(maxChar + 1, n);
        int[] sa = new int[n];
        int[] order = new int[n];
        int[] rank = new int[n];
        int[] next = new int[n];
        for (int i = 0; i < n; i++) {
            order[i] = i;
            rank[i] = s.charAt(i);
        }
        countingSort(order, rank, sa, m);
        for (int k = 1; ; k <<= 1) {
            int p = 0;
            for (int i = Math.max(0, n - k); i < n; i++) {
                order[p++] = i;
            }
            for (int j = 0; j < n; j++) {
                if (sa[j] >= k) {
                    order[p++] = sa[j] - k;
                }
            }
            countingSort(order, rank, sa, m);
            next[sa[0]] = 0;
            for (int i = 1; i < n; i++) {
                int prev = sa[i - 1];
                int cur = sa[i];
                int prevSecond = prev + k < n ? rank[prev + k] : -1;
                int curSecond = cur + k < n ? rank[cur + k] : -1;
                boolean same = rank[prev] == rank[cur] && prevSecond == curSecond;
                next[cur] = next[prev] + (same ? 0 : 1);
            }
            System.arraycopy(next, 0, rank, 0, n);
            if (rank[sa[n - 1]] == n - 1) {
                break;
            }
        }
        return sa;
    }

    private static void countingSort(int[] order, int[] key, int[] out, int m) {
        int[] count = new int[m + 1];
        for (int idx : order) {
            count[key[idx] + 1]++;
        }
        for (int i = 0; i < m; i++) {
            count[i + 1] += count[i];
        }
        for (int idx : order) {
            out[count[key[idx]]++] = idx;
        }
    }
}