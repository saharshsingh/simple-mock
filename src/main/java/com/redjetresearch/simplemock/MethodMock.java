package com.redjetresearch.simplemock;

/**
 * Used to specify mock implementation for a method
 *
 * @author Saharsh Singh
 */
public interface MethodMock {
	Object runMockImplementation(Object[] args);
}
