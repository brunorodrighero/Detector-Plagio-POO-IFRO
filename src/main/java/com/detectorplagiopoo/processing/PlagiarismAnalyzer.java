package com.detectorplagiopoo.processing;

import com.detectorplagiopoo.model.PlagiarismResult;
import com.detectorplagiopoo.model.SimilarityMetric;
import com.detectorplagiopoo.model.TextInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class PlagiarismAnalyzer {
    private final List<SimilarityMetric> metrics;
    private final double threshold;
    private final MinHash minHash;

    public PlagiarismAnalyzer(List<SimilarityMetric> metrics, double threshold) {
        this.metrics = metrics;
        this.threshold = threshold;
        this.minHash = new MinHash(100); // 100 hash functions
    }

    public List<PlagiarismResult> analyze(List<TextInfo> texts) throws InterruptedException {
        // Gerar assinaturas MinHash para todos os textos
        Map<TextInfo, int[]> signatures = new HashMap<>();
        for (TextInfo text : texts) {
            signatures.put(text, minHash.getSignature(text.getNGrams()));
        }

        List<Callable<PlagiarismResult>> tasks = new ArrayList<>();
        for (int i = 0; i < texts.size(); i++) {
            for (int j = i + 1; j < texts.size(); j++) {
                TextInfo t1 = texts.get(i);
                TextInfo t2 = texts.get(j);

                // Filtro inicial baseado em hash
                if (t1.getTextHash().equals(t2.getTextHash())) {
                    tasks.add(new PlagiarismTask(t1, t2, metrics, threshold));
                    continue;
                }

                // Filtro MinHash
                int[] sig1 = signatures.get(t1);
                int[] sig2 = signatures.get(t2);
                double estimatedSim = minHash.estimateSimilarity(sig1, sig2);
                if (estimatedSim >= threshold / 2) { // Limiar ajustado para MinHash
                    tasks.add(new PlagiarismTask(t1, t2, metrics, threshold));
                }
            }
        }

        ExecutorService exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<PlagiarismResult>> futs = exec.invokeAll(tasks);
        exec.shutdown();
        List<PlagiarismResult> results = new ArrayList<>();
        for (Future<PlagiarismResult> f : futs) {
            try {
                PlagiarismResult result = f.get();
                if (result != null) {
                    results.add(result);
                }
            } catch (Exception ignored) {
            }
        }
        return results;
    }
}