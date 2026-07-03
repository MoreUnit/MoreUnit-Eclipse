package org.moreunit.mock.dependencies;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.jupiter.api.Test;
import org.moreunit.core.log.Logger;

public class DependencyInjectionPointStoreTest
{
    @Test
    public void should_store_constructors_setters_and_fields() throws JavaModelException
    {
        DependencyInjectionPointStore store = new DependencyInjectionPointStore(mock(Logger.class));

        IMethod constructor = mock(IMethod.class);
        when(constructor.isConstructor()).thenReturn(true);

        IMethod setter = mock(IMethod.class);
        when(setter.isConstructor()).thenReturn(false);

        IField field = mock(IField.class);

        store.setInjectionPoints(List.of(constructor, setter, field));

        assertEquals(1, store.getConstructors().size());
        assertEquals(1, store.getSetters().size());
        assertEquals(1, store.getFields().size());
    }

    @Test
    public void should_clear_before_inserting_new_members() throws JavaModelException
    {
        DependencyInjectionPointStore store = new DependencyInjectionPointStore(mock(Logger.class));

        IMethod constructor = mock(IMethod.class);
        when(constructor.isConstructor()).thenReturn(true);
        store.setInjectionPoints(List.of(constructor));
        assertTrue(store.getConstructors().size() > 0);

        store.setInjectionPoints(List.of());
        assertTrue(store.getConstructors().isEmpty());
    }
}
