## Mockspresso v1 -> v2 Translation Guide

This guide is intended to ease the transition from Mockspresso v1 -> v2. We will mainly focus on the kotlin extensions in v1, since java tests are not supported by v2.

This is not a replacement for checking the [Api KDocs]({{ site.docsDir }}/api/com.episode6.mockspresso2/index.html)

#### Entry-point
While we rarely interacted with the result of `MockspressoBuilder.build()` in v1, in v2 we build delegated properties from it. Because of this we recommending shortening the name of your `val mockspresso`. We've been using `mxo`.

```diff
-val mockspresso = BuildMockspresso.with().build()
+val mxo = MockspressoBuilder().build() // function and val name both changed
```

#### Builder methods
The names methods on the builder have changed slightly...

```diff
 val mxo = MockspressoBuilder()
 
-  .injector(InjectionConfig)
+  .makeRealObjectsWith(RealObjectMaker)
 
-  .mocker(MockerConfig)
+  .makeFallbackObjectsWith(FallbackObjectMaker)
 
-  .specialObjectMaker(SpecialObjectMaker)
+  .addDynamicObjectMaker(DynamicObjectMaker)
 
-  .dependencyOf { Thing() }
+  .dependency { Thing() }
 
-  .realClassOf<Type>()
+  .realInstance<Type>()
 
-  .realImplOf<BIND, IMPL>()
+  .realImplementation<BIND, IMPL>()
 
   .build()
```

#### JUnit4 Rule
Mockspresso1 had junit4 rule support baked in. Mockspresso2 includes junit4 rule support in the [`plugins-junit4`]({{ site.docsDir }}/plugins-junit4/com.episode6.mockspresso2.plugins.junit4/index.html#918458742%2FFunctions%2F991293141) module instead.

```diff
-@get:Rule val mockspresso = BuildMockspresso.with().buildRule()
+@get:Rule val mxo = MockspressoBuilder().build().junitRule()
```

#### Delegated Properties
Mockspresso2 replaces annotation processing with delegated properties...

```diff
 // define real objects
 
-@RealObject lateinit var coffeeMaker: CoffeeMaker
+val coffeeMaker: CoffeeMaker by mxo.realInstance()
 
-@RealObject @QualifierAnnotation lateinit var coffeeMaker: CoffeeMaker
+val coffeeMaker: CoffeeMaker by mxo.realInstance(createAnnotation<QualifierAnnotation>())
 
-@RealObject(implementation = CoffeeMakerImpl::class) lateinit var coffeeMaker: CoffeeMaker
+val coffeeMaker by mxo.realImplementation<CoffeeMaker, CoffeeMakerImpl>()
 
 // add mocks (requires one of the mock support modules)
 
-@Mock lateinit var mockThing: Thing 
+val mockThing: Thing by mxo.mock()
 
-@Dependency val mockThing: Thing = mock()
+val mockThing: Thing by mxo.mock()

 // add other dependencies
 
-@Dependency val thing: Thing = Thing()
+val thing: Thing by mxo.dependency { Thing() }
 
-@Dependency(bindAs = Thing::class) val fakeThing = FakeThing()
+val fakeThing by mxo.fake<Thing, FakeThing> { FakeThing() }
```
**Note:** `createAnnotation()` is part of `kotlin-reflect`, not mockspresso
