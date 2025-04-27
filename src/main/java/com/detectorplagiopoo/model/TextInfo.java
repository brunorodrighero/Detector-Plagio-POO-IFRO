package com.detectorplagiopoo.model;

import java.util.List;

public class TextInfo {
    private final String fileName;
    private final String filePath;
    private final List<String> nGrams;

    public TextInfo(String fileName, String filePath, List<String> nGrams) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.nGrams = nGrams;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public List<String> getNGrams() {
        return nGrams;
    }
}