# Lab 12 — Threads and Locks

Package `edu.psu.se411.lab12_threads` · artifact `lab12_threads`

Three main programs:

| Class | Covers | Run with |
|---|---|---|
| `App` | part 1 — **fixed** thread pool | `clean package exec:java` |
| `AppCachedPool` | part 1 — **cached** thread pool | `clean package exec:java -Dexec.mainClass=edu.psu.se411.lab12_threads.AppCachedPool` |
| `AppLocks` | parts 2 and 3 — locks, deadlock, resource ordering | `clean package exec:java -Dexec.mainClass=edu.psu.se411.lab12_threads.AppLocks` |

In Eclipse, put the `-Dexec.mainClass=…` part in the **Goals** box after the
goals, or add it as a parameter in the run configuration.

---

## Part 1 — fixed pool vs cached pool

### Measured results

One million transactions per scenario, per-account `synchronized` (the Lab 11
solution), measured on a **2-core** Linux container. Absolute numbers on your
Mac will be lower; the ratio is what matters.

| Pool | Accounts | Time | Peak live threads | Total |
|---|---|---|---|---|
| fixed (2) | 1000 | **500 ms** | 8 | conserved |
| fixed (2) | 10 | **349 ms** | 8 | conserved |
| cached | 1000 | **4064 ms** | 67 | conserved |
| cached | 10 | **3824 ms** | 75 | conserved |

### What do you observe?

**The cached pool is roughly 8× slower, and the peak thread count says why.**

A fixed pool creates its threads once and reuses them: 2 worker threads, 8 live
threads in the whole JVM, one million tasks pulled off a shared queue.

A cached pool creates a new thread whenever no *idle* one is available. A thread
blocked on a lock is not idle — so under contention the pool keeps
manufacturing threads, peaking at 67–75 here. Every one of them costs a stack
allocation, an OS thread, and a slot in the scheduler's rotation. With tasks
this short (two field updates), that overhead dwarfs the work itself.

The rule it demonstrates: **a cached pool suits many short, independent tasks;
a fixed pool suits CPU-bound tasks that contend.** These tasks contend, so the
fixed pool wins.

**Account count made little difference on this machine** — 1000 vs 10 accounts
changed the time by ~30% for the fixed pool and ~6% for the cached pool, which
is close to run-to-run noise. The reason is that only 2 cores were available, so
at most 2 threads could ever contend. On a machine with 8 or 16 cores the
10-account case should be measurably slower than the 1000-account case, because
ten `synchronized` monitors cannot keep sixteen cores busy while a thousand
can. Worth re-running on your Mac and comparing.

### A note on the starter's pool size

The starter wrote:

```java
ExecutorService executor = Executors.newFixedThreadPool(nbTransactions);
```

One thread per task. At the starter's 100,000 that is merely wasteful — it
defeats the entire purpose of a pool. At the one million this lab asks for it
simply cannot run: each thread reserves its own stack and the JVM dies long
before the millionth.

`App` therefore sizes the pool to `Runtime.getRuntime().availableProcessors()`.
A fixed pool is sized to the **hardware**, not to the workload.

---

## Part 2 — replace `synchronized` with locks. What could go wrong?

### Answer: deadlock

Swapping the per-account `synchronized` methods for per-account
`ReentrantLock`s, held across the whole transfer, finally makes a transfer
*atomic* — and introduces a circular wait.

`Transaction.executeWithNaiveLocks()` takes the **sender's** lock first:

```java
first = sender.getLock();      // thread 1: acc_A    thread 2: acc_B
second = receiver.getLock();   // thread 1: acc_B    thread 2: acc_A
first.lock();
    second.lock();             // both block here, forever
```

- Thread 1 runs A → B: holds A's lock, wants B's.
- Thread 2 runs B → A: holds B's lock, wants A's.

Neither releases what it holds. Two threads, two locks, opposite order.

`AppLocks` reproduces this deterministically (a 100 ms pause between the two
acquisitions turns a rare race into a certainty) and then asks the JVM to
confirm it:

```
NEITHER transfer completed after 3 seconds. Deadlocked.

  The JVM confirms it. ThreadMXBean.findDeadlockedThreads() names:
    - transfer-A-to-B
    - transfer-B-to-A
```

`ThreadMXBean.findDeadlockedThreads()` detects cycles over both monitors and
`ReentrantLock`s, so this is the JVM's own verdict, not an inference from a
hang. The two threads are daemons, so the program can still exit.

**Why this is worse than the bug it replaced.** The Lab 11 bug corrupted a
number; you saw the wrong total and knew something was wrong. This one hangs,
produces no output, and may only appear under load in production. Nothing in
the code looks wrong.

---

## Part 3 — resource ordering

### The fix

Impose a **total order** on accounts and always take locks in ascending order.
`CryptoAccount.getOrderKey()` returns the id; `executeWithOrderedLocks()` uses
it:

```java
if (sender.getOrderKey().compareTo(receiver.getOrderKey()) < 0) {
    firstLock  = sender.getLock();
    secondLock = receiver.getLock();
} else {
    firstLock  = receiver.getLock();   // receiver sorts lower, so it goes first
    secondLock = sender.getLock();
}
```

### Why it works

A deadlock cycle requires at least one thread travelling *down* the order while
another travels *up*. If every thread only ever goes up, no cycle can form —
**guaranteed**, not merely unlikely. Same scenario, same 100 ms window:

```
Both transfers completed.
  Both threads wanted acc_A and acc_B. Both took acc_A first,
  because acc_A < acc_B. So one waited at the door instead of
  inside the room, and there was no cycle to form.

  total: 20000.00 (started at 20000.00)
```

The phrase to remember: one thread waits *at the door* rather than *inside the
room* holding a lock the other needs.

### Requirements for the order

- **Total** — for any two distinct accounts, one is strictly before the other.
  Ids give that.
- **Stable** — it must not change while locks are held. Ids are immutable.
- **Agreed by everyone** — every code path that locks two accounts must use the
  same comparison. One method that forgets re-opens the deadlock.

Ids are zero-padded (`acc_000` … `acc_999`) so they sort numerically. That is
cosmetic: any total order prevents deadlock, including the lexicographic one.

### Self-transfers

`sender == receiver` returns early. The locks are `ReentrantLock`, so locking
the same account twice from one thread would be harmless, but it is skipped
explicitly rather than relying on reentrancy.

### Ordered locks vs one global monitor

From `AppLocks`, 1,000,000 transactions, fixed pool of 2:

```
synchronized    1000 accounts   1000000 tx    550 ms   conserved
ordered locks   1000 accounts   1000000 tx    616 ms   conserved

synchronized      10 accounts   1000000 tx    695 ms   conserved
ordered locks     10 accounts   1000000 tx    452 ms   conserved
```

On 2 cores these are within noise, so **this machine cannot show the advantage**.
The argument for ordered locks here is correctness, not speed: it is the only
one of the three strategies that makes a transfer *atomic* without serialising
the whole system. On a many-core machine the gap should open up, because
per-account locks let unrelated transfers proceed in parallel while a global
monitor cannot.

---

## Summary of the three strategies

| Strategy | Total conserved | Transfer atomic | Concurrent | Deadlock-free |
|---|---|---|---|---|
| no locking (Lab 11 part 1) | ✗ | ✗ | ✓ | ✓ |
| per-account `synchronized` | ✓ | ✗ | partly | ✓ |
| naive per-account locks | ✓ | ✓ | ✓ | **✗** |
| ordered per-account locks | ✓ | ✓ | ✓ | ✓ |

Only the last row gets every column.

---

## If the build fails on the compiler release

The starter `pom.xml` sets `<maven.compiler.source>24</maven.compiler.source>`
and the matching target. If Eclipse's embedded Maven runs on an older JVM the
build fails with *"release version 24 not supported"*. Change both to `21` —
nothing in this code needs anything newer. (Same issue as Lab 09.)
