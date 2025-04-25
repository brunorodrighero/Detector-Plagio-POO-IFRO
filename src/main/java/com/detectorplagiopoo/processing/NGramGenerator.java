package com.detectorplagiopoo.processing;
import java.util.ArrayList;
import java.util.List;

public class NGramGenerator {
    private final int size;
    public NGramGenerator(int size) { this.size = size; }
    public List<String> generate(List<String> tokens) {
        List<String> ngrams = new ArrayList<>();
        for (int i = 0; i <= tokens.size() - size; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < size; j++) sb.append(tokens.get(i + j)).append(' ');
            ngrams.add(sb.toString().trim());
        }
        return ngrams;
    }
}
