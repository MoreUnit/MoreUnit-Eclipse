package org.moreunit.codemining;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.IProgressMonitor;
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

    private Preferences preferences;

    public MoreUnitCodeMiningProvider()
    {
        this.preferences = Preferences.getInstance();
    }

    @Override
    public CompletableFuture<List< ? extends ICodeMining>> provideCodeMinings(ITextViewer viewer, IProgressMonitor monitor)
    {
        ITextEditor textEditor = super.getAdapter(ITextEditor.class);
        ITypeRoot unit = EditorUtility.getEditorInputJavaElement(textEditor, true);
        if(unit == null || ! preferences.shouldEnableMoreUnitCodeMining(unit.getJavaProject()))
        {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }
        return CompletableFuture.supplyAsync(() -> {
            monitor.isCanceled();
            try
            {
                IJavaElement[] elements = unit.getChildren();
                List<ICodeMining> minings = new ArrayList<>(elements.length);
                collectMinings(unit, textEditor, unit.getChildren(), minings, viewer, monitor);
                return minings;
            }
            catch (JavaModelException e)
            {
                // Should never occur
            }
            return Collections.emptyList();
        });
    }

    private void collectMinings(ITypeRoot unit, ITextEditor textEditor, IJavaElement[] elements, List<ICodeMining> minings, ITextViewer viewer, IProgressMonitor monitor) throws JavaModelException
    {

        if(! (textEditor instanceof JavaEditor))
        {
            return;
        }

        for (IJavaElement element : elements)
        {
            if(monitor.isCanceled())
            {
                return;
            }
            if(element.getElementType() == IJavaElement.TYPE)
            {
                collectMinings(unit, textEditor, ((IType) element).getChildren(), minings, viewer, monitor);
            }
            else if((element.getElementType() != IJavaElement.METHOD))
            {
                continue;
            }
            // support methods, classes
            boolean addMining = false;
            if(element instanceof IType)
            {
                IType type = (IType) element;
                if(type.isClass())
                {
                    addMining = true;
                }
            }
            else if(element instanceof IMethod)
            {
                addMining = true;
            }
            if(addMining)
            {
                try
                {
                    minings.add(new JumpCodeMining(element, viewer.getDocument(), this));
                }
                catch (BadLocationException e)
                {
                    // Should never occur
                }
            }
        }
    }

}
