package com.detectorplagiopoo.processing;

import com.detectorplagiopoo.model.PDFInfo;
import com.detectorplagiopoo.model.PlagiarismResult;
import com.detectorplagiopoo.model.SimilarityMetric;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class PlagiarismTask implements Callable<PlagiarismResult> {
    private final PDFInfo p1, p2;
    private final List<SimilarityMetric> metrics;
    private final double threshold;

    public PlagiarismTask(PDFInfo p1, PDFInfo p2, List<SimilarityMetric> metrics, double threshold) {
        this.p1 = p1;
        this.p2 = p2;
        this.metrics = metrics;
        this.threshold = threshold;
    }

    @Override
    public PlagiarismResult call() {
        Map<String, Double> similarities = new HashMap<>();
        Map<String, List<String>> excerpts = new HashMap<>();

        for (SimilarityMetric metric : metrics) {
            double sim = metric.compute(p1.getNGrams(), p2.getNGrams());
            similarities.put(metric.getName(), sim);

            List<String> metricExcerpts = new ArrayList<>();
            if (sim >= threshold) {
                List<String> com = new ArrayList<>(p1.getNGrams());
                com.retainAll(p2.getNGrams());
                metricExcerpts = ExcerptUtil.mergeConsecutiveNGrams(com, p1.getNGrams());
            }
            excerpts.put(metric.getName(), metricExcerpts);
        }

        return new PlagiarismResult(p1, p2, similarities, excerpts, threshold);
    }
}