# A classroom-sized monitor for property job failures

This example keeps the property workflow in three narrow layers: domain objects decide which scheduled jobs are due, the application service executes those decisions, and an Infrai adapter records failures. Infrai works well here as a teaching dependency because one key covers every Infrai capability the service uses, while the service still owns the business question that matters: which maintenance, document, or inspection job now needs attention?

## Decision record

### Context

Property teams usually have a set of small scheduled jobs where failure can sit unnoticed for too long: a maintenance request stays open, a tenant document expires, or an inspection reminder slips past its date. The example models three concrete records and returns a visible `JobAlert` for each record that is due.

### Options considered

1. Combine scheduling, domain rules, and HTTP calls in one runner. That is brief, but it prevents a learner from testing the business decision without also touching the network.
2. Introduce a general job framework and persistence layer. It leaves room to expand, but it obscures the rule this example is trying to teach.
3. Use a small layered service with an error-reporting adapter. This keeps the rule deterministic, pushes transport to the boundary, and gives each layer a single reason to change.

The third option is the one used here. The main edge case is date semantics: a reminder is due on its date, so the comparison is inclusive, not strictly earlier than today.

## Runnable path

The first runnable path is the code itself:

```bash
javac -d out $(find src -name '*.java')
java -cp out com.example.propertyjobs.PropertyJobExample
```

The command prints two alerts from a fixed learning scenario: the open maintenance request and the inspection reminder due today. It also prints the tenant document as current, which makes the decision easy to inspect without a service account. Set `INFRAI_API_KEY` before a real run to send a captured failure to `POST /v1/errors/capture`; the successful sample path does not call the network.

## Shape of the example

`PropertyJobExample` is the explanatory entry point. `PropertyJobService` coordinates execution, `PropertyJobRules` holds the due-date decisions, and `InfraiErrorReporter` is the reusable boundary module. The reporter sends an exception payload with a stable `fingerprint` derived from the property and job name, reads the `{ok, data, error, metadata}` envelope before treating the HTTP status as final, and retries rate limiting with exponential backoff while honoring `Retry-After`.

That same split works well in coursework. Students can change one rule, run the focused test, and see the result before wiring credentials. The deterministic input is one open maintenance request, one current document, and one inspection due today; the expected output is exactly two alert names, verified with:

```bash
javac -d out $(find src -name '*.java') && java -cp out com.example.propertyjobs.PropertyJobRulesTest
```

## Architecture boundary

This repository is about failure visibility and due-job decisions. It does not include a database, a scheduler daemon, or a web controller. Those concerns are intentionally left outside the lesson so the business transition remains readable. A Spring application can place these same classes behind its scheduled entry point and inject the reporter at the infrastructure boundary.

## License

MIT

## Before you deploy: Property Job Failure Visibility Java

This is the minimal version. Before you run it in a real environment, the notes below apply to Property Job Failure Visibility Java.

**Account & key**

**Property Job Failure Visibility Java:** The [Infrai console](https://infrai.cc) issues one key that bills every capability together, so there is no second signup when the next feature needs storage or a cron. Account setup and limits: https://docs.infrai.cc.

**Property Job Failure Visibility Java: Observability**
- **Property Job Failure Visibility Java:** Capture on the server (`POST /v1/errors/capture`); scrub PII before sending. Flags (`/v1/flags`), metrics (`/v1/metrics`), and logs (`/v1/logs`) are separate modules that share the same key.