package com.detectorplagiopoo.processing;

import com.detectorplagiopoo.model.SimilarityMetric;

import java.util.List;

public class LevenshteinMetric implements SimilarityMetric {
    @Override
    public double compute(List<String> a, List<String> b) {
        String textA = String.join(" ", a);
        String textB = String.join(" ", b);
        int lenA = textA.length(), lenB = textB.length();
        if (lenA == 0 && lenB == 0) return 1.0;
        if (lenA == 0 || lenB == 0) return 0.0;

        int[][] dp = new int[lenA + 1][lenB + 1];
        for (int i = 0; i <= lenA; i++) dp[i][0] = i;
        for (int j = 0; j <= lenB; j++) dp[0][j] = j;

        for (int i = 1; i <= lenA; i++) {
            for (int j = 1; j <= lenB; j++) {
                int cost = textA.charAt(i - 1) == textB.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }
        double maxLen = Math.max(lenA, lenB);
        return 1.0 - (dp[lenA][lenB] / maxLen);
    }

    @Override
    public String getName() {
        return "Levenshtein";
    }
}