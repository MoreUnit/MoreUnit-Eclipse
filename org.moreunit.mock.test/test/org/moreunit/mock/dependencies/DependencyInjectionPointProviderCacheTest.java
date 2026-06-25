package org.moreunit.mock.dependencies;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DependencyInjectionPointProviderCacheTest
{
    @Mock
    private DependencyInjectionPointProvider provider;

    @Test
    public void should_cache_constructors_from_provider() throws JavaModelException
    {
        IMethod ctor1 = mock(IMethod.class);
        IMethod ctor2 = mock(IMethod.class);
        when(provider.getConstructors()).thenReturn(Arrays.asList(ctor1, ctor2));

        DependencyInjectionPointProviderCache cache = new DependencyInjectionPointProviderCache(provider);
        Collection<IMethod> result = cache.getConstructors();

        assertEquals(2, result.size());
    }

    @Test
    public void should_cache_setters_from_provider() throws JavaModelException
    {
        IMethod setter1 = mock(IMethod.class);
        when(provider.getSetters()).thenReturn(Collections.singletonList(setter1));

        DependencyInjectionPointProviderCache cache = new DependencyInjectionPointProviderCache(provider);
        Collection<IMethod> result = cache.getSetters();

        assertEquals(1, result.size());
    }

    @Test
    public void should_cache_fields_from_provider() throws JavaModelException
    {
        Field field1 = mock(Field.class);
        when(provider.getFields()).thenReturn(Collections.singletonList(field1));

        DependencyInjectionPointProviderCache cache = new DependencyInjectionPointProviderCache(provider);
        Collection<Field> result = cache.getFields();

        assertEquals(1, result.size());
    }

    @Test
    public void should_not_call_provider_more_than_once() throws JavaModelException
    {
        when(provider.getConstructors()).thenReturn(Collections.emptyList());
        when(provider.getSetters()).thenReturn(Collections.emptyList());
        when(provider.getFields()).thenReturn(Collections.emptyList());

        DependencyInjectionPointProviderCache cache = new DependencyInjectionPointProviderCache(provider);

        // Call multiple times
        cache.getConstructors();
        cache.getConstructors();
        cache.getSetters();
        cache.getFields();

        // Provider is called only once (in constructor)
        org.mockito.Mockito.verify(provider).getConstructors();
        org.mockito.Mockito.verify(provider).getSetters();
        org.mockito.Mockito.verify(provider).getFields();
    }

    @Test
    public void should_rethrow_exception_on_subsequent_calls() throws JavaModelException
    {
        JavaModelException originalException = new JavaModelException(new RuntimeException("test error"), 1);
        when(provider.getConstructors()).thenThrow(originalException);

        DependencyInjectionPointProviderCache cache = new DependencyInjectionPointProviderCache(provider);

        assertThrows(JavaModelException.class, () -> cache.getConstructors());
        assertThrows(JavaModelException.class, () -> cache.getSetters());
        assertThrows(JavaModelException.class, () -> cache.getFields());
    }

    @Test
    public void should_return_empty_collections_when_provider_returns_empty() throws JavaModelException
    {
        when(provider.getConstructors()).thenReturn(Collections.emptyList());
        when(provider.getSetters()).thenReturn(Collections.emptyList());
        when(provider.getFields()).thenReturn(Collections.emptyList());

        DependencyInjectionPointProviderCache cache = new DependencyInjectionPointProviderCache(provider);

        assertEquals(0, cache.getConstructors().size());
        assertEquals(0, cache.getSetters().size());
        assertEquals(0, cache.getFields().size());
    }
}
