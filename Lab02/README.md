# Lab 02 — Introduction to Java Generics

Package: `edu.psu.se411.generics`
Run: `Main.java` → Run As → Java Application

| Exercise | Topic | File |
|---|---|---|
| 1 | Generic class with a `List` attribute, array constructor, print method | `PrintableList.java` |
| 2 | Bounded type parameter `<T extends Number>`, `setItem` / `getItem`, add and sum | `NumberBox.java` |
| 3 | Generic transformation interface | `Transformer.java` |
| 3 | Type-changing pipeline `PipeLine<T, R>` | `PipeLine.java` |
| 4 | Wildcards: `printList(List<?>)`, `sumNumbers(List<? extends Number>)` | `Main.java` |

## Notes

**Exercise 2 — why the bound matters.** `T extends Number` lets the class call
`doubleValue()` on the stored item, and makes `NumberBox<String>` a compile
error rather than a runtime one.

**Exercise 3 — how the type changes along the chain.** `add()` does not mutate
the pipeline; it returns a *new* `PipeLine<T, V>` whose output type is that of
the transformer just added. So the compiler tracks the type as it changes:

```
PipeLine<String, String>  →  add(String→String)   →  PipeLine<String, String>
                          →  add(String→Integer)  →  PipeLine<String, Integer>
                          →  add(Integer→Boolean) →  PipeLine<String, Boolean>
```

`execute()` walks the list in order. The one unchecked cast inside it is safe
because `add()`'s signature already guarantees each transformer's input type
matches the previous one's output type — the information just isn't available
at run time, since generics are erased.

**Exercise 4 — `<?>` vs `<? extends Number>`.** The unbounded wildcard lets us
read elements as `Object`; the upper-bounded one lets us read them as `Number`,
which is what makes `doubleValue()` available. Neither allows adding to the
list, because the compiler doesn't know the actual element type.

## Expected output

```
=== Exercise 1: PrintableList ===
Aziz
Homood
Sara
Khalid
90
75
100

=== Exercise 2: NumberBox ===
intBox item      : 10
intBox + 5       : 15.0
doubleBox item   : 2.5
doubleBox + 0.75 : 3.25
intBox + 2.5     : 12.5
sum of ints      : 15.0
sum of doubles   : 7.0

=== Exercise 3: PipeLine ===
steps in pipeline    : 4
"  hello  " -> true
"  hi  "    -> false
doubler.execute(5)   : 11
empty.execute("abc") : abc

=== Exercise 4: Wildcards ===
red
green
blue
1
2
3
sumNumbers(ints)    : 6.0
sumNumbers(doubles) : 4.0
```
