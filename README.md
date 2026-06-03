# SingleStore IntelliJ Plugin

IntelliJ IDEA plugin that integrates the [SingleStore language server](https://github.com/singlestore-labs/language-server) to provide SQL intelligence for `.s2db.sql` files.

## Features

- **LSP integration** — activates automatically for any file ending in `.s2db.sql`
- **Transport flexibility** — connects to the language server over TCP or WebSocket; the protocol is inferred from the server address (no separate dropdown needed)
- **Schema-aware completions** — forwards database credentials to the language server on startup so it can introspect your SingleStore engine for table, column, and procedure completions
- **Live configuration updates** — applying settings changes takes effect immediately without restarting the IDE (see [Configuration behaviour](#configuration-behaviour) below)
- **Custom file icon** — `.s2db.sql` files get a SingleStore icon in the Project tree and editor tabs (light and dark theme variants included)

## Requirements

- A JetBrains IDE **version 2024.3 or later** that includes the IntelliJ LSP client. The LSP client ships with all paid JetBrains IDEs; it is **not** available in IntelliJ IDEA Community edition.

  Confirmed compatible IDEs:
  - IntelliJ IDEA Ultimate
  - WebStorm
  - PyCharm Professional
  - GoLand
  - CLion
  - Rider
  - DataGrip
  - RubyMine

- A running [SingleStore language server](https://github.com/singlestore-labs/language-server) instance accessible from your machine

## Configuration

Open **Settings → Tools → SingleStore Language Server**.

### Server address

The protocol is inferred automatically from the address format:

| Format | Protocol |
|---|---|
| `host:port` (e.g. `127.0.0.1:8080`) | TCP |
| `ws://host:port/path` | WebSocket |
| `wss://host:port/path` | WebSocket over TLS |

### Database credentials

These are forwarded to the language server as `initializationOptions.database` so it can connect to your SingleStore engine and provide schema-aware completions:

| Field | Description |
|---|---|
| **Database host** | Hostname or IP of the SingleStore engine |
| **Database port** | MySQL-protocol port (default `3306`) |
| **Database username** | User with at least read access to the target schemas |
| **Database password** | Password for the database user |
| **Database name** | Default database (optional) |
| **Use SSL** | Enable TLS for the database connection |

> If no database credentials are provided, or the connection fails, the language server still starts and provides syntax highlighting and grammar-based completions — only schema-aware features (table/column completions) will be unavailable.

## Configuration behaviour

Changes applied via **Settings → Tools → SingleStore Language Server** take effect immediately for all open projects without restarting the IDE:

- **Server address changed** — the plugin closes the current connection, creates a new proxy to the new address, and restarts the LSP session. Open `.s2db.sql` files reconnect automatically.
- **Database credentials changed** — the plugin sends a `workspace/didChangeConfiguration` notification to the running language server with the updated `database` block. The server drops its existing database connection, opens a fresh one, and restarts the schema cache updater — no LSP session interruption occurs.

## Development

### Prerequisites

- A JDK with `javac` (full JDK, not just a JRE). The build uses [Temurin](https://adoptium.net/) 17+ by default.

### Run in a sandbox IDE

```bash
./gradlew runIde -Dorg.gradle.java.home=/path/to/jdk
```

### Run tests

```bash
./gradlew test -Dorg.gradle.java.home=/path/to/jdk
```

### Build distributable ZIP

```bash
./gradlew buildPlugin -Dorg.gradle.java.home=/path/to/jdk
```

## Release

Push a version tag to trigger the [release workflow](.github/workflows/release.yml):

```bash
git tag v1.2.3
git push origin v1.2.3
```

The workflow builds the plugin, signs it, publishes it to the [JetBrains Marketplace](https://plugins.jetbrains.com/), and creates a GitHub Release with the ZIP attached.

Required repository secrets: `PUBLISH_TOKEN`, `CERTIFICATE_CHAIN`, `PRIVATE_KEY`, `PRIVATE_KEY_PASSWORD`.
