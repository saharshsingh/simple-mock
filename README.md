# simple-mock
Very lightweight Java mocking library built for typical unit testing. Having used various mocking frameworks, I always found myself using very little of the entire framework, while still feeling like I was learning a whole new language. The inspiration behind this library is to keep mocking of dependencies in Java unit testing simple, intuitive, and unopinionated. 

## Features

Simple Mock is primarily used to create mock implementations of interfaces and mock subclasses for classes with public, no arg constructors. Interface mocks are pure [Java proxies](https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Proxy.html), while classes are mocked using the famous [cglib library](https://github.com/cglib/cglib). The Simple Mock libarary exposes a very simple API via the `com.saharsh.simplemock.SimpleMock` class. All the features of this library are exposed as static methods in this class. The library allows you to:

* Create mock implementations of classes and interfaces
* Override implementation of methods in mocked classes and interfaces easily and on the fly.
* Verify invocations of methods in mocked types, including order in which they occurred and arguments that were passed.
* Override the value of any field, instance or static, of any class, regardless of actual visibility and mutability.

## Example

In this simple example, we will test the following class:

```java
public class SomeClass {

    private final AnotherClass someDependency;
    private String argToSendToDependency = "Some text";

    public SomeClass(AnotherClass someDependency) {
        this.someDependency = someDependency;
    }

    public int returnWhatDependencyReturns() {
        return someDependency.returnSomeNumber(argToSendToDependency);
    }

}
```

This class depends on the following class:

```java
public class AnotherClass {

    public static final int NUMBER_TO_RETURN = 5;

    public int returnSomeNumber(String someArg) {
        return NUMBER_TO_RETURN;
    }
}
```
We can write a Simple Mock based test as follows:

```java
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.saharsh.simplemock.MethodMock;
import com.saharsh.simplemock.Mock;
import com.saharsh.simplemock.SimpleMock;

public class TestSomeClass {

    private SomeClass classUnderTest;
    private Mock<AnotherClass> someDependency;

    @Before
    public void setup() {

        // Create a mock implementation of the dependency
        someDependency = SimpleMock.mockType(AnotherClass.class);

        // provide the mocked implementation to class under test
        classUnderTest = new SomeClass(someDependency.getMocked());
    }

    @Test
    public void test_returnsWhatDependencyReturns_for_returning_correct_value() {

        // override the value of a hidden variable
        SimpleMock.mockField(classUnderTest, "argToSendToDependency", "Overridden text");

        // Provide custom mock implementation for a method on the fly
        someDependency.setMockImplmentation(new MethodMock() {
            @Override
            public Object runMockImplementation(Object[] args) {
                return 100;
            }
        }, "returnSomeNumber", String.class);
        Assert.assertEquals(100, classUnderTest.returnWhatDependencyReturns());

        // In simple cases, you can just provide a mock return value for a method
        someDependency.setReturnValue(500, "returnSomeNumber", String.class);
        Assert.assertEquals(500, classUnderTest.returnWhatDependencyReturns());

        // Verify previous invocation of mocked methods
        Object[] argsPassed = someDependency.getLastRequest("returnSomeNumber", String.class);
        Assert.assertEquals(1, argsPassed.length);
        Assert.assertEquals("Overridden text", argsPassed[0]);
    }
}
```
