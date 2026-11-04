# Lab 09 — Managing an Inventory with Generics and the Strategy Pattern

Package `edu.psu.se411.lab09_inventory` · main class `App` · artifact `lab09_inventory`
**Run:** right-click `pom.xml` → Run As → Maven build… → Goals: `clean package exec:java`

## Structure

```
edu.psu.se411.lab09_inventory
├── App.java                        main program: parts 1, 2 and 3
├── model/
│   ├── Item.java                   abstract: id + name, shared once
│   ├── Book.java                   + authorName
│   └── ElectronicDevice.java       + category
├── inventory/
│   └── Inventory.java              generic container + findItems
└── search/
    ├── SearchStrategy.java         the Strategy interface (functional)
    ├── SearchById.java             SearchStrategy<Item>
    ├── SearchByName.java           SearchStrategy<Item>
    ├── SearchByAuthor.java         SearchStrategy<Book>
    └── SearchByCategory.java       SearchStrategy<ElectronicDevice>
```

Only `App.java` existed in the starter; everything else is new. The starter's
`pom.xml` and `log4j.properties` are unchanged.

## Part 1 — the generic inventory

`Inventory<T>` holds a `List<T>` and offers `addItem`, `removeItem`,
`getAllItems` and `size`. Being generic is the whole point: one class serves
`Inventory<Item>`, `Inventory<Book>` and even `Inventory<String>`, and the
compiler refuses to let a device into a book inventory.

```java
Inventory<Item> catalogue = new Inventory<>();
catalogue.addItem(new Book("B-001", "Concurrency in Java", "Brian Goetz"));
catalogue.addItem(new ElectronicDevice("E-001", "MacBook Pro 14", "Laptop"));
// catalogue.addItem("a loose string");   // compile error — as it should be
```

`getAllItems()` returns an **unmodifiable view**, so callers cannot sidestep the
null check in `addItem`.

`Item` is abstract and holds the id and the name — declared once — with `Book`
and `ElectronicDevice` adding only their own field. That is the code reuse.

## Part 2 — the Strategy pattern

```java
@FunctionalInterface
public interface SearchStrategy<T> {
    boolean matches(T item);
}
```

Searching is behaviour that varies, so it is lifted out of `Inventory` and
packaged as an object. The inventory never grows an `if` per search kind:

```java
public List<T> findItems(SearchStrategy<? super T> strategy) {
    List<T> matches = new ArrayList<>();
    for (T item : items) {
        if (strategy.matches(item)) matches.add(item);
    }
    return matches;
}
```

Adding a sixth way to search means writing one new class. `Inventory` does not
change.

### Why `? super T` and not just `SearchStrategy<T>`

This is the detail worth defending in a viva.

A strategy that can examine **any `Item`** is obviously able to examine a
`Book`. But `SearchStrategy<Item>` is *not* a `SearchStrategy<Book>` — generics
are invariant — so with a plain `SearchStrategy<T>` parameter this would not
compile:

```java
Inventory<Book> library = new Inventory<>();
library.findItems(new SearchByName("Java"));   // SearchByName is SearchStrategy<Item>
```

The lower-bounded wildcard `? super T` permits exactly that, which means
`SearchById` and `SearchByName` are written **once** and work on every
inventory instead of being duplicated per item type.

This is the **"consumer super"** half of PECS: the strategy *consumes* items, so
its type parameter takes `super`.

The reverse is correctly still forbidden: `SearchByAuthor` is a
`SearchStrategy<Book>`, and the compiler will not hand it an `Inventory<Item>`,
because an `ElectronicDevice` has no author. That is the type system doing its
job, not getting in the way.

### Strategy as a lambda

`SearchStrategy` has one abstract method, so it is a functional interface and a
lambda is a strategy:

```java
catalogue.findItems(item -> item.getName().contains("Pro"));

SearchStrategy<Item> idStartsWithB = item -> item.getId().startsWith("B-");
catalogue.findItems(idStartsWithB);
```

`@FunctionalInterface` makes the compiler enforce the single-method rule — add a
second abstract method and the interface itself stops compiling, before any
lambda breaks.

## Part 3 — displaying any inventory

```java
public static void displayInventory(Inventory<?> inventory) {
    for (Object item : inventory.getAllItems()) {
        System.out.println("   " + item);
    }
}
```

`Inventory<?>` is the **unbounded wildcard** — "an inventory of some type I do
not care about". Enough to read and print, because every object has
`toString()`. The price: the compiler knows the elements only as `Object`, so
if this method needed `getId()` it would have to be declared
`Inventory<? extends Item>`.

The demo calls it with both `Inventory<Item>` and `Inventory<String>` to show
that it really does accept any inventory.

## Expected output (abridged)

```
=== Part 1: a generic Inventory<Item> ===
items in the catalogue: 6
removed Logitech MX Master -> 5 left
added it back -> 6

=== Part 2: searching with strategies ===

findItems(new SearchByName("Concurrency in Java"))
   Book               B-001    Concurrency in Java                by Brian Goetz

library.findItems(new SearchByName("Java"))  [Item strategy on a Book inventory]
   Book               B-010    Java Generics and Collections      by Maurice Naftalin
   Book               B-011    Java Puzzlers                      by Joshua Bloch

store.findItems(new SearchByCategory("Laptop"))
   Electronic device  E-010    Dell XPS 15                        category: Laptop
   Electronic device  E-012    ThinkPad X1                        category: Laptop

lambda: items whose name contains "Pro"
   Electronic device  E-001    MacBook Pro 14                     category: Laptop
   Electronic device  E-002    iPhone 17 Pro                      category: Phone

=== Part 3: displayInventory(Inventory<?>) ===
Inventory contents (6 items):
   ...
Inventory contents (2 items):
   USB-C cable
   Charger 65W
```

## Logging

Starter configuration, unchanged: `slf4j-api` + `slf4j-log4j12`, with
`log4j.properties` sending output to **`logs/log.out`** (note: directly under
`logs/`, not `logs/App/log4j/` as in Labs 06–08).

The Console shows the program's `System.out` report; `logger.info` for start and
close goes to that file.

## If the build fails on the compiler release

The starter `pom.xml` sets:

```xml
<release>24</release>
```

Eclipse's embedded Maven runs on its own bundled JVM. If that JVM is older than
24, the build fails immediately with *"release version 24 not supported"*.

Fix: change `24` to `21`. Nothing in this code uses anything newer than Java 8
(generics, wildcards and lambdas), so the level is irrelevant to the lab — and
21 is what every other lab in this repo uses.
