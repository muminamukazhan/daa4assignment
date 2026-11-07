package textanalysis;

public final class Kasai {
    private Kasai() {
    }

    public static int[] buildLcp(String s, int[] suffixArray) {
        int n = s.length();
        int[] lcp = new int[n];
        if (n == 0) {
            return lcp;
        }
        int[] rank = new int[n];
        for (int i = 0; i < n; i++) {
            rank[suffixArray[i]] = i;
        }
        int h = 0;
        for (int i = 0; i < n; i++) {
            if (rank[i] > 0) {
                int j = suffixArray[rank[i] - 1];
                while (i + h < n && j + h < n && s.charAt(i + h) == s.charAt(j + h)) {
                    h++;
                }
                lcp[rank[i]] = h;
                if (h > 0) {
                    h--;
                }
            } else {
                h = 0;
            }
        }
        return lcp;
    }
}