package com.detectorplagiopoo.model;
import java.util.List;

public interface SimilarityMetric {
    double compute(List<String> a, List<String> b);
    String getName();
}
