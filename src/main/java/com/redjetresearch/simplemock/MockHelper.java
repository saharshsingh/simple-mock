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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * Contains convenience methods that enable mocking of dependencies for unit
 * tests
 * 
 * @author Saharsh Singh
 */
public class MockHelper {

	/**
	 * Use this method to set the value of a private instance field
	 *
	 * @param target
	 *            object containing the private instance field
	 * @param fieldName
	 *            name of the private instance field
	 * @param value
	 *            new value that private instance field should be set to
	 */
	public static void setPrivateField(Object target, String fieldName,
			Object value) {
		try {
			Class<?> type = target.getClass();
			Field field = type.getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(target, value);
		} catch (Exception e) {
			throw new MockException(e);
		}
	}

	/**
	 * Get the value of a private instance field. This method can be used when
	 * no public getter exists for a certain instance field
	 *
	 * @param target
	 *            object containing the private instance field
	 * @param fieldName
	 *            name of the private instance field
	 * @return value of the private instance field
	 */
	public static Object getPrivateField(Object target, String fieldName) {
		try {
			Class<?> type = target.getClass();
			Field field = type.getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(target);
		} catch (Exception e) {
			throw new MockException(e);
		}
	}

	/**
	 * Same as {@link #getPrivateField(Object, String)}, but attempts to cast
	 * the returning value with the specified type
	 *
	 * @param target
	 *            object containing the private instance field
	 * @param fieldName
	 *            name of the private instance field
	 * @param returnType
	 *            anticipated type of the return value
	 * @return value of the private instance field
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getPrivateField(Object target, String fieldName,
			Class<T> returnType) {
		try {
			return (T) getPrivateField(target, fieldName);
		} catch (Exception e) {
			throw new MockException(e);
		}
	}

	/**
	 * Creates a mocked instance of the give type. For interfaces a JAVA proxy
	 * is used. For concrete class types, 'Code Generation Library' is used.
	 * <p>
	 * NOTE: For concrete class types, only classes with a 'no-arg' constructor
	 * can be mocked
	 *
	 * @param toMock
	 *            - type to mock
	 * @return a mock container that can be used to inject return values and
	 *         capture arguments for specific methods
	 */
	@SuppressWarnings("unchecked")
	public static <T> Mock<T> mock(Class<T> toMock) {

		// instantiate mock container
		final Mock<T> mock = new Mock<T>(toMock);

		// generate mocked instance
		if (toMock.isInterface()) {

			// for interfaces, create a Java proxy
			InvocationHandler handler = new InvocationHandler() {

				@Override
				public Object invoke(Object proxy, Method method, Object[] args)
						throws Throwable {
					return mock.runMethod(method, args);
				}
			};
			mock.setMocked((T) Proxy.newProxyInstance(toMock.getClassLoader(),
					new Class<?>[] { toMock }, handler));
		} else {

			// otherwise, use CGLib
			MethodInterceptor handler = new MethodInterceptor() {

				@Override
				public Object intercept(Object object, Method method,
						Object[] args, MethodProxy proxy) throws Throwable {
					return mock.runMethod(method, args);
				}
			};
			mock.setMocked((T) Enhancer.create(toMock, handler));
		}

		return mock;
	}

}
