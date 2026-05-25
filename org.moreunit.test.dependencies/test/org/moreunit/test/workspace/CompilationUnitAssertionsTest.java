package org.moreunit.test.workspace;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CompilationUnitAssertionsTest
{
    private static final String NL = System.getProperty("line.separator");

    @Mock
    private CompilationUnitHandler actualCu;

    private TestAssertions assertions;

    @BeforeEach
    public void setUp() throws Exception
    {
        MockitoAnnotations.openMocks(this);
        assertions = new TestAssertions(actualCu);
    }

    @Test
    public void should_normalize_spaces_when_comparing_sources() throws Exception
    {
        when(actualCu.getActualSource()).thenReturn("public  class SomeClass      " + NL + "{ \t private String aField; } ");

        assertions.whenCreatingSourceThenReturn("   public class     SomeClass " + NL + "{ private   String aField; }");

        assertions.hasSameSourceAsIn("some_file_containing_the_expected_source.txt");
    }

    @Test
    public void should_ignore_jdk_dependent_imports() throws Exception
    {
        when(actualCu.getActualSource()).thenReturn("import java.util.concurrent.Callable;\npublic  class SomeClass      " + NL + "{ \t private String aField; } ");

        assertions.whenCreatingSourceThenReturn("   public class     SomeClass " + NL + "{ private   String aField; }");

        assertions.hasSameSourceAsIn("some_file_containing_the_expected_source.txt");
    }

    /**
     * A test-only concrete subclass of CompilationUnitAssertions.
     */
    private static class TestAssertions extends CompilationUnitAssertions
    {
        private String sourceToCreate;

        public TestAssertions(CompilationUnitHandler actualCu)
        {
            super(actualCu);
        }

        public void whenCreatingSourceThenReturn(String sourceToCreate)
        {
            this.sourceToCreate = sourceToCreate;
        }

        @Override
        protected String getSource(SourceFolderHandler srcFolderHandler, String expectedSourceFile)
        {
            return sourceToCreate;
        }
    }
}
