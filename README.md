# SimpleEconomy API

The **SimpleEconomy API** provides a standardized way for plugins to interact with the **SimpleEconomy** plugin. It allows managing player balances, accounts, and money transfers asynchronously.

All operations return `CompletableFuture` to ensure non-blocking behavior.

## Purpose

The API lets other plugins:

* Retrieve and modify player balances.
* Deposit or withdraw money.
* Transfer funds between players.
* Check if a player has an account or enough balance.

It **does not implement the economy itself**; it only provides an interface to interact with the SimpleEconomy plugin.

## Maven Dependency

```xml
<repositories>
    <repository>
        <id>central</id>
        <url>https://repo.maven.apache.org/maven2/</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>io.github.alzyy</groupId>
        <artifactId>simpleeconomy-api</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

## Gradle Dependency

```gradle
repositories {
    mavenCentral()
}

dependencies {
    implementation 'io.github.alzyy:simpleeconomy-api:1.0.0'
}
```

## Imports

```java
import it.alzy.simpleeconomy.api.EconomyProvider;
import it.alzy.simpleeconomy.api.TransactionResult;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
```

## Core Interface: `EconomyProvider`

| Method                                        | Description                                | Returns                                                    |
| --------------------------------------------- | ------------------------------------------ | ---------------------------------------------------------- |
| `getBalance(UUID uuid)`                       | Retrieves a player’s balance.              | `CompletableFuture<Double>`                                |
| `setBalance(UUID uuid, double amount)`        | Sets a player’s balance.                   | `CompletableFuture<Void>`                                  |
| `deposit(UUID uuid, double amount)`           | Adds money to a player’s balance.          | `CompletableFuture<Void>`                                  |
| `detract(UUID uuid, double amount)`           | Removes money from a player’s balance.     | `CompletableFuture<Boolean>` (false if insufficient funds) |
| `hasAccount(UUID uuid)`                       | Checks if a player has an economy account. | `CompletableFuture<Boolean>`                               |
| `hasEnough(UUID uuid, double amount)`         | Checks if a player has enough money.       | `CompletableFuture<Boolean>`                               |
| `transfer(UUID from, UUID to, double amount)` | Transfers money between players.           | `CompletableFuture<TransactionResult>`                     |

### Notes

* All methods are **asynchronous** to prevent blocking the main server thread.
* `TransactionResult` indicates the outcome of a transfer (success, insufficient funds, etc.).
* UUIDs are used to identify players, compatible with online and offline accounts.

## Example Usage

```java
EconomyProvider provider = SimpleEconomyAPI.getProvider();
UUID playerId = player.getUniqueId();

provider.getBalance(playerId).thenAccept(balance -> {
    System.out.println("Player balance: " + balance);
});
```

