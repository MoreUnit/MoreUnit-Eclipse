package org.moreunit.mock.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CategoryTest
{
    @Test
    public void should_create_category_with_id_and_name()
    {
        Category cat = new Category("mock", "Mocking");
        assertEquals("mock", cat.id());
        assertEquals("Mocking", cat.name());
    }

    @Test
    public void should_be_equal_when_ids_match()
    {
        Category c1 = new Category("mock", "Mocking");
        Category c2 = new Category("mock", "Different Name");
        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    public void should_not_be_equal_when_ids_differ()
    {
        Category c1 = new Category("mock", "Mocking");
        Category c2 = new Category("stub", "Mocking");
        assertNotEquals(c1, c2);
    }

    @Test
    public void should_not_be_equal_to_null()
    {
        Category c = new Category("mock", "Mocking");
        assertNotEquals(null, c);
    }

    @Test
    public void should_not_be_equal_to_non_category()
    {
        Category c = new Category("mock", "Mocking");
        assertNotEquals("mock", c);
    }

    @Test
    public void should_compare_by_name()
    {
        Category c1 = new Category("a", "Alpha");
        Category c2 = new Category("b", "Beta");
        assertEquals(-1, c1.compareTo(c2));
    }

    @Test
    public void should_compare_with_null_names()
    {
        Category c1 = new Category("a", null);
        Category c2 = new Category("b", null);
        assertEquals(0, c1.compareTo(c2));
    }

    @Test
    public void should_include_id_in_toString()
    {
        Category c = new Category("mock", "Mocking");
        String str = c.toString();
        assertNotNull(str);
        assert(str.contains("mock"));
    }

    @Test
    public void should_compute_hash_code_even_when_id_is_null()
    {
        assertEquals(31, new Category(null, "Mocking").hashCode());
    }

    @Test
    public void should_not_be_equal_when_id_is_null_and_other_id_is_not()
    {
        Category c1 = new Category(null, "Mocking");
        Category c2 = new Category("mock", "Mocking");

        assertNotEquals(c1, c2);
        assertNotEquals(c2, c1);
    }

    @Test
    public void should_be_equal_when_both_ids_are_null()
    {
        Category c1 = new Category(null, "Mocking");
        Category c2 = new Category(null, "Other");

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    public void should_compare_names_case_insensitively()
    {
        Category c1 = new Category("a", "beta");
        Category c2 = new Category("b", "ALPHA");

        assertTrue(c1.compareTo(c2) > 0);
        assertTrue(c2.compareTo(c1) < 0);
    }
}
