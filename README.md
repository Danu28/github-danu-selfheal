# SelfHealingDriver

The `SelfHealingDriver` interface is a part of the Self-Healing WebDriver framework. It extends the standard WebDriver interface and provides additional functionality for self-healing capabilities in your automation tests. This is inspired from Healenium-web library here i simplified setup with SelfHealingDriver.setup() method along with few improvements.
It will Generate Heal Report that you can see in base path (Heal-output).
[View HTML Report](heal-output/reports/index.html)

## Getting Started

### Prerequisites

Before using the `SelfHealingDriver`, make sure you have the following prerequisites:

- A WebDriver compatible browser driver (e.g., ChromeDriver, GeckoDriver, etc.) installed and configured.
- Java 11+ development environment set up on your machine.
  
### Adding Self-Healing WebDriver Dependency

To add the Self-Healing WebDriver dependency to your Maven project, include the GitHub Packages repository and the dependency in your `pom.xml` file:

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/Danu28/github-danu-selfheal</url>
    </repository>
</repositories>

<dependencies>
    <!-- Self-Healing WebDriver -->
    <dependency>
            <groupId>io.github.danu28</groupId>
            <artifactId>github-danu-selfheal</artifactId>
            <version>2.0.0-SNAPSHOT</version>
        </dependency>
    <!-- Other dependencies -->
</dependencies>
```
> **Note:** **2.0.0-SNAPSHOT breaking** — `groupId` `org`→`io.github.danu28` (see `AUDIT_REPORT.md` H-04). For `1.x` use `org:github-danu-selfheal:1.0-SNAPSHOT`. All `2.x` uses `io.github.danu28`.

### Storage modes

- `storage.mode=file` (default) — per-locator `heal-output/selenium/<page>_<hash>` files, best for large parallel suite (see `STORAGE_ADVICE.md`).
- `storage.mode=h2` — single `heal.db` via H2 `2.2.224` optional dep (`AUTO_SERVER=TRUE`, MVStore). Enable with:
  ```properties
  storage.mode=h2
  heal.db=heal-output/heal.db
  ```
  ```xml
  <dependency><groupId>com.h2database</groupId><artifactId>h2</artifactId><version>2.2.224</version></dependency>
  ```
- `heal.acceptUrl` — configures report `Accept` endpoint (default `http://localhost:8091`), replaced at `setup()` time.

### Setup
To configure the `SelfHealingDriver`, you can use the `setup` method provided by the interface. It sets up the WebDriver with the appropriate settings using `ConfigFactory`. Here's how to use it:

```java
SelfHealingDriver.setup();
```

## Usage

### Creating a Self-Healing Driver

You can create a `SelfHealingDriver` in several ways, depending on your requirements:

**Configuration:**

   To create a self-healing driver with default configuration properties, use the following method:

   ```java
   WebDriver delegate = ...; // Your delegate WebDriver instance like new ChromeDriver();
   SelfHealingDriver selfHealingDriver = SelfHealingDriver.create(delegate);
   ```
## Contributing

Contributions to the Self-Healing WebDriver framework are welcome! If you would like to contribute, please follow these guidelines:

1. Fork the repository.
2. Create a new branch for your feature or bug fix.
3. Make your changes and submit a pull request.
