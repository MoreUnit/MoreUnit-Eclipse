package org.moreunit.decorator;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.ui.IDecoratorManager;
import org.moreunit.MoreUnitPlugin;
import org.moreunit.core.log.Logger;
import org.moreunit.elements.ClassTypeFacade;
import org.moreunit.elements.TypeFacade;
import org.moreunit.images.ImageDescriptorCenter;
import org.moreunit.util.MoreUnitContants;

/**
 * Handles the decoration of java files. If the class has a testcase a overlay
 * icon is added.
 */
// TODO force re-decoration on preferences change (use refreshAll)
public class UnitDecorator extends LabelProvider implements ILightweightLabelDecorator
{
    private final Logger logger;

    public UnitDecorator()
    {
        this(MoreUnitPlugin.getDefault().getLogger());
    }

    public UnitDecorator(Logger logger)
    {
        this.logger = logger;
    }

    public void decorate(Object element, IDecoration decoration)
    {
        StringBuilder logMessage = null;
        if(logger.traceEnabled())
        {
            logMessage = new StringBuilder("UnitDecorator.decorate(").append(element).append(") -> ");
        }

        ICompilationUnit cu = getCompilationUnitIfIsTypeUnderTest(element, logMessage);
        if(cu == null)
        {
            return;
        }

        ClassTypeFacade javaFileFacade = new ClassTypeFacade(cu);
        if(javaFileFacade.hasTestCase())
        {
            if(logger.traceEnabled())
            {
                logger.trace(logMessage.append("has test cases => DECORATED").toString());
            }
            handleClassDecoration(decoration);
        }
        else
        {
            if(logger.traceEnabled())
            {
                logger.trace(logMessage.append("has no test cases").toString());
            }
        }
    }

    private void handleClassDecoration(IDecoration decoration)
    {
        ImageDescriptor imageDescriptor = ImageDescriptorCenter.getTestCaseLabelImageDescriptor();
        decoration.addOverlay(imageDescriptor, IDecoration.TOP_RIGHT);
    }

    /**
     * This method checks the type of the <code>element</code> and tries to get
     * the compilation unit. The method returns null if <code>element</code> is
     * the wrong type or if it is a test case.
     */
    public ICompilationUnit getCompilationUnitIfIsTypeUnderTest(Object element, StringBuilder logMessage)
    {
        IResource objectResource = (IResource) element;
        if(objectResource.getType() != IResource.FILE)
        {
            if(logger.traceEnabled())
            {
                logger.trace(logMessage.append("is a not a file").toString());
            }
            return null;
        }

        IJavaElement javaElement = JavaCore.create(objectResource);
        if(javaElement == null)
        {
            if(logger.traceEnabled())
            {
                logger.trace(logMessage.append("is a not a Java element").toString());
            }
            return null;
        }

        if(javaElement.getElementType() != IJavaElement.COMPILATION_UNIT)
        {
            if(logger.traceEnabled())
            {
                logger.trace(logMessage.append("is a not a compilation unit").toString());
            }
            return null;
        }

        ICompilationUnit compilationUnit = (ICompilationUnit) javaElement;
        // primary type may be null in case of empty .java file or
        // package-info.java, etc...
        if(compilationUnit.findPrimaryType() == null)
        {
            if(logger.traceEnabled())
            {
                logger.trace(logMessage.append("is a compilation unit without type").toString());
            }
            return null;
        }

        if(TypeFacade.isTestCase(compilationUnit.findPrimaryType()))
        {
            if(logger.traceEnabled())
            {
                logger.trace(logMessage.append("is a test case").toString());
            }
            return null;
        }

        if(logger.traceEnabled())
        {
            logger.trace(logMessage.append("is not a test case, ").toString());
        }

        return compilationUnit;
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
