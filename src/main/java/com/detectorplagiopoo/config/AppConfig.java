package com.detectorplagiopoo.config;

public class AppConfig {
    private double similarityThreshold;
    private int nGramSize;

    public AppConfig() {
        this.similarityThreshold = 0.04;    // 4%
        this.nGramSize = 5;                 // sequência de 5 palavras
    }

    public double getSimilarityThreshold() {
        return similarityThreshold;
    }

    public void setSimilarityThreshold(double similarityThreshold) {
        this.similarityThreshold = similarityThreshold;
    }

    public int getNGramSize() {
        return nGramSize;
    }

    public void setNGramSize(int nGramSize) {
        this.nGramSize = nGramSize;
    }
}