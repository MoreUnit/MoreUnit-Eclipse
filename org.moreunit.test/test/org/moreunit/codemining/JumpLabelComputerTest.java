package org.moreunit.codemining;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
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

    /**
     * The minings of a compilation unit wait for its labels without holding a
     * thread each: a slow search must not exhaust the common pool of the JVM,
     * which the rest of Eclipse uses (LSP4E waited for it in the UI thread).
     */
    @Test
    public void minings_should_not_hold_threads_of_the_common_pool_while_the_labels_are_computed() throws Exception
    {
        // with a parallelism of 1, the default executor of CompletableFuture is not the common pool
        assumeTrue(ForkJoinPool.getCommonPoolParallelism() > 1, "the common pool is not used");
        // a method of its own, so that no labels of another test are in the cache
        final IMethod foo = context.getPrimaryTypeHandler("com.Foo").addMethod("public int slowToSearch()", "return 0;").get();
        final ICompilationUnit fooUnit = context.getCompilationUnit("com.Foo");
        final CountDownLatch searchDone = new CountDownLatch(1);
        final JumpLabelComputer computer = new JumpLabelComputer(fooUnit, elementsOf(fooUnit), JumpLabelComputer.DEFAULT_MAX_CACHE_AGE)
        {
            @Override
            protected Map<String, String> computeLabels()
            {
                try
                {
                    // a slow search
                    searchDone.await(30, TimeUnit.SECONDS);
                }
                catch (final InterruptedException e)
                {
                    Thread.currentThread().interrupt();
                }
                return super.computeLabels();
            }
        };
        final IDocument document = new Document(fooUnit.getSource());

        final List<JumpCodeMining> minings = new ArrayList<>();
        final List<CompletableFuture<Void>> resolutions = new ArrayList<>();
        try
        {
            for (int i = 0; i < 2 * ForkJoinPool.getCommonPoolParallelism() + 2; i++)
            {
                final JumpCodeMining mining = new JumpCodeMining(foo, document, null, computer);
                minings.add(mining);
                // as the code mining manager resolves a mining
                resolutions.add(mining.resolve(null, new NullProgressMonitor()));
            }

            assertEquals("free", CompletableFuture.supplyAsync(() -> "free").get(10, TimeUnit.SECONDS), "the common pool should run other tasks");
            assertTrue(resolutions.stream().noneMatch(CompletableFuture::isDone), "the labels are not computed yet");
        }
        finally
        {
            searchDone.countDown();
        }
        for (int i = 0; i < minings.size(); i++)
        {
            resolutions.get(i).get(30, TimeUnit.SECONDS);
            assertEquals("", minings.get(i).getLabel());
        }
    }

    /**
     * Each refresh of the minings creates a computer: while a search runs,
     * the computers of the next refreshes wait for it instead of searching
     * again.
     */
    @Test
    public void computers_of_the_same_unit_should_share_a_running_search() throws Exception
    {
        final IMethod foo = context.getPrimaryTypeHandler("com.Foo").addMethod("public int searchedOnce()", "return 0;").get();
        final ICompilationUnit fooUnit = context.getCompilationUnit("com.Foo");
        final CountDownLatch searchDone = new CountDownLatch(1);
        final AtomicInteger searches = new AtomicInteger();
        final List<IJavaElement> elements = elementsOf(fooUnit);
        final List<CompletableFuture<String>> labels = new ArrayList<>();
        try
        {
            for (int refresh = 0; refresh < 3; refresh++)
            {
                labels.add(new JumpLabelComputer(fooUnit, elements, JumpLabelComputer.DEFAULT_MAX_CACHE_AGE)
                {
                    @Override
                    protected Map<String, String> computeLabels()
                    {
                        searches.incrementAndGet();
                        try
                        {
                            searchDone.await(30, TimeUnit.SECONDS);
                        }
                        catch (final InterruptedException e)
                        {
                            Thread.currentThread().interrupt();
                        }
                        return super.computeLabels();
                    }
                }.label(foo));
            }
        }
        finally
        {
            searchDone.countDown();
        }
        for (final CompletableFuture<String> label : labels)
        {
            assertEquals("", label.get(30, TimeUnit.SECONDS));
        }
        assertEquals(1, searches.get());
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

        // the computer that follows another one may be created as soon as this one answers
        for (int i = 0; i < 200; i++)
        {
            final CountingJumpLabelComputer first = new CountingJumpLabelComputer(fooUnit, elementsOf(fooUnit), 0);
            first.labelFor(foo);

            final CountingJumpLabelComputer second = new CountingJumpLabelComputer(fooUnit, elementsOf(fooUnit), 0);
            second.labelFor(foo);

            assertEquals(1, first.computationCount);
            assertEquals(1, second.computationCount);
        }
    }
}
