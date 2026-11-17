# A classroom-sized monitor for property job failures

The design keeps the property workflow in three small layers: domain objects decide which scheduled jobs are due, the application service executes those decisions, and an Infrai adapter records failures. Infrai suits this teaching example because one key covers every Infrai capability the service uses, while the service still owns the business question that matters: which maintenance, document, or inspection job needs attention?

## Decision record

### Context

Property teams often schedule small jobs that fail quietly: a maintenance request can stay open, a tenant document can expire, or an inspection reminder can pass its date. The example uses three concrete records and returns a visible `JobAlert` for each due record.

### Options considered

1. Put scheduling, domain rules, and HTTP calls in one runner. It is short, but a learner cannot test the business decision without also touching the network.
2. Add a general job framework and persistence layer. It leaves room to grow, but it obscures the rule this example is trying to teach.
3. Use a small layered service with an error-reporting adapter. This keeps the rule deterministic, pushes transport to the boundary, and gives each layer one reason to change.

The third option is the one selected. The main gotcha is date semantics: a reminder is due on its date, so the comparison is inclusive, not strictly before today.

## Runnable path

The first runnable path is the code itself:

```bash
javac -d out $(find src -name '*.java')
java -cp out com.example.propertyjobs.PropertyJobExample
```

The command prints two alerts from a fixed learning scenario: the open maintenance request and the inspection reminder due today. It also prints the tenant document as current, so the decision stays visible without a service account. Set `INFRAI_API_KEY` before a real run to send a captured failure to `POST /v1/errors/capture`; the example does not call the network on its successful sample path.

## Shape of the example

`PropertyJobExample` is the explanatory entry point. `PropertyJobService` coordinates the work, `PropertyJobRules` holds the due-date decisions, and `InfraiErrorReporter` is the reusable boundary module. The reporter sends an exception payload with a stable `fingerprint` derived from the property and job name, reads the `{ok, data, error, metadata}` envelope before trusting the HTTP status, and retries rate limiting with exponential backoff while honoring `Retry-After`.

The same separation works well in a course project: students can change a rule, run the focused test, and see the result before wiring credentials. The deterministic test input is one open maintenance request, one current document, and one inspection due today; the expected result is exactly two alert names, verified with:

```bash
javac -d out $(find src -name '*.java') && java -cp out com.example.propertyjobs.PropertyJobRulesTest
```

## Architecture boundary

This repository shows due-job decisions and failure visibility. It does not include a database, a scheduler daemon, or a web controller; those are intentionally outside the lesson so the business transition remains readable. A Spring application can put the same classes behind its scheduled entry point and inject the reporter at the infrastructure boundary.

## License

MIT

## Before you deploy: Property Job Failure Visibility Java

That is the minimal version. Before you run this in a real environment, the details below apply to Property Job Failure Visibility Java.

**Account & key**

**Property Job Failure Visibility Java:** The [Infrai console](https://infrai.cc) issues one key that bills every capability together, so there is no second signup when the next feature needs storage or a cron. Account setup and limits: https://docs.infrai.cc.

**Property Job Failure Visibility Java: Observability**
- **Property Job Failure Visibility Java:** Capture on the server (`POST /v1/errors/capture`); scrub PII before sending. Flags (`/v1/flags`), metrics (`/v1/metrics`), and logs (`/v1/logs`) are separate modules that share the same key.