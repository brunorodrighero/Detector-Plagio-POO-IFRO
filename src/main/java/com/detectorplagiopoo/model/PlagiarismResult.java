package com.detectorplagiopoo.model;

import java.util.List;
import java.util.Map;

public class PlagiarismResult {
    private final PDFInfo pdf1;
    private final PDFInfo pdf2;
    private final Map<String, Double> similarities; // Mapa de métrica -> similaridade
    private final Map<String, List<String>> excerpts; // Mapa de métrica -> trechos copiados
    private final double threshold;
    private final boolean hasPlagiarism;

    public PlagiarismResult(PDFInfo pdf1, PDFInfo pdf2, Map<String, Double> similarities, Map<String, List<String>> excerpts, double threshold) {
        this.pdf1 = pdf1;
        this.pdf2 = pdf2;
        this.similarities = similarities;
        this.excerpts = excerpts;
        this.threshold = threshold;
        this.hasPlagiarism = similarities.values().stream().anyMatch(sim -> sim >= threshold);
    }

    public PDFInfo getPdf1() { return pdf1; }
    public PDFInfo getPdf2() { return pdf2; }
    public Map<String, Double> getSimilarities() { return similarities; }
    public Map<String, List<String>> getExcerpts() { return excerpts; }
    public boolean hasPlagiarism() { return hasPlagiarism; }
    public double getThreshold() { return threshold; }
}