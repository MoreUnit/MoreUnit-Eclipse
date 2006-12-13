/**
 * 
 */
package org.moreunit.elements;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.moreunit.ui.MethodPage;

/**
 * @author vera
 *
 */
public class MethodTreeContentProvider implements ITreeContentProvider {
	
	List<IMethod> methods = new ArrayList<IMethod>();
	
	public MethodTreeContentProvider(IType javaFileFile) {
		resetMethods(javaFileFile);
	}

	private void resetMethods(IType javaFileFile) {
		methods = new ArrayList<IMethod>();
		if(!TypeFacade.isTestCase(javaFileFile))
			try {
				ClassTypeFacade typeFacade = new ClassTypeFacade(javaFileFile.getCompilationUnit());
				IMethod[] allMethods = javaFileFile.getMethods();
				
				for (IMethod method : allMethods) {
					if(typeFacade.getCorrespondingTestMethods(method).size() == 0)
						methods.add(method);
				}
			} catch (JavaModelException e) {
				methods = new ArrayList<IMethod>();
			}
	}

	public Object[] getChildren(Object parentElement) {
		return null;
	}

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
		return false;
	}

	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof MethodPage)
			resetMethods(((MethodPage)inputElement).getInputType());
		
		return methods.toArray();
	}

	public void dispose() {
		methods = null;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
 	}

}
