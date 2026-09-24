package org.moreunit.codemining;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.codemining.AbstractCodeMiningProvider;
import org.eclipse.jface.text.codemining.ICodeMining;
import org.eclipse.jface.text.codemining.ICodeMiningProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.moreunit.preferences.Preferences;

/**
 * {@link ICodeMiningProvider} for MoreUnit.
 */
public class MoreUnitCodeMiningProvider extends AbstractCodeMiningProvider
{

    private final Preferences preferences;

    public MoreUnitCodeMiningProvider()
    {
        this.preferences = Preferences.getInstance();
    }

    @Override
    public CompletableFuture<List< ? extends ICodeMining>> provideCodeMinings(ITextViewer viewer, IProgressMonitor monitor)
    {
        final ITextEditor textEditor = super.getAdapter(ITextEditor.class);
        final ITypeRoot unit = EditorUtility.getEditorInputJavaElement(textEditor, true);
        if(unit == null || ! preferences.shouldEnableMoreUnitCodeMining(unit.getJavaProject()))
        {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }
        return CompletableFuture.supplyAsync(() -> {
            monitor.isCanceled();
            try
            {
                final IJavaElement[] elements = unit.getChildren();
                final List<IJavaElement> minableElements = new ArrayList<>(elements.length);
                collectElements(unit, textEditor, elements, minableElements, monitor);
                if(minableElements.isEmpty())
                {
                    return Collections.emptyList();
                }

                // all the minings of a compilation unit share the same label
                // computer, so that the corresponding test cases / classes
                // under test are searched only once per refresh
                final JumpLabelComputer labelComputer = new JumpLabelComputer(unit instanceof final ICompilationUnit compilationUnit ? compilationUnit : null, minableElements);

                final List<ICodeMining> minings = new ArrayList<>(minableElements.size());
                for (final IJavaElement element : minableElements)
                {
                    try
                    {
                        minings.add(new JumpCodeMining(element, viewer.getDocument(), this, labelComputer));
                    }
                    catch (final BadLocationException e)
                    {
                        // Should never occur
                    }
                }
                return minings;
            }
            catch (final JavaModelException e)
            {
                // Should never occur
            }
            return Collections.emptyList();
        });
    }

    private void collectElements(ITypeRoot unit, ITextEditor textEditor, IJavaElement[] elements, List<IJavaElement> minableElements, IProgressMonitor monitor) throws JavaModelException
    {

        if(! (textEditor instanceof JavaEditor))
        {
            return;
        }

        for (final IJavaElement element : elements)
        {
            if(monitor.isCanceled())
            {
                return;
            }
            if(element.getElementType() == IJavaElement.TYPE)
            {
                collectElements(unit, textEditor, ((IType) element).getChildren(), minableElements, monitor);
            }
            else if((element.getElementType() != IJavaElement.METHOD))
            {
                continue;
            }
            // support methods, classes
            boolean addMining = false;
            if(element instanceof final IType type && preferences.shouldEnableJumpToClassCodeMining(unit.getJavaProject()))
            {
                if(type.isClass() || type.isEnum() || type.isInterface() || type.isRecord() || type.isAnnotation())
                {
                    addMining = true;
                }
            }
            else if(element instanceof IMethod && preferences.shouldEnableJumpToMethodCodeMining(unit.getJavaProject()))
            {
                addMining = true;
            }
            if(addMining)
            {
                minableElements.add(element);
            }
        }
    }

}
