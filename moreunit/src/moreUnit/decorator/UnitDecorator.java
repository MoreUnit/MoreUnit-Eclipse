package moreUnit.decorator;

import moreUnit.MoreUnitPlugin;
import moreUnit.images.ImageDescriptorCenter;
import moreUnit.util.MagicNumbers;
import moreUnit.util.PluginTools;

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
 * @author vera
 * 01.11.2005
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
			
			IType type = PluginTools.getTestKlasseVomKlassenNamen(javaTypeOfResource);
			if(type != null) {
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