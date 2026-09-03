package org.moreunit.mock.wizard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.jupiter.api.Test;

public class DependenciesTreeMemberComparatorTest
{
    private final DependenciesTreeMemberComparator comparator = new DependenciesTreeMemberComparator();

    @Test
    public void methods_should_come_before_fields()
    {
        final IMethod method = mock(IMethod.class);
        final IField field = mock(IField.class);

        assertTrue(comparator.compare(method, field) < 0);
        assertTrue(comparator.compare(field, method) > 0);
    }

    @Test
    public void constructors_should_come_before_setters() throws JavaModelException
    {
        final IMethod constructor = mock(IMethod.class);
        final IMethod setter = mock(IMethod.class);
        when(constructor.isConstructor()).thenReturn(true);
        when(setter.isConstructor()).thenReturn(false);

        assertTrue(comparator.compare(constructor, setter) < 0);
        assertTrue(comparator.compare(setter, constructor) > 0);
    }

    @Test
    public void constructors_with_more_params_should_come_first() throws JavaModelException
    {
        final IMethod manyParams = mock(IMethod.class);
        final IMethod fewParams = mock(IMethod.class);
        when(manyParams.isConstructor()).thenReturn(true);
        when(fewParams.isConstructor()).thenReturn(true);
        when(manyParams.getNumberOfParameters()).thenReturn(3);
        when(fewParams.getNumberOfParameters()).thenReturn(1);

        assertTrue(comparator.compare(manyParams, fewParams) < 0);
        assertTrue(comparator.compare(fewParams, manyParams) > 0);
    }

    @Test
    public void same_type_members_should_be_ordered_by_name()
    {
        final IField a = mock(IField.class);
        final IField b = mock(IField.class);
        when(a.getElementName()).thenReturn("apple");
        when(b.getElementName()).thenReturn("banana");

        assertTrue(comparator.compare(a, b) < 0);
        assertTrue(comparator.compare(b, a) > 0);
    }
}
