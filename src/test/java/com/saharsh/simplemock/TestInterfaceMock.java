package com.saharsh.simplemock;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestInterfaceMock {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestInterfaceMock.class);

    private Mock<InterfaceToMock> mock;

    @Before
    public void setup() {
        mock = SimpleMock.mockType(InterfaceToMock.class);
        Assert.assertNotNull(mock);
    }

    @Test
    public void test_setResponse_for_simple_case() {
        SomeClass expectedResponse = new SomeClass();
        mock.setReturnValue(expectedResponse, "returnSomeClass");

        SomeClass response = mock.getMocked().returnSomeClass();

        Assert.assertSame(expectedResponse, response);
    }

    @Test
    public void test_setResponse_when_specific_argument_types_specified() {
        SomeClass expectedNoArgResponse = new SomeClass();
        SomeClass expectedArgsResponse = new SomeClass();
        mock.setReturnValue(expectedNoArgResponse, "returnSomeClass");
        mock.setReturnValue(expectedArgsResponse, "returnSomeClass", int.class, String.class);

        SomeClass noArgResponse = mock.getMocked().returnSomeClass();
        SomeClass argsResponse = mock.getMocked().returnSomeClass(0, "Hello");

        Assert.assertSame(expectedNoArgResponse, noArgResponse);
        Assert.assertSame(expectedArgsResponse, argsResponse);
    }

    @Test
    public void test_setResponse_when_no_zero_arg_method_present_and_no_argument_types_specified() {

        // only one method defined
        try {
            mock.setReturnValue(new SomeClass(), "noArgVersionNotDefined");
            Assert.fail("Expected " + MockException.class.getName() + " to be thrown!");
        } catch (MockException e) {
            LOGGER.info("Captured expected exception: " + e.getClass() + "[" + e.getMessage() + "]");
        }

        // multiple methods defined
        try {
            mock.setReturnValue(new AnotherClass(), "returnAnotherClass");
            Assert.fail("Expected " + MockException.class.getName() + " to be thrown!");
        } catch (MockException e) {
            LOGGER.info("Captured expected exception: " + e.getClass() + "[" + e.getMessage() + "]");
        }

        Assert.assertNull(mock.getMocked().noArgVersionNotDefined("Hello"));
        Assert.assertNull(mock.getMocked().returnAnotherClass(new SomeClass()));
        Assert.assertNull(mock.getMocked().returnAnotherClass(new SomeClass(), true));
    }

    @Test
    public void test_setResponse_when_type_abstract_and_declaring_type_interface() {
        Mock<AbstractImplOfMocked> abstractMock = SimpleMock.mockType(AbstractImplOfMocked.class);
        final SomeClass expected = new SomeClass();
        abstractMock.setReturnValue(expected, "noArgVersionNotDefined", String.class);
        Assert.assertSame(expected, abstractMock.getMocked().noArgVersionNotDefined(""));
    }

    @Test
    public void test_mocking_non_existent_method() {
        try {
            SimpleMock.mockType(AbstractImplOfMocked.class).setReturnValue(new Object(), "doesNotExist");
            Assert.fail("Expected exception");
        } catch (MockException e) {
            LOGGER.info("Caught expected exception: {}", e.getMessage());
        }
    }

    private static interface InterfaceToMock {
        SomeClass returnSomeClass();

        SomeClass returnSomeClass(int someInteger, String someString);

        SomeClass noArgVersionNotDefined(String argument);

        AnotherClass returnAnotherClass(SomeClass someClass);

        AnotherClass returnAnotherClass(SomeClass someClass, boolean someCondition);

    }

    private static interface IntermediateInterface extends InterfaceToMock {
    }

    public static abstract class AbstractImplOfMocked implements IntermediateInterface {
    }

    private static class SomeClass {
    }

    private static class AnotherClass {
    }

}
