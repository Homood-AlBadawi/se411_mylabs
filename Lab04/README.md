# Lab 04 — Maven intro (JavaFX + Site plugin)

Group id `psu.se411` · artifact id `lab04` · package `psu.se411.lab04`

## Exercise 1 — Maven project dependencies

| Step | Where |
|---|---|
| 1. Maven project named after the main project | `pom.xml` (`<artifactId>lab04</artifactId>`) |
| 2. Class with a `main`, in a team namespace | `src/main/java/psu/se411/lab04/MainClass.java` |
| 3.a/b JavaFX dependency | `<dependencies>` → `org.openjfx:javafx-controls:23` |
| 3.c JavaFX plugin with the `run` goal | `<build><plugins>` → `javafx-maven-plugin:0.0.8`, `<mainClass>psu.se411.lab04.MainClass</mainClass>` |
| 3. `MainClass extends Application`, `main` → `launch()`, `start(Stage)` | `MainClass.java` |
| 4. Run it | goals: `clean compile javafx:run` |

**Run:** right-click `pom.xml` → Run As → Maven build… → Goals: `clean compile javafx:run` → Run.
A 420×220 window titled *My Project* appears.

Do **not** use Run As → Java Application. That fails with *"JavaFX runtime
components are missing"*, because JavaFX 23 ships as modules that have to be on
the module path — putting them there is precisely what `javafx:run` does.

## Exercise 2 — Maven site plugin

Added to `pom.xml`: `<name>`, `<description>`, `<url>`, `<licenses>`,
`<organization>`, `<developers>`, plus a `<reporting>` section with
`maven-project-info-reports-plugin` (that is the plugin that turns the metadata
above into the pages of the site).

**Generate:** right-click `pom.xml` → Run As → Maven build… → Goals: `clean site`

**Open:** `target/site/index.html` → right-click → Open With → System Editor.

### Optional step 4 — improving the generated site

Already done here: the `<reporting>` section, a real `<description>`, license,
organization and developer entries, so the site has populated *Project
Information*, *Licenses*, *Team* and *Dependencies* pages instead of empty ones.

Two further improvements you can add if you want more:

- A javadoc report — add to `<reporting><plugins>`:
  `org.apache.maven.plugins:maven-javadoc-plugin:3.8.0`.
  (Left out by default because it needs a full JDK, not just a JRE, and fails
  the whole `site` build if javadoc is missing.)
- A custom landing page — create `src/site/markdown/index.md`; its content
  replaces the default "About" page.

## Java version note

`maven.compiler.release` is **21**, not 1.8 like Lab02/Lab03: JavaFX 23
requires Java 21 or newer. Eclipse's bundled JDK 21 satisfies this.
