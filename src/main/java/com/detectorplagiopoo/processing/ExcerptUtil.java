package com.detectorplagiopoo.processing;
import java.util.*;

public class ExcerptUtil {
    public static List<String> mergeConsecutiveNGrams(List<String> common, List<String> original) {
        if (common.isEmpty()) return Collections.emptyList();
        Set<String> uniq = new HashSet<>(common);
        List<String> sorted = new ArrayList<>(uniq);
        sorted.sort(Comparator.comparingInt(original::indexOf));
        List<String> excerpts = new ArrayList<>();
        StringBuilder cur = new StringBuilder(sorted.get(0));
        int last = original.indexOf(sorted.get(0));
        for (int i=1;i<sorted.size();i++) {
            String ngram = sorted.get(i);
            int idx = original.indexOf(ngram);
            if (idx==last+1) {
                String[] w = ngram.split("\\s+");
                cur.append(' ').append(w[w.length-1]);
            } else {
                excerpts.add(cur.toString()); cur = new StringBuilder(ngram);
            }
            last = idx;
        }
        excerpts.add(cur.toString());
        return excerpts;
    }
}
