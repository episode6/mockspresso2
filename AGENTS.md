# Agent Guidelines for mockspresso2

## Validating the Project

Run checks and docs generation:

```bash
./gradlew check dokkaGenerateHtml
```

This command:
- Compiles all modules
- Runs all tests across every subproject
- Generates Dokka HTML documentation

A passing build requires exit code 0 with no test failures.

## Project Structure

This is a Kotlin Multiplatform project targeting JVM only. Key modules:

- `api/` — public API (MockspressoBuilder, MockspressoProperties, etc.)
- `core/` — internal implementation
- `reflect/` — reflection utilities and type tokens
- `plugins/core/` — core plugins (real object maker, etc.)
- `plugins/junit4/`, `plugins/junit5/` — JUnit test runner integrations
- `plugins/mockito/`, `plugins/mockito-factories/` — Mockito support
- `plugins/mockk/` — MockK support
- `plugins/dagger2/`, `plugins/javax-inject/` — injection framework support

## Build Configuration

- **Kotlin**: 2.3.21 with explicit API mode — all public declarations require `public` modifier
- **JVM target**: 17
- **Gradle**: 9.5.1
- **Docs**: Dokka 2.x

## Key Constraints

- **JVM only**: Do not add non-JVM targets
- **Explicit API**: Every public function, class, interface, and typealias must have an explicit `public` modifier
- **assertk**: Use `assertFailure { }` not `assertThat { }.isFailure()` (API changed in 0.28.x)
- **Mockito inline**: Tests in `plugins-mockito` require `-Dnet.bytebuddy.experimental=true` JVM arg because Mockito 4.x byte-buddy only supports up to Java 20
- **Gradle 9**: `useJUnitPlatform()` test modules must declare `testRuntimeOnly("org.junit.platform:junit-platform-launcher")` explicitly

## Skills

See `.agents/` for available skills:
- `.agents/release-branch-skill/` — cut/create a new release branch (e.g. "cut a release branch"); also registered under `.claude/skills/` for auto-trigger
- `.agents/ship-release-skill/` — ship a release
