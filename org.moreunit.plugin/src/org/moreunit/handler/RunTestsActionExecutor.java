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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.moreunit.actions.RunTestAction;
import org.moreunit.actions.RunTestFromCompilationUnitAction;
import org.moreunit.actions.RunTestFromTypeAction;
import org.moreunit.actions.RunTestsOfSelectedMemberAction;
import org.moreunit.core.util.Jobs;
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
import org.moreunit.ui.ChooseDialog;
import org.moreunit.ui.MemberContentProvider;
import org.moreunit.ui.TreeActionElement;
import org.moreunit.util.FeatureDetector;
import org.moreunit.util.MemberJumpHistory;
import org.moreunit.util.SearchTools;

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

    public void executeRunTestAction(IEditorPart editorPart, String launchMode)
    {
        ICompilationUnit compilationUnit = createCompilationUnitFrom(editorPart);
        executeRunAllTestsAction(compilationUnit, launchMode);
    }

    private ICompilationUnit createCompilationUnitFrom(IEditorPart editorPart)
    {
        IFile file = editorPart.getEditorInput().getAdapter(IFile.class);
        return JavaCore.createCompilationUnitFrom(file);
    }

    public void executeRunTestAction(ICompilationUnit compilationUnit, String launchMode)
    {
        executeRunAllTestsAction(compilationUnit, launchMode);
    }

    private void executeRunAllTestsAction(ICompilationUnit compilationUnit, String launchMode)
    {
        saveIfNeeded(compilationUnit);
        Jobs.waitForIndexExecuteAndRunInUI("Running tests ... ", () -> {

            Collection<IType> testCases = new LinkedHashSet<>();
            IType selectedJavaType = compilationUnit.findPrimaryType();

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
            return resolveAbstractTestCases(testCases);
        }, testCases -> runTests(testCases, launchMode));
    }

    private Collection<IType> resolveAbstractTestCases(Collection<IType> testCases)
    {
        Collection<IType> resolvedTestCases = new LinkedHashSet<>();
        for (IType testCase : testCases)
        {
            resolvedTestCases.addAll(resolveAbstractTestCase(testCase));
        }
        return resolvedTestCases;
    }

    private Collection<IType> resolveAbstractTestCase(IType testCase)
    {
        try
        {
            if(! Flags.isAbstract(testCase.getFlags()))
            {
                return Collections.singleton(testCase);
            }

            Collection<IType> concreteSubclasses = SearchTools.findConcreteSubclasses(testCase);
            if(concreteSubclasses.isEmpty())
            {
                return Collections.singleton(testCase);
            }
            if(concreteSubclasses.size() == 1)
            {
                return Collections.singleton(concreteSubclasses.iterator().next());
            }

            Collection<IType> choice = chooseSubclasses(testCase, concreteSubclasses);
            if(choice != null && ! choice.isEmpty())
            {
                for (IType type : choice)
                {
                    MemberJumpHistory.getInstance().registerJump(testCase, type);
                }
                return choice;
            }
        }
        catch (JavaModelException e)
        {
            // ignore and return original
        }
        return Collections.singleton(testCase);
    }

    private void saveIfNeeded(ICompilationUnit compilationUnit)
    {
        IEditorPart editorPart = EditorUtility.isOpenInEditor(compilationUnit);
        if(editorPart != null && editorPart.isDirty())
        {
            editorPart.doSave(new NullProgressMonitor());
        }
    }

    public void executeRunTestsOfSelectedMemberAction(IEditorPart editorPart, String launchMode)
    {
        ICompilationUnit compilationUnit = createCompilationUnitFrom(editorPart);
        IMethod methodFromEditor = editorPart == null ? null : new EditorPartFacade(editorPart).getFirstNonAnonymousMethodSurroundingCursorPosition();
        executeRunTestsOfSelectedMemberAction(methodFromEditor, compilationUnit, launchMode);
    }

    private void executeRunTestsOfSelectedMemberAction(IMethod methodFromEditor, ICompilationUnit compilationUnit, String launchMode)
    {
        saveIfNeeded(compilationUnit);
        Jobs.waitForIndexExecuteAndRunInUI("Running tests ... ", () -> {
            Collection<IMember> testElements = new LinkedHashSet<>();
            IType selectedJavaType = compilationUnit.findPrimaryType();

            if(TypeFacade.isTestCase(selectedJavaType))
            {
                testElements.add(getTestElementFromTestCase(methodFromEditor, selectedJavaType));
            }
            else
            {
                IJavaProject javaProject = compilationUnit.getJavaProject();
                MethodSearchMode searchMode = Preferences.getInstance().getMethodSearchMode(javaProject);
                ClassTypeFacade typeFacade = new ClassTypeFacade(compilationUnit);

                IMethod methodUnderTest = methodFromEditor;

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
                testElements.add(getTestElementFromTestCase(methodFromEditor, selectedJavaType));
            }
            return resolveAbstractTestElements(testElements);
        }, testElements -> runTests(testElements, launchMode));
    }

    private Collection<IMember> resolveAbstractTestElements(Collection<IMember> testElements)
    {
        Collection<IMember> resolvedTestElements = new LinkedHashSet<>();
        for (IMember testElement : testElements)
        {
            resolvedTestElements.addAll(resolveAbstractTestElement(testElement));
        }
        return resolvedTestElements;
    }

    private Collection<IMember> resolveAbstractTestElement(IMember testElement)
    {
        IType type = testElement instanceof IType ? (IType) testElement : testElement.getDeclaringType();
        try
        {
            if(! Flags.isAbstract(type.getFlags()))
            {
                return Collections.singleton(testElement);
            }

            Collection<IType> concreteSubclasses = SearchTools.findConcreteSubclasses(type);
            if(concreteSubclasses.isEmpty())
            {
                return Collections.singleton(testElement);
            }
            if(concreteSubclasses.size() == 1)
            {
                return Collections.singleton(substituteType(testElement, concreteSubclasses.iterator().next()));
            }

            Collection<IType> chosenSubclasses = chooseSubclasses(type, concreteSubclasses);
            if(chosenSubclasses != null && ! chosenSubclasses.isEmpty())
            {
                Collection<IMember> resolvedElements = new ArrayList<>();
                for (IType chosenSubclass : chosenSubclasses)
                {
                    MemberJumpHistory.getInstance().registerJump(testElement, chosenSubclass);
                    resolvedElements.add(substituteType(testElement, chosenSubclass));
                }
                return resolvedElements;
            }
        }
        catch (JavaModelException e)
        {
            // ignore and return original
        }
        return Collections.singleton(testElement);
    }

    private IMember substituteType(IMember testElement, IType newType)
    {
        if(testElement instanceof IType)
        {
            return newType;
        }
        // It's a method. We return the method with the same name from the new type if it exists.
        // If not, we still return the method from the abstract class, but the launcher should handle it.
        // Actually, JDT's JUnit launcher handles methods from superclasses correctly if the target type is the subclass.
        // But for clarity, let's see if we can find the method in the subclass.
        IMethod method = (IMethod) testElement;
        IMethod subclassMethod = newType.getMethod(method.getElementName(), method.getParameterTypes());
        if(subclassMethod.exists())
        {
            return subclassMethod;
        }
        return method;
    }

    private Collection<IType> chooseSubclasses(IType abstractType, Collection<IType> concreteSubclasses)
    {
        IMember defaultSelection = MemberJumpHistory.getInstance().getLastCorrespondingJumpMember(abstractType);
        if(! concreteSubclasses.contains(defaultSelection))
        {
            defaultSelection = null;
        }

        MemberContentProvider contentProvider = new MemberContentProvider(concreteSubclasses, (IType) defaultSelection);
        contentProvider.withAction(new AllSubclassesAction(concreteSubclasses));

        String promptText = "Choose concrete subclass for " + abstractType.getElementName();
        return Display.getDefault().syncCall(() -> {
            ChooseDialog<Object> dialog = new ChooseDialog<>(promptText, contentProvider);
            Object choice = dialog.getChoice();
            if(choice instanceof Collection)
            {
                return (Collection<IType>) choice;
            }
            if(choice instanceof IType)
            {
                return Collections.singleton((IType) choice);
            }
            return null;
        });
    }

    private static class AllSubclassesAction implements TreeActionElement<Collection<IType>>
    {
        private final Collection<IType> concreteSubclasses;

        public AllSubclassesAction(Collection<IType> concreteSubclasses)
        {
            this.concreteSubclasses = concreteSubclasses;
        }

        @Override
        public boolean provideElement()
        {
            return true;
        }

        @Override
        public Collection<IType> execute()
        {
            return concreteSubclasses;
        }

        @Override
        public String getText()
        {
            return "All concrete subclasses";
        }

        @Override
        public Image getImage()
        {
            return null;
        }
    }

    /**
     * Returns the test method that is selected in editor if any, otherwise
     * returns the test case.
     */
    private IMember getTestElementFromTestCase(IMethod methodFromEditor, IType testCaseType)
    {
        if(methodFromEditor != null && new MethodFacade(methodFromEditor).isTestMethod())
        {
            return methodFromEditor;
        }

        return testCaseType;
    }

    private void runTests(Collection< ? extends IMember> testElements, String launchMode)
    {
        IJavaElement aTestMember = testElements.iterator().next();
        if(aTestMember != null)
        {
            String testType = Preferences.getInstance().getTestType(aTestMember.getJavaProject());
            testLauncher.launch(testType, testElements, launchMode);
        }
    }
}