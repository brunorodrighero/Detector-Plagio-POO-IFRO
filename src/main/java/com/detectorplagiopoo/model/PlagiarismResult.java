package com.detectorplagiopoo.model;

import java.util.List;
import java.util.Map;

public class PlagiarismResult {
    private final TextInfo text1;
    private final TextInfo text2;
    private final Map<String, Double> similarities;
    private final Map<String, List<String>> excerpts;
    private final double threshold;
    private final boolean hasPlagiarism;

    public PlagiarismResult(TextInfo text1, TextInfo text2, Map<String, Double> similarities, Map<String, List<String>> excerpts, double threshold) {
        this.text1 = text1;
        this.text2 = text2;
        this.similarities = similarities;
        this.excerpts = excerpts;
        this.threshold = threshold;
        this.hasPlagiarism = similarities.values().stream().anyMatch(sim -> sim >= threshold);
    }

    public TextInfo getText1() {
        return text1;
    }

    public TextInfo getText2() {
        return text2;
    }

    public Map<String, Double> getSimilarities() {
        return similarities;
    }

    public Map<String, List<String>> getExcerpts() {
        return excerpts;
    }

    public boolean hasPlagiarism() {
        return hasPlagiarism;
    }

    public double getThreshold() {
        return threshold;
    }
}