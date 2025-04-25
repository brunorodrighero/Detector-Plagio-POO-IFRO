package com.detectorplagiopoo.processing;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BreakIteratorTokenizer implements Tokenizer {
    private final BreakIterator iterator;

    public BreakIteratorTokenizer() {
        this.iterator = BreakIterator.getWordInstance(new Locale("pt", "BR")); // Fixado em pt_BR
    }
    @Override
    public List<String> tokenize(String text) {
        List<String> tokens = new ArrayList<>();
        iterator.setText(text);
        int start = iterator.first();
        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            String token = text.substring(start, end).trim();
            if (!token.isEmpty()) tokens.add(token);
        }
        return tokens;
    }
}
