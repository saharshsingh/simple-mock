package com.redjetresearch.simplemock;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestInterfaceMock {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(TestInterfaceMock.class);

	private Mock<InterfaceToMock> mock;

	@Before
	public void setup() {
		mock = Mocker.mock(InterfaceToMock.class);
		Assert.assertNotNull(mock);
	}

	@Test
	public void test_setResponse_for_simple_case() {
		SomeClass expectedResponse = new SomeClass();
		mock.setResponse(expectedResponse, "returnSomeClass");

		SomeClass response = mock.getMocked().returnSomeClass();

		Assert.assertSame(expectedResponse, response);
	}

	@Test
	public void test_setResponse_when_specific_argument_types_specified() {
		SomeClass expectedNoArgResponse = new SomeClass();
		SomeClass expectedArgsResponse = new SomeClass();
		mock.setResponse(expectedNoArgResponse, "returnSomeClass");
		mock.setResponse(expectedArgsResponse, "returnSomeClass", int.class,
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
		mock.setResponse(expectedResponse, "noArgVersionNotDefined");

		// multiple methods defined
		try {
			mock.setResponse(new AnotherClass(), "returnAnotherClass");
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

	private static interface InterfaceToMock {
		SomeClass returnSomeClass();

		SomeClass returnSomeClass(int someInteger, String someString);

		SomeClass noArgVersionNotDefined(String argument);

		AnotherClass returnAnotherClass(SomeClass someClass);

		AnotherClass returnAnotherClass(SomeClass someClass,
				boolean someCondition);

	}

	private static class SomeClass {
	}

	private static class AnotherClass {
	}

}
