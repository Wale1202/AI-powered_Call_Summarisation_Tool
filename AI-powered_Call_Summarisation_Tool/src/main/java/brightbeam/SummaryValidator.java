package brightbeam;

import java.util.ArrayList;
import java.util.List;

final class SummaryValidator {

    private static final int MAX_SUMMARY_CHARACTERS = 1500;

    private static final String CALLER_HEADING = "Caller:";
    private static final String SUBJECT_HEADING = "Subject:";
    private static final String EXECUTIVE_SUMMARY_HEADING = "Executive Summary:";
    private static final String NEXT_STEPS_HEADING = "Next Steps:";

    List<String> validate(String generatedSummary) {
        List<String> validationErrors = new ArrayList<>();

        if (generatedSummary == null || generatedSummary.isBlank()) {
            validationErrors.add("Generated summary is blank.");
            return validationErrors;
        }

        if (!generatedSummary.contains(CALLER_HEADING)) {
            validationErrors.add("Missing required heading: " + CALLER_HEADING);
        }
        if (!generatedSummary.contains(SUBJECT_HEADING)) {
            validationErrors.add("Missing required heading: " + SUBJECT_HEADING);
        }
        if (!generatedSummary.contains(EXECUTIVE_SUMMARY_HEADING)) {
            validationErrors.add("Missing required heading: " + EXECUTIVE_SUMMARY_HEADING);
        }
        if (!generatedSummary.contains(NEXT_STEPS_HEADING)) {
            validationErrors.add("Missing required heading: " + NEXT_STEPS_HEADING);
        }

        if (generatedSummary.length() > MAX_SUMMARY_CHARACTERS) {
            validationErrors.add(
                    "Generated summary exceeds " + MAX_SUMMARY_CHARACTERS
                            + " characters (actual: " + generatedSummary.length() + ").");
        }

        return validationErrors;
    }
}
