# Lab 03 - JUnit Intro: Coverage Notes

**Class under test:** `edu.psu.se411.model.Stack<E>`
**Test class:** `edu.psu.se411.model.StackTest` (13 tests)

## What is covered

| Member | Branch / case | Test |
|---|---|---|
| `Stack()` | default capacity 10 | every test using `stringStack` |
| `Stack(int)` | `capacity > 0` (true branch) | `stack_grows_beyond_its_initial_capacity` |
| `Stack(int)` | `capacity > 0` (false branch: 0 and negative) | `non_positive_capacity_falls_back_to_default` |
| `push(E)` | normal value | `testPushAndPop`, `elements_are_popped_in_reverse_order` |
| `push(E)` | `null` element | `push_null_element_is_allowed` |
| `push(E)` | empty string (not the same as empty stack) | `push_empty_string_is_a_real_element` |
| `push(E)` | growth past initial capacity | `stack_grows_beyond_its_initial_capacity` |
| `pop()` | non-empty branch, LIFO order | `testPushAndPop`, `push_then_pop_single_element`, `elements_are_popped_in_reverse_order` |
| `pop()` | empty branch -> `NoSuchElementException` + exact message | `pop_empty_stack` |
| `pop()` | empty branch reached again after emptying | `pop_after_stack_is_emptied_throws_again` |
| state across calls | interleaved push/pop | `interleaved_push_and_pop_keeps_correct_order` |
| state across calls | duplicates popped one by one | `duplicate_values_are_popped_one_by_one` |
| generics | works with `Integer`, incl. MIN/MAX values | `stack_works_with_another_type_parameter` |
| instance isolation | two stacks do not share state | `separate_stack_instances_do_not_share_state` |

Both branches of the only conditional in each method (`capacity > 0` in the
constructor, `elements.isEmpty()` in `pop`) are exercised, so branch coverage of
the class is 100%.

## What is NOT covered, and why

- **`peek`, `isEmpty`, `size`, `clear`** - not covered because they **do not exist**
  in the provided `Stack` class. The class only exposes two constructors, `push`
  and `pop`. There is no public way to observe the stack's size, so "the stack is
  empty" can only be asserted indirectly, by checking that `pop()` throws.
- **The private field `elements`** - not tested directly, on purpose. It is only
  exercised through the public API (`push` / `pop`), as required.
- **Capacity as a limit** - there is no "stack full" behaviour to test: the
  constructor argument is only an `ArrayList` initial-capacity hint, so pushing
  past it grows the list instead of failing. `stack_grows_beyond_its_initial_capacity`
  documents that this is intended behaviour rather than a missing check.
- **Thread safety / concurrent access** - out of scope for this lab; the class
  makes no concurrency guarantees.
- **`App.main`** - an empty auto-generated stub with no behaviour to assert.

## Note on the lab handout

The handout suggests asserting the exception message

```java
assertTrue(thrown.getMessage().equals("Stack is empty,can't pop"));
```

but the message actually thrown by the provided `Stack` class is

```java
"Stack is empty, cannot pop"
```

The test uses the real message; copying the handout string verbatim makes the
test fail.

## Note on the build

The starter `pom.xml` declared only `junit-jupiter-api`. Running `mvn test` with
that alone reports "No tests to run", because the JUnit 5 **engine** is missing.
Added to `pom.xml`:

- `junit-jupiter-engine` (test scope) - the runtime that executes the tests;
- `maven-surefire-plugin` 3.2.5 - the Surefire version that discovers JUnit 5 tests;
- `maven.compiler.source/target = 1.8` - required for the lambdas used by `assertThrows`.

The original file is kept as `pom.xml.bak`.
