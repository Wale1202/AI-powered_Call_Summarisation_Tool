package com.example;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link SummaryValidator}.
 *
 * These tests cover the deterministic structural guarantees the validator
 * enforces before a generated summary is shown to a user. Factual accuracy is
 * out of scope here — it cannot be checked deterministically.
 */
class SummaryValidatorTest {

    private final SummaryValidator summaryValidator = new SummaryValidator();

    private static final String VALID_SUMMARY = """
            Caller: Jane Doe, policyholder, inbound

            Subject:
            Follow-up on claim CL-12345.

            Executive Summary:
            Caller phoned to check progress on her motor claim.
            - Confirmed contact details on file.
            - Asked for an update by Friday.

            Next Steps:
            Brightbeam: Call the caller back by Friday with an update.
            Other: None
            """;

    // Why this matters: a correctly-formatted summary must pass cleanly, otherwise
    // the validator would block every real output and the app would be unusable.
    @Test
    void validSummaryPassesValidation() {
        // Arrange
        String generatedSummary = VALID_SUMMARY;

        // Act
        List<String> validationErrors = summaryValidator.validate(generatedSummary);

        // Assert
        assertTrue(validationErrors.isEmpty(),
                "Expected no errors but got: " + validationErrors);
    }

    // Why this matters: a blank LLM response must never be presented as a summary;
    // it is the clearest signal the upstream call failed silently.
    @Test
    void blankSummaryIsRejected() {
        // Arrange
        String generatedSummary = "   \n\t  ";

        // Act
        List<String> validationErrors = summaryValidator.validate(generatedSummary);

        // Assert
        assertFalse(validationErrors.isEmpty());
        assertTrue(validationErrors.get(0).toLowerCase().contains("blank"));
    }

    // Why this matters: without a Caller line the downstream reader cannot tell
    // who the call was with — the single most important fact in the summary.
    @Test
    void missingCallerHeadingIsRejected() {
        // Arrange
        String generatedSummary = VALID_SUMMARY.replace("Caller:", "From:");

        // Act
        List<String> validationErrors = summaryValidator.validate(generatedSummary);

        // Assert
        assertTrue(validationErrors.stream().anyMatch(e -> e.contains("Caller:")),
                "Expected a missing-Caller error but got: " + validationErrors);
    }

    // Why this matters: the Subject line is the one-glance description used in
    // queues and lists; missing it makes triage impossible.
    @Test
    void missingSubjectHeadingIsRejected() {
        // Arrange
        String generatedSummary = VALID_SUMMARY.replace("Subject:", "Topic:");

        // Act
        List<String> validationErrors = summaryValidator.validate(generatedSummary);

        // Assert
        assertTrue(validationErrors.stream().anyMatch(e -> e.contains("Subject:")),
                "Expected a missing-Subject error but got: " + validationErrors);
    }

    // Why this matters: the Executive Summary is the narrative body; without it
    // the output is just headers and bullet points without context.
    @Test
    void missingExecutiveSummaryHeadingIsRejected() {
        // Arrange
        String generatedSummary = VALID_SUMMARY.replace("Executive Summary:", "Summary:");

        // Act
        List<String> validationErrors = summaryValidator.validate(generatedSummary);

        // Assert
        assertTrue(validationErrors.stream().anyMatch(e -> e.contains("Executive Summary:")),
                "Expected a missing-Executive-Summary error but got: " + validationErrors);
    }

    // Why this matters: Next Steps drives the actual follow-up work. A summary
    // without it is informational only and will leave the claim stalled.
    @Test
    void missingNextStepsHeadingIsRejected() {
        // Arrange
        String generatedSummary = VALID_SUMMARY.replace("Next Steps:", "Actions:");

        // Act
        List<String> validationErrors = summaryValidator.validate(generatedSummary);

        // Assert
        assertTrue(validationErrors.stream().anyMatch(e -> e.contains("Next Steps:")),
                "Expected a missing-Next-Steps error but got: " + validationErrors);
    }

    // Why this matters: the 1,500-character cap is a hard requirement from
    // CLAUDE.md; over-long summaries must be rejected so the prompt can be
    // adjusted rather than truncated downstream.
    @Test
    void summaryLongerThan1500CharactersIsRejected() {
        // Arrange — build a summary that has every required heading but exceeds the cap.
        String filler = "x".repeat(1600);
        String generatedSummary = """
                Caller: Test
                Subject: Test
                Executive Summary: %s
                Next Steps: Brightbeam: do thing. Other: None
                """.formatted(filler);

        // Act
        List<String> validationErrors = summaryValidator.validate(generatedSummary);

        // Assert
        assertTrue(generatedSummary.length() > 1500, "Test fixture should exceed the cap");
        assertEquals(1, validationErrors.size(),
                "Only the length error should fire: " + validationErrors);
        assertTrue(validationErrors.get(0).contains("1500"));
    }
}
