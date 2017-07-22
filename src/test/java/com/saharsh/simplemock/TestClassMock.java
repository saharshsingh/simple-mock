package com.saharsh.simplemock;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestClassMock {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestClassMock.class);

    private Mock<ClassToMock> mock;

    @Before
    public void setup() {
        mock = SimpleMock.mockType(ClassToMock.class);
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
    public void test_mocking_method_declared_in_super_class() {
        final AnotherClass expected = new AnotherClass();
        mock.setReturnValue(expected, "returnAnotherClass", AnotherClass.class);
        Assert.assertSame(expected, mock.getMocked().returnAnotherClass(new AnotherClass()));
    }

    @Test
    public void test_getLastRequest_for_returning_correct_request() {

        // setup
        mock.getMocked().returnSomeClass(1, "random1");
        mock.getMocked().returnSomeClass(2, "random2");
        mock.getMocked().returnSomeClass(3, "random3");
        mock.getMocked().returnSomeClass(4, "random4");
        mock.getMocked().returnSomeClass(5, "random5");

        // run
        Object[] lastRequest = mock.getLastRequest("returnSomeClass", int.class, String.class);

        // verify
        Assert.assertNotNull(lastRequest);
        Assert.assertEquals(2, lastRequest.length);
        Assert.assertEquals(5, lastRequest[0]);
        Assert.assertEquals("random5", lastRequest[1]);
    }

    @Test
    public void test_getAllCapturedRequests_for_returning_correct_requests() {

        // setup
        mock.getMocked().returnSomeClass(1, "random1");
        mock.getMocked().returnSomeClass(2, "random2");
        mock.getMocked().returnSomeClass(3, "random3");
        mock.getMocked().returnSomeClass(4, "random4");
        mock.getMocked().returnSomeClass(5, "random5");

        // run
        List<Object[]> capturedRequests = mock.getAllCapturedRequests("returnSomeClass", int.class, String.class);

        // verify
        Assert.assertNotNull(capturedRequests);
        Assert.assertEquals(5, capturedRequests.size());
        for (int i = 1; i <= 5; i++) {
            Object[] lastRequest = capturedRequests.get(i - 1);
            Assert.assertNotNull(lastRequest);
            Assert.assertEquals(2, lastRequest.length);
            Assert.assertEquals(i, lastRequest[0]);
            Assert.assertEquals("random" + i, lastRequest[1]);
        }
    }

    @Test
    public void test_gettingCapturedRequests_when_method_not_yet_called() {
        mock.getMocked().returnSomeClass();
        Assert.assertNull(mock.getLastRequest("returnSomeClass", int.class, String.class));
        Assert.assertEquals(0, mock.getAllCapturedRequests("returnSomeClass", int.class, String.class).size());
    }

    @Test
    public void test_gettingCapturedRequests_after_clearCapturedRequests_called() {

        // setup
        mock.getMocked().returnSomeClass(1, "random1");
        mock.getMocked().returnSomeClass(2, "random2");
        mock.getMocked().returnSomeClass(3, "random3");
        mock.getMocked().returnSomeClass(4, "random4");
        mock.getMocked().returnSomeClass(5, "random5");
        mock.clearCapturedRequests();

        // verify
        Assert.assertNull(mock.getLastRequest("returnSomeClass", int.class, String.class));
        Assert.assertEquals(0, mock.getAllCapturedRequests("returnSomeClass", int.class, String.class).size());
    }

    @Test
    public void test_gettingCapturedRequests_for_specific_method() {

        // setup
        mock.getMocked().returnSomeClass(1, "random1");
        mock.getMocked().returnSomeClass(2, "random2");
        mock.getMocked().returnSomeClass(3, "random3");
        mock.getMocked().returnSomeClass(4, "random4");
        mock.getMocked().returnSomeClass(5, "random5");
        mock.getMocked().returnSomeClass();
        mock.clearCapturedRequests("returnSomeClass", int.class, String.class);

        // verify
        Assert.assertNull(mock.getLastRequest("returnSomeClass", int.class, String.class));
        Assert.assertEquals(0, mock.getAllCapturedRequests("returnSomeClass", int.class, String.class).size());
        Assert.assertNotNull(mock.getLastRequest("returnSomeClass"));
        Assert.assertEquals(1, mock.getAllCapturedRequests("returnSomeClass").size());
    }

    @Test
    public void test_clearCapturedRequests_when_no_requests_yet_made() {
        mock.clearCapturedRequests();
        Assert.assertEquals(0, mock.getAllCapturedRequests("returnSomeClass", int.class, String.class).size());
        mock.clearCapturedRequests("returnSomeClass", int.class, String.class);
        Assert.assertEquals(0, mock.getAllCapturedRequests("returnSomeClass", int.class, String.class).size());
    }

    @Test
    public void test_mocking_non_existent_method() {
        try {
            mock.setReturnValue(new Object(), "doesNotExist");
            Assert.fail("Expected exception");
        } catch (MockException e) {
            LOGGER.info("Caught expected exception: {}", e.getMessage());
        }
    }

    public static class ClassToMock extends SuperClassOfMocked {

        SomeClass returnSomeClass() {
            return new SomeClass();
        }

        SomeClass returnSomeClass(int someInteger, String someString) {
            return new SomeClass();
        }

        SomeClass noArgVersionNotDefined(String argument) {
            return new SomeClass();
        }

        AnotherClass returnAnotherClass(SomeClass someClass) {
            return new AnotherClass();
        }

        AnotherClass returnAnotherClass(SomeClass someClass, boolean someCondition) {
            return new AnotherClass();
        }

    }

    public static class SuperClassOfMocked {

        AnotherClass returnAnotherClass(AnotherClass anotherClass) {
            return anotherClass;
        }
    }

    private static class SomeClass {
    }

    private static class AnotherClass {
    }

}
