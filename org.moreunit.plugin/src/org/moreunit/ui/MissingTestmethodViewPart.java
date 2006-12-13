package org.moreunit.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;
import org.moreunit.elements.EditorPartFacade;

/**
 * @author vera
 */
public class MissingTestmethodViewPart extends PageBookView {
	
	MethodPage activePage;
	
	public MissingTestmethodViewPart() {
		super();
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
	}

	@Override
	public void setFocus() {
	}

	@Override
	protected IPage createDefaultPage(PageBook book) {
		MessagePage page = new MessagePage();
		initPage(page);
		page.createControl(book);
		page.setMessage("kein Outline");
		return page;
	}

	@Override
	protected PageRec doCreatePage(IWorkbenchPart part) {
		activePage = new MethodPage(new EditorPartFacade((IEditorPart) part));
		initPage((IPageBookViewPage)activePage);
		activePage.createControl(getPageBook());
		return new PageRec(part, activePage);
	}

	@Override
	protected void doDestroyPage(IWorkbenchPart part, PageRec pageRecord) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected IWorkbenchPart getBootstrapPart() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isImportant(IWorkbenchPart part) {
		 return (part instanceof IEditorPart);
	}
	
	@Override
	public void partOpened(IWorkbenchPart part) {
		super.partOpened(part);
	}
	
	@Override
	public void partActivated(IWorkbenchPart part) {
		super.partActivated(part);
	}
	
	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
		//super.partBroughtToTop(part);
		partActivated(part);
		activePage.updateUI();
	}

}
