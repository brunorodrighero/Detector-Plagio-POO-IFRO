package com.detectorplagiopoo.processing;

import org.odftoolkit.simple.TextDocument;

import java.io.File;

public class OdtTextExtractor implements TextExtractor {
    @Override
    public String extractText(File file) throws Exception {
        TextDocument doc = TextDocument.loadDocument(file);
        String text = doc.getContentRoot().getTextContent();
        doc.close();
        return text;
    }
}