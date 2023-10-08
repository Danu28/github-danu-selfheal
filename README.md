# SelfHealingDriver

The `SelfHealingDriver` interface is a part of the Self-Healing WebDriver framework. It extends the standard WebDriver interface and provides additional functionality for self-healing capabilities in your automation tests. This is inspired from Helenium-web library here i simplified setup with SelfHealingDriver.setup() method along with few improvements.

## Getting Started

### Prerequisites

Before using the `SelfHealingDriver`, make sure you have the following prerequisites:

- A WebDriver compatible browser driver (e.g., ChromeDriver, GeckoDriver, etc.) installed and configured.
- Java development environment set up on your machine.
  
### Adding Self-Healing WebDriver Dependency

To add the Self-Healing WebDriver dependency to your Maven project, you can include the following dependency in your `pom.xml` file:

```xml
<dependencies>
    <!-- Self-Healing WebDriver -->
    <dependency>
            <groupId>org</groupId>
            <artifactId>github-danu-selfheal</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
    <!-- Other dependencies -->
</dependencies>
```

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
