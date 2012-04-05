package org.moreunit.ui;

import java.util.ArrayList;
import java.util.List;

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
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, true);
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
        List<IJavaProject> javaProjectsFromWorkspace = PluginTools.getJavaProjectsFromWorkspace();
        String[] result = new String[javaProjectsFromWorkspace.size()];

        for (int i = 0; i < javaProjectsFromWorkspace.size(); i++)
        {
            result[i] = javaProjectsFromWorkspace.get(i).getElementName();
        }

        return result;
    }

    @Override
    public void setFocus()
    {
    }

    public void widgetDefaultSelected(SelectionEvent e)
    {
    }

    public void widgetSelected(SelectionEvent e)
    {
        String projectName = ((Combo) e.getSource()).getText();
        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
        selectedJavaProject = JavaCore.create(project);
        treeViewer.refresh();
    }

    public IJavaProject getSelectedJavaProject()
    {
        return selectedJavaProject;
    }

    public void doubleClick(DoubleClickEvent event)
    {
        ITreeSelection selection = (ITreeSelection) this.treeViewer.getSelection();
        ICompilationUnit compilationUnit = (ICompilationUnit) selection.getFirstElement();
        new EditorUI().open(compilationUnit);
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
    }
    
    public void resourceChanged(IResourceChangeEvent event)
    {
        if(event.getType() == IResourceChangeEvent.PRE_DELETE)
        {
            updateProjectsInComboBox();
            return;
        }
        
        if (event.getType() != IResourceChangeEvent.POST_CHANGE)
            return;
        
        if(selectedJavaProject == null)
            return;
        
        IResourceDelta delta = event.getDelta();
        IResourceDelta projectDelta = delta.findMember(selectedJavaProject.getPath());
        if(projectDelta == null)
        {
            checkNewProject(delta);
            return;
        }
        
        final ArrayList<IResource> addedOrRemovedResource = new ArrayList<IResource>();
        IResourceDeltaVisitor visitor = new IResourceDeltaVisitor()
        {
            public boolean visit(IResourceDelta delta) throws CoreException
            {
                if(delta.getKind() == IResourceDelta.ADDED || delta.getKind() == IResourceDelta.REMOVED)
                {
                    if(delta.getResource().getType() == IResource.FILE && "java".equals(delta.getResource().getFileExtension()))
                    {
                        addedOrRemovedResource.add(delta.getResource());
                    }
                }
                return true;
            }
        };
        
        try
        {
            projectDelta.accept(visitor);
        }
        catch (CoreException e)
        {
            e.printStackTrace();
        }
        
        if(!addedOrRemovedResource.isEmpty())
        {
            PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
            {
                
                public void run()
                {
                    treeViewer.refresh();
                }
            });
        }
    }
    
    private void checkNewProject(IResourceDelta delta)
    {
        final ArrayList<IResource> addedProjects = new ArrayList<IResource>();
        IResourceDeltaVisitor visitor = new IResourceDeltaVisitor()
        {
            public boolean visit(IResourceDelta delta) throws CoreException
            {
                if(delta.getKind() == IResourceDelta.ADDED || delta.getKind() == IResourceDelta.REMOVED)
                {
                    if(delta.getResource().getType() == IResource.PROJECT)
                    {
                        addedProjects.add(delta.getResource());
                    }
                }
                return true;
            }
        };
        
        try
        {
            delta.accept(visitor);
        }
        catch (CoreException e)
        {
            e.printStackTrace();
        }
        
        if(!addedProjects.isEmpty())
        {
            updateProjectsInComboBox();
        }
    }

    protected void updateProjectsInComboBox()
    {
        PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
        {
            public void run()
            {
                projectComboBox.setItems(getNamesOfJavaProjects());
            }
        });
        return;
    }
}
