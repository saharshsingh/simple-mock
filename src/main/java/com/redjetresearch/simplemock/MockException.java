package com.redjetresearch.simplemock;

public class MockException extends RuntimeException {
	
	private static final long serialVersionUID = 4645180969088358242L;

	MockException(Throwable cause) {
		super(cause);
	}
	
	MockException(String message) {
		super(message);
	}
	
	MockException(String message, Throwable cause) {
		super(message, cause);
	}
}
