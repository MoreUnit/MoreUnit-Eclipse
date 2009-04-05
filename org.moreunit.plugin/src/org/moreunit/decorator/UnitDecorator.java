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

// $Log: not supported by cvs2svn $
// Revision 1.6 2009/04/05 19:08:33 gianasista
// Switch to gnu code formatter
//
// Revision 1.5 2009/01/23 21:19:28 gianasista
// Refactoring + Tests
//
// Revision 1.4 2008/02/20 19:20:32 gianasista
// Rename of classes for constants
//
// Revision 1.3 2006/09/18 20:00:02 channingwalton
// the CVS substitions broke with my last check in because I put newlines in
// them
//
// Revision 1.2 2006/09/18 19:56:00 channingwalton
// Fixed bug [ 1537839 ] moreunit cannot find test class if it is in wrong
// package. Also found a classcast exception in UnitDecorator whicj I've guarded
// for.Fixed the Class wizard icon
//
// Revision 1.1.1.1 2006/08/13 14:31:15 gianasista
// initial
//
// Revision 1.1 2006/06/22 20:22:29 gianasista
// package rename
//
// Revision 1.1 2006/06/19 20:08:48 gianasista
// CVS Refactoring
//
// Revision 1.6 2006/05/23 19:37:09 gianasista
// Splitted JavaFileFacade into two classes
//
// Revision 1.5 2006/05/15 19:49:09 gianasista
// removed deprecated method call
//
// Revision 1.4 2006/05/12 17:52:02 gianasista
// added comments
//
// Revision 1.3 2006/01/30 21:12:32 gianasista
// Further Refactorings (moved methods from singleton classes like PluginTools
// to facade classes)
//
// Revision 1.2 2006/01/19 21:39:44 gianasista
// Added CVS-commit-logging to all java-files
//
