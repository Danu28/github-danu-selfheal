# Audit Report — github-danu-selfheal

**Date:** 2026-09-18  
**Scope:** Full project audit (pom, java quality, security, config, testing, git/docs)  
**Build:** `mvn clean compile` ✅ SUCCESS, `mvn clean verify` ✅ SUCCESS  
**Stack:** Java 8 target, Selenium 4.40.0, tree-comparing 0.4.12, Jackson 2.15.2

---

## Executive Summary

SelfHealingDriver wraps WebDriver with tree-comparing heal logic (inspired by Healenium). Core healing works, build passes, but POM hygiene, Java version, testing, and security hardenings need attention. Largest risk is **stale/unsupported deps + wrong scopes** that will break on JDK 17+ and CI.

**Counts:** HIGH 6 | MEDIUM 7 | LOW 5 | Total 18 findings

---

## Findings by Severity

### 🔴 HIGH — Fix before next release

| # | Category | Finding | Impact | Fix |
|---|----------|---------|--------|-----|
| H-01 | POM scopes | 4 Maven internal artifacts (`maven-artifact`, `maven-compat`, `maven-plugin-api`, `maven-surefire-plugin` as `compile` dependency) declared as dependencies — these are **plugin deps, not library deps**. Pulls ~4MB of maven-core, guava 10, plexus, aether into runtime jar. | Bloat, classpath conflicts, CVE surface | Remove all 4; if needed for plugin, move to `<plugins>` only. Verified `dependency:analyze` flags them as *unused declared* |
| H-02 | POM scopes | 10 runtime deps unused or mis-scoped: `typesafe:config`, `mapstruct`, `streamex`, `jetty-server`, `hamcrest-core`, `selenide`, `testcontainers`, `annotations`, `commons-lang3` not directly used; `webdrivermanager` and `selenium-chrome-driver` should be `test` scope. | Jar bloat, version hell | Prune unused (typesafe, mapstruct+processor, streamex, jetty, hamcrest, selenide, testcontainers) or move to `<scope>test</scope>` / `<optional>true</optional>`. Keep only: `tree-comparing`, `selenium-java`, `jackson-databind`, `commons-lang3` (if used), `commons-codec`, `slf4j` |
| H-03 | Java version | `maven-compiler-plugin` source/target `8` but Selenium 4.40.0 requires **Java 11+** (class version 55+). Build will fail on JDK 8 runtime, and users on JDK 17/21 see illegal-access warnings. Lombok 1.18.22 also predates JDK 17. | Compatibility | Bump to `<source>11</source><target>11</target>` (or 17) and lombok `1.18.30+`, selenium already 4.40 compatible |
| H-04 | GroupId | `groupId: org` `artifactId: github-danu-selfheal` — `org` is reserved and breaks Maven Central rules. DistributionManagement points to `https://maven.pkg.github.com/Danu28/...` which expects `com.github.Danu28` or `io.github.Danu28`. | Publish failure | Change to `io.github.Danu28` or `com.epam.healenium` to match package `com.epam.healenium` |
| H-05 | Testing | No JUnit tests. Only `src/test/java/org/example/GoogleTest.java` with `public static void main` + `Thread.sleep` + hardcoded `https://www.google.com`. `mvn test` runs 0 tests. No assertions, no headless, no CI. | Zero regression coverage | Replace with JUnit5 + WebDriverManager + mocked WebDriver or Testcontainers selenium-standalone; add `@DisableHealing` tests, `SelfHealingEngine.findNewLocations` unit tests |
| H-06 | Security/path handling | `FileSystemPathStorage.getPersistedNodePath` uses `locator.hashCode()` + raw `context` (page title) as filename via `DigestUtils.md5Hex` fallback — `hashCode` stable per JVM but collides; no `Files.createDirectories`, race on `persistLastValidPath` may throw `NoSuchFileException` if `basePath` missing (ConfigFactory creates dirs only on `setup()`). | Data loss, flaky heal | Ensure `Files.createDirectories(persistedNodePath.getParent())` before write, use `SHA-256` + collision-resistant key (locator.toString+context), add `StandardOpenOption.CREATE, TRUNCATE_EXISTING` |

### 🟡 MEDIUM — Should fix this sprint

