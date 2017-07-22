package com.saharsh.simplemock;

/**
 * Used within the package to wrap around checked exceptions and convert them
 * into runtime exceptions
 *
 * @author Saharsh Singh
 */
class MockException extends RuntimeException {

	private static final long serialVersionUID = 4645180969088358242L;

    private MockException(Throwable cause) {
		super(cause);
	}

    public static MockException wrap(Throwable cause) {
        if (cause instanceof MockException) {
            return (MockException) cause;
        }
        return new MockException(cause);
	}
}
