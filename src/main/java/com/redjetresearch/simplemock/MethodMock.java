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
 * Used to specify mock implementation for a method
 *
 * @author Saharsh Singh
 */
public interface MethodMock {
	Object runMockImplementation(Object[] args);
}
