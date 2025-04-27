package com.detectorplagiopoo.processing;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class PlainTextExtractor implements TextExtractor {
    @Override
    public String extractText(File file) throws Exception {
        return Files.readString(file.toPath());
    }
}