# Lab 05 — Exception Handling

Group id `edu.psu.se411` · main class `edu.psu.se411.App`
**Run:** right-click `pom.xml` → Run As → Maven build… → Goals: `clean package exec:java`

```
src/main/java/edu/psu/se411/
├── App.java                                  main + validateAge
├── exceptions/                               package for exception classes only
│   ├── InvalidAgeException.java              Ex 1
│   └── InsufficientFundsException.java       Ex 2
└── model/
    ├── Wallet.java                           Ex 2
    └── BankAccount.java                      Ex 2
```

## Exercise 1 — custom exceptions

| Step | Where |
|---|---|
| 1. Maven project + class with `main` | `pom.xml`, `App.java` |
| 2. `InvalidAgeException` in a package holding only exceptions | `exceptions/InvalidAgeException.java` |
| 3. `validateAge(int)` — throws below 18, prints the valid message at 18+ | `App.validateAge` |
| 4. Called from `main` | `App.exercise1()` |
| 5. `exec-maven-plugin` 3.5.0 with `<mainClass>edu.psu.se411.App</mainClass>` | `pom.xml` |

`InvalidAgeException extends Exception`, so it is **checked**: the compiler
forces every caller to catch it or declare `throws`. A `RuntimeException` would
compile even if nobody handled it, which defeats the purpose of the exercise.

## Exercise 2 — the online wallet

`wallet.withdrawTo(account, amount)` moves money from the wallet into a bank
account and throws `InsufficientFundsException` when the amount exceeds the
balance.

## Exception-handling review

A review of this code's exception handling, and what was done about each point:

| Point | Applied here |
|---|---|
| Checked vs unchecked chosen deliberately | Business-rule failures the caller can recover from (`InvalidAgeException`, `InsufficientFundsException`) are **checked**. Programming errors (null account, negative amount) throw the unchecked `IllegalArgumentException` — a caller cannot sensibly recover from passing −50. |
| No empty or swallowing `catch` blocks | Every `catch` reports what happened. Nothing is caught and discarded. |
| Never `catch (Exception e)` | Each handler names the specific type it can deal with. A blanket catch would also swallow bugs such as `NullPointerException`. |
| Exceptions carry data, not just text | `InsufficientFundsException` exposes `getRequested()`, `getAvailable()` and `getShortfall()`, so a handler can react without parsing the message. `InvalidAgeException` exposes `getAge()`. |
| Fail before mutating state | `withdrawTo` runs all checks **before** touching the balances, so a declined withdrawal leaves the wallet and the account untouched — no partial transfer. |
| Validate at the boundary | Constructors reject empty owners/account numbers and negative opening balances, so an object can never exist in an invalid state. |
| Messages are actionable | "tried to withdraw 1000.00 but only 300.00 is available (short by 700.00)" instead of "error". |
| `serialVersionUID` declared | Both exception classes declare it, silencing the serialization warning `Exception` subclasses otherwise produce. |

Not done on purpose: no logging framework (out of scope for a console lab), and
no retry logic (the caller decides, not the wallet).

## Expected output

```
=== Exercise 1: validateAge ===
Age valid message. (age = 25)
Age valid message. (age = 18)
Rejected -> Invalid age: 17. Age must be 18 or higher.
Rejected -> Invalid age: 0. Age must be 18 or higher.

=== Exercise 2: online wallet ===
Before  : Wallet[Homood] balance = 500.00 | BankAccount[SA-1122334455] balance = 0.00
OK      : withdrew 200.00 -> wallet 300.00, account 200.00
DECLINED: Insufficient funds: tried to withdraw 1000.00 but only 300.00 is available (short by 700.00).
          (short by 700.00 - nothing was transferred)
OK      : withdrew 300.00 -> wallet 0.00, account 500.00
BAD INPUT: Withdrawal amount must be positive
After   : Wallet[Homood] balance = 0.00 | BankAccount[SA-1122334455] balance = 500.00
```
