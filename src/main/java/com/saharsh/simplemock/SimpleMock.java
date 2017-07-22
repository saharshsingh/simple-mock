package com.saharsh.simplemock;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * Contains convenience methods that enable mocking of dependencies and
 * protected state.
 *
 * @author Saharsh Singh
 */
public final class SimpleMock {

    // static access only
    private SimpleMock() {}

    /**
     * Creates a mocked instance of the given type. For interfaces a JAVA proxy
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
    public static <T> Mock<T> mockType(Class<T> toMock) {

        // instantiate mock container
        final Mock<T> mock = new Mock<T>(toMock);

        // generate mocked instance
        if (toMock.isInterface()) {

            // for interfaces, create a Java proxy
            InvocationHandler handler = new InvocationHandler() {

                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    return mock.runMethod(method, args);
                }
            };
            mock.setMocked((T) Proxy.newProxyInstance(toMock.getClassLoader(), new Class<?>[] { toMock }, handler));
        } else {

            // otherwise, use CGLib
            MethodInterceptor handler = new MethodInterceptor() {

                @Override
                public Object intercept(Object object, Method method, Object[] args, MethodProxy proxy)
                        throws Throwable {
                    return mock.runMethod(method, args);
                }
            };
            mock.setMocked((T) Enhancer.create(toMock, handler));
        }

        return mock;
    }

    /**
     * Use this method to set the value of any instance field, regardless of
     * visibility.
     *
     * @param target
     *            object containing the instance field.
     * @param fieldName
     *            name of the field
     * @param value
     *            new value that the field should be set to
     */
    public static void mockField(Object target, String fieldName, Object value) {
        try {
            Field field = findField(target.getClass(), fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw MockException.wrap(e);
        }
    }

    /**
     * Use this method to set the value of any static field, regardless of
     * visibility
     * 
     * @param containingClass
     *            class containing the instance field.
     * @param fieldName
     *            name of the field
     * @param value
     *            new value that the field should be set to
     */
    public static void mockStaticField(Class<?> containingClass, String fieldName, Object value) {
        try {
            Field field = findField(containingClass, fieldName);
            field.setAccessible(true);
            field.set(null, value);
        } catch (Exception e) {
            throw MockException.wrap(e);
        }
    }

    /**
     * Get the value of an instance field, regardless of its visibility.
     *
     * @param target
     *            object containing the instance field
     * @param fieldName
     *            name of the field
     * @return value of the field
     */
    public static Object getFieldValue(Object target, String fieldName) {
        try {
            Field field = findField(target.getClass(), fieldName);
            field.setAccessible(true);
            return field.get(target);
        } catch (Exception e) {
            throw MockException.wrap(e);
        }
    }

    /**
     * Same as {@link #getFieldValue(Object, String)}, but attempts to cast the
     * returning value with the specified type
     *
     * @param target
     *            object containing the instance field
     * @param fieldName
     *            name of the instance field
     * @param returnType
     *            anticipated type of the return value
     * @return value of the instance field
     */
    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Object target, String fieldName, Class<T> returnType) {
        try {
            return (T) getFieldValue(target, fieldName);
        } catch (Exception e) {
            throw MockException.wrap(e);
        }
    }

    /**
     * Get the value of a static field, regardless of its visibility.
     *
     * @param containingClass
     *            class containing the static field
     * @param fieldName
     *            name of the field
     * @return value of the field
     */
    public static Object getStaticFieldValue(Class<?> containingClass, String fieldName) {
        try {
            Field field = findField(containingClass, fieldName);
            field.setAccessible(true);
            return field.get(null);
        } catch (Exception e) {
            throw MockException.wrap(e);
        }
    }

    /**
     * Same as {@link #getStaticFieldValue(Object, String)}, but attempts to
     * cast the returning value with the specified type
     *
     * @param containingClass
     *            class containing the static field
     * @param fieldName
     *            name of the static field
     * @param returnType
     *            anticipated type of the return value
     * @return value of the static field
     */
    @SuppressWarnings("unchecked")
    public static <T> T getStaticFieldValue(Class<?> containingClass, String fieldName, Class<T> returnType) {
        try {
            return (T) getStaticFieldValue(containingClass, fieldName);
        } catch (Exception e) {
            throw MockException.wrap(e);
        }
    }

    private static Field findField(Class<?> type, String fieldName) throws NoSuchFieldException {
        try {
            return type.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            if (type.getSuperclass() != null) {
                return findField(type.getSuperclass(), fieldName);
            }
            throw e;
        }
    }

}