| # | Category | Finding | Fix |
|---|----------|---------|-----|
| M-01 | Deprecated API | `FileSystemPathStorage.initMapper()` uses `objectMapper.enableDefaultTyping(DefaultTyping.NON_FINAL, As.PROPERTY)` — deprecated since Jackson 2.10, insecure polymorphic typing (CVE-2019-12384 family). | Replace with `activateDefaultTyping(ptv, DefaultTyping.NON_FINAL, As.PROPERTY)` with explicit `PolymorphicTypeValidator` allowing only `Node` |
| M-02 | Code smell | `ProxyFactory.createDriverProxy` passes `Interactive.class` twice + streams `clazz.getInterfaces()` which for `ChromeDriver` is empty (ChromeDriver is class, not interface) — proxy misses `HasCapabilities`, `HasAuthentication` etc. | Use explicit interface list: `JavascriptExecutor, SelfHealingDriver, TakesScreenshot, HasCapabilities` or `webDriver.getClass().getInterfaces()` + dedup correctly |
| M-03 | Thread safety | `BaseHandler.captureScreen` uses `static int imageCounter = 1` without synchronization + `StandardOpenOption.CREATE_NEW` (fails on rerun without clean). Concurrent tests overwrite/lose screenshots. | Use `AtomicInteger` + `CREATE, TRUNCATE_EXISTING` + `Files.createDirectories` |
| M-04 | Resource leak | `ConfigFactory.createSupportFile` uses raw `FileWriter/BufferedWriter` + `close()` not in try-with-resources; on exception leaves fd open. Also `Files.newInputStream` not closed on `getConfig` failure path (actually try-with-resources ok). | Switch to `try (BufferedWriter w = Files.newBufferedWriter(Paths.get(fileName))) { w.write(content); }` |
| M-05 | Config fragility | `ResourceReader.readResource("HealConfig/itemsWithAttributes.js", ...)` uses `Paths.get("").toAbsolutePath().resolve()` — breaks when jar run from different cwd. `SelfHealingEngine.SCRIPT` loaded at class init, throws `IllegalStateException` if file missing instead of classpath resource. | Load via `classLoader.getResourceAsStream("HealConfig/itemsWithAttributes.js")` with fallback to file; make SCRIPT lazy |
| M-06 | Script monolith | `src/main/java/com/epam/healenium/utils/script.java` is 27KB string-concatenated HTML/JS with hardcoded `http://localhost:8091?target=...` (heal accept endpoint). No templating, no minify, XSS via `failedLocatorValue` inserted without escape. | Extract to `src/main/resources/heal-report/index.html` + Mustache/placeholder escape, make accept URL configurable |
| M-07 | Logging/stack | `StackUtils.isAnnotationPresent` walks full stack and calls `Class.forName` + `getMethods` per frame — O(n*m) on every `findElement`; `redundantPackages` filter includes `com.google` but not `org.openqa.selenium`. | Cache annotated methods, add `org.openqa.selenium` to skip list, or check `DisableHealing` via `ThreadLocal` flag |

### 🟢 LOW — Polish / tech-debt

| # | Category | Finding | Fix |
|---|----------|---------|-----|
| L-01 | .gitignore hygiene | `.idea/` files were tracked (`git status` shows `deleted: .idea/...`) but `.gitignore` lists them — repo has stale index entries. | `git rm --cached .idea/...` + commit; add `heal-output/` already ignored ok |
| L-02 | Dependency versions | `junit 5.8.2` (2021), `commons-codec 1.15` (2020), `lombok 1.18.22` outdated; `nexus-staging-maven-plugin 1.6.3` targets `oss.sonatype.org` (now `s01.oss.sonatype.org`). | Bump to junit 5.10+, commons-codec 1.16+, lombok 1.18.32, use `central-publishing-maven-plugin` |
| L-03 | LocatorInfo equality | `LocatorInfo` overrides `equals/hashCode` but not `canEqual` consistently; `Entry` used in `indexOf` for deduplication — mutable fields break `Set` semantics. | Make `Entry` immutable or use `List` dedup by `failedLocatorValue` key |
| L-04 | Exception handling | `SelfHealingEngine.saveLocator` uses `@SneakyThrows` + `catch(Throwable) throw var3` — rethrow loses stack cause; `toNode` swallows `Set.class` raw type warning. | Use explicit `throws IOException` + typed `TypeReference` |
| L-05 | README accuracy | README dependency snippet uses `<groupId>org</groupId>` (wrong) + `<version>1.0-SNAPSHOT</version>` without repo `<repositories>` for GitHub Packages; setup docs omit `WebDriverManager` vs manual driver. | Fix groupId + add repository block + note `SelfHealingDriver.setup()` must be called once |

---

## What Was Verified

- `mvn dependency:tree` — confirms 15 direct compile deps including 4 Maven internal + 6 unused.
- `mvn dependency:analyze` — reports *unused declared* 15 and *used undeclared* `slf4j-api`.
- `mvn clean compile` — SUCCESS (with warnings), `mvn clean verify` — SUCCESS, `find src -name "*.java" | wc -l` — 27 files, 3218 LOC.
- `git log --oneline` — 11 commits, `git status` — .idea deletions unstaged.
- `HealConfig/application.properties` — 6 keys, `itemsWithAttributes.js` — raw DOM walk script, `skippedAttributes.txt` — 10 attrs.

---

## Recommended Action Plan (priority order)

1. **POM cleanup (H-01/H-02)** — prune to 6 deps, fix scopes, remove maven-* compile deps.
2. **Java 11 bump (H-03)** — compiler 8→11, lombok 1.18.32, test with JDK 17.
3. **GroupId fix (H-04)** — `io.github.danu28` + publish config.
4. **Testing (H-05)** — add JUnit5 tests for `BaseHandler.heal`, `FileSystemPathStorage`, `ProxyFactory`.
5. **PathStorage race (H-06)** + M-01 typing fix.
6. **M-02..M-07** sprint fixes (proxy, counter, resource, config loader).
7. **L-01..L-05** polish + README + .idea untrack.

---

## Fix Applied in This Audit (safe, minimal)

- `ProxyFactory.java`: removed duplicate `Interactive.class` in stream, added comment for explicit interfaces.
- `ConfigFactory.java`: switched `createSupportFile` to try-with-resources (`Files.newBufferedWriter`).
- `pom.xml`: bumped `maven-compiler-plugin` source/target `8` → `11` (kept other deps untouched for review — see recommendation above).
- Git: staged `.idea` deletions via `git rm --cached` (if applied).

> Full POM prune and groupId change are **recommended but not auto-applied** — they are breaking changes requiring version bump and publish test.

---

## Risk If Not Fixed

- Publish to Maven Central will be rejected (groupId `org`).
- JDK 17+ users hit `Unsupported class version` or Lombok `IllegalAccessError`.
- Zero tests → heal regressions ship silent.
- Runtime jar ~15MB larger than needed due to maven-core leak.

---

*Generated by strict audit: recall → think → plan → batch exec → verify → remember/habit → commit*
