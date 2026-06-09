package com.example;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.errors.OpenAIServiceException;
import com.openai.models.ChatCompletion;
import com.openai.models.ChatCompletionCreateParams;

final class OpenRouterSummaryClient {

    private static final String OPENROUTER_BASE_URL = "https://openrouter.ai/api/v1";
    private static final String SUMMARY_MODEL = "openai/gpt-4o";
    private static final long MAX_RESPONSE_TOKENS = 1024L;

    private final OpenAIClient openAiClient;

    OpenRouterSummaryClient() {
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "OPENAI_API_KEY environment variable is not set. "
                            + "Export it before running the application.");
        }
        this.openAiClient = OpenAIOkHttpClient.builder()
                .apiKey(apiKey)
                .baseUrl(OPENROUTER_BASE_URL)
                .build();
    }

    String requestSummaryFromOpenRouter(String summaryPrompt) {
        ChatCompletionCreateParams requestParams = ChatCompletionCreateParams.builder()
                .model(SUMMARY_MODEL)
                .maxTokens(MAX_RESPONSE_TOKENS)
                .addUserMessage(summaryPrompt)
                .build();

        ChatCompletion completion;
        try {
            completion = openAiClient.chat().completions().create(requestParams);
        } catch (OpenAIServiceException apiFailure) {
            throw new RuntimeException(
                    "OpenRouter API request failed (status " + apiFailure.statusCode() + "): "
                            + apiFailure.getMessage(),
                    apiFailure);
        } catch (RuntimeException transportFailure) {
            throw new RuntimeException(
                    "Could not reach OpenRouter API: " + transportFailure.getMessage(),
                    transportFailure);
        }

        return completion.choices().stream()
                .findFirst()
                .flatMap(choice -> choice.message().content())
                .orElseThrow(() -> new RuntimeException(
                        "OpenRouter response contained no text content."));
    }
}
