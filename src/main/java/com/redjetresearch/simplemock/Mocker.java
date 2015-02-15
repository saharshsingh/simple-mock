package com.redjetresearch.simplemock;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public final class Mocker {

	@SuppressWarnings("unchecked")
	public static <T> Mock<T> mock(Class<T> toMock) {
		final Mock<T> mock = new Mock<T>(toMock);

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
