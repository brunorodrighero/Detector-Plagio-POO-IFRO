package com.detectorplagiopoo.processing;

import com.detectorplagiopoo.model.TextInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TextProcessor {
    private final Tokenizer tokenizer;
    private final NGramGenerator nGramGen;

    public TextProcessor(Tokenizer tokenizer, NGramGenerator nGramGen) {
        this.tokenizer = tokenizer;
        this.nGramGen = nGramGen;
    }

    public List<TextInfo> processFolder(File folder) {
        List<File> files = new ArrayList<>();
        FileScanner.collectFiles(folder, files);
        List<TextInfo> infos = new ArrayList<>();
        for (File f : files) {
            try {
                TextExtractor extractor = getExtractor(f);
                String text = extractor.extractText(f);
                List<String> tokens = tokenizer.tokenize(text);
                List<String> ngrams = nGramGen.generate(tokens);
                infos.add(new TextInfo(f.getName(), f.getAbsolutePath(), ngrams));
            } catch (Exception ignore) {
                // Ignorar erros silenciosamente por agora
            }
        }
        return infos;
    }

    private TextExtractor getExtractor(File file) {
        String name = file.getName().toLowerCase();
        if (name.endsWith(".pdf")) {
            return new PDFTextExtractor();
        } else {
            return new PlainTextExtractor();
        }
    }
}