# micro-games

A Paper plugin built with Kotlin (multi-module).

## Modules

- **micro-games-api**: Shared API and interfaces
- **micro-games-plugin**: Bukkit plugin implementation

## Setup

1. On Unix/macOS: `chmod +x gradlew` (make executable, if needed)
2. Build: `./gradlew build` (Unix/macOS) or `gradlew.bat build` (Windows)
3. The plugin JAR is in `micro-games-plugin/build/libs/micro-games.jar`

## Development

- Run `./gradlew shadowJar` to build the shaded JAR
- Place the JAR in your server's `plugins` folder
