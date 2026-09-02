package org.moreunit.core.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.moreunit.core.matching.CamelCaseNameTokenizer;
import org.moreunit.core.matching.SeparatorNameTokenizer;
import org.moreunit.core.matching.TestFileNamePattern;

public class FileNamePatternDemoTest
{
    private Display display;
    private Shell shell;

    @BeforeEach
    public void createShell()
    {
        try
        {
            display = Display.getDefault();
        }
        catch (Throwable t)
        {
            display = null;
        }
        assumeTrue(display != null, "No SWT display available");
        shell = new Shell(display);
    }

    @AfterEach
    public void disposeShell()
    {
        if(shell != null && ! shell.isDisposed())
        {
            shell.dispose();
        }
    }

    private FileNamePatternDemo createDemo(TestFileNamePattern pattern, AtomicInteger sizeChanges)
    {
        FileNamePatternDemo demo = new FileNamePatternDemo()
        {
            protected TestFileNamePattern getPattern()
            {
                return pattern;
            }

            protected void sizeChanged()
            {
                sizeChanges.incrementAndGet();
            }
        };
        demo.createContents(shell);
        return demo;
    }

    private Text inputField(FileNamePatternDemo demo)
    {
        try
        {
            Field field = FileNamePatternDemo.class.getDeclaredField("inputField");
            field.setAccessible(true);
            return (Text) field.get(demo);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private void testLinkSelected(FileNamePatternDemo demo)
    {
        Link link = findLink(shell);
        assertNotNull(link, "Demo should contain the 'Test' link");

        link.notifyListeners(SWT.Selection, new Event());
    }

    private Link findLink(Shell shell)
    {
        return findLink(shell, 0);
    }

    private Link findLink(Shell shell, int index)
    {
        java.util.List<Link> links = new java.util.ArrayList<>();
        collectLinks(shell, links);
        return links.size() > index ? links.get(index) : null;
    }

    private void collectLinks(Shell shell, java.util.List<Link> links)
    {
        collectLinks((org.eclipse.swt.widgets.Composite) shell, links);
    }

    private void collectLinks(org.eclipse.swt.widgets.Composite composite, java.util.List<Link> links)
    {
        for (org.eclipse.swt.widgets.Control control : composite.getChildren())
        {
            if(control instanceof Link)
            {
                links.add((Link) control);
            }
            else if(control instanceof org.eclipse.swt.widgets.Composite)
            {
                collectLinks((org.eclipse.swt.widgets.Composite) control, links);
            }
        }
    }

    @Test
    public void should_generate_simple_camelcase_source_file_name() throws Exception
    {
        // given
        TestFileNamePattern pattern = new TestFileNamePattern("${srcFile}Test", new CamelCaseNameTokenizer());

        // then
        assertEquals(FileNamePatternDemo.generateSourceFileName(pattern), "FooBar");
    }

    @Test
    public void should_generate_simple_source_file_name_with_separator() throws Exception
    {
        // given
        TestFileNamePattern pattern = new TestFileNamePattern("${srcFile}_test", new SeparatorNameTokenizer("_"));

        // then
        assertEquals(FileNamePatternDemo.generateSourceFileName(pattern), "foo_bar");
    }

    @Test
    public void should_generate_source_file_name_with_stars_and_groups() throws Exception
    {
        // given
        TestFileNamePattern pattern = new TestFileNamePattern("(bla|bli)*${srcFile}-*(plop|plip)*", new SeparatorNameTokenizer("-"));

        // then
        assertEquals(FileNamePatternDemo.generateSourceFileName(pattern), "foo-bar");
    }

    @Test
    public void should_generate_and_evaluate_source_file_name_when_input_is_empty()
    {
        TestFileNamePattern pattern = new TestFileNamePattern("${srcFile}Test", new CamelCaseNameTokenizer());
        AtomicInteger sizeChanges = new AtomicInteger();
        FileNamePatternDemo demo = createDemo(pattern, sizeChanges);

        testLinkSelected(demo);

        // the demo generated a source file name and displayed the evaluation
        assertEquals("FooBar", inputField(demo).getText());
        assertTrue(sizeChanges.get() > 0, "sizeChanged() should have been called");
    }

    @Test
    public void should_evaluate_test_file_when_input_matches_pattern()
    {
        TestFileNamePattern pattern = new TestFileNamePattern("${srcFile}Test", new CamelCaseNameTokenizer());
        AtomicInteger sizeChanges = new AtomicInteger();
        FileNamePatternDemo demo = createDemo(pattern, sizeChanges);

        inputField(demo).setText("FooTest");

        testLinkSelected(demo);

        assertEquals("FooTest", inputField(demo).getText(), "input should be left untouched when not empty");
        assertTrue(sizeChanges.get() > 0, "sizeChanged() should have been called");
    }

    @Test
    public void should_refresh_input_when_pattern_changes()
    {
        TestFileNamePattern pattern = new TestFileNamePattern("${srcFile}_test", new SeparatorNameTokenizer("_"));
        AtomicInteger sizeChanges = new AtomicInteger();
        FileNamePatternDemo demo = createDemo(pattern, sizeChanges);

        demo.patternChanged();

        assertEquals("foo_bar", inputField(demo).getText());
    }
}
