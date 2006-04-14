package moreUnit.elements;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author vera
 * 29.03.2006 21:35:05
 */
public class MethodContentProvider implements IStructuredContentProvider {
	
	List methods;
	
	public MethodContentProvider(List methods) {
		this.methods = methods;
	}

	public Object[] getElements(Object inputElement) {
		return methods.toArray();
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}


// $Log: not supported by cvs2svn $