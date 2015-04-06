/*
 * COPYRIGHT (2015) RAYTHEON COMPANY
 * ALL RIGHTS RESERVED, An Unpublished Work
 *
 * This software was developed pursuant to Contract Number (specify contract number)
 * with the US Government.
 *
 * The US Government's rights in and to this copyrighted software
 * are as specified in DFAR 252.227-7014 which is made part
 * of the above contract.
 */
package com.redjetresearch.simplemock;

/**
 * Used within the package to wrap around checked exceptions and convert them
 * into runtime exceptions
 *
 * @author Saharsh Singh
 */
class MockException extends RuntimeException {

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
