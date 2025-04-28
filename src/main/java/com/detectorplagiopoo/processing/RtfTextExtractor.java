package com.detectorplagiopoo.processing;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;

import java.io.File;
import java.io.FileInputStream;

public class RtfTextExtractor implements TextExtractor {
    @Override
    public String extractText(File file) throws Exception {
        try (FileInputStream fis = new FileInputStream(file);
             HWPFDocument doc = new HWPFDocument(fis)) {
            WordExtractor extractor = new WordExtractor(doc);
            return extractor.getText();
        }
    }
}