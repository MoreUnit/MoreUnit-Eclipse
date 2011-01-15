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

import java.util.Collection;
import java.util.LinkedHashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.IEditorPart;
import org.moreunit.actions.RunTestAction;
import org.moreunit.actions.RunTestFromCompilationUnitAction;
import org.moreunit.actions.RunTestFromTypeAction;
import org.moreunit.actions.RunTestsOfSelectedMemberAction;
import org.moreunit.elements.ClassTypeFacade;
import org.moreunit.elements.EditorPartFacade;
import org.moreunit.elements.MethodFacade;
import org.moreunit.elements.TypeFacade;
import org.moreunit.launch.TestLauncher;
import org.moreunit.preferences.Preferences;
import org.moreunit.util.FeatureDetector;

/**
 * Executes the actions "Run test(s)" launched from the handlers:<br>
 * <ul>
 * <li>key actions: {@link RunTestActionHandler} and
 * {@link RunTestsOfSelectedMemberAction}</li>
 * <li>menu action provided by the popup menu in the editor:
 * {@link RunTestAction} and {@link RunTestsOfSelectedMemberAction}</li>
 * <li>menu action provided by the popup menu in the package explorer:
 * {@link RunTestFromCompilationUnitAction} and {@link RunTestFromTypeAction}</li>
 * </ul>
 * The handler is a singleton.
 * 
 * @author vera 25.10.2005
 * @version 30.09.2010
 */
public class RunTestsActionExecutor
{
    private static RunTestsActionExecutor instance;

    private final FeatureDetector featureDetector;
    private final TestLauncher testLauncher;

    private RunTestsActionExecutor()
    {
        featureDetector = new FeatureDetector();
        testLauncher = new TestLauncher();
    }

    public static RunTestsActionExecutor getInstance()
    {
        if(instance == null)
        {
            instance = new RunTestsActionExecutor();
        }

        return instance;
    }

    public void executeRunTestAction(IEditorPart editorPart)
    {
        ICompilationUnit compilationUnit = createCompilationUnitFrom(editorPart);
        executeRunAllTestsAction(editorPart, compilationUnit);
    }

    private ICompilationUnit createCompilationUnitFrom(IEditorPart editorPart)
    {
        IFile file = (IFile) editorPart.getEditorInput().getAdapter(IFile.class);
        return JavaCore.createCompilationUnitFrom(file);
    }

    public void executeRunTestAction(ICompilationUnit compilationUnit)
    {
        executeRunAllTestsAction(null, compilationUnit);
    }

    private void executeRunAllTestsAction(IEditorPart editorPart, ICompilationUnit compilationUnit)
    {
        IType selectedJavaType = compilationUnit.findPrimaryType();

        Collection<IType> testCases = new LinkedHashSet<IType>();
        if(TypeFacade.isTestCase(selectedJavaType))
        {
            testCases.add(selectedJavaType);
        }
        else
        {
            IJavaProject javaProject = selectedJavaType.getJavaProject();
            ClassTypeFacade typeFacade = new ClassTypeFacade(compilationUnit);

            if(featureDetector.isTestSelectionRunSupported(javaProject))
            {
                testCases.addAll(typeFacade.getCorrespondingTestCases());
            }
            else
            {
                testCases.add(typeFacade.getOneCorrespondingTestCase(true, "Run test..."));
            }
        }

        if(! testCases.isEmpty())
        {
            runTests(testCases);
        }
    }

    public void executeRunTestsOfSelectedMemberAction(IEditorPart editorPart)
    {
        ICompilationUnit compilationUnit = createCompilationUnitFrom(editorPart);
        executeRunTestsOfSelectedMemberAction(editorPart, compilationUnit);
    }

    private void executeRunTestsOfSelectedMemberAction(IEditorPart editorPart, ICompilationUnit compilationUnit)
    {
        IType selectedJavaType = compilationUnit.findPrimaryType();

        Collection<IMember> testElements = new LinkedHashSet<IMember>();
        if(TypeFacade.isTestCase(selectedJavaType))
        {
            testElements.add(getTestElementFromTestCase(editorPart, selectedJavaType));
        }
        else
        {
            IJavaProject javaProject = compilationUnit.getJavaProject();
            boolean extendedSearch = Preferences.getInstance().shouldUseTestMethodExtendedSearch(javaProject);
            ClassTypeFacade typeFacade = new ClassTypeFacade(compilationUnit);
            
            IMethod methodUnderTest = null;
            if(editorPart != null)
            {
                methodUnderTest = new EditorPartFacade(editorPart).getFirstNonAnonymousMethodSurroundingCursorPosition();
            }

            if(featureDetector.isTestSelectionRunSupported(selectedJavaType.getJavaProject()))
            {
                testElements.addAll(typeFacade.getCorrespondingTestMembers(methodUnderTest, extendedSearch));
            }
            else
            {
                testElements.add(typeFacade.getOneCorrespondingMember(methodUnderTest, true, extendedSearch, "Run test..."));
            }
        }

        if(! testElements.isEmpty())
        {
            runTests(testElements);
        }
    }

    /**
     * Returns the test method that is selected in editor if any, otherwise
     * returns the test case.
     */
    private IMember getTestElementFromTestCase(IEditorPart editorPart, IType testCaseType)
    {
        if(editorPart == null)
        {
            return testCaseType;
        }

        IMethod method = new EditorPartFacade(editorPart).getFirstNonAnonymousMethodSurroundingCursorPosition();
        if(method != null && new MethodFacade(method).isTestMethod())
        {
            return method;
        }

        return testCaseType;
    }

    private void runTests(Collection< ? extends IMember> testElements)
    {
        IJavaElement aTestMember = testElements.iterator().next();
        String testType = Preferences.getInstance().getTestType(aTestMember.getJavaProject());
        testLauncher.launch(testType, testElements);
    }
}

// $Log: not supported by cvs2svn $
// Revision 1.2  2011/01/09 13:05:53  ndemengel
// Clarifies the code by splitting EditorActionExecutor into logically distinct parts: CreateTestMethod-, Jump- and RunTests-ActionExecutor
//
//
// RunTestsActionExecutor extracted from EditorActionExecutor
//
// Revision 1.32 2011/01/08 19:48:08 ndemengel
// Fixes NullPointerExceptions
//
// Revision 1.31 2010/10/17 11:02:34 ndemengel
// Reviews extended method search (simplified for better accuracy)
//
// Revision 1.30 2010/10/16 18:50:04 ndemengel
// Refactors test launch ands removes unused dialog
//
// Revision 1.29 2010/10/08 16:09:28 ndemengel
// Activates TestNG extension
//
// Revision 1.28 2010/09/30 21:46:09 makkimesser
// Incomplete - task 3072083: Generate test creates TestNG-tests only
// http://sourceforge.net/tracker/?group_id=156007&atid=798056&func=detail&assignee=2466398&aid=3072083
//
// Revision 1.27 2010/09/20 19:41:05 gianasista
// Bugfix: second dialog while generate test action
//
// Revision 1.26 2010/09/18 15:56:55 makkimesser
// Fixed:
// task 3042170: Extension point is not called missing testmethods view
// task 3043350: Extension point incomplete with new testclass
//
// Revision 1.25 2010/09/12 13:59:23 ndemengel
// Adds support for launching multiple test cases with JUnit
//
// Revision 1.24 2010/09/02 10:50:18 ndemengel
// Feature Requests 3036484: part 2, adds a new shortcut to only run tests
// corresponding to the selected member, modifies old shortcut to run all tests
// corresponding to the selected member.
// Also adds consistency in moreUnit labels.
//
// Revision 1.23 2010/08/15 17:05:00 ndemengel
// Feature Requests 3036484: part 1, prevents running a non-test method
//
// Revision 1.22 2010/08/05 21:23:11 makkimesser
// Provided info to Extension point clients if a new test class is created
// instead of adding a one test case to an existing test class. So they can
// handle this in an appropriate way.
//
// Revision 1.21 2010/07/27 21:48:24 makkimesser
// Unresolved, unused import deleted, that caused a compiler error
//
// Revision 1.20 2010/07/26 18:15:57 ndemengel
// Refactoring
//
// Revision 1.19 2010/07/10 15:04:56 makkimesser
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
