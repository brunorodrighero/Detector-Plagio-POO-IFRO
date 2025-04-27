package com.detectorplagiopoo.processing;

import com.detectorplagiopoo.model.SimilarityMetric;

import java.util.*;
import java.util.stream.Collectors;

public class CosineMetric implements SimilarityMetric {
    private final Set<String> stopWords = new HashSet<>(Arrays.asList("de", "o", "a", "instituto", "federal", "ciência", "tecnologia"));

    @Override
    public double compute(List<String> a, List<String> b) {
        // Filtrar stop words
        List<String> filteredA = a.stream()
                .filter(w -> !stopWords.contains(w.toLowerCase()))
                .collect(Collectors.toList());
        List<String> filteredB = b.stream()
                .filter(w -> !stopWords.contains(w.toLowerCase()))
                .collect(Collectors.toList());

        // Calcular frequências
        Map<String, int[]> freq = new HashMap<>();
        for (String w : filteredA) freq.computeIfAbsent(w, k -> new int[2])[0]++;
        for (String w : filteredB) freq.computeIfAbsent(w, k -> new int[2])[1]++;
        double dot = 0, magA = 0, magB = 0;
        for (int[] f : freq.values()) {
            dot += f[0] * f[1];
            magA += f[0] * f[0];
            magB += f[1] * f[1];
        }
        return (magA == 0 || magB == 0) ? 0 : dot / (Math.sqrt(magA) * Math.sqrt(magB));
    }

    @Override
    public String getName() {
        return "Cosine";
    }
}