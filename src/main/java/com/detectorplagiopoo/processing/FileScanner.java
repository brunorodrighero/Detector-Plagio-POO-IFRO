package com.detectorplagiopoo.processing;
import java.io.File;
import java.util.List;

public class FileScanner {
    public static void collectPDFFiles(File directory, List<File> pdfFiles) {
        if (!directory.isDirectory()) return;
        File[] files = directory.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isDirectory()) collectPDFFiles(f, pdfFiles);
            else if (f.getName().toLowerCase().endsWith(".pdf")) pdfFiles.add(f);
        }
    }
}
