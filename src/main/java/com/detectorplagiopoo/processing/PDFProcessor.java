/*package com.detectorplagiopoo.processing;
import com.detectorplagiopoo.model.PDFInfo;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PDFProcessor {
    private final Tokenizer tokenizer;
    private final NGramGenerator nGramGen;

    public PDFProcessor(Tokenizer tokenizer, NGramGenerator nGramGen) {
        this.tokenizer = tokenizer;
        this.nGramGen = nGramGen;
    }

    public List<PDFInfo> processFolder(File folder) {
        List<File> files = new ArrayList<>();
        FileScanner.collectPDFFiles(folder, files);
        List<PDFInfo> infos = new ArrayList<>();
        for (File f : files) {
            try (PDDocument doc = PDDocument.load(f)) {
                PDDocumentInformation info = doc.getDocumentInformation();
                String text = new PDFTextStripper().getText(doc);
                List<String> tokens = tokenizer.tokenize(text);
                List<String> ngrams = nGramGen.generate(tokens);
                infos.add(new PDFInfo(f.getName(), f.getAbsolutePath(), info.getAuthor(), info.getTitle(), ngrams));
            } catch (IOException ignore) {}
        }
        return infos;
    }
}
*/