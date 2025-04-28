package com.detectorplagiopoo.processing;

import java.util.List;
import java.util.Random;

public class MinHash {
    private final int numHashes;
    private final long[] seeds;

    public MinHash(int numHashes) {
        this.numHashes = numHashes;
        this.seeds = new long[numHashes];
        Random random = new Random();
        for (int i = 0; i < numHashes; i++) {
            seeds[i] = random.nextLong();
        }
    }

    public int[] getSignature(List<String> ngrams) {
        int[] signature = new int[numHashes];
        for (int i = 0; i < numHashes; i++) {
            signature[i] = Integer.MAX_VALUE;
            for (String ngram : ngrams) {
                long hash = hash(ngram, seeds[i]);
                signature[i] = (int) Math.min(signature[i], hash);
            }
        }
        return signature;
    }

    private long hash(String str, long seed) {
        long hash = seed;
        for (char c : str.toCharArray()) {
            hash = 31 * hash + c;
        }
        return hash & 0x7FFFFFFF; // Garantir valor positivo
    }

    public double estimateSimilarity(int[] sig1, int[] sig2) {
        int matches = 0;
        for (int i = 0; i < numHashes; i++) {
            if (sig1[i] == sig2[i]) matches++;
        }
        return (double) matches / numHashes;
    }
}