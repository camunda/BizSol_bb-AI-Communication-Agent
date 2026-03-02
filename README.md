# Camunda Business Solution Communication Agent

This repository contains a sample implementation of a communication agent for multichannel customer interactions.
It receives inbound communication, maps it to a canonical `SupportCase`, matches existing cases, and orchestrates downstream communication with specialist agents.

## Prerequisites

- **Java 21** (JDK)
- **Maven 3.9+**
- **Camunda 8.8+** - e.g. [c8run](https://docs.camunda.io/docs/self-managed/setup/deploy/local/c8run/) for local development, or a [SaaS](https://docs.camunda.io/docs/guides/create-cluster/) / Self-Managed cluster
- **Docker** (only for integration tests via Testcontainers)

## Mandatory inputs

- `supportCase` (object) when starting `message-receiver` directly or publishing `CustomerCommunicationReceived`
- Required `supportCase` fields are defined in `src/main/resources/support-case.schema.json`:
  - `subject` (string)
  - `request` (string)
  - `communicationContext` (object)
  - `attachments` (array)
  - `receivedDateTime` (ISO-8601 local date-time string)

## Customizations

### Mandatory

- Open `communication-agent.bpmn` in [Camunda Modeler](https://modeler.cloud.camunda.io/) (Web or Desktop)
- Select the AI Agent subprocess and configure:
  - **Model provider** (OpenAI-compatible, Bedrock, etc.)
  - **Model**
  - **Credentials / API key**
- Review and adapt the AI system prompt and tool behavior in `communication-agent.bpmn` to your customer communication policy.
- Open `business-agent.bpmn` and implement our first business process.
- Open `message-receiver.bpmn` and configure inbound connectors:
  - `StartEvent_Email`: IMAP host/port, username, password
  - `StartEvent_WebHook`: webhook context and auth settings
- Open `case-matching.bpmn` and replace the sample case-matching logic with customer-specific correlation rules.

### Optional

- Switch matching mode in `case-matching.bpmn` with process variable `use_feel`:
  - `true` -> FEEL script task (`Task_CaseMatching`)
  - `false` or unset -> job worker (`CaseMatching`)
- Extend canonical input schema and models for additional channels/metadata:
  - `src/main/resources/support-case.schema.json`
  - `src/main/java/io/camunda/bizsol/bb/comm_agent/models/*`
- For Camunda SaaS, use `src/main/resources/application-saas.yaml` and provide cluster credentials.
- Adjust worker configuration in `src/main/resources/application.yaml` (default port: `8080`).

## Camunda artifacts

The `/camunda-artifacts` directory contains:

| File | Purpose |
|------|---------|
| `communication-agent.bpmn` | Main communication orchestration process. |
| `message-receiver.bpmn` | Inbound adapter process (email/webhook) that maps incoming communication and emits `CustomerCommunicationReceived`. |
| `case-matching.bpmn` | Correlation key evaluation (FEEL or worker). |
| `message-sender.bpmn` | Outbound communication subprocess for channel-specific customer replies. |
| `business-agent.bpmn` | Specialist sub-agent process, triggered by communication orchestration. |

## Running

### 1. Customize and Deploy the BPMN to Camunda

Deploy **at minimum**:

- `communication-agent.bpmn`
- `message-receiver.bpmn`
- `case-matching.bpmn`
- `message-sender.bpmn`

Deploy `business-agent.bpmn` if you use specialist-agent delegation in the communication flow.

### 2. Connect the Job Worker to the Camunda cluster

The application in `src/main/java/io/camunda/bizsol/bb/comm_agent/CommAgentApplication.java` runs the `CaseMatching` job worker.
By default (without extra config), the Camunda SDK targets local endpoints compatible with c8run.

To point to a different cluster, set:

```bash
export CAMUNDA_CLIENT_REST_ADDRESS=http://localhost:8080
```

or adapt `src/main/resources/application.yaml` / `src/main/resources/application-saas.yaml`.

### 3. Start the Job Worker application

```bash
mvn spring-boot:run
```

The application starts on port **8080**.

### 4. Start a process instance

Start a process instance of `message-receiver` with:

```json
{
  "supportCase": {
    "subject": "Policy question",
    "request": "I need help updating my policy details.",
    "communicationContext": {
      "channel": "email",
      "conversationId": "conv-123",
      "emailAddress": "customer@example.com"
    },
    "attachments": [],
    "receivedDateTime": "2026-03-02T10:15:00"
  }
}
```

## Running tests

```bash
mvn test
```

Tests use [Camunda Process Test](https://docs.camunda.io/docs/apis-tools/testing/getting-started/) with Testcontainers.

The build enforces:

- **60 %** BPMN path coverage (via `camunda-process-test`)
- **80 %** line coverage (via JaCoCo)
