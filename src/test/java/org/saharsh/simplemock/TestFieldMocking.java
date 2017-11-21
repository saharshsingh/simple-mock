package org.saharsh.simplemock;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestFieldMocking {

    private static final Logger log = LoggerFactory.getLogger(TestFieldMocking.class);

    private SomeClass instance;

    @Before
    public void setup() {
        instance = new SomeClass();
    }

    @Test
    public void test_mocking_private_field() {
        SimpleMock.mockField(instance, "field", "overridden");
        Assert.assertEquals("overridden", SimpleMock.getFieldValue(instance, "field", String.class));
    }

    @Test
    public void test_mocking_default_field() {
        SimpleMock.mockField(instance, "fieldDefaultVisibility", "overridden");
        Assert.assertEquals("overridden", SimpleMock.getFieldValue(instance, "fieldDefaultVisibility", String.class));
    }

    @Test
    public void test_mocking_protected_field() {
        SimpleMock.mockField(instance, "fieldProtectedVisibility", "overridden");
        Assert.assertEquals("overridden", SimpleMock.getFieldValue(instance, "fieldProtectedVisibility", String.class));
    }

    @Test
    public void test_mocking_public_field() {
        SimpleMock.mockField(instance, "fieldPublicVisibility", "overridden");
        Assert.assertEquals("overridden", SimpleMock.getFieldValue(instance, "fieldPublicVisibility", String.class));
    }

    @Test
    public void test_mocking_static_field() {
        SimpleMock.mockStaticField(SomeClass.class, "staticField", "overridden");
        Assert.assertEquals("overridden", SimpleMock.getStaticFieldValue(SomeClass.class, "staticField", String.class));
        SimpleMock.mockField(instance, "staticField", "overridden");
        Assert.assertEquals("overridden", SimpleMock.getFieldValue(instance, "staticField", String.class));
    }

    @Test
    public void test_mocking_private_field_in_super_class() {
        SimpleMock.mockField(instance, "baseField", "overridden");
        Assert.assertEquals("overridden", SimpleMock.getFieldValue(instance, "baseField", String.class));
    }

    @Test
    public void test_mocking_default_field_in_super_class() {
        SimpleMock.mockField(instance, "baseFieldDefaultVisibility", "overridden");
        Assert.assertEquals("overridden",
                SimpleMock.getFieldValue(instance, "baseFieldDefaultVisibility", String.class));
    }

    @Test
    public void test_mocking_protected_field_in_super_class() {
        SimpleMock.mockField(instance, "baseFieldProtectedVisibility", "overridden");
        Assert.assertEquals("overridden",
                SimpleMock.getFieldValue(instance, "baseFieldProtectedVisibility", String.class));
    }

    @Test
    public void test_mocking_public_field_in_super_class() {
        SimpleMock.mockField(instance, "baseFieldPublicVisibility", "overridden");
        Assert.assertEquals("overridden",
                SimpleMock.getFieldValue(instance, "baseFieldPublicVisibility", String.class));
    }

    @Test
    public void test_mocking_static_field_in_super_class() {
        SimpleMock.mockStaticField(SomeClass.class, "baseStatic", "overridden");
        Assert.assertEquals("overridden", SimpleMock.getStaticFieldValue(SomeClass.class, "baseStatic", String.class));
        SimpleMock.mockField(instance, "baseStatic", "overridden");
        Assert.assertEquals("overridden", SimpleMock.getFieldValue(instance, "baseStatic", String.class));
    }

    @Test
    public void test_mocking_non_existent_field() {
        try {
            SimpleMock.mockField(instance, "doesNotExist", "SomeValue");
            Assert.fail("Expected exception");
        } catch (MockException e) {
            log.info("Caught expected exception: {}", e.getMessage());
        }
        try {
            SimpleMock.getFieldValue(instance, "doesNotExist", String.class);
            Assert.fail("Expected exception");
        } catch (MockException e) {
            log.info("Caught expected exception: {}", e.getMessage());
        }
    }

    @Test
    public void test_mocking_non_existent_static_field() {
        try {
            SimpleMock.mockStaticField(SomeClass.class, "doesNotExist", "SomeValue");
            Assert.fail("Expected exception");
        } catch (MockException e) {
            log.info("Caught expected exception: {}", e.getMessage());
        }
        try {
            SimpleMock.getStaticFieldValue(SomeClass.class, "doesNotExist", String.class);
            Assert.fail("Expected exception");
        } catch (MockException e) {
            log.info("Caught expected exception: {}", e.getMessage());
        }
    }

    public static class SomeBaseClass {
        @SuppressWarnings("unused")
        private static String baseStatic = "BaseStaticValue";
        @SuppressWarnings("unused")
        private String baseField = "BaseField";
        String baseFieldDefaultVisibility = "BaseFieldDefaultVisibility";
        protected String baseFieldProtectedVisibility = "BaseFieldProtectedVisibility";
        public final String baseFieldPublicVisibility = "BaseFieldPublicVisibility";
    }

    public static class SomeClass extends SomeBaseClass {
        @SuppressWarnings("unused")
        private static String staticField = "StaticValue";
        @SuppressWarnings("unused")
        private String field = "Field";
        String fieldDefaultVisibility = "FieldDefaultVisibility";
        protected String fieldProtectedVisibility = "FieldProtectedVisibility";
        public final String fieldPublicVisibility = "FieldPublicVisibility";
    }
}
