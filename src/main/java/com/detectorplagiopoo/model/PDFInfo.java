package com.detectorplagiopoo.model;
import java.util.List;

public class PDFInfo {
    private final String fileName;
    private final String filePath;
    private final String author;
    private final String title;
    private final List<String> nGrams;

    public PDFInfo(String fileName, String filePath, String author, String title, List<String> nGrams) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.author = author != null ? author : "Desconhecido";
        this.title = title != null ? title : "Sem título";
        this.nGrams = nGrams;
    }
    public String getFileName() { return fileName; }
    public String getFilePath() { return filePath; }
    public String getAuthor() { return author; }
    public String getTitle() { return title; }
    public List<String> getNGrams() { return nGrams; }
}
