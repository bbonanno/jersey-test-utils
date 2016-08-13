# Jersey Test Utils


Provides a set of test classes and helpers to facilitate the testing of applications using Jersey 2 server and/or client

## If you want to contribute
Just be aware that [Project Lombok](https://projectlombok.org/) is used in compile time, so you will need to intall the plug-in in your IDE (and enable Annotation Processing if you are using IntelliJ IDEA)

## How to use

To test a resource your application exposes (Check JerseyServerRuleExampleTest for a full working example):

```java
//Declare the server rule and pass in the resource you want to test, 
//this rule will create a new Jersey 2 test server with the correct setup for each test, don't worry, it's very fast
@Rule
public final JerseyServerRule jerseyServerRule = new JerseyServerRule(resource);

@Test
public void aTest() throws Exception {
    //use Jersey client as you normally do to hit the resource you are testing
    String result = jerseyServerRule.webTarget()
        .path("/some/path")
        .request()
        .get(String.class);

    //assert whatever you want to assert
}
```

To test a resource/dependency your application consumes (Check JerseyClientRuleExampleTest for a full working example):
 
```java
//Define an interface that looks like the resource your app has to consume (iniside the test class is the recommended place)
@Path("path/to")
interface MockDependency {
    @GET
    @Path("what/i/want/{pathParam}")
    String someResource(@PathParam("pathParam") String pathParam, @QueryParam("queryParam") String queryParam);
}

//Get an implementation of that interface, I used mockito, but you could use any mocking tool or you could create your own test stub
private final MockDependency mockDependency = mock(MockDependency.class);

//Declare the client rule and pass in the mock/stub resource you just created, 
//this rule will create a new Jersey 2 test server with the correct setup for each test, don't worry, it's very fast
@Rule
public final JerseyClientRule jerseyClientRule = new JerseyClientRule(mockDependency);

//This would be the class you want to test, the one that consumes the external resource/dependency
//as you can see, that class should depend either on a Jersey 2 client or a Jersey 2 WebTarget and use it to do the proper http calls
private final TestService testObj = new TestService(jerseyClientRule.webTarget());

//As you can see, the test looks like any other normal test, you forget about the http stuff and deal with mocks/stuff
//as usual, but the object methods will be only called if your class does the right http calls
@Test
public void testSomeExternalCall() throws Exception {
    //given
    String param1 = "meh";
    String param2 = "blah";
    String expectedResult = "some data";

    when(mockDependency.someResource(anyString(), anyString())).thenReturn(expectedResult);

    //when
    String result = testObj.loadSomeData(param1, param2);

    //then
    assertThat(result).isEqualTo(expectedResult);
    verify(mockDependency).someResource(param1, param2);
}
```

## License

```
Copyright 2016 Bruno Bonanno

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
