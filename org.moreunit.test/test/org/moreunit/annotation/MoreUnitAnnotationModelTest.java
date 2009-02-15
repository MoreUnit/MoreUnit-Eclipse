package org.moreunit.annotation;

import java.util.Iterator;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.moreunit.SimpleProjectTestCase;
import org.moreunit.WorkspaceHelper;

public class MoreUnitAnnotationModelTest extends SimpleProjectTestCase
{
	private IType cutType;
	private IType testcaseType;
	
	@Override
	protected void setUp() throws Exception 
	{
		super.setUp();
		
		cutType = WorkspaceHelper.createJavaClass(sourcesPackage, "Hello");
		testcaseType = WorkspaceHelper.createJavaClass(testPackage, "HelloTest");
	}
	
	@Override
	protected void tearDown() throws Exception 
	{
		super.tearDown();
		
		cutType = null;
		testcaseType = null;
	}
	
	public void testUpdateAnnotationsMethodWithTest() throws JavaModelException, PartInitException 
	{
		// Method
		WorkspaceHelper.createMethodInJavaType(cutType, "public int getNumberOne()", "return 1");
		// Corresponding testmethod
		WorkspaceHelper.createMethodInJavaType(testcaseType, "public void testGetNumberOne()", "");
		
		MoreUnitAnnotationModel annotationModel = createModelForOpenedTextEditor();
		
		Iterator result = annotationModel.getAnnotationIterator();
		assertTrue(result.hasNext());
		assertTrue(result.next() instanceof MoreUnitAnnotation);
	}
	
	private MoreUnitAnnotationModel createModelForOpenedTextEditor() throws PartInitException, JavaModelException 
	{
		ITextEditor openedEditor = (ITextEditor) JavaUI.openInEditor(cutType);
		
		IDocumentProvider provider = openedEditor.getDocumentProvider();
		IDocument document = provider.getDocument(openedEditor.getEditorInput());
		
		return new MoreUnitAnnotationModel(document, openedEditor);
	}

	public void testUpdateAnnotationsWithoutTest() throws JavaModelException, PartInitException 
	{
		// Method
		WorkspaceHelper.createMethodInJavaType(cutType, "public int getNumberOne()", "return 1");
		
		MoreUnitAnnotationModel annotationModel = createModelForOpenedTextEditor();
		Iterator result = annotationModel.getAnnotationIterator();
		assertFalse(result.hasNext());
	}

	public void testAttach() throws PartInitException, JavaModelException 
	{
		ITextEditor openedEditor = (ITextEditor) JavaUI.openInEditor(cutType); // this will call attach
		checkAnnotationModelAttached(openedEditor);
	}
	
	private void checkAnnotationModelAttached(ITextEditor textEditor)
	{
		MoreUnitAnnotationModel annotationModel = getAnnotationModelFromTextEditor(textEditor);
		assertNotNull(annotationModel);
	}
	
	private void checkAnnotationModelDetached(ITextEditor textEditor)
	{
		MoreUnitAnnotationModel annotationModel = getAnnotationModelFromTextEditor(textEditor);
		assertNull(annotationModel);
	}
	
	private MoreUnitAnnotationModel getAnnotationModelFromTextEditor(ITextEditor textEditor)
	{
		IDocumentProvider provider = textEditor.getDocumentProvider();
		IAnnotationModelExtension modelExtension = (IAnnotationModelExtension) provider.getAnnotationModel(textEditor.getEditorInput());
		return (MoreUnitAnnotationModel) modelExtension.getAnnotationModel("org.moreunit.model_key");
	}

	public void testDetach() throws PartInitException, JavaModelException 
	{
		ITextEditor openedEditor = (ITextEditor) JavaUI.openInEditor(cutType);
		MoreUnitAnnotationModel.detach(openedEditor);
		checkAnnotationModelDetached(openedEditor);
	}

	public void testAddAnnotation() throws PartInitException, JavaModelException 
	{
		MoreUnitAnnotationModel annotationModel = createModelForOpenedTextEditor();
		try 
		{
			annotationModel.addAnnotation(null, null);
			fail();
		} catch (Exception exc) 
		{
			assertTrue(exc instanceof UnsupportedOperationException);
		}
	}

	public void testGetPosition() throws PartInitException, JavaModelException 
	{
		MoreUnitAnnotationModel annotationModel = createModelForOpenedTextEditor();
		MoreUnitAnnotation annotation = new MoreUnitAnnotation(5,6);
		Position result = annotationModel.getPosition(annotation);
		assertEquals(5, result.getOffset());
		assertEquals(6, result.getLength());
	}

	public void testRemoveAnnotation() throws PartInitException, JavaModelException 
	{
		MoreUnitAnnotationModel annotationModel = createModelForOpenedTextEditor();
		try 
		{
			annotationModel.removeAnnotation(null);
			fail();
		} catch (Exception exc) 
		{
			assertTrue(exc instanceof UnsupportedOperationException);
		}
	}
}
