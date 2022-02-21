## Mockspresso v1 -> v2 Translation Guide

This guide is intended to ease the transition from Mockspresso v1 -> v2. We will mainly focus on the kotlin extensions in v1, since java tests are not supported by v2.

This is not a replacement for checking the [Api KDocs](dokka/api/com.episode6.mxo2/index.html#616616919%2FClasslikes%2F2089714443)

#### Entry-point
While we rarely interacted with the result of `MockspressoBuilder.build()` in v1, but in v2 we build delegated properties from it. Because of this we recommending shortening the name of your `val mockspresso`

```diff
-val mockspresso = BuildMockspresso().build()
+val mxo = MockspressoBuilder().build() // function and val name have changed
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
 
 // add mocks
 
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