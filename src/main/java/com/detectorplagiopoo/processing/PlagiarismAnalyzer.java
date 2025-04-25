package com.detectorplagiopoo.processing;

import com.detectorplagiopoo.model.PlagiarismResult;
import com.detectorplagiopoo.model.SimilarityMetric;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class PlagiarismAnalyzer {
    private final List<SimilarityMetric> metrics;
    private final double threshold;

    public PlagiarismAnalyzer(List<SimilarityMetric> metrics, double threshold) {
        this.metrics = metrics;
        this.threshold = threshold;
    }

    public List<PlagiarismResult> analyze(List<com.detectorplagiopoo.model.PDFInfo> pdfs) throws InterruptedException {
        List<Callable<PlagiarismResult>> tasks = new ArrayList<>();
        for (int i = 0; i < pdfs.size(); i++) {
            for (int j = i + 1; j < pdfs.size(); j++) {
                tasks.add(new PlagiarismTask(pdfs.get(i), pdfs.get(j), metrics, threshold));
            }
        }
        ExecutorService exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<PlagiarismResult>> futs = exec.invokeAll(tasks);
        exec.shutdown();
        List<PlagiarismResult> results = new ArrayList<>();
        for (Future<PlagiarismResult> f : futs) {
            try {
                results.add(f.get());
            } catch (Exception ignored) {}
        }
        return results;
    }
}