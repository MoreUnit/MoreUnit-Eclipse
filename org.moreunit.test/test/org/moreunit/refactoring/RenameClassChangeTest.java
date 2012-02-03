package org.moreunit.refactoring;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.ltk.core.refactoring.Change;
import org.junit.Test;

public class RenameClassChangeTest
{

    private static final String ELEMENT_NAME_NEW = "Anything";
    private static final String ELEMENT_NAME_OLD = "Something";

    private static final IProgressMonitor PROGRESS_MONITOR = new NullProgressMonitor();

    @Test
    public void getModifiedElement_should_return_type_to_rename()
    {
        IType typeMock = createTypeRenameMockWithOldElementName();
        RenameClassChange change = new RenameClassChange(typeMock, ELEMENT_NAME_NEW);
        assertThat(change.getModifiedElement()).isEqualTo(typeMock);
    }

    private IType createTypeRenameMockWithOldElementName()
    {
        IType createMock = mock(IType.class);
        when(createMock.getElementName()).thenReturn(ELEMENT_NAME_OLD);
        IJavaElement firstParentMock = createFirstParentMockForTypeToRename();
        when(createMock.getParent()).thenReturn(firstParentMock);
        return createMock;
    }

    private IJavaElement createFirstParentMockForTypeToRename()
    {
        IJavaElement parentMock = mock(IJavaElement.class);
        when(parentMock.getElementType()).thenReturn(IJavaElement.COMPILATION_UNIT);
        IPackageFragment packageFragmentMock = createPackageFragmentMockWhichContainsTypeNewElementName();
        when(parentMock.getParent()).thenReturn(packageFragmentMock);
        return parentMock;
    }

    private IPackageFragment createPackageFragmentMockWhichContainsTypeNewElementName()
    {
        IPackageFragment mock = mock(IPackageFragment.class);
        ICompilationUnit compilationUnitMock = createCompilationUnitMockWithTypeNewElementName();
        when(mock.getCompilationUnit(ELEMENT_NAME_NEW + ".java")).thenReturn(compilationUnitMock);
        return mock;
    }

    private ICompilationUnit createCompilationUnitMockWithTypeNewElementName()
    {
        ICompilationUnit compilationUnitMock = mock(ICompilationUnit.class);
        IType typeMock = createTypeMockWithNewElementName();
        when(compilationUnitMock.getType(ELEMENT_NAME_NEW)).thenReturn(typeMock);
        return compilationUnitMock;
    }

    private IType createTypeMockWithNewElementName()
    {
        IType newTypeMock = mock(IType.class);
        when(newTypeMock.getElementName()).thenReturn(ELEMENT_NAME_NEW);
        return newTypeMock;
    }

    @Test
    public void getName_should_return_descriptive_string()
    {
        IType typeMock = createTypeRenameMockWithOldElementName();
        RenameClassChange change = new RenameClassChange(typeMock, ELEMENT_NAME_NEW);
        String expected = String.format("Rename %s to %s", ELEMENT_NAME_OLD, ELEMENT_NAME_NEW);
        assertThat(change.getName()).isEqualTo(expected);
    }

    @Test
    public void issValid_should_return_not_null()
    {
        IType typeMock = createTypeRenameMockWithOldElementName();
        RenameClassChange change = new RenameClassChange(typeMock, ELEMENT_NAME_NEW);
        assertThat(change.isValid(PROGRESS_MONITOR)).isNotNull();
    }

    @Test
    public void perform_should_return_a_rename_class_change_instance() throws CoreException
    {
        IType typeMock = createTypeRenameMockWithOldElementName();
        RenameClassChange change = new RenameClassChange(typeMock, ELEMENT_NAME_NEW);
        Change perform = change.perform(PROGRESS_MONITOR);

        assertThat(perform).isNotNull().isInstanceOf(RenameClassChange.class);
        String expected = String.format("Rename %s to %s", ELEMENT_NAME_NEW, ELEMENT_NAME_OLD);
        assertThat(perform.getName()).isEqualTo(expected);
    }
}