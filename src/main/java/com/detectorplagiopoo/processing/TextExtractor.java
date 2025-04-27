package com.detectorplagiopoo.processing;

import java.io.File;

public interface TextExtractor {
    String extractText(File file) throws Exception;
}