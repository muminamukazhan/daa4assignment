package textanalysis;

import java.util.List;

public interface PatternMatcher {
    String name();

    List<Integer> search(String text, String pattern);

    long comparisons();
}