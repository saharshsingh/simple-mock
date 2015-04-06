package com.redjetresearch.simplemock;

import org.junit.Assert;
import org.junit.Before;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestClassMock {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(TestClassMock.class);

	private Mock<ClassToMock> mock;

	@Before
	public void setup() {
		mock = MockHelper.mock(ClassToMock.class);
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
		mock.setReturnValue(expectedArgsResponse, "returnSomeClass", int.class,
				String.class);

		SomeClass noArgResponse = mock.getMocked().returnSomeClass();
		SomeClass argsResponse = mock.getMocked().returnSomeClass(0, "Hello");

		Assert.assertSame(expectedNoArgResponse, noArgResponse);
		Assert.assertSame(expectedArgsResponse, argsResponse);
	}

	@Test
	public void test_setResponse_when_no_zero_arg_method_present_and_no_argument_types_specified() {
		SomeClass expectedResponse = new SomeClass();

		// only one method defined
		mock.setReturnValue(expectedResponse, "noArgVersionNotDefined");

		// multiple methods defined
		try {
			mock.setReturnValue(new AnotherClass(), "returnAnotherClass");
			Assert.fail("Expected " + MockException.class.getName()
					+ " to be thrown!");
		} catch (MockException e) {
			LOGGER.info("Captured expected exception: " + e.getClass() + "["
					+ e.getMessage() + "]");
		}

		Assert.assertSame(expectedResponse, mock.getMocked()
				.noArgVersionNotDefined("Hello"));
		Assert.assertNull(mock.getMocked().returnAnotherClass(new SomeClass()));
		Assert.assertNull(mock.getMocked().returnAnotherClass(new SomeClass(),
				true));
	}

	public static class ClassToMock {
		
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

		AnotherClass returnAnotherClass(SomeClass someClass,
				boolean someCondition) {
			return new AnotherClass();
		}

	}

	private static class SomeClass {
	}

	private static class AnotherClass {
	}

}
