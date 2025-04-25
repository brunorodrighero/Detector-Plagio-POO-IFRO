package com.detectorplagiopoo.processing;
import com.detectorplagiopoo.model.SimilarityMetric;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CosineMetric implements SimilarityMetric {
    @Override public double compute(List<String> a, List<String> b) {
        Map<String,int[]> freq = new HashMap<>();
        for (String w: a) freq.computeIfAbsent(w,k->new int[2])[0]++;
        for (String w: b) freq.computeIfAbsent(w,k->new int[2])[1]++;
        double dot=0, magA=0, magB=0;
        for (int[] f: freq.values()) {
            dot += f[0]*f[1]; magA += f[0]*f[0]; magB += f[1]*f[1];
        }
        return (magA==0||magB==0)?0: dot/(Math.sqrt(magA)*Math.sqrt(magB));
    }
    @Override public String getName() { return "Cosine"; }
}
