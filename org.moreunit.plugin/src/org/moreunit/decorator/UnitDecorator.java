package org.moreunit.decorator;

import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.ui.IDecoratorManager;
import org.moreunit.MoreUnitPlugin;
import org.moreunit.elements.ClassTypeFacade;
import org.moreunit.elements.TypeFacade;
import org.moreunit.images.ImageDescriptorCenter;
import org.moreunit.util.MoreUnitContants;

/**
 * Handles the decoration of java files. If the class has a testcase a overlay
 * icon is added.
 */
public class UnitDecorator extends LabelProvider implements ILightweightLabelDecorator
{

    public void decorate(Object element, IDecoration decoration)
    {
        ICompilationUnit javaTypeOfResource = tryToGetCompilationUnitFromElement(element);
        if(javaTypeOfResource == null)
            return;

        if(hasTestCase(javaTypeOfResource))
        {
            handleClassDecoration(decoration);
        }
    }

    private void handleClassDecoration(IDecoration decoration)
    {
        ImageDescriptor imageDescriptor = ImageDescriptorCenter.getTestCaseLabelImageDescriptor();
        decoration.addOverlay(imageDescriptor, IDecoration.TOP_RIGHT);
    }

    private boolean hasTestCase(ICompilationUnit compilationUnit)
    {
        ClassTypeFacade javaFileFacade = new ClassTypeFacade(compilationUnit);
        Set<IType> correspondingTestcases = javaFileFacade.getCorrespondingTestCaseList();
        return correspondingTestcases != null && correspondingTestcases.size() > 0;
    }

    /**
     * This method checks the type of the <code>element</code> and tries to get
     * the compilation unit The method returns null if <code>element</code> is
     * the wrong type or if it is a test case.
     */
    public ICompilationUnit tryToGetCompilationUnitFromElement(Object element)
    {
        IResource objectResource = (IResource) element;
        if(objectResource.getType() != IResource.FILE)
            return null;

        IJavaElement javaElement = JavaCore.create(objectResource);
        if(javaElement == null)
            return null;

        if(javaElement.getElementType() != IJavaElement.COMPILATION_UNIT)
            return null;

        if(TypeFacade.isTestCase(((ICompilationUnit) javaElement).findPrimaryType()))
            return null;

        return (ICompilationUnit) javaElement;
    }

    public static UnitDecorator getUnitDecorator()
    {
        IDecoratorManager decoratorManager = MoreUnitPlugin.getDefault().getWorkbench().getDecoratorManager();

        if(decoratorManager.getEnabled(MoreUnitContants.TEST_CASE_DECORATOR))
            return (UnitDecorator) decoratorManager.getBaseLabelProvider(MoreUnitContants.TEST_CASE_DECORATOR);
        else
            return null;
    }

    public void refreshAll()
    {
        UnitDecorator unitDecorator = getUnitDecorator();

        if(unitDecorator != null)
            unitDecorator.fireLabelProviderChanged(new LabelProviderChangedEvent(unitDecorator));
    }
}