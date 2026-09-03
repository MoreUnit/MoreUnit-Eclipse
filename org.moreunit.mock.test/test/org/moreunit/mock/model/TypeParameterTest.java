package org.moreunit.mock.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TypeParameterTest
{
    @Test
    public void should_create_regular_type_parameter()
    {
        final TypeParameter p = new TypeParameter("java.lang.String");
        assertEquals("java.lang.String", p.fullyQualifiedClassName);
        assertEquals("", p.wildcardExpression());
        assertTrue(p.hasName());
    }

    @Test
    public void should_create_extending_wildcard()
    {
        final TypeParameter p = TypeParameter.extending("java.util.Set");
        assertEquals("? extends ", p.wildcardExpression());
        assertEquals("java.util.Set", p.fullyQualifiedClassName);
    }

    @Test
    public void should_create_super_wildcard()
    {
        final TypeParameter p = TypeParameter.superOf("java.lang.String");
        assertEquals("? super ", p.wildcardExpression());
        assertEquals("java.lang.String", p.fullyQualifiedClassName);
    }

    @Test
    public void should_create_unbounded_wildcard()
    {
        final TypeParameter p = TypeParameter.wildcard();
        assertEquals("?", p.wildcardExpression());
        assertEquals("", p.fullyQualifiedClassName);
        assertFalse(p.hasName());
    }

    @Test
    public void should_create_from_kind_regular()
    {
        final TypeParameter p = TypeParameter.create(TypeParameter.Kind.REGULAR, "java.lang.String");
        assertEquals("", p.wildcardExpression());
        assertEquals("java.lang.String", p.fullyQualifiedClassName);
    }

    @Test
    public void should_create_from_kind_wildcard_extends()
    {
        final TypeParameter p = TypeParameter.create(TypeParameter.Kind.WILDCARD_EXTENDS, "java.util.Set");
        assertEquals("? extends ", p.wildcardExpression());
    }

    @Test
    public void should_create_from_kind_wildcard_super()
    {
        final TypeParameter p = TypeParameter.create(TypeParameter.Kind.WILDCARD_SUPER, "java.lang.String");
        assertEquals("? super ", p.wildcardExpression());
    }

    @Test
    public void should_create_from_kind_wildcard_unbounded()
    {
        final TypeParameter p = TypeParameter.create(TypeParameter.Kind.WILDARD_UNBOUNDED, "java.lang.String");
        assertEquals("?", p.wildcardExpression());
    }

    @Test
    public void should_add_base_type_annotations()
    {
        final TypeParameter p = TypeParameter.extending("java.util.Set");
        p.withBaseTypeAnnotations("com.foo.NonNull");

        assertEquals(1, p.baseTypeAnnotations.size());
        assertEquals("com.foo.NonNull", p.baseTypeAnnotations.get(0).fullyQualifiedClassName);
    }

    @Test
    public void should_detect_has_name_when_class_name_is_not_empty()
    {
        final TypeParameter p = new TypeParameter("java.lang.String");
        assertTrue(p.hasName());
    }

    @Test
    public void should_detect_no_name_when_class_name_is_empty()
    {
        final TypeParameter p = new TypeParameter("");
        assertFalse(p.hasName());
    }

    @Test
    public void should_be_equal_when_all_fields_match()
    {
        final TypeParameter p1 = TypeParameter.extending("java.util.Set").withBaseTypeAnnotations("com.foo.NonNull");
        final TypeParameter p2 = TypeParameter.extending("java.util.Set").withBaseTypeAnnotations("com.foo.NonNull");

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    public void should_not_be_equal_when_wildcard_expression_differs()
    {
        final TypeParameter p1 = TypeParameter.extending("java.util.Set");
        final TypeParameter p2 = TypeParameter.superOf("java.util.Set");

        assertNotEquals(p1, p2);
    }

    @Test
    public void should_not_be_equal_when_base_type_annotations_differ()
    {
        final TypeParameter p1 = new TypeParameter("java.lang.String").withBaseTypeAnnotations("com.foo.A");
        final TypeParameter p2 = new TypeParameter("java.lang.String").withBaseTypeAnnotations("com.foo.B");

        assertNotEquals(p1, p2);
    }

    @Test
    public void should_include_details_in_toString()
    {
        final TypeParameter p = TypeParameter.extending("java.util.Set");
        final String str = p.toString();

        assertNotNull(str);
        assert(str.contains("? extends "));
        assert(str.contains("java.util.Set"));
    }

    @Test
    public void should_compute_hash_code_even_when_wildcard_expression_and_base_type_annotations_are_null() throws Exception
    {
        // defensive branches: the fields are never null in production, but the
        // null-guarded hash code must still be computed correctly
        final TypeParameter p1 = new TypeParameter("java.lang.String");
        setField(p1, "wildcardExpression", null);
        setField(p1, "baseTypeAnnotations", null);

        final TypeParameter p2 = new TypeParameter("java.lang.String");
        setField(p2, "wildcardExpression", null);
        setField(p2, "baseTypeAnnotations", null);

        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    public void should_not_be_equal_when_null_wildcard_expression_is_compared_with_non_null_one() throws Exception
    {
        final TypeParameter p1 = new TypeParameter("java.util.Set");
        setField(p1, "wildcardExpression", null);

        final TypeParameter p2 = TypeParameter.extending("java.util.Set");

        assertFalse(p1.equals(p2));
        assertFalse(p2.equals(p1));
    }

    @Test
    public void should_not_be_equal_when_null_base_type_annotations_are_compared_with_non_null_ones() throws Exception
    {
        final TypeParameter p1 = new TypeParameter("java.lang.String");
        setField(p1, "baseTypeAnnotations", null);

        final TypeParameter p2 = new TypeParameter("java.lang.String").withBaseTypeAnnotations("com.foo.A");

        assertFalse(p1.equals(p2));
        assertFalse(p2.equals(p1));
    }

    @Test
    public void should_be_equal_when_both_wildcard_expression_and_base_type_annotations_are_null() throws Exception
    {
        final TypeParameter p1 = new TypeParameter("java.util.Set");
        setField(p1, "wildcardExpression", null);
        setField(p1, "baseTypeAnnotations", null);

        final TypeParameter p2 = new TypeParameter("java.util.Set");
        setField(p2, "wildcardExpression", null);
        setField(p2, "baseTypeAnnotations", null);

        assertTrue(p1.equals(p2));
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception
    {
        Class<?> type = target.getClass();
        while (type != null)
        {
            try
            {
                final java.lang.reflect.Field field = type.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(target, value);
                return;
            }
            catch (final NoSuchFieldException e)
            {
                type = type.getSuperclass();
            }
        }
        throw new NoSuchFieldException(fieldName);
    }
}
