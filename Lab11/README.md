# Lab 11 — Introduction to Threads: the Double Spending Problem

Package `edu.psu.se411.lab11_threads` · main class `App` · artifact `lab11_threads`
**Run:** right-click `pom.xml` → Run As → Maven build… → Goals: `clean package exec:java`
Takes ~2–3 seconds: two runs of 10,000 threads each.

## What the program shows

10 accounts × 1000 riyals = **10,000 riyals in the system**. Money only ever
moves between accounts, so that total must be identical before and after any
number of transactions.

The program runs the same 10,000 random transactions twice:

| Run | Method used | Result |
|---|---|---|
| 1 | `t.execute()` — no synchronization | total is **wrong**, by tens of riyals, differently each time |
| 2 | `Transaction.executeSynchronized(t)` | total is **exactly 10,000**, every time |

Sample from two consecutive runs — note the unsynchronized figure is not even
consistently wrong in the same direction, which is the signature of a race:

```
run A   without synchronized : 9983.98    (difference -16.02)
run B   without synchronized : 10055.72   (difference +55.72)
        with synchronized    : 10000.00   (difference +0.00)
```

## Part 1 — why it breaks

`CryptoAccount.addToBalance` is a **read-modify-write**:

```java
balance = balance + amount;      // read balance, add, write back — three steps
```

Two threads can both read 1000.0, both compute 1000.0 + their amount, and both
write. The second write overwrites the first, so one deposit vanishes. The
field is a `Double` (the wrapper, as in the starter), so each update also
unboxes and re-boxes, widening the window further.

There is a **second** race on top of that one. Even if each account's methods
were individually atomic, a transaction is two operations:

```java
sender.retrieveFromBalance(amount);   // money has left the sender
receiver.addToBalance(amount);        // ...and has not arrived yet
```

Between those lines the money exists nowhere. Another thread reading the system
total at that instant sees less than 10,000.

## Part 2 — one `synchronized` method

```java
public static synchronized void executeSynchronized(Transaction transaction) {
    transaction.transfer();
}
```

**The `static` is the whole trick, and it is the thing to be able to explain.**

A `synchronized` *instance* method locks `this`. Every transaction is a
different `Transaction` object, so 10,000 transactions would take 10,000
different locks and exclude nobody — the program would be exactly as broken,
while looking fixed.

A `static synchronized` method locks the single `Transaction.class` object, so
every transfer in the system serialises against every other. Both races above
disappear at once: the read-modify-write is protected, *and* the debit/credit
pair becomes atomic.

### Why synchronizing `CryptoAccount` would not have worked

Marking `addToBalance` and `retrieveFromBalance` as `synchronized` is the
tempting answer. It fixes race 1 and leaves race 2 completely intact: each half
of the transfer is atomic, but the pair is not, so the total still goes wrong.

Fixing it at the account level properly would mean locking *both* accounts for
the duration of the transfer — which introduces deadlock unless the locks are
always taken in a consistent order. That is why one global lock is the right
answer for this lab.

### The cost

Serialising everything is correct but not concurrent: transfers between
unrelated accounts now wait for each other. That is the trade-off the lab is
demonstrating. A production system would lock per account pair in a fixed
order, or use `AtomicLong` / `LongAdder` on integer minor units.

## Design notes

**The three-argument constructor.** The handout's sample code is
`new Transaction(randomA, randomB, randomAmount)` — no id. The starter class
only had a four-argument constructor, so a three-argument one was added that
generates `tx_1`, `tx_2`, … via an `AtomicInteger` (itself thread-safe, since
10,000 threads' worth of transactions are built on the main thread but the
counter costs nothing to make safe).

**Starting balance.** The starter set `initialBalance = 10000.0`, which gives a
system total of 100,000. The handout says *"ten accounts with 1000 riyals in
each account… a total of 10000 riyals"*, so this is set to `1000.0`.
`nbTransactions` was raised from 1000 to 10000 for the same reason.

**The broken starter line.** `App.java` shipped with an incomplete statement
(`system.out.println("Total balance  before transaction:" +get)`) that does not
compile. It is replaced by the `getTotalBalance` call the handout asks for.

**Floating-point tolerance.** The conservation check uses a tolerance of 1e-6,
not `==`. Summing the same ten `double` balances in a different order can land
on 10000.000000000005 — that residue is IEEE-754 arithmetic, not a race. A real
race moves the total by whole riyals. (Real money would use `BigDecimal` or
integer minor units; the starter uses `Double`, so the comparison has to
tolerate it.)

**Exception handling.** `retrieveFromBalance` throws
`InsufficientBalanceException` when the sender cannot pay. `transfer()` debits
*before* crediting, so a failed debit means nothing is credited and the total is
unaffected — the transaction is simply skipped.

## Logging

Starter configuration, unchanged: `slf4j-api` + `slf4j-log4j12`, output to
**`logs/log.out`**. Start and close are logged there; the report itself goes to
the Console via `System.out`.
