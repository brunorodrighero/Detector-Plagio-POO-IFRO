package com.detectorplagiopoo.processing;

import com.detectorplagiopoo.model.TextInfo;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
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
                String textHash = calculateHash(text);
                List<String> tokens = tokenizer.tokenize(text);
                List<String> ngrams = nGramGen.generate(tokens);
                infos.add(new TextInfo(f.getName(), f.getAbsolutePath(), ngrams, textHash));
            } catch (Exception e) {
                System.err.println("Erro ao processar arquivo " + f.getName() + ": " + e.getMessage());
            }
        }
        return infos;
    }

    private TextExtractor getExtractor(File file) {
        String name = file.getName().toLowerCase();
        if (name.endsWith(".pdf")) {
            return new PDFTextExtractor();
        } else if (name.endsWith(".docx")) {
            return new DocxTextExtractor();
        } else if (name.endsWith(".rtf")) {
            return new RtfTextExtractor();
        } else if (name.endsWith(".odt")) {
            return new OdtTextExtractor();
        } else {
            return new PlainTextExtractor();
        }
    }

    private String calculateHash(String text) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(text.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}