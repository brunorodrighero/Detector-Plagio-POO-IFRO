package com.detectorplagiopoo.processing;

import com.detectorplagiopoo.model.PlagiarismResult;
import com.detectorplagiopoo.model.SimilarityMetric;
import com.detectorplagiopoo.model.TextInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class PlagiarismTask implements Callable<PlagiarismResult> {
    private final TextInfo t1, t2;
    private final List<SimilarityMetric> metrics;
    private final double threshold;

    public PlagiarismTask(TextInfo t1, TextInfo t2, List<SimilarityMetric> metrics, double threshold) {
        this.t1 = t1;
        this.t2 = t2;
        this.metrics = metrics;
        this.threshold = threshold;
    }

    @Override
    public PlagiarismResult call() {
        Map<String, Double> similarities = new HashMap<>();
        Map<String, List<String>> excerpts = new HashMap<>();

        for (SimilarityMetric metric : metrics) {
            double sim = metric.compute(t1.getNGrams(), t2.getNGrams());
            similarities.put(metric.getName(), sim);

            List<String> metricExcerpts = new ArrayList<>();
            if (sim >= threshold) {
                List<String> com = new ArrayList<>(t1.getNGrams());
                com.retainAll(t2.getNGrams());
                metricExcerpts = ExcerptUtil.mergeConsecutiveNGrams(com, t1.getNGrams());
            }
            excerpts.put(metric.getName(), metricExcerpts);
        }

        return new PlagiarismResult(t1, t2, similarities, excerpts, threshold);
    }
}