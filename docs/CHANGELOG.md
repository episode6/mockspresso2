# ChangeLog

### v2.0.0-alpha04-SNAPSHOT - Unreleased

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
