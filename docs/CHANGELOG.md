# ChangeLog

### v2.0.0-alpha03-SNAPSHOT - Unreleased


### v2.0.0-alpha02-SNAPSHOT - Unreleased

- Remove mention of `Dependencies` and real object interceptors from the standard extension functions in the api. The
  interceptors specifically are a bit too powerful and easy to mis-use / misunderstand. Replace the interceptors with
  simple init blocks. Both the interceptors and dynamic `Dependencies` are available in the interface methods.
- Renamed interface method `realImplementation` to `interceptRealImplementation` to differentiate it from the extension
  functions (which retain their old names), since the intentions of the two lambdas are totally different.

### v2.0.0-alpha01 - Released 2/21/2022

- Complete kotlin-multiplatform rewrite of mockspresso
