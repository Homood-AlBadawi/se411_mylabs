# Lab 07 — Polymorphism in Java

Travel agency booking system · package `edu.psu.se411.lab07` · main class `App`
**Run:** right-click `pom.xml` → Run As → Maven build… → Goals: `clean package exec:java`

## Structure

```
edu.psu.se411.lab07
├── App.java                        main program (test driver + file logging)
├── config/AgencyConfig.java        every rate and limit, in one place
├── exceptions/
│   ├── MissingInformationException.java
│   └── InvalidArgumentException.java
├── model/
│   ├── Booking.java                abstract base — shared state + shared validation
│   ├── FlightBooking.java
│   ├── TrainBooking.java
│   ├── CarRentalBooking.java
│   └── SeatClass.java              enum, each constant carries its own rate
└── service/BookingService.java     computeTotalPrice(Booking) — the polymorphic entry point
```

## How the requirements are met

| Requirement | Where |
|---|---|
| a. Maven project | `pom.xml` |
| b. All classes to support the system | the ten classes above |
| c. Inheritance + polymorphism | `Booking` is abstract with an abstract `computeTotalPrice()`; the three subclasses override it |
| d. Maximise code reuse | the four shared fields, their constructor validation, `toString()`, and the `requireProvided` / `requireInRange` helpers live once in `Booking` |
| e. Main program testing the classes | `App.main` — prices a mixed list, totals it, then exercises all six failure rules |
| f. File logger for start/stop and raised exceptions | SLF4J + log4j12; `logger.info` on start and end, `logger.warn`/`logger.error` on every raised exception |

## The polymorphism

`BookingService.computeTotalPrice(Booking booking)` accepts the **abstract** type
and calls `booking.computeTotalPrice()`. There is no `instanceof`, no cast and
no `switch` on booking type anywhere in the project — the JVM dispatches to the
right override at run time.

```java
for (Booking booking : bookings) {     // one loop
    double price = service.computeTotalPrice(booking);   // one call
}                                       // three different pricing rules
```

Adding a fourth transport mode (a bus, a ferry) means writing one new subclass
of `Booking`. `BookingService` and `App`'s loop do not change.

The same idea appears again in `SeatClass`: each enum constant carries its own
per-kilometre rate, so `TrainBooking` prices a ticket without branching on the
seat class.

## Pricing rules

| Type | Set at creation | Entered later | Total price | Allowed range |
|---|---|---|---|---|
| Flight | base ticket price | luggage weight | `base + weight × EXTRA_LUGGAGE_RATE` | 0–40 kg |
| Train | seat class | distance in km | `distance × seatClass rate` | 1–2000 km |
| Car rental | daily rate | number of days | `dailyRate × days` | 1–30 days |

Fields entered later are **boxed types** (`Double`, `Integer`) that start as
`null`. That is what makes "not provided yet" distinguishable from a real value
of zero — a primitive `double` would silently be `0.0` and the
`MissingInformationException` rule could not be implemented.

## The two custom exceptions

Both are **checked** (`extends Exception`), so the compiler forces callers to
handle them.

- `MissingInformationException` — a value that arrives later is still absent.
  Carries `getFieldName()` so a handler knows what to ask for.
- `InvalidArgumentException` — a value was supplied but is out of range.
  Carries the field, the value and the allowed bounds.

They are separate types on purpose: *"you haven't told me yet"* and *"what you
told me isn't allowed"* need different responses, so they must be
distinguishable in a `catch` block.

## Configuration

`AgencyConfig` holds `EXTRA_LUGGAGE_RATE`, `TRAIN_STANDARD_RATE`,
`TRAIN_FIRST_CLASS_RATE`, `MIN`/`MAX_LUGGAGE_WEIGHT`, `MIN`/`MAX_TRAIN_DISTANCE`,
`MIN`/`MAX_RENTAL_DAYS` and the currency label. Private constructor, so it
cannot be instantiated. Changing a price is a one-line edit in one file.

## Logging

Same setup as Lab 06: `slf4j-api` + `slf4j-log4j12`, configured by
`src/main/resources/log4j.properties`.

**Output goes to `logs/App/log4j/log.out`, not the Eclipse Console.** The
Console shows only Maven's own output plus the program's `System.out` report.

Lower `log4j.rootLogger` from `INFO` to `DEBUG` to see more.

## Expected output

```
=== Pricing every booking through one Booking reference ===
FL-001       Flight[FL-001] Homood AlBadawi to Istanbul on 2...    2437.50 SAR
TR-002       Train[TR-002] Sara Al-Otaibi to Dammam on 2026-...     270.00 SAR
TR-003       Train[TR-003] Khalid Al-Harbi to Qassim on 2026...     157.50 SAR
CR-004       Car rental[CR-004] Noura Al-Zahrani to Abha on ...     960.00 SAR

=== Agency total for the whole mixed list ===
Total: 3825.00 SAR

=== Business rules ===
flight with no luggage weight declared     -> MissingInformationException: ...
flight with 55 kg of luggage               -> InvalidArgumentException: ...
train with no distance set                 -> MissingInformationException: ...
train of 2500 km                           -> InvalidArgumentException: ...
car rental with no duration                -> MissingInformationException: ...
car rental of 45 days                      -> InvalidArgumentException: ...
```

Worth noticing in the first block: TR-002 and TR-003 are both 450 km, but cost
270.00 and 157.50 — same distance, different seat class, and the pricing code
never branches to decide that.

## Note on the handout's annex

The annex's `<mainClass>` still reads `edu.spu.se411.lab06_logging.App` — copied
from Lab 06. This project uses `edu.psu.se411.lab07.App`, and the exec plugin is
version 3.6.3 as the annex specifies.
