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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used as a wrapper for mocked instances. This wrapper can be used to inject
 * return values and capture arguments passed into specific methods
 *
 * @author Saharsh Singh
 *
 * @param <T>
 *            - type of mocked instance wrapped within an instance of this class
 */
public class Mock<T> {

    private final Map<Method, MethodMock> responses = new HashMap<Method, MethodMock>();
    private final Map<Method, ArrayList<Object[]>> capturedRequests = new HashMap<Method, ArrayList<Object[]>>();
    private T mocked;
    private final Class<T> mockedType;

    /**
     * Shouldn't be a need to instantiate this outside of
     * {@link MockHelper#mock(Class)}
     *
     * @param mockedType
     *            - type of mocked instance wrapped within an instance of this
     *            class. Specified again here since the parameterized <T> type
     *            is erased during compilation
     */
    Mock(Class<T> mockedType) {
        this.mockedType = mockedType;
    }

    /**
     * Use this to specify a return value for a specific method. By default,
     * mocked methods will return 'null'.
     * <p>
     * NOTE: Calling this method will overwrite any previous
     * {@link Mock#setReturnValue(Object, String, Class...)} and
     * {@link Mock#setMockImplmentation(MethodMock, String, Class...)} calls on
     * this instance
     *
     * @param returnValue
     *            - value to be returned when the mocked method is called
     * @param methodName
     *            - name of the instance method to be mocked
     * @param argumentTypes
     *            - leave this blank if only one method by the specified name
     *            exists, or if return value for the no-arg method is being
     *            mocked. Otherwise list the argument types in order they are
     *            specified for the method of interest.
     */
    public void setReturnValue(final Object returnValue, String methodName,
            Class<?>... argumentTypes) {
        setMockImplmentation(new MethodMock() {
            @Override
            public Object runMockImplementation(Object[] args) {
                return returnValue;
            }
        }, methodName, argumentTypes);
    }

    /**
     * A lot more flexible than
     * {@link Mock#setReturnValue(Object, String, Class...)}, this method allows
     * you to define a mock implementation of the instance method of interest
     * using the {@link MethodMock} interface
     * <p>
     * NOTE: Calling this method will overwrite any previous
     * {@link Mock#setReturnValue(Object, String, Class...)} and
     * {@link Mock#setMockImplmentation(MethodMock, String, Class...)} calls on
     * this instance
     *
     * @param mockImpl
     *            new implementation of the method
     * @param methodName
     *            name of the instance method to be mocked
     * @param argumentTypes
     *            leave this blank if only one method by the specified name
     *            exists, or if return value for the no-arg method is being
     *            mocked. Otherwise list the argument types in order they are
     *            specified for the method of interest.
     */
    public void setMockImplmentation(MethodMock mockImpl, String methodName,
            Class<?>... argumentTypes) {
        Method method = findMethod(methodName, argumentTypes);
        responses.put(method, mockImpl);
    }

    /**
     * @param methodName
     *            - name of the instance method
     * @param argumentTypes
     *            - leave this blank if only one method by the specified name
     *            exists, or if the method of interest is a 'no-arg method'.
     *            Otherwise list the argument types in order they are specified
     *            for the method of interest.
     * @return the arguments passed in during the last time the specified method
     *         was invoked
     */
    public Object[] getLastRequest(String methodName, Class<?>... argumentTypes) {
        Method method = findMethod(methodName, argumentTypes);
        List<Object[]> requests = capturedRequests.get(method);
        if (requests == null || requests.size() < 1) {
            return new Object[] {};
        }
        return requests.get(requests.size() - 1);
    }

    /**
     * @param methodName
     *            - name of the instance method
     * @param argumentTypes
     *            - leave this blank if only one method by the specified name
     *            exists, or if the method of interest is a 'no-arg method'.
     *            Otherwise list the argument types in order they are specified
     *            for the method of interest.
     * @return the arguments passed in during each invocation of the specified
     *         method via the mocked instance
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> getAllCapturedRequests(String methodName,
            Class<?>... argumentTypes) {
        Method method = findMethod(methodName, argumentTypes);
        ArrayList<Object[]> requests = capturedRequests.get(method);
        if (requests == null) {
            return new ArrayList<Object[]>();
        }
        return (List<Object[]>) requests.clone();
    }

    /** Clear all previously captured requests */
    public void clearCapturedRequests() {
        capturedRequests.clear();
    }

    /**
     * Used by proxies to invoke methods on the mocked instance
     *
     * @param method
     *            - method to invoke
     * @param args
     *            - arguments to pass into the method during invocation
     * @return result of invocation. 'null' if a return value hasn't been set
     *         using {@link Mock#setReturnValue(Object, String, Class...)}
     */
    Object runMethod(Method method, Object[] args) {

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
        MethodMock mockImpl = responses.get(method);
        if (mockImpl == null) {
            return null;
        }
        return mockImpl.runMockImplementation(args);
    }

    /** @return the mocked instance */
    public T getMocked() {
        return mocked;
    }

    /**
     * @param mocked
     *            the mocked instance
     */
    void setMocked(T mocked) {
        this.mocked = mocked;
    }

    // find method from mocked type given name and argument types
    private Method findMethod(String methodName, Class<?>... argumentTypes) {

        if (argumentTypes.length > 0) {
            try {
                return mockedType.getDeclaredMethod(methodName, argumentTypes);
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
}