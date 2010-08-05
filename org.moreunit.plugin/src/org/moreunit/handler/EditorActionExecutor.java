/**
 * MoreUnit-Plugin for Eclipse V3.5.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License - v 1.0.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See Eclipse Public License for more details.
 */
package org.moreunit.handler;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.texteditor.ITextEditor;
import org.moreunit.MoreUnitPlugin;
import org.moreunit.actions.CreateTestMethodEditorAction;
import org.moreunit.actions.CreateTestMethodHierarchyAction;
import org.moreunit.actions.JumpAction;
import org.moreunit.annotation.MoreUnitAnnotationModel;
import org.moreunit.elements.ClassTypeFacade;
import org.moreunit.elements.EditorPartFacade;
import org.moreunit.elements.TestmethodCreator;
import org.moreunit.elements.TypeFacade;
import org.moreunit.extensionpoints.IAddTestMethodContext;
import org.moreunit.extensionpoints.IAddTestMethodParticipator;
//import org.moreunit.launch.JunitTestLaunchShortcut;
import org.moreunit.launch.TestLauncher;
import org.moreunit.log.LogHandler;
import org.moreunit.preferences.Preferences;
import org.moreunit.util.MoreUnitContants;

/**
 * Handles the actions which are delegated from the handlers:<br>
 * <ul>
 * <li>key actions like {@link CreateTestMethodActionHandler} and
 * {@link JumpActionHandler}</li>
 * <li>menu action provided by the popup menu in the editor like
 * {@link CreateTestMethodEditorAction} and {@link JumpAction}</li>
 * <li>menu action provided by the popup menu in the package explorer like
 * {@link CreateTestMethodHierarchyAction}</li>
 * </ul>
 * The handler is a singelton.
 * 
 * @author vera 25.10.2005, extended andreas 16.06.2010
 */
public class EditorActionExecutor
{

    private static EditorActionExecutor instance;

    private EditorActionExecutor()
    {
    }

    public static EditorActionExecutor getInstance()
    {
        if(instance == null)
        {
            instance = new EditorActionExecutor();
        }

        return instance;
    }

    public void executeCreateTestMethodAction(IEditorPart editorPart)
    {
        EditorPartFacade editorPartFacade = new EditorPartFacade(editorPart);
        ICompilationUnit compilationUnitCurrentlyEdited = editorPartFacade.getCompilationUnit();

        ICompilationUnit compilationUnitForUnitUnderTest = null;
        ICompilationUnit compilationUnitForTestCase = null;

        if(TypeFacade.isTestCase(compilationUnitCurrentlyEdited.findPrimaryType()))
        {
            compilationUnitForTestCase = compilationUnitCurrentlyEdited;
        }
        else
        {
            compilationUnitForUnitUnderTest = compilationUnitCurrentlyEdited;
            ClassTypeFacade classTypeFacade = new ClassTypeFacade(compilationUnitForUnitUnderTest);
            IType oneCorrespondingTestCase = classTypeFacade.getOneCorrespondingTestCase(true);

            // This happens if the user chooses cancel from the wizard
            if(oneCorrespondingTestCase == null)
            {
                return;
            }
            compilationUnitForTestCase = oneCorrespondingTestCase.getCompilationUnit();
        }

        // Create test method template
        TestmethodCreator testmethodCreator = new TestmethodCreator(editorPartFacade.getCompilationUnit(), Preferences.getInstance().getTestType(editorPartFacade.getJavaProject()), Preferences.getInstance().getTestMethodDefaultContent(editorPartFacade.getJavaProject()));
        IMethod methodUnderTest = editorPartFacade.getMethodUnderCursorPosition();
        IMethod createdMethod = testmethodCreator.createTestMethod(methodUnderTest);

        // Call extensions on our extension point, allowing to modify the
        // created testmethod
        AddTestMethodContext addTestMethodContext = new AddTestMethodContext(compilationUnitForTestCase, createdMethod, compilationUnitForUnitUnderTest, methodUnderTest);
        addTestMethodContext.setNewTestClassCreated(testmethodCreator.isNewTestClassCreated());
        callAddTestMethodParticipants(addTestMethodContext);

        // If test modified test method is given, use it
        IMethod modifiedTestMethod = addTestMethodContext.getTestMethod();
        if(modifiedTestMethod != null)
        {
            createdMethod = modifiedTestMethod;
        }

        if((createdMethod != null) && createdMethod.getElementName().endsWith(MoreUnitContants.SUFFIX_NAME))
        {
            markMethodSuffix(editorPartFacade, createdMethod);

        }

        if(editorPart instanceof ITextEditor)
        {
            MoreUnitAnnotationModel.updateAnnotations((ITextEditor) editorPart);
        }
    }

    /**
     * Try to find extensions to the extension point and, if any, run them.
     * 
     * @param context Context for extension runner.
     */
    private void callAddTestMethodParticipants(final IAddTestMethodContext context)
    {

        // Create ExtensionID
        String extensionName = "addTestmethodParticipator";
        String extensionID = MoreUnitPlugin.PLUGIN_ID + "." + extensionName;

        // Search extensions
        try
        {
            IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(extensionID);

            // Run all extensions found
            for (IConfigurationElement e : config)
            {

                // Create Object from class definition
                final Object extension = e.createExecutableExtension("class");
                LogHandler.getInstance().handleInfoLog("Found extension to " + extensionName + ": " + extension.getClass());

                // Cast and try to start extension
                if(extension instanceof IAddTestMethodParticipator)
                {

                    // Create safe runner
                    ISafeRunnable runnable = new ISafeRunnable()
                    {
                        public void handleException(Throwable throwable)
                        {
                            LogHandler.getInstance().handleExceptionLog("Error running extension", throwable);
                        }

                        public void run() throws Exception
                        {
                            LogHandler.getInstance().handleInfoLog("Run extension");
                            ((IAddTestMethodParticipator) extension).addTestMethod(context);
                        }
                    };
                    SafeRunner.run(runnable);
                }
                else
                {
                    LogHandler.getInstance().handleWarnLog("Bad class for extension point");
                }
            }
        }
        catch (Exception e)
        {
            LogHandler.getInstance().handleExceptionLog(e);
        }
    }

    private void markMethodSuffix(EditorPartFacade testCaseTypeFacade, IMethod newMethod)
    {
        ISelectionProvider selectionProvider = testCaseTypeFacade.getEditorPart().getSite().getSelectionProvider();

        ISelection exactSelection = null;
        try
        {
            ISourceRange range = newMethod.getNameRange();
            int offset = range.getOffset();
            int length = range.getLength();

            int suffixLength = MoreUnitContants.SUFFIX_NAME.length();
            exactSelection = new TextSelection(offset + length - suffixLength, suffixLength);
        }
        catch (JavaModelException exc)
        {
            LogHandler.getInstance().handleExceptionLog(exc);
        }
        revealInEditor(testCaseTypeFacade.getEditorPart(), newMethod);
        selectionProvider.setSelection(exactSelection);
    }

    private static void revealInEditor(IEditorPart editorPart, IMethod method)
    {
        JavaUI.revealInEditor(editorPart, (IJavaElement) method);
    }

    public void executeJumpAction(IEditorPart editorPart)
    {
        IFile file = (IFile) editorPart.getEditorInput().getAdapter(IFile.class);
        ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(file);
        executeJumpAction(editorPart, compilationUnit);
    }

    public void executeJumpAction(ICompilationUnit compilationUnit)
    {
        executeJumpAction(null, compilationUnit);
    }

    private void executeJumpAction(IEditorPart editorPart, ICompilationUnit compilationUnit)
    {
        IMethod methodUnderCursorPosition = getMethodUnderCursorPosition(editorPart);
        boolean extendedSearch = Preferences.getInstance().shouldUseTestMethodExtendedSearch(compilationUnit.getJavaProject());

        TypeFacade typeFacade = TypeFacade.createFacade(compilationUnit);
        IMember memberToJump = typeFacade.getOneCorrespondingMember(methodUnderCursorPosition, true, extendedSearch, "Jump to...");
        if(memberToJump != null)
        {
            jumpToMember(memberToJump);
        }
    }

    private IMethod getMethodUnderCursorPosition(IEditorPart editorPart)
    {
        return editorPart == null ? null : new EditorPartFacade(editorPart).getMethodUnderCursorPosition();
    }

    private void jumpToMember(IMember memberToJump)
    {
        if(memberToJump instanceof IMethod)
        {
            IMethod methodToJump = (IMethod) memberToJump;
            IEditorPart openedEditor = openInEditor(methodToJump.getDeclaringType().getParent());
            revealInEditor(openedEditor, methodToJump);
        }
        else
        {
            openInEditor(memberToJump.getParent());
        }
    }

    private static IEditorPart openInEditor(IJavaElement element)
    {
        IEditorPart openedEditorPart = null;
        try
        {
            openedEditorPart = JavaUI.openInEditor(element);
        }
        catch (PartInitException exc)
        {
            LogHandler.getInstance().handleExceptionLog(exc);
        }
        catch (JavaModelException exc)
        {
            LogHandler.getInstance().handleExceptionLog(exc);
        }
        return openedEditorPart;
    }

    public void executeRunTestAction(IEditorPart editorPart)
    {
        IFile file = (IFile) editorPart.getEditorInput().getAdapter(IFile.class);
        ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(file);
        executeRunTestAction(editorPart, compilationUnit);
    }

    public void executeRunTestAction(ICompilationUnit compilationUnit)
    {
        executeRunTestAction(null, compilationUnit);
    }

    private void executeRunTestAction(IEditorPart editorPart, ICompilationUnit compilationUnit)
    {
        IType selectedJavaType = compilationUnit.findPrimaryType();

        IJavaElement testElement = null;
        if(TypeFacade.isTestCase(selectedJavaType))
        {
            testElement = getTestElementFromTestCase(editorPart, selectedJavaType);
        }
        else
        {
            testElement = getTestElementFromClassUnderTest(editorPart, compilationUnit);
        }

        if(testElement != null)
        {
            runTest(testElement);
        }
    }

    /**
     * Returns the test method that is selected in editor if any, otherwise
     * returns the test case.
     */
    private IJavaElement getTestElementFromTestCase(IEditorPart editorPart, IType testCaseType)
    {
        if(editorPart == null)
        {
            return testCaseType;
        }

        IMethod testMethod = new EditorPartFacade(editorPart).getMethodUnderCursorPosition();
        if(testMethod != null)
        {
            return testMethod;
        }

        return testCaseType;
    }

    /**
     * Tries to return a unique test method that corresponds to the method
     * selected in editor if any; otherwise (if no method is selected or if
     * several test methods exist for it) returns one corresponding test case if
     * it exists.
     */
    private IJavaElement getTestElementFromClassUnderTest(IEditorPart editorPart, ICompilationUnit compilationUnit)
    {
        ClassTypeFacade javaFileFacade = new ClassTypeFacade(compilationUnit);

        if(editorPart != null)
        {
            IMethod methodUnderTest = new EditorPartFacade(editorPart).getMethodUnderCursorPosition();
            if(methodUnderTest != null)
            {
                List<IMethod> testMethods = javaFileFacade.getCorrespondingTestMethods(methodUnderTest);
                if(testMethods.size() == 1)
                {
                    return testMethods.get(0);
                }
            }
        }

        return javaFileFacade.getOneCorrespondingTestCase(true);
    }

    private void runTest(IJavaElement testElement)
    {
        String testType = Preferences.getInstance().getTestType(testElement.getJavaProject());
        new TestLauncher(testType).launch(testElement);
    }

}

// $Log: not supported by cvs2svn $
// Revision 1.21  2010/07/27 21:48:24  makkimesser
// Unresolved, unused import deleted, that caused a compiler error
//
// Revision 1.20  2010/07/26 18:15:57  ndemengel
// Refactoring
//
// Revision 1.19  2010/07/10 15:04:56  makkimesser
// Call of extensions refactored
//
// Revision 1.18 2010/06/30 22:54:41 makkimesser
// ExtensionPoint extended
// Documentation added/improved
//
// Revision 1.17 2010/06/18 20:05:38 gianasista
// extended test method search
//
// Revision 1.16 2010/04/13 19:17:11 gianasista
// support for launching testNG tests
//
// Revision 1.15 2010/02/06 21:07:26 gianasista
// Patch for Running Tests from CUT
//
// Revision 1.14 2009/09/11 19:52:04 gianasista
// Bugfix: NPE when switching from package explorer without open editor parts
//
// Revision 1.13 2009/04/05 19:14:27 gianasista
// code formatter
//
// Revision 1.12 2009/02/15 17:28:38 gianasista
// annotations instead of marker
//
// Revision 1.11 2009/01/15 19:06:38 gianasista
// Patch from Zach: configurable content for test method
//
// Revision 1.10 2008/02/20 19:21:18 gianasista
// Rename of classes for constants
//
// Revision 1.9 2008/02/04 20:03:11 gianasista
// Bugfix: project specific settings
//
// Revision 1.8 2007/11/19 20:54:55 gianasista
// Patch from Bjoern: project specific settings
//
// Revision 1.7 2007/08/12 17:09:54 gianasista
// Refactoring: Test method creation
//
// Revision 1.6 2006/12/22 19:03:00 gianasista
// changed textselection after creation of another testmethod
//
// Revision 1.5 2006/11/25 15:00:26 gianasista
// Create second testmethod
//
// Revision 1.4 2006/11/04 08:50:19 channingwalton
// Fix for [ 1579660 ] Testcase selection dialog opens twice
//
// Revision 1.3 2006/10/15 18:31:16 gianasista
// Started to implement the feature to create several test methods for one
// methods
//
// Revision 1.2 2006/10/02 18:22:23 channingwalton
// added actions for jumping from views. added some tests for project
// properties. improved some of the text
//
// Revision 1.1.1.1 2006/08/13 14:31:16 gianasista
// initial
//
// Revision 1.1 2006/06/22 20:22:29 gianasista
// package rename
//
// Revision 1.1 2006/06/19 20:08:48 gianasista
// CVS Refactoring
//
// Revision 1.2 2006/05/23 19:39:15 gianasista
// Splitted JavaFileFacade into two classes
//
// Revision 1.1 2006/05/20 16:08:21 gianasista
// Rename of MoreUnitActionHandler, new name EditorActionExecutor
//
// Revision 1.18 2006/05/18 06:57:48 channingwalton
// fixed some warnings and deprecated APIs
//
// Revision 1.17 2006/05/15 19:50:42 gianasista
// removed deprecated method call
//
// Revision 1.16 2006/05/14 19:08:57 gianasista
// JumpToTest uses TypeChoiceDialog
//
// Revision 1.15 2006/05/12 22:33:42 channingwalton
// added class creation wizards if type to jump to does not exist
//
// Revision 1.14 2006/05/12 17:53:07 gianasista
// added comments
//
// Revision 1.13 2006/04/21 05:57:17 gianasista
// Feature: Jump from testcase back to class under test
//
// Revision 1.12 2006/04/14 17:14:22 gianasista
// Refactoring Support with dialog
//
// Revision 1.11 2006/03/21 20:59:49 gianasista
// Bugfix JumpToTest
//
// Revision 1.10 2006/02/27 19:55:56 gianasista
// Started hover support
//
// Revision 1.9 2006/01/30 21:12:31 gianasista
// Further Refactorings (moved methods from singleton classes like PluginTools
// to facade classes)
//
// Revision 1.8 2006/01/28 15:48:25 gianasista
// Moved several methods from PluginTools to EditorPartFacade
//
// Revision 1.7 2006/01/25 21:27:19 gianasista
// First refactoring to smarter code (Replacing util-classes)
//
// Revision 1.6 2006/01/20 21:33:30 gianasista
// Organize Imports
//
// Revision 1.5 2006/01/19 21:38:32 gianasista
// Added CVS-commit-logging to all java-files
//
