# singlestore-intellij-plugin

IntelliJ plugin client for `singlestore-labs/language-server` built with the IntelliJ Platform Gradle Plugin (`org.jetbrains.intellij.platform`).

## Features

- Connects to SingleStore language server over:
  - TCP (`host:port`)
  - WebSocket (`ws://...` or `wss://...`)
- Configurable server address and protocol in Settings.
- Configurable database credentials sent via `initializationOptions.database`:
  - host, port, username, password, database (optional), ssl

## Configuration

Open **Settings | Tools | SingleStore Language Server** and configure:

- **Server protocol**: `TCP` or `WEBSOCKET`
- **Server address**:
  - TCP: `127.0.0.1:8080`
  - WebSocket: `ws://127.0.0.1:8080/`
- **Database credentials** used by the language server for schema-aware completion.

The plugin starts LSP support for `.sql` files and sends initialization options compatible with the SingleStore language server README.
