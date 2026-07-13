# ChangeLog

### v2.1.0 - Released 6/21/2026

- Fix `ship-release.py` to parse `### v<VERSION>` changelog headers (was incorrectly looking for `##`)
- Fix broken Dokka KDoc links in `autoFactoryMock` (fully-qualify cross-package references to `autoFactory` extensions)
- Upgrade Kotlin 1.7.10 -> 2.3.21 (explicit API mode; all public declarations now require an explicit `public` modifier)
- Upgrade Gradle wrapper 7.5.1 -> 9.5.1 (lazy task registration, `layout.buildDirectory`, explicit junit-platform-launcher dependency)
- Upgrade Dokka 1.7.10 -> 2.2.0 with the new aggregation model
- Raise JVM target from 1.8 to 17
- Update test dependencies: assertk 0.25 -> 0.28.1 (`assertThat{}.isFailure()` -> `assertFailure{}`), mockk 1.13.3 -> 1.14.11
- Fix Mockito 4.x on JDK 21+ by adding `-Dnet.bytebuddy.experimental=true`
- Modernize CI: checkout@v6, setup-java@v5, gradle/actions/setup-gradle@v6, single macOS runner, and a verify-docs workflow for PRs
- Publish to the Sonatype Central Portal: correct the artifact namespace path, exclude Maven local-repo metadata, and upload the signed bundle as a build artifact

### v2.0.1 - Released 3/9/2023

- Update junit4 rule to ensure teardown is called even when the test fails/throws
- All dependencies are now created when the mockspresso instance is ensured (not just those required by real objects)

### v2.0.0 - Released 12/30/2022

- Update junit5 -> 5.9.1
- Update dagger2 -> 2.44.2
- Update mockito -> 4.11.0
- Update mockk -> 1.13.3
- Build configuration updates

### v2.0.0-rc2 - Released 12/17/2022

- Added `fun MockspressoProperties.addDynamicObjectMaker`

### v2.0.0-rc1 - Released 10/24/2022

- No code changes

### v2.0.0-alpha06 - Released 10/23/2022

- Build system updates (no code changes)

### v2.0.0-alpha05 - Released 3/13/2022

- Renamed package from `com.episode6.mxo2` -> `com.episode6.mockspresso2` for clarity

### v2.0.0-alpha04 - Released 3/6/2022

- Transition from git-flow to trunk based development / releases

### v2.0.0-alpha03 - Released 2/26/2022

- Tweak lambda syntax in `realInstance` / `realImpl` extension functions so receiver is used instead of parameter.

### v2.0.0-alpha02 - Released 2/24/2022

- Remove mention of `Dependencies` and real object interceptors from the standard extension functions in the api. The
  interceptors specifically are a bit too powerful and easy to mis-use / misunderstand. Replace the interceptors with
  simple init blocks. Both the interceptors and dynamic `Dependencies` are available in the interface methods.
- Renamed interface method `realImplementation` to `interceptRealImplementation` to differentiate it from the extension
  functions (which retain their old names), since the intentions of the two lambdas are totally different.

### v2.0.0-alpha01 - Released 2/21/2022

- Complete kotlin-multiplatform rewrite of mockspresso
