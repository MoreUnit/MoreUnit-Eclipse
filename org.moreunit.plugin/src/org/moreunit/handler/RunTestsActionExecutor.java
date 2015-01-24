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

import static org.moreunit.elements.CorrespondingMemberRequest.newCorrespondingMemberRequest;

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
import org.moreunit.elements.ClassTypeFacade.CorrespondingTestCase;
import org.moreunit.elements.CorrespondingMemberRequest;
import org.moreunit.elements.CorrespondingMemberRequest.MemberType;
import org.moreunit.elements.EditorPartFacade;
import org.moreunit.elements.MethodFacade;
import org.moreunit.elements.TypeFacade;
import org.moreunit.launch.TestLauncher;
import org.moreunit.preferences.Preferences;
import org.moreunit.preferences.Preferences.MethodSearchMode;
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
                CorrespondingTestCase testCase = typeFacade.getOneCorrespondingTestCase(true, "Run test...");
                if(testCase.found() && ! testCase.hasJustBeenCreated())
                {
                    testCases.add(testCase.get());
                }
            }
        }

        if(testCases.isEmpty())
        {
            testCases.add(selectedJavaType);
        }

        runTests(testCases);
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
            MethodSearchMode searchMode = Preferences.getInstance().getMethodSearchMode(javaProject);
            ClassTypeFacade typeFacade = new ClassTypeFacade(compilationUnit);

            IMethod methodUnderTest = null;
            if(editorPart != null)
            {
                methodUnderTest = new EditorPartFacade(editorPart).getFirstNonAnonymousMethodSurroundingCursorPosition();
            }

            if(methodUnderTest != null && featureDetector.isTestSelectionRunSupported(selectedJavaType.getJavaProject()))
            {
                testElements.addAll(typeFacade.getCorrespondingTestMethods(methodUnderTest, searchMode));
            }
            else
            {
                CorrespondingMemberRequest request = newCorrespondingMemberRequest() //
                        .withExpectedResultType(MemberType.TYPE_OR_METHOD) //
                        .withCurrentMethod(methodUnderTest) //
                        .methodSearchMode(searchMode) //
                        .createClassIfNoResult("Run test...") //
                        .build();

                testElements.add(typeFacade.getOneCorrespondingMember(request));
            }
        }

        if(testElements.isEmpty())
        {
            testElements.add(getTestElementFromTestCase(editorPart, selectedJavaType));
        }

        runTests(testElements);
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
        if(aTestMember != null)
        {
            String testType = Preferences.getInstance().getTestType(aTestMember.getJavaProject());
            testLauncher.launch(testType, testElements);
        }
    }
}