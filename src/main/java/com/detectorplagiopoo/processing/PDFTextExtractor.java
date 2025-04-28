package com.detectorplagiopoo.processing;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class PDFTextExtractor implements TextExtractor {
    @Override
    public String extractText(File file) throws IOException {
        if (file == null || !file.exists() || !file.canRead()) {
            throw new IOException("Arquivo não encontrado ou inacessível: " +
                    (file != null ? file.getName() : "null"));
        }

        try (PDDocument doc = Loader.loadPDF(file)) {
            if (doc.getNumberOfPages() <= 0) {
                throw new IOException("Arquivo PDF inválido ou sem páginas: " + file.getName());
            }
            return new PDFTextStripper().getText(doc);
        } catch (IOException e) {
            throw new IOException("Erro ao processar o PDF: " + file.getName(), e);
        }
    }
}