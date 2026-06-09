# Brightbeam Call Summarisation Coding Checklist

## Goal

Build the smallest complete working solution first.

The application should:

1. Accept an insurance call-transcript file as input.
2. Validate the file before sending content to the LLM.
3. Use an LLM to generate a structured summary.
4. Validate the generated output.
5. Print or save the final summary in the required format.
6. Remain easy to read, test, modify and explain.

Keep the solution simple. Do not add frameworks, layers or abstractions unless they solve a clear problem.

---

## Before coding

* Summarise the requirement in plain English.
* Identify the smallest end-to-end workflow.
* Separate required features from optional enhancements.
* State assumptions clearly.
* Ask for clarification when an assumption could materially change the solution.
* Inspect the sample training examples before finalising the prompt.
* Identify common failure modes from the good, okay and bad examples.
* Decide how the application will accept a transcript file.
* Decide where output validation belongs.

Start with:

```text
transcript file
→ validate input
→ create prompt
→ call LLM
→ validate response
→ print summary
```

---

## Required output format

The generated summary must follow this structure:

```text
Caller: [Name if known], [relationship to claim], [direction]

Subject:
[One-line description of what the call was about]

Executive Summary:
[Paragraph explaining what happened on the call and why]
- [Key detail or fact]
- [Additional details as needed]

Next Steps:
[COMPANY_NAME]: [Action to be taken by the company]
Other: [Action to be taken by other parties, or "None"]
```

Add these sections only when relevant:

```text
Liability Summary:
[Only if liability was discussed]

Negotiation Summary:
[Only if negotiation occurred]

Vehicle Damage:
Vehicle Status: [Status]
Towage: [Details or "None"]
Car hire: [Details or "None"]

Injury:
Treatment: [Details of injury discussion]

Property:
[Details of property damage discussion]
```

Do not include an optional section if the topic was not discussed.

The total generated summary must not exceed 1,500 characters.

---

## Prompt design

The prompt should clearly instruct the model to:

* Extract information only from the transcript.
* Avoid inventing missing facts.
* Preserve accurate names, dates, amounts, contact details and reference numbers.
* Identify the caller accurately.
* Distinguish the caller from the policyholder, solicitor, representative, family member or other party.
* Identify whether the call is inbound or outbound when possible.
* Include company and other-party actions under `Next Steps`.
* Use professional language.
* Avoid internal jargon.
* Avoid duplicated information.
* Include conditional sections only when relevant.
* Keep the total output within 1,500 characters.
* State uncertainty carefully when speech-to-text quality makes a fact unclear.

Separate stable instructions from the transcript.

Use a clear boundary such as:

```text
<call_transcript>
...
</call_transcript>
```

Do not place the API key inside the prompt.

---

## Transcript quality

Expect noisy speech-to-text input.

The transcript may contain:

* encoding artifacts
* garbled words
* fragmented sentences
* false starts
* filler words
* unclear speaker attribution
* incomplete thoughts

Do not silently invent missing information.

When a fact is uncertain:

* omit it if it is not essential
* phrase it cautiously if it is important
* avoid confident statements not supported by the transcript

---

## API request

* Load the API key from an environment variable.
* Never hard-code, print or log the API key.
* Choose a model deliberately.
* Set an appropriate response-token limit.
* Use a low-variation configuration where appropriate for consistent summaries.
* Explain parameter choices before changing them.
* Handle missing API keys clearly.
* Handle failed API requests clearly.
* Do not silently ignore API errors.

---

## Output validation

Validate the LLM response before printing or saving it.

Check deterministically where possible:

1. The response is not blank.
2. The required `Caller` section exists.
3. The required `Subject` section exists.
4. The required `Executive Summary` section exists.
5. The required `Next Steps` section exists.
6. The total summary length is less than or equal to 1,500 characters.
7. The output does not contain markdown fences unless explicitly required.
8. The output does not contain obviously duplicated sections.
9. Optional sections are not added automatically when irrelevant.
10. A parsing or validation error is shown clearly.

Do not assume valid formatting means the facts are correct.

Review factual quality separately.

---

## Quality checklist

Before accepting a generated summary, ask:

1. Can I identify the caller's issue or query?
2. Can I identify the action taken or required?
3. Are names, dates, reference numbers, amounts and contact details accurate?
4. Has the model invented any confirmation that was not in the transcript?
5. Could another customer agent understand what happened without replaying the call?
6. Is the tone professional enough to share externally?
7. Are the caller's identity, relationship and direction correct?
8. Are optional sections included only when relevant?
9. Is information repeated unnecessarily?
10. Is the summary less than or equal to 1,500 characters?

---

## Common failure modes

Watch for:

* Labelling a third-party solicitor as the customer.
* Missing that the caller is a family member or representative.
* Confusing which company insures which party.
* Inventing a waiver, bank-detail confirmation or settlement agreement.
* Misstating reference numbers, emails, names, dates or amounts.
* Omitting important callback details or requested actions.
* Including irrelevant optional sections.
* Repeating the same fact in multiple sections.
* Treating unclear transcript text as a confirmed fact.

---

## Code quality

* Use descriptive variable and method names.
* Avoid vague names such as `data`, `result`, `temp`, `flag`, `value` and `process()`.
* Prefer names such as:

  * `transcriptFile`
  * `callTranscript`
  * `summaryPrompt`
  * `generatedSummary`
  * `validationErrors`
  * `validateGeneratedSummary()`
  * `buildSummaryPrompt()`
  * `requestSummaryFromClaude()`
* Prefer early returns over deeply nested conditionals.
* Keep methods focused on one responsibility.
* Avoid unnecessary classes, frameworks and design patterns.
* Do not rewrite working code unless the change solves a real problem.
* Explain proposed changes before editing multiple files.
* Keep transcript parsing, prompt creation, API calls and output validation logically separate.
* Avoid logging transcript contents unless necessary.
* Avoid exposing sensitive customer information in debug logs.

---

## Suggested minimal structure

Keep the project small.

```text
CallSummarisationApp
TranscriptFileReader
SummaryPromptBuilder
ClaudeSummaryClient
SummaryValidator
```

Only add a class when it has a clear responsibility.

A simpler structure is acceptable if it remains readable and testable.

---

## Testing

Before adding optional features:

1. Compile or run the application.
2. Test one normal transcript.
3. Test a blank transcript file.
4. Test a missing file.
5. Test a transcript containing garbled text.
6. Test an LLM response missing a required section.
7. Test an LLM response exceeding 1,500 characters.
8. Test an API failure.
9. Confirm no API key is exposed.
10. Confirm existing behaviour still works.

Prioritise unit tests for critical deterministic logic:

* blank-file validation
* missing-file handling
* required-section validation
* 1,500-character limit
* malformed or blank LLM response
* optional-section checks where feasible

Do not call the live LLM API in unit tests unless explicitly required.

Use mock responses for output-validation tests.

---

## Prompt evaluation

Use the provided training examples to understand:

* expected format
* common failure modes
* quality expectations
* factual accuracy requirements

Evaluate the prompt against a representative mix of:

* good examples
* okay examples
* bad examples
* short calls
* complex calls
* noisy transcripts
* calls with optional sections
* calls without optional sections

Record:

* transcript tested
* expected important facts
* generated output
* formatting issues
* factual issues
* prompt change made
* whether the change improved other cases

Do not optimise for one transcript while breaking others.

---

## README

Add a root-level `README.md` explaining:

1. What the application does.
2. The smallest architecture overview.
3. Requirements.
4. How to configure the API key.
5. How to install dependencies.
6. How to run the application.
7. How to run tests.
8. Assumptions.
9. Known limitations.
10. What would be improved with more time.

---

## Security and AI safety

* Never hard-code, print or commit API keys.
* Load secrets from environment variables.
* Do not treat model output as automatically correct.
* Do not expose sensitive transcript details in logs.
* Keep deterministic validation separate from model-generated reasoning.
* Require manual review before any high-impact downstream action.
* Do not claim unsupported facts.
* Keep factual extraction grounded in transcript evidence.

---

## Optional enhancements

Only attempt these after the core solution works:

* Structured JSON output followed by deterministic formatting.
* A prompt-evaluation script.
* Batch processing for multiple files.
* A simple web or API interface.
* Clear retry behaviour for temporary API failures.
* Improved validation reporting.
* A concise evaluation report comparing prompt versions.

Do not add optional features at the expense of a stable core solution.

---

## Final review

Before showing the solution, answer:

* What is the smallest useful version?
* Does the full end-to-end flow run on my laptop?
* Does the summary follow the required format?
* Is the summary within 1,500 characters?
* Did I test a normal transcript?
* Did I test invalid input?
* Did I test one noisy transcript?
* Did I validate required sections?
* What did AI generate?
* What did I verify, simplify or change?
* Which API parameters did I choose and why?
* What trade-off did I make?
* What would I improve with another hour?
* Can I explain every method and design decision?
