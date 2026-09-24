package org.moreunit.codemining;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.jupiter.api.Test;
import org.moreunit.test.context.ContextTestCase;
import org.moreunit.test.context.Preferences;
import org.moreunit.test.context.Project;

@Preferences(testClassNameTemplate = "${srcFile}Test", testSrcFolder = "test")
@Project(mainCls = "com:Foo", testCls = "com:FooTest")
public class JumpLabelComputerTest extends ContextTestCase
{
    private static class CountingJumpLabelComputer extends JumpLabelComputer
    {
        int computationCount;

        CountingJumpLabelComputer(ICompilationUnit compilationUnit, List<IJavaElement> elements, long maxCacheAge)
        {
            super(compilationUnit, elements, maxCacheAge);
        }

        @Override
        protected Map<String, String> computeLabels()
        {
            computationCount++;
            return super.computeLabels();
        }
    }

    private List<IJavaElement> elementsOf(ICompilationUnit compilationUnit) throws Exception
    {
        final List<IJavaElement> elements = new ArrayList<>();
        for (final IType type : compilationUnit.getTypes())
        {
            elements.add(type);
            Collections.addAll(elements, type.getMethods());
        }
        return elements;
    }

    private String labelOf(ICompilationUnit compilationUnit, IJavaElement element) throws Exception
    {
        return new JumpLabelComputer(compilationUnit, elementsOf(compilationUnit)).labelFor(element);
    }

    @Test
    public void labelFor_should_return_jump_label_for_method_and_class_having_a_corresponding_one() throws Exception
    {
        final IMethod foo = context.getPrimaryTypeHandler("com.Foo").addMethod("public int foo()", "return 0;").get();
        context.getPrimaryTypeHandler("com.FooTest").addMethod("@Test\npublic void foo()", "");
        final ICompilationUnit fooUnit = context.getCompilationUnit("com.Foo");

        assertEquals(" Jump to test method", labelOf(fooUnit, foo));
        assertEquals(" Jump to test class", labelOf(fooUnit, fooUnit.getTypes()[0]));
    }

    @Test
    public void labelFor_should_return_empty_label_for_method_without_corresponding_one() throws Exception
    {
        final IMethod foo = context.getPrimaryTypeHandler("com.Foo").addMethod("public int foo()", "return 0;").get();
        final ICompilationUnit fooUnit = context.getCompilationUnit("com.Foo");

        assertEquals("", labelOf(fooUnit, foo));
    }

    @Test
    public void labelFor_should_return_jump_label_for_test_method_having_a_corresponding_one() throws Exception
    {
        context.getPrimaryTypeHandler("com.Foo").addMethod("public int foo()", "return 0;");
        final IMethod fooTest = context.getPrimaryTypeHandler("com.FooTest").addMethod("@Test\npublic void foo()", "").get();
        final ICompilationUnit fooTestUnit = context.getCompilationUnit("com.FooTest");

        assertEquals(" Jump to tested method", labelOf(fooTestUnit, fooTest));
        assertEquals(" Jump to tested class", labelOf(fooTestUnit, fooTestUnit.getTypes()[0]));
    }

    @Test
    public void labels_should_be_computed_only_once_for_all_elements() throws Exception
    {
        final IMethod foo = context.getPrimaryTypeHandler("com.Foo").addMethod("public int foo()", "return 0;").get();
        context.getPrimaryTypeHandler("com.Foo").addMethod("public int bar()", "return 1;").get();
        final ICompilationUnit fooUnit = context.getCompilationUnit("com.Foo");
        final List<IJavaElement> elements = elementsOf(fooUnit);
        final CountingJumpLabelComputer computer = new CountingJumpLabelComputer(fooUnit, elements, JumpLabelComputer.DEFAULT_MAX_CACHE_AGE);

        for(int round = 0; round < 3; round++)
        {
            for (final IJavaElement element : elements)
            {
                computer.labelFor(element);
            }
        }

        assertEquals(1, computer.computationCount);
        assertEquals("", computer.labelFor(foo));
    }

    @Test
    public void labels_should_not_be_computed_again_while_the_structure_is_unchanged() throws Exception
    {
        final IMethod foo = context.getPrimaryTypeHandler("com.Foo").addMethod("public int foo()", "return 0;").get();
        final ICompilationUnit fooUnit = context.getCompilationUnit("com.Foo");

        final CountingJumpLabelComputer first = new CountingJumpLabelComputer(fooUnit, elementsOf(fooUnit), JumpLabelComputer.DEFAULT_MAX_CACHE_AGE);
        first.labelFor(foo);

        final CountingJumpLabelComputer second = new CountingJumpLabelComputer(fooUnit, elementsOf(fooUnit), JumpLabelComputer.DEFAULT_MAX_CACHE_AGE);
        second.labelFor(foo);

        assertEquals(1, first.computationCount);
        assertEquals(0, second.computationCount);
    }

    @Test
    public void labels_should_be_computed_again_when_the_structure_changes() throws Exception
    {
        final IMethod foo = context.getPrimaryTypeHandler("com.Foo").addMethod("public int foo()", "return 0;").get();
        final ICompilationUnit fooUnit = context.getCompilationUnit("com.Foo");

        final CountingJumpLabelComputer first = new CountingJumpLabelComputer(fooUnit, elementsOf(fooUnit), JumpLabelComputer.DEFAULT_MAX_CACHE_AGE);
        first.labelFor(foo);

        final IMethod bar = context.getPrimaryTypeHandler("com.Foo").addMethod("public int bar()", "return 1;").get();
        final CountingJumpLabelComputer second = new CountingJumpLabelComputer(fooUnit, elementsOf(fooUnit), JumpLabelComputer.DEFAULT_MAX_CACHE_AGE);
        second.labelFor(bar);

        assertEquals(1, first.computationCount);
        assertEquals(1, second.computationCount);
    }

    @Test
    public void labels_should_be_computed_again_when_the_corresponding_test_case_changes() throws Exception
    {
        final IMethod foo = context.getPrimaryTypeHandler("com.Foo").addMethod("public int foo()", "return 0;").get();
        final ICompilationUnit fooUnit = context.getCompilationUnit("com.Foo");

        final CountingJumpLabelComputer first = new CountingJumpLabelComputer(fooUnit, elementsOf(fooUnit), JumpLabelComputer.DEFAULT_MAX_CACHE_AGE);
        assertEquals("", first.labelFor(foo));

        context.getPrimaryTypeHandler("com.FooTest").addMethod("@Test\npublic void foo()", "");

        final CountingJumpLabelComputer second = new CountingJumpLabelComputer(fooUnit, elementsOf(fooUnit), JumpLabelComputer.DEFAULT_MAX_CACHE_AGE);
        assertEquals(" Jump to test method", second.labelFor(foo));
        assertEquals(1, second.computationCount);
    }

    @Test
    public void labels_should_be_computed_again_when_they_expire() throws Exception
    {
        final IMethod foo = context.getPrimaryTypeHandler("com.Foo").addMethod("public int foo()", "return 0;").get();
        final ICompilationUnit fooUnit = context.getCompilationUnit("com.Foo");

        final CountingJumpLabelComputer first = new CountingJumpLabelComputer(fooUnit, elementsOf(fooUnit), 0);
        first.labelFor(foo);

        final CountingJumpLabelComputer second = new CountingJumpLabelComputer(fooUnit, elementsOf(fooUnit), 0);
        second.labelFor(foo);

        assertEquals(1, first.computationCount);
        assertEquals(1, second.computationCount);
    }
}
