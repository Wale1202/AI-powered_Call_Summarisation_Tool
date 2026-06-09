package com.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

final class TranscriptFileReader {

    private TranscriptFileReader() {
    }

    static String readTranscriptFile(Path transcriptFilePath) throws IOException {
        if (!Files.exists(transcriptFilePath)) {
            throw new IOException("Transcript file does not exist: " + transcriptFilePath);
        }
        if (!Files.isRegularFile(transcriptFilePath)) {
            throw new IOException("Transcript path is not a regular file: " + transcriptFilePath);
        }

        String callTranscript = Files.readString(transcriptFilePath);

        if (callTranscript.isBlank()) {
            throw new IllegalArgumentException(
                    "Transcript file is blank: " + transcriptFilePath);
        }
        
        return callTranscript;
    }
}
