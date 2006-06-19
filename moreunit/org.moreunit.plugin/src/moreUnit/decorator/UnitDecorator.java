package moreUnit.decorator;

import java.util.Set;

import moreUnit.MoreUnitPlugin;
import moreUnit.elements.ClassTypeFacade;
import moreUnit.elements.TypeFacade;
import moreUnit.images.ImageDescriptorCenter;
import moreUnit.util.MagicNumbers;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.ui.IDecoratorManager;

/**
 * Handles the decoration of java files.
 * If the class has a testcase a overlay icon is added. 
 */
public class UnitDecorator extends LabelProvider implements ILightweightLabelDecorator {
	
	public void decorate(Object element, IDecoration decoration) {
		IResource objectResource = (IResource) element;
		
		if(objectResource.getType() != IResource.FILE)
			return;
		
		try {
			ICompilationUnit javaTypeOfResource = (ICompilationUnit) JavaCore.create(objectResource);
			
			if(javaTypeOfResource == null)
				return ;
			
			if(TypeFacade.isTestCase(javaTypeOfResource.findPrimaryType()))
				return;
			
			ClassTypeFacade javaFileFacade = new ClassTypeFacade(javaTypeOfResource);
			Set<IType> correspondingTestcases = javaFileFacade.getCorrespondingTestCaseList();
			if(correspondingTestcases != null && correspondingTestcases.size() > 0) {
				ImageDescriptor imageDescriptor = ImageDescriptorCenter.getTestCaseLabelImageDescriptor();
				decoration.addOverlay(imageDescriptor, IDecoration.TOP_RIGHT);
			}
		} catch(ClassCastException exc) {}
	}
	
	public static UnitDecorator getUnitDecorator() {
		IDecoratorManager decoratorManager = MoreUnitPlugin.getDefault().getWorkbench().getDecoratorManager();
		
		if(decoratorManager.getEnabled(MagicNumbers.TEST_CASE_DECORATOR))
			return (UnitDecorator) decoratorManager.getBaseLabelProvider(MagicNumbers.TEST_CASE_DECORATOR);
		else
			return null;
	}
	
	public void refreshAll() {
		UnitDecorator unitDecorator = getUnitDecorator();
		
		if(unitDecorator != null)
			unitDecorator.fireLabelProviderChanged(new LabelProviderChangedEvent(unitDecorator));
	}
}

// $Log: not supported by cvs2svn $
// Revision 1.6  2006/05/23 19:37:09  gianasista
// Splitted JavaFileFacade into two classes
//
// Revision 1.5  2006/05/15 19:49:09  gianasista
// removed deprecated method call
//
// Revision 1.4  2006/05/12 17:52:02  gianasista
// added comments
//
// Revision 1.3  2006/01/30 21:12:32  gianasista
// Further Refactorings (moved methods from singleton classes like PluginTools to facade classes)
//
// Revision 1.2  2006/01/19 21:39:44  gianasista
// Added CVS-commit-logging to all java-files
//