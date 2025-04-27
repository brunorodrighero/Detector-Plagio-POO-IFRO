package com.detectorplagiopoo.processing;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;

public class PDFTextExtractor implements TextExtractor {
    @Override
    public String extractText(File file) throws Exception {
        try (PDDocument doc = PDDocument.load(file)) {
            return new PDFTextStripper().getText(doc);
        }
    }
}