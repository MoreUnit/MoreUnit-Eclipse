package org.moreunit.wizards;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.junit.util.JUnitStubUtility;
import org.eclipse.jdt.internal.junit.util.JUnitStubUtility.GenStubSettings;
import org.eclipse.jdt.junit.wizards.NewTestCaseWizardPageOne;
import org.eclipse.jdt.junit.wizards.NewTestCaseWizardPageTwo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.moreunit.log.LogHandler;
import org.moreunit.preferences.Preferences;

/**
 * @author vera
 *
 * 24.08.2007 20:14:28
 */
public class MoreUnitNewTestCaseWizardPageOne extends NewTestCaseWizardPageOne {

	private Button junti3Toggle;
	private Button unit4Toggle;
	private Button testNgToggle;
	
	private Preferences preferences;
	private IJavaProject javaProject;
	
	public MoreUnitNewTestCaseWizardPageOne(NewTestCaseWizardPageTwo page2, Preferences preferences, IJavaProject javaProject) {
		super(page2);
		this.preferences = preferences;
		this.javaProject = javaProject;
	}
	
	protected void createJUnit4Controls(Composite composite, int nColumns) {
		Composite inner= new Composite(composite, SWT.NONE);
		inner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, nColumns, 1));
		GridLayout layout= new GridLayout(3, false);
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		inner.setLayout(layout);
		
		SelectionAdapter listener= new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				testTypeSelectionChanged();
			}
		};
		
		junti3Toggle = new Button(inner, SWT.RADIO);
		junti3Toggle.setText("JUnit 3");
		junti3Toggle.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 1, 1));
		junti3Toggle.setSelection(preferences.shouldUseJunit3Type(javaProject));
		junti3Toggle.setEnabled(true);
		
		unit4Toggle = new Button(inner, SWT.RADIO);
		unit4Toggle.setText("JUnit 4");
		unit4Toggle.setSelection(preferences.shouldUseJunit4Type(javaProject));
		unit4Toggle.setEnabled(true);
		unit4Toggle.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 1, 1));
		unit4Toggle.addSelectionListener(listener);
		
		testNgToggle = new Button(inner, SWT.RADIO);
		testNgToggle.setText("TestNG");
		testNgToggle.setSelection(preferences.shouldUseTestNgType(javaProject));
		testNgToggle.setEnabled(true);
		testNgToggle.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 1, 1));
		testNgToggle.addSelectionListener(listener);
	}
	
	private void testTypeSelectionChanged() {
		if(junti3Toggle.getSelection()) {
			setJUnit4(false, true);
		} else if(unit4Toggle.getSelection()) {
			setJUnit4(true, true);
			setSuperClass(preferences.getTestSuperClass(javaProject), true);
		} else if(testNgToggle.getSelection()) {
			setJUnit4(false, false);
			setSuperClass(preferences.getTestSuperClass(javaProject), true);
			handleFieldChanged(JUNIT4TOGGLE);
		}
	}
	
	protected IStatus[] getStatusList() {
		if(isTestNgSelected()) {
			IStatus[] superList = super.getStatusList();
			IStatus[] newList = new IStatus[superList.length-1];
			for(int i=0; i<newList.length; i++)
				newList[i] = superList[i];
			
			return newList;
		}
		else
			return super.getStatusList();
	}

	private boolean isTestNgSelected() {
		return testNgToggle != null && testNgToggle.getSelection();
	}
	
	@Override
	protected IStatus superClassChanged() {
		if(isTestNgSelected())
			return new MoreUnitStatus();
		
		return super.superClassChanged();
	}
	
	@Override
	public void createType(IProgressMonitor monitor) throws CoreException, InterruptedException {
		super.createType(monitor);
		addTestNgImportIfNecessary();
	}
	
	/*
	@Override
	protected void createTypeMembers(IType type, ImportsManager imports, IProgressMonitor monitor) throws CoreException {
		if (fMethodStubsButtons.isSelected(IDX_CONSTRUCTOR))
			createConstructor(type, imports); 	
		
		if (fMethodStubsButtons.isSelected(IDX_SETUP_CLASS)) {
			createSetUpClass(type, imports);
		}
		
		if (fMethodStubsButtons.isSelected(IDX_TEARDOWN_CLASS)) {
			createTearDownClass(type, imports);
		}
		
		if (fMethodStubsButtons.isSelected(IDX_SETUP)) {
			createSetUp(type, imports);
		}
		
		if (fMethodStubsButtons.isSelected(IDX_TEARDOWN)) {
			createTearDown(type, imports);
		}

		if (fClassUnderTest != null) {
			createTestMethodStubs(type, imports);
		}
		
		if (isJUnit4()) { 
			imports.addStaticImport("org.junit.Assert", "*", false); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	*/
	
	/*
	private void createConstructor(IType type, ImportsManager imports) throws CoreException {
		ITypeHierarchy typeHierarchy= null;
		IType[] superTypes= null;
		String content;
		IMethod methodTemplate= null;
		if (type.exists()) {
			typeHierarchy= type.newSupertypeHierarchy(null);
			superTypes= typeHierarchy.getAllSuperclasses(type);
			for (int i= 0; i < superTypes.length; i++) {
				if (superTypes[i].exists()) {
					IMethod constrMethod= superTypes[i].getMethod(superTypes[i].getElementName(), new String[] {"Ljava.lang.String;"}); //$NON-NLS-1$
					if (constrMethod.exists() && constrMethod.isConstructor()) {
						methodTemplate= constrMethod;
						break;
					}
				}
			}
		}
		GenStubSettings settings= JUnitStubUtility.getCodeGenerationSettings(type.getJavaProject());
		settings.createComments= isAddComments();
		
		if (methodTemplate != null) {
			settings.callSuper= true;				
			settings.methodOverwrites= true;
			content= JUnitStubUtility.genStub(type.getCompilationUnit(), getTypeName(), methodTemplate, settings, null, imports);
		} else {
			final String delimiter= getLineDelimiter();
			StringBuffer buffer= new StringBuffer(32);
			buffer.append("public "); //$NON-NLS-1$
			buffer.append(getTypeName());
			buffer.append('(');
			if (!isJUnit4()) {
				buffer.append(imports.addImport("java.lang.String")).append(" name"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			buffer.append(") {"); //$NON-NLS-1$
			buffer.append(delimiter);
			if (!isJUnit4()) {
				buffer.append("super(name);").append(delimiter); //$NON-NLS-1$
			}
			buffer.append('}');
			buffer.append(delimiter);
			content= buffer.toString();
		}
		type.createMethod(content, null, true, null);	
	}
	*/
	
	/*
	private String getLineDelimiter() throws JavaModelException{
		IType classToTest= getClassUnderTest();
		
		if (classToTest != null && classToTest.exists() && classToTest.getCompilationUnit() != null)
			return classToTest.getCompilationUnit().findRecommendedLineSeparator();
		
		return getPackageFragment().findRecommendedLineSeparator();
	}
	*/
	
	private void addTestNgImportIfNecessary() {
		if(isTestNgSelected())
			try {
				getCreatedType().getCompilationUnit().createImport("org.testng.annotations.*", null, null);
			} catch (JavaModelException e) {
				LogHandler.getInstance().handleExceptionLog(e);
			}
	}
}
