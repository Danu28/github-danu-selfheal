# Storage for Large Test Suite — Files vs H2 vs SQLite

**Verdict: Keep `files` as default. Add H2 as *optional* if you need query. Avoid SQLite in this Java lib.**

Winner from `think:1789736926441:x69d` — *Files* 6.8 > H2 5.8 > SQLite 4.3.

## Why this matters at scale

Large suite = 500–5000 tests, 10–50 parallel workers (Surefire forkCount, Grid, Testcontainers), each writes `persistLastValidPath` per `findElement` success + reads `getLastValidPath` on heal. Workload is **write-heavy, sharded by `pageName + locator.hashCode()`**, read-only on failure. No cross-locator transactions needed.

## 7-axis matrix

| Axis | Files (`FileSystemPathStorage` today) | H2 (embedded, pure Java) | SQLite (`sqlite-jdbc`) |
|------|--------------------------------------|---------------------------|--------------------------|
| **Concurrency** | ✅ Per-file shard → no lock, scales linearly to N workers (already fixed `Files.createDirectories` in step2). | ✅ MVStore + MVCC tolerates concurrent writes, single file but row-level. | ❌ Single-writer lock → `SQLITE_BUSY` under parallel Surefire/Grid, needs `WAL` + retry |
| **Perf (10k locators)** | `Files.write` ~0.2ms, no pool, OS page-cache. Slow only if `heal-output` on NFS. | `INSERT` ~0.5ms via connection pool (Hikari), batch OK. | Similar to H2 but JNI crossing adds 20-30% |
| **Native dep** | None | None (pure Java, ~1.2MB) | ❌ Per-OS binary `sqlite-native` (~1.5MB + arch matrix, breaks `aarch64` CI) — reverts pom prune win `6fa55a1` |
| **Query/history** | ❌ `grep` + `data.json` only, no SQL | ✅ `SELECT healedLocator, count(*) GROUP BY failedLocator` for heal trends, `WHERE updatedAt > 7d` | ✅ Same as H2 |
| **CI artifacts** | ✅ `heal-output/**` as artifact, easy `zip`. Many files (inode pressure at 50k locators). | ✅ Single `heal.db` artifact, easy to archive/upload. | ✅ Same single file |
| **Ops/migration** | Zero — delete dir to reset | Flyway `V1__heal_paths` if schema changes | Same + native upgrade |
| **Jar size** | +0KB | +1.2MB `h2:2.2.224 optional` | +1.5MB + natives |

## Recommendation by use-case

- **Default (you now):** **Files** — keep as is. Tuning below makes it large-suite ready without new dep.
- **You need `heal trends` dashboard / cross-run dedup:** Add **H2 optional module** (`selfheal-storage-h2`), keep files as `storage.mode=file` default, `h2` only when `heal.storage=h2` is set. Don't ship SQLite.
- **Never:** SQLite as core — native + lock kills Java parallelism.

## Tuning files for large suite (no code change if you follow)

```properties
# HealConfig/application.properties
basePath=heal-output/selenium         # keep sharded, not one huge dir
reportPath=heal-output/reports
screenshotPath=heal-output/screenshots/
recovery-tries=3
heal-enabled=true
# add if large:
heal.storage=file
heal.cleanup.onStart=true            # ConfigFactory.cleanDirectory(screenshots) already
```

Knobs to add (optional, low risk):
- `Files.createDirectories` already fixed — ensures parallel `mkdirs` race-free.
- Keep `persistLastValidPath` synchronized? At 50 workers, per-file `synchronized` is hotspot → switch to `java.nio.channels.FileLock` or shard lock `ConcurrentHashMap<pageName, Lock>`.
- Add `heal-output` per worker: `basePath=heal-output/${surefire.forkNumber}/selenium` avoids cross-worker `data.json` overwrite in `saveLocatorInfo`.
- Rotate `heal-output` on CI: `git clean -fdx heal-output` before run, archive after.

## If you still want H2 (design, not implemented yet)

```properties
heal.storage=h2
heal.db=heal-output/heal.db
```
```xml
<!-- pom optional, not in main -->
<dependency>
  <groupId>com.h2database</groupId><artifactId>h2</artifactId><version>2.2.224</version><optional>true</optional>
</dependency>
```
Schema sketch:
```sql
CREATE TABLE heal_paths(locatorHash VARCHAR(64) PRIMARY KEY, pageName VARCHAR(192), nodesJson CLOB, updatedAt TIMESTAMP);
CREATE TABLE heal_events(id BIGINT AUTO_INCREMENT, failedType VARCHAR(64), failedValue CLOB, healedType VARCHAR(64), healedValue CLOB, pageName VARCHAR(192), createdAt TIMESTAMP);
```
Keep `PathStorage` interface pluggable — `FileSystemPathStorage` default, `H2PathStorage` behind `storage.mode`.

## Bottom line

- **Large suite, parallel Grid:** files wins today, H2 only if you need SQL history.
- **SQLite:** avoid — same query as H2 but native + lock penalty in Java.
- **Next step if you agree:** I can implement the `storage.mode` pluggable switch + H2 optional module in one plan (no core bloat), or just add per-fork `basePath` sharding (2-line fix).

*Generated: 2026-09-18, audit step2 context `6fa55a1`, think `1789736926441:x69d`*
