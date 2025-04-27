package com.detectorplagiopoo.processing;

import java.io.File;
import java.util.List;

public class FileScanner {
    public static void collectFiles(File directory, List<File> files) {
        if (!directory.isDirectory()) return;
        File[] fileArray = directory.listFiles();
        if (fileArray == null) return;
        for (File f : fileArray) {
            if (f.isDirectory()) {
                collectFiles(f, files);
            } else if (f.getName().toLowerCase().matches(".*\\.(pdf|txt|doc|docx|rtf|md|html|csv|java|py|cpp|c|js|ts|json|xml|yaml|yml)$")) {
                files.add(f);
            }
        }
    }
}