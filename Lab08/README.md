# Lab 08 — Advanced OO: the Observer Pattern

Package `edu.spu.se411.lab08` · main class `App` · artifact `lab08_observer`
**Run:** right-click `pom.xml` → Run As → Maven build… → Goals: `clean package exec:java`
Takes about 12 seconds — the simulation sleeps 1 second between rounds.

## Structure

```
edu.spu.se411.lab08
├── App.java                        main program: 2 sensors, 2 observers, simulation
├── observer/
│   ├── Observer.java               given by the handout
│   ├── Subject.java                given by the handout
│   └── AbstractSubject.java        register / unregister / notify, written ONCE
├── sensor/
│   ├── Sensor.java                 abstract: name + reading + "set then notify"
│   ├── TemperatureSensor.java
│   └── HumiditySensor.java
└── observers/
    ├── SensorObserver.java         narrows Subject → Sensor, written ONCE
    ├── DashboardObserver.java      prints to the console
    └── LoggerObserver.java         writes to the log file
```

## How the pattern works here

A sensor holds a list of `Observer`. It knows nothing about dashboards or loggers —
only that each entry has an `update` method. `setReading()` is the single place a
sensor's state changes, so it is the single place that calls `notifyObservers()`.

```
                    setReading(24.7)
  TemperatureSensor ───────────────► notifyObservers()
                                          │
                        ┌─────────────────┴─────────────────┐
                        ▼                                   ▼
              DashboardObserver                      LoggerObserver
              prints to console                      writes to log.out
```

One-to-many: two observers, one event, two completely different reactions, and
neither sensor was changed to add either of them.

## Where the code reuse is

Two abstract classes carry everything that would otherwise be duplicated:

| Class | Written once | Inherited by |
|---|---|---|
| `AbstractSubject` | `register`, `unregister`, `notifyObservers`, the observer list, and `clone()` | every subject — here, every sensor |
| `Sensor` | name, reading, and the rule "change state then notify" | `TemperatureSensor`, `HumiditySensor` |
| `SensorObserver` | the `Subject` → `Sensor` narrowing | `DashboardObserver`, `LoggerObserver` |

A new sensor kind is two methods (`getQuantity`, `getUnit`). A new observer kind
is one method (`onReading`).

## Requirement 3 — the clone gets its own observers

`AbstractSubject` implements `Cloneable`. The interesting part is that a plain
`super.clone()` is **not** enough:

```java
@Override
public AbstractSubject clone() throws CloneNotSupportedException {
    AbstractSubject copy = (AbstractSubject) super.clone();
    copy.observers = new ArrayList<>();   // <-- the whole point
    return copy;
}
```

`Object.clone()` is a **shallow** copy: it copies the *reference* to the observer
list, so the original and the copy would share one list. Registering an observer
on the clone would silently register it on the original too. Replacing the list
with a fresh empty one is what makes the clone independent, which is exactly what
the handout asks for.

`Sensor.clone()` overrides it again purely to return `Sensor` instead of
`AbstractSubject` (a **covariant return type**), so callers do not have to cast.

The reading itself is a `double` — a primitive, copied by value — so no deep copy
is needed for it.

Proof in the output:

```
original observers: 1
clone observers   : 0   <-- the clone starts with none

after registering one observer on the clone:
original observers: 1
clone observers   : 1

setting the CLONE's reading   - only the clone's observer reacts
setting the ORIGINAL's reading - the clone's observer stays silent
```

## One design note worth defending

`Observer.update(Subject)` is fixed by the handout, and `Subject` has no way to
expose a reading. This is the "pull" form of the pattern: the observer is told
*that* something changed and then pulls the state it wants. So an observer must
narrow the `Subject` to a `Sensor`.

That narrowing is done **once**, in `SensorObserver.update()`, which is `final`.
Subclasses override `onReading(Sensor)` and never see an `instanceof`. The
alternative — an `instanceof` in every observer — is the copy-paste this avoids.

## What the program does

1. Creates a temperature sensor and a humidity sensor.
2. Creates a dashboard observer and a logger observer, and registers **both on
   both** sensors.
3. Runs the handout's loop: 10 rounds, new random readings each round, 1 second
   apart. Four reactions per round (2 sensors × 2 observers).
4. Unregisters the dashboard from the temperature sensor and sets one more
   reading — it reaches the log file only, showing `unregister` works.
5. Clones the temperature sensor and demonstrates that the clone's observers are
   its own.

## Logging

Same setup as Labs 06 and 07: `slf4j-api` + `slf4j-log4j12`, configured by
`src/main/resources/log4j.properties`.

**The logger observer writes to `logs/App/log4j/log.out`, not the console.** The
Eclipse Console shows the dashboard observer's output and Maven's own; refresh the
project (F5) and open that file to see the logger observer's half.

## Note on the package name

The handout's annex specifies `<mainClass>edu.spu.se411.lab08.App</mainClass>`, so
this project uses `edu.spu.se411` — matching Lab 06, not Lab 07's `edu.psu`.
(`spu` appears to be a long-standing typo in the professor's templates, but the
pom and the package have to agree with each other, and the annex is the
instruction for this lab.)
