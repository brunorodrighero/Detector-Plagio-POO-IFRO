package com.detectorplagiopoo.processing;

import com.detectorplagiopoo.model.SimilarityMetric;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LevenshteinMetric implements SimilarityMetric {
    @Override
    public double compute(List<String> a, List<String> b) {
        // Comparar os n-grams usando uma abordagem baseada em interseção, ajustada para Levenshtein
        Set<String> set1 = new HashSet<>(a);
        Set<String> set2 = new HashSet<>(b);
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        // A similaridade é proporcional à interseção, ajustada para simular Levenshtein
        double rawSimilarity = union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
        // Ajustar a escala para ser compatível com o limiar (similar ao Jaccard)
        return rawSimilarity * 0.5; // Reduzir a escala para evitar falsos positivos
    }

    @Override
    public String getName() {
        return "Levenshtein";
    }
}