package com.redjetresearch.simplemock;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mock<T> {

	private Map<Method, Object> responses = new HashMap<Method, Object>();
	private Map<Method, ArrayList<Object[]>> capturedRequests = new HashMap<Method, ArrayList<Object[]>>();
	private T mocked;
	private final Class<T> mockedType;
	
	Mock(Class<T> mockedType) {
		this.mockedType = mockedType;
	}

	public void setResponse(Object response, String methodName,
			Class<?>... argumentTypes) {
		Method method = findMethod(methodName, argumentTypes);
		setResponse(response, method);
	}

	public Object[] getLastRequest(String methodName, Class<?> argumentTypes) {

		Method method = findMethod(methodName, argumentTypes);
		List<Object[]> requests = capturedRequests.get(method);
		if (requests == null || requests.size() < 1) {
			return new Object[] {};
		}
		return requests.get(requests.size() - 1);
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getAllCapturedRequests(String methodName,
			Class<?> argumentTypes) {
		Method method = findMethod(methodName, argumentTypes);
		ArrayList<Object[]> requests = capturedRequests.get(method);
		if (requests == null) {
			return new ArrayList<Object[]>();
		}
		return (List<Object[]>) requests.clone();
	}
	
	public T getMocked() {
		return mocked;
	}
	
	protected Object runMethod(Method method, Object[] args) {
		
		// capture request
		ArrayList<Object[]> requests;
		synchronized (capturedRequests) {
			requests = capturedRequests.get(method);
			if (requests == null) {
				requests = new ArrayList<Object[]>();
				capturedRequests.put(method, requests);
			}
		}
		requests.add(args);
		
		// return response
		return responses.get(method);
	}

	void setMocked(T mocked) {
		this.mocked = mocked;
	}

	private Method findMethod(String methodName, Class<?>... argumentTypes) {

		if (argumentTypes.length > 0) {
			try {
				return mockedType.getDeclaredMethod(methodName,
						argumentTypes);
			} catch (NoSuchMethodException e) {
				throw new MockException(e);
			}
		}

		Method method = null;
		boolean multipleFound = false;

		for (Method candidateMethod : mockedType.getDeclaredMethods()) {
			if (candidateMethod.getName().equals(methodName)) {
				if (candidateMethod.getParameterTypes().length == 0) {

					// exact match - set right away!!
					return candidateMethod;
				} else if (method == null) {
					method = candidateMethod;
				} else {

					// not throwing exception yet as a no-arg version may exist
					multipleFound = true;
				}
			}
		}

		// check for error
		if (method == null) {
			throw new MockException(new NoSuchMethodException(methodName
					+ " not defined for " + mockedType.getName()));
		} else if (multipleFound) {
			throw new MockException("Multiple methods named " + methodName
					+ " defined in " + mockedType.getName()
					+ ". Each takes one or more arguments!");
		}

		// return
		return method;
	}

	private void setResponse(Object response, Method method) {
		responses.put(method, response);
	}
}
