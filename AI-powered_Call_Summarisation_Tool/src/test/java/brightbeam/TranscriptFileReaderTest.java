package brightbeam;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link TranscriptFileReader}.
 *
 * The reader is the input boundary: anything it lets through reaches the LLM
 * and costs tokens, so its rejection rules must be deterministic and tested.
 */
class TranscriptFileReaderTest {

    // Why this matters: a blank transcript would otherwise be sent to the LLM and
    // produce a hallucinated summary about nothing — we must fail loud and early.
    @Test
    void blankTranscriptFileIsRejected(@TempDir Path tempDir) throws IOException {
        // Arrange
        Path blankTranscript = tempDir.resolve("blank.txt");
        Files.writeString(blankTranscript, "   \n\t  ");

        // Act + Assert
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> TranscriptFileReader.readTranscriptFile(blankTranscript));
        assertTrue(thrown.getMessage().toLowerCase().contains("blank"),
                "Expected blank-file message, got: " + thrown.getMessage());
    }

    // Why this matters: a typo'd path must produce a clear error rather than a
    // confusing NullPointerException or silent empty-string read downstream.
    @Test
    void missingTranscriptFileIsRejected(@TempDir Path tempDir) {
        // Arrange
        Path missingTranscript = tempDir.resolve("does-not-exist.txt");

        // Act + Assert
        IOException thrown = assertThrows(
                IOException.class,
                () -> TranscriptFileReader.readTranscriptFile(missingTranscript));
        assertTrue(thrown.getMessage().contains("does not exist"),
                "Expected does-not-exist message, got: " + thrown.getMessage());
    }

    // Sanity check that a normal transcript is returned verbatim so the reader
    // doesn't accidentally strip or transform content the prompt depends on.
    @Test
    void normalTranscriptFileIsReturnedVerbatim(@TempDir Path tempDir) throws IOException {
        // Arrange
        String callTranscript = "Agent: Hello.\nCaller: Hi, I'd like to make a claim.\n";
        Path transcriptFile = tempDir.resolve("call.txt");
        Files.writeString(transcriptFile, callTranscript);

        // Act
        String readBack = TranscriptFileReader.readTranscriptFile(transcriptFile);

        // Assert
        assertEquals(callTranscript, readBack);
    }
}
