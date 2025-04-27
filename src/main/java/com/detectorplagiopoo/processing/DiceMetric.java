package com.detectorplagiopoo.processing;

import com.detectorplagiopoo.model.SimilarityMetric;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DiceMetric implements SimilarityMetric {
    @Override
    public double compute(List<String> a, List<String> b) {
        Set<String> set1 = new HashSet<>(a);
        Set<String> set2 = new HashSet<>(b);
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        double intersectionSize = intersection.size();
        double totalSize = set1.size() + set2.size();

        return totalSize == 0 ? 0.0 : (2.0 * intersectionSize) / totalSize;
    }

    @Override
    public String getName() {
        return "Dice";
    }
}