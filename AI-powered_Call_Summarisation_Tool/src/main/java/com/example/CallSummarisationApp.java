package com.example;

import java.nio.file.Path;
import java.util.List;

public final class CallSummarisationApp {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: call-summarisation <transcript-file-path>");
            System.exit(1);
            return;
        }

        Path transcriptFilePath = Path.of(args[0]);

        String callTranscript;
        try {
            callTranscript = TranscriptFileReader.readTranscriptFile(transcriptFilePath);
        } catch (Exception failure) {
            System.err.println("Failed to load transcript: " + failure.getMessage());
            System.exit(2);
            return;
        }

        System.out.println("Transcript loaded successfully from: " + transcriptFilePath);
        System.out.println("Transcript length (characters): " + callTranscript.length());

        SummaryPromptBuilder summaryPromptBuilder = new SummaryPromptBuilder();
        String summaryPrompt = summaryPromptBuilder.buildSummaryPrompt(callTranscript);

        String generatedSummary;
        try {
            OpenRouterSummaryClient openRouterSummaryClient = new OpenRouterSummaryClient();
            generatedSummary = openRouterSummaryClient.requestSummaryFromOpenRouter(summaryPrompt);
        } catch (Exception summaryFailure) {
            System.err.println("Failed to generate summary: " + summaryFailure.getMessage());
            System.exit(3);
            return;
        }

        SummaryValidator summaryValidator = new SummaryValidator();
        List<String> validationErrors = summaryValidator.validate(generatedSummary);
        if (!validationErrors.isEmpty()) {
            System.err.println("Generated summary failed validation:");
            for (String validationError : validationErrors) {
                System.err.println("- " + validationError);
            }
            System.exit(4);
            return;
        }

        System.out.println();
        System.out.println("--- Generated Summary ---");
        System.out.println(generatedSummary);
    }
}
