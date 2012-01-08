package org.moreunit.annotation;

import org.moreunit.test.SimpleProjectTestCase;

public abstract class MoreUnitAnnotationModelTest extends SimpleProjectTestCase
{
    /*
    private IType cutType;
    private IType testcaseType;

    @Override
    public void setUp() throws Exception
    {
        super.setUp();

        cutType = WorkspaceHelper.createJavaClass(sourcesPackage, "Hello");
        testcaseType = WorkspaceHelper.createJavaClass(testPackage, "HelloTest");
    }

    @Override
    public void tearDown() throws Exception
    {
        super.tearDown();

        cutType = null;
        testcaseType = null;
    }

    @Test
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

    @Test
    public void testUpdateAnnotationsWithoutTest() throws JavaModelException, PartInitException
    {
        // Method
        WorkspaceHelper.createMethodInJavaType(cutType, "public int getNumberOne()", "return 1");

        MoreUnitAnnotationModel annotationModel = createModelForOpenedTextEditor();
        Iterator result = annotationModel.getAnnotationIterator();
        assertFalse(result.hasNext());
    }

    @Test
    public void testAttach() throws PartInitException, JavaModelException
    {
        ITextEditor openedEditor = (ITextEditor) JavaUI.openInEditor(cutType); // this
                                                                               // will
                                                                               // call
                                                                               // attach
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

    @Test
    public void testDetach() throws PartInitException, JavaModelException
    {
        ITextEditor openedEditor = (ITextEditor) JavaUI.openInEditor(cutType);
        MoreUnitAnnotationModel.detach(openedEditor);
        checkAnnotationModelDetached(openedEditor);
    }

    @Test
    public void testAddAnnotation() throws PartInitException, JavaModelException
    {
        MoreUnitAnnotationModel annotationModel = createModelForOpenedTextEditor();
        try
        {
            annotationModel.addAnnotation(null, null);
            fail();
        }
        catch (Exception exc)
        {
            assertTrue(exc instanceof UnsupportedOperationException);
        }
    }

    @Test
    public void testGetPosition() throws PartInitException, JavaModelException
    {
        MoreUnitAnnotationModel annotationModel = createModelForOpenedTextEditor();
        MoreUnitAnnotation annotation = new MoreUnitAnnotation(5, 6);
        Position result = annotationModel.getPosition(annotation);
        assertEquals(5, result.getOffset());
        assertEquals(6, result.getLength());
    }

    @Test
    public void testRemoveAnnotation() throws PartInitException, JavaModelException
    {
        MoreUnitAnnotationModel annotationModel = createModelForOpenedTextEditor();
        try
        {
            annotationModel.removeAnnotation(null);
            fail();
        }
        catch (Exception exc)
        {
            assertTrue(exc instanceof UnsupportedOperationException);
        }
    }
    */
}
