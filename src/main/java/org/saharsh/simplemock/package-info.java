/**
 * <h2>Description</h2>
 * <p>
 * This is a flat package that contains the entirety of a very lightweight Java
 * mocking library built for typical unit testing. Having used various mocking
 * frameworks, I always found myself using very little of the entire framework,
 * while still feeling like I was learning a whole new language. The inspiration
 * behind this library is to keep mocking of dependencies in Java unit testing
 * simple, intuitive, and unopinionated.
 * </p>
 * 
 * <h2>Features</h2>
 * 
 * <p>
 * Simple Mock is primarily used to create mock implementations of interfaces
 * and mock subclasses for classes with public, no arg constructors. Interface
 * mocks are pure <a href=
 * 'https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/Proxy.html'>Java
 * proxies</a>, while classes are mocked using the famous
 * <a href='https://github.com/cglib/cglib'>cglib library</a>. The Simple Mock
 * libarary exposes a very simple API via the
 * {@link org.saharsh.simplemock.SimpleMock} class. All the features of this
 * library are exposed as static methods in this class. The library allows you
 * to:
 * </p>
 * <ul>
 * <li>Create mock implementations of classes and interfaces</li>
 * <li>Override implementation of methods in mocked classes and interfaces
 * easily and on the fly.</li>
 * <li>Verify invocations of methods in mocked types, including order in which
 * they occurred and arguments that were passed.</li>
 * <li>Override the value of any field, instance or static, of any class,
 * regardless of actual visibility and mutability.</li>
 * </ul>
 * 
 * <h2>Example</h2>
 * <p>
 * In this simple example, we will test the following class:
 * </p>
 * 
 * <pre>
 * public class SomeClass {
 *
 *     private final AnotherClass someDependency;
 *     private String argToSendToDependency = "Some text";
 *
 *     public SomeClass(AnotherClass someDependency) {
 *         this.someDependency = someDependency;
 *     }
 *
 *     public int returnWhatDependencyReturns() {
 *         return someDependency.returnSomeNumber(argToSendToDependency);
 *     }
 * }
 * </pre>
 *
 * <p>
 * This class depends on the following class:
 * </p>
 *
 * <pre>
 * public class AnotherClass {
 *
 *     public static final int NUMBER_TO_RETURN = 5;
 *
 *     public int returnSomeNumber(String someArg) {
 *         return NUMBER_TO_RETURN;
 *     }
 * }
 * </pre>
 *
 * <p>
 * We can write a Simple Mock based test as follows:
 * </p>
 *
 * <pre>
 * import org.junit.Assert;
 * import org.junit.Before;
 * import org.junit.Test;
 * 
 * import com.saharsh.simplemock.MethodMock;
 * import com.saharsh.simplemock.Mock;
 * import com.saharsh.simplemock.SimpleMock;
 *
 * public class TestSomeClass {
 *
 *     private SomeClass classUnderTest;
 *     private Mock&lt;AnotherClass&gt; someDependency;
 *
 *     &#64;Before
 *     public void setup() {
 *
 *         // Create a mock implementation of the dependency
 *         someDependency = SimpleMock.mockType(AnotherClass.class);
 *
 *         // provide the mocked implementation to class under test
 *         classUnderTest = new SomeClass(someDependency.getMocked());
 *     }
 *
 *     &#64;Test
 *     public void test_returnsWhatDependencyReturns_for_returning_correct_value() {
 *
 *         // override the value of a hidden variable
 *         SimpleMock.mockField(classUnderTest, "argToSendToDependency", "Overridden text");
 *
 *         // Provide custom mock implementation for a method on the fly
 *         someDependency.setMockImplmentation(new MethodMock() {
 *             &#64;Override
 *             public Object runMockImplementation(Object[] args) {
 *                 return 100;
 *             }
 *         }, "returnSomeNumber", String.class);
 *         Assert.assertEquals(100, classUnderTest.returnWhatDependencyReturns());
 *
 *         // In simple cases, you can just provide a mock return value for a
 *         // method
 *         someDependency.setReturnValue(500, "returnSomeNumber", String.class);
 *         Assert.assertEquals(500, classUnderTest.returnWhatDependencyReturns());
 *
 *         // Verify previous invocation of mocked methods
 *         Object[] argsPassed = someDependency.getLastRequest("returnSomeNumber", String.class);
 *         Assert.assertEquals(1, argsPassed.length);
 *         Assert.assertEquals("Overridden text", argsPassed[0]);
 *     }
 * }
 * </pre>
 */
package org.saharsh.simplemock;