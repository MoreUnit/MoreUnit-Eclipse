package org.moreunit.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchPattern;
import org.junit.jupiter.api.Test;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Project;
import org.moreunit.test.workspace.TypeHandler;

public class SearchToolsTest extends ContextTestCase
{
    @Project(mainCls = "AbstractTest")
    @Test
    public void findConcreteSubclasses_should_find_all_concrete_subclasses() throws Exception
    {
        final TypeHandler abstractTypeHandler = context.getPrimaryTypeHandler("AbstractTest");
        abstractTypeHandler.get().getCompilationUnit().getBuffer().setContents("public abstract class AbstractTest {}");
        abstractTypeHandler.get().getCompilationUnit().save(null, true);

        abstractTypeHandler.createSubclass("ConcreteTest");
        abstractTypeHandler.createSubclass("AnotherConcreteTest");
        final TypeHandler anotherAbstractTypeHandler = abstractTypeHandler.createSubclass("AnotherAbstractTest");
        anotherAbstractTypeHandler.get().getCompilationUnit().getBuffer().setContents("public abstract class AnotherAbstractTest extends AbstractTest {}");
        anotherAbstractTypeHandler.get().getCompilationUnit().save(null, true);

        final Collection<IType> concreteSubclasses = SearchTools.findConcreteSubclasses(abstractTypeHandler.get());

        assertEquals(2, concreteSubclasses.size());
    }

    @Project(mainCls = "AbstractTest2")
    @Test
    public void findConcreteSubclasses_should_find_only_concrete_ones() throws Exception
    {
        final TypeHandler abstractTypeHandler = context.getPrimaryTypeHandler("AbstractTest2");
        abstractTypeHandler.get().getCompilationUnit().getBuffer().setContents("public abstract class AbstractTest2 {}");
        abstractTypeHandler.get().getCompilationUnit().save(null, true);

        abstractTypeHandler.createSubclass("ConcreteTest2");

        final Collection<IType> concreteSubclasses = SearchTools.findConcreteSubclasses(abstractTypeHandler.get());

        assertEquals(1, concreteSubclasses.size());
    }

    @Project(mainCls = "ITest")
    @Test
    public void findConcreteSubclasses_should_handle_interfaces() throws Exception
    {
        final TypeHandler interfaceHandler = context.getPrimaryTypeHandler("ITest");
        interfaceHandler.get().getCompilationUnit().getBuffer().setContents("public interface ITest {}");
        interfaceHandler.get().getCompilationUnit().save(null, true);

        interfaceHandler.createSubclass("ConcreteTest3");
        final TypeHandler abstractTypeHandler = interfaceHandler.createSubclass("AbstractTest3");
        abstractTypeHandler.get().getCompilationUnit().getBuffer().setContents("public abstract class AbstractTest3 implements ITest {}");
        abstractTypeHandler.get().getCompilationUnit().save(null, true);

        final Collection<IType> concreteSubclasses = SearchTools.findConcreteSubclasses(interfaceHandler.get());

        assertEquals(1, concreteSubclasses.size());
    }

    @Project(mainCls = "SearchTarget", testCls = "SearchTargetTest")
    @Test
    public void search_should_return_the_types_matching_the_given_pattern() throws Exception
    {
        final IJavaSearchScope scope = SearchEngine.createJavaSearchScope(new org.eclipse.jdt.core.IJavaElement[] { context.getProjectHandler().get() });

        final SearchPattern pattern = SearchPattern.createPattern("SearchTarget*", IJavaSearchConstants.TYPE, IJavaSearchConstants.DECLARATIONS, SearchPattern.R_PATTERN_MATCH);

        final Collection<IType> matches = SearchTools.search(pattern, scope);

        assertEquals(2, matches.size());
        assertTrue(matches.stream().anyMatch(t -> "SearchTarget".equals(t.getElementName())));
        assertTrue(matches.stream().anyMatch(t -> "SearchTargetTest".equals(t.getElementName())));
    }

    @Project(mainCls = "OrTarget")
    @Test
    public void createSearchPattern_should_combine_patterns_with_or() throws Exception
    {
        final Method method = SearchTools.class.getDeclaredMethod("createSearchPattern", Collection.class, int.class, int.class, int.class);
        method.setAccessible(true);

        final SearchPattern pattern = (SearchPattern) method.invoke(null, java.util.List.of("OrTarget", "DoesNotExist"), IJavaSearchConstants.TYPE, IJavaSearchConstants.DECLARATIONS, SearchPattern.R_EXACT_MATCH);

        assertNotNull(pattern);

        final IJavaSearchScope scope = SearchEngine.createJavaSearchScope(new org.eclipse.jdt.core.IJavaElement[] { context.getProjectHandler().get() });
        final Collection<IType> matches = SearchTools.search(pattern, scope);
        assertEquals(1, matches.size());
        assertEquals("OrTarget", matches.iterator().next().getElementName());
    }
}
