# Lab 06 — Logging with SLF4J

Package `edu.spu.se411.lab06_logging` · main class `App`
**Run:** right-click `pom.xml` → Run As → Maven build… → Goals: `clean package exec:java`

## The point of the lab

SLF4J is a **facade**, not a logging library. The Java code only ever talks to
`org.slf4j.Logger`; which library actually writes the logs is decided by a
dependency in `pom.xml`. Swapping logback for log4j changed **zero lines of Java**
in this project — only the pom.

## Steps, and what each one proved

| Step | What was added | Result |
|---|---|---|
| 2 | `org.slf4j:slf4j-api:2.0.16` only | `SLF4J: No SLF4J providers were found.` — the facade has no implementation |
| 3 | `Logger` field + `logger.info("Application is starting...")` | still nothing logged, same warning |
| 4 | `ch.qos.logback:logback-classic` | messages appear on the console — a provider is now on the classpath |
| 5 | removed logback, added `org.slf4j:slf4j-log4j12:2.0.16` + `log4j.properties` | messages go to a **file**, in the configured pattern |

The logback dependency is kept in `pom.xml` as a commented-out block so the
intermediate step is still visible.

## log4j.properties

```properties
log4j.rootLogger = INFO, LOG1
log4j.appender.LOG1=org.apache.log4j.FileAppender
log=./logs/App/log4j
log4j.appender.LOG1.File=${log}/log.out
log4j.appender.LOG1.layout=org.apache.log4j.PatternLayout
log4j.appender.LOG1.layout.conversionPattern=%d %p [%t] %C - %m%n
```

- `%d` timestamp · `%p` priority · `%t` thread · `%C` class · `%m` message · `%n` newline
- Output goes to **`logs/App/log4j/log.out`**, not the console — check that file,
  not the Eclipse Console, or it looks like nothing happened.
- Set `rootLogger = ERROR` and re-run: the file gets nothing, because every
  message in this program is INFO or below. Set it to `INFO` to see the
  start/end lines, and to `DEBUG` to see the wallet operations as well.

## Log levels used (the handout's table)

| Event | Level | Where |
|---|---|---|
| Application starts / ends | `info` | `App.main` |
| Wallet account created | `debug` | `WalletAccount` constructor |
| Deposit, withdraw | `debug` | `WalletAccount.deposit` / `withdraw` |
| `InsufficientFundsException` object created | `warn` | the exception's constructor |
| Thrown exception | `error` | the `catch` blocks in `App` |

## Logging-strategy review

An AI review of the logging, and what was changed as a result:

| Finding | Fix applied |
|---|---|
| String concatenation inside log calls builds the message even when the level is disabled | Used SLF4J's `{}` placeholders everywhere: `logger.debug("Deposit requested: amount={}, balance={}", amount, balance)`. The formatting only happens if the level is enabled. |
| `System.out.println` bypasses the logging framework entirely — no level, no timestamp, no file | All the starter's `println` calls replaced with `logger` calls at the right level. |
| Same failure logged at both the throw site and the catch site produces duplicate noise | The exception is logged at `error` **only** in the handler. The throw site does not log it. |
| Exception logged as `e.getMessage()` loses the stack trace | The throwable is passed as the last argument (`logger.error("...{}", amount, e)`), which SLF4J recognises and prints with its stack trace. |
| One shared or misnamed logger makes filtering by class impossible | Each class has its own `private static final Logger` created with `LoggerFactory.getLogger(ThatClass.class)`. |
| Levels chosen by habit rather than by audience | `info` = lifecycle a operator cares about; `debug` = per-transaction detail for a developer; `warn` = something suspicious but handled; `error` = an operation failed. |

Two things flagged but **not** changed, and why:

- **log4j 1.2 is end-of-life** (superseded by log4j 2 / reload4j, and carries
  known CVEs). Kept because the lab specifies `slf4j-log4j12`. In real code the
  binding would be logback or `log4j-slf4j2-impl`.
- **No `isDebugEnabled()` guards.** With `{}` placeholders they are redundant —
  they would only help if an argument were itself expensive to compute.

## If `slf4j-log4j12:2.0.16` fails to resolve

The 2.x line moved the log4j 1.2 binding to reload4j. If Maven cannot find the
artifact, swap the dependency for the drop-in fork — `log4j.properties` is
unchanged:

```xml
<dependency>
  <groupId>org.slf4j</groupId>
  <artifactId>slf4j-reload4j</artifactId>
  <version>2.0.16</version>
</dependency>
```

## Note on the build

`maven.compiler.release` is set to **21**. The starter pom had no compiler
configuration, which leaves Maven on a very old default source level.

## Sample output

`sample-log.out` in this folder is a real run captured from `logs/App/log4j/log.out`.
It contains two executions back to back (the appender is in append mode):

1. `log4j.rootLogger = INFO` — only the two lifecycle lines, the WARN from the
   exception constructor and the two ERRORs with their stack traces.
2. `log4j.rootLogger = DEBUG` — the same, plus every `WalletAccount` operation.

That pair is the level-filtering experiment the handout asks for. The committed
configuration is back at `INFO`; `logs/` itself is gitignored because it is
generated output.
