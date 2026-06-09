# Insurance Call Summariser

A small Java command-line application that uses the OpenAI API to generate a structured summary from an insurance call transcript.

## How it works

The application:

1. Accepts the path to a transcript text file as a command-line argument.
2. Checks that the file exists and is not blank.
3. Reads the transcript content.
4. Builds a prompt instructing the AI model to summarise the call accurately and professionally.
5. Sends the prompt to the OpenAI API.
6. Validates that the generated summary:

   * is not blank
   * contains the required sections
   * does not exceed 1,500 characters
7. Prints the validated summary to the terminal.

The required summary sections are:

```text
Caller:
Subject:
Executive Summary:
Next Steps:
```

Optional sections are included only when relevant, such as:

```text
Liability Summary:
Negotiation Summary:
Vehicle Damage:
Injury:
Property:
```

## How to set up the environment

### Requirements

Install:

* Java 21
* Maven
* An OpenAI API key

### Configure the API key

The application reads the API key from an environment variable named:

```text
OPENAI_API_KEY
```

Do not hard-code the key inside the source code.

#### macOS or Linux

```bash
export OPENAI_API_KEY="your_api_key_here"
```

#### Windows PowerShell

```powershell
$env:OPENAI_API_KEY="your_api_key_here"
```

Run the application from the same terminal session after setting the variable.

## How to run it

### Compile the project

```bash
mvn clean compile
```

### Run the tests

```bash
mvn test
```

### Run the application

```bash
mvn exec:java \
  -Dexec.mainClass="com.example.CallSummarisationApp" \
  -Dexec.args="path/to/transcript.txt"
```

Replace:

```text
path/to/transcript.txt
```

with the location of the transcript file you want to summarise.

Example:

```bash
mvn exec:java \
  -Dexec.mainClass="com.example.CallSummarisationApp" \
  -Dexec.args="examples/sample-transcript.txt"
```
