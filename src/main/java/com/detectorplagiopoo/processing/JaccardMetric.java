package com.detectorplagiopoo.processing;
import com.detectorplagiopoo.model.SimilarityMetric;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JaccardMetric implements SimilarityMetric {
    @Override public double compute(List<String> a, List<String> b) {
        Set<String> s1 = new HashSet<>(a), s2 = new HashSet<>(b);
        Set<String> inter = new HashSet<>(s1); inter.retainAll(s2);
        Set<String> uni = new HashSet<>(s1); uni.addAll(s2);
        return uni.isEmpty() ? 0.0 : (double) inter.size() / uni.size();
    }
    @Override public String getName() { return "Jaccard"; }
}
