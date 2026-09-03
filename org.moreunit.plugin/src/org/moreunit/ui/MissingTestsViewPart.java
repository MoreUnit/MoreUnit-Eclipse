package org.moreunit.ui;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.moreunit.elements.MissingClassTreeContentProvider;
import org.moreunit.util.PluginTools;

public class MissingTestsViewPart extends ViewPart implements SelectionListener, IDoubleClickListener, IResourceChangeListener
{
    private TreeViewer treeViewer;
    private IJavaProject selectedJavaProject;
    private Combo projectComboBox;

    @Override
    public void createPartControl(Composite parent)
    {
        final Composite composite = new Composite(parent, SWT.NONE);
        final GridLayout layout = new GridLayout(1, true);
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        projectComboBox = new Combo(composite, SWT.NONE);
        projectComboBox.setItems(getNamesOfJavaProjects());
        projectComboBox.addSelectionListener(this);
        projectComboBox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        treeViewer = new TreeViewer(composite);
        treeViewer.setContentProvider(new MissingClassTreeContentProvider());
        treeViewer.setLabelProvider(new JavaElementLabelProvider());
        treeViewer.setInput(this);
        treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
        treeViewer.addDoubleClickListener(this);

        ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
    }

    private String[] getNamesOfJavaProjects()
    {
        return PluginTools.getJavaProjectsFromWorkspace().stream().map(IJavaProject::getElementName).sorted(String.CASE_INSENSITIVE_ORDER).toArray(String[]::new);
    }

    @Override
    public void setFocus()
    {
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e)
    {
    }

    @Override
    public void widgetSelected(SelectionEvent e)
    {
        final String projectName = ((Combo) e.getSource()).getText();
        final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
        selectedJavaProject = JavaCore.create(project);
        treeViewer.refresh();
        treeViewer.expandAll();
    }

    public IJavaProject getSelectedJavaProject()
    {
        return selectedJavaProject;
    }

    @Override
    public void doubleClick(DoubleClickEvent event)
    {
        final ITreeSelection selection = (ITreeSelection) this.treeViewer.getSelection();
        final Object firstElement = selection.getFirstElement();
        if(firstElement instanceof final ICompilationUnit compilationUnit)
        {
            new EditorUI().open(compilationUnit);
        }
        else
        {
            final PackageExplorerPart part = PackageExplorerPart.getFromActivePerspective();
            part.selectAndReveal(firstElement);
            part.setFocus();
        }
    }

    @Override
    public void dispose()
    {
        super.dispose();
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
    }

    @Override
    public void resourceChanged(IResourceChangeEvent event)
    {
        if(event.getType() == IResourceChangeEvent.PRE_DELETE)
        {
            updateProjectsInComboBox();
            return;
        }

        if((event.getType() != IResourceChangeEvent.POST_CHANGE) || (selectedJavaProject == null))
            return;

        final IResourceDelta delta = event.getDelta();
        final IResourceDelta projectDelta = delta.findMember(selectedJavaProject.getPath());
        if(projectDelta == null)
        {
            checkNewProject(delta);
            return;
        }

        final ArrayList<IResource> addedOrRemovedResource = new ArrayList<>();
        final IResourceDeltaVisitor visitor = delta1 -> {
            if(delta1.getKind() == IResourceDelta.ADDED || delta1.getKind() == IResourceDelta.REMOVED)
            {
                if(delta1.getResource().getType() == IResource.FILE && "java".equals(delta1.getResource().getFileExtension()))
                {
                    addedOrRemovedResource.add(delta1.getResource());
                }
            }
            return true;
        };

        try
        {
            projectDelta.accept(visitor);
        }
        catch (final CoreException e)
        {
            e.printStackTrace();
        }

        if(! addedOrRemovedResource.isEmpty())
        {
            PlatformUI.getWorkbench().getDisplay().asyncExec(() -> treeViewer.refresh());
        }
    }

    private void checkNewProject(IResourceDelta delta)
    {
        final ArrayList<IResource> addedProjects = new ArrayList<>();
        final IResourceDeltaVisitor visitor = delta1 -> {
            if(delta1.getKind() == IResourceDelta.ADDED || delta1.getKind() == IResourceDelta.REMOVED)
            {
                if(delta1.getResource().getType() == IResource.PROJECT)
                {
                    addedProjects.add(delta1.getResource());
                }
            }
            return true;
        };

        try
        {
            delta.accept(visitor);
        }
        catch (final CoreException e)
        {
            e.printStackTrace();
        }

        if(! addedProjects.isEmpty())
        {
            updateProjectsInComboBox();
        }
    }

    protected void updateProjectsInComboBox()
    {
        PlatformUI.getWorkbench().getDisplay().asyncExec(() -> projectComboBox.setItems(getNamesOfJavaProjects()));
        return;
    }
}
