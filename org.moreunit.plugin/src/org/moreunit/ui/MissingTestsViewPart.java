package org.moreunit.ui;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.moreunit.elements.MissingClassTreeContentProvider;
import org.moreunit.util.PluginTools;

public class MissingTestsViewPart extends ViewPart implements SelectionListener
{

    private TreeViewer treeViewer;
    private IJavaProject selectedJavaProject;

    @Override
    public void createPartControl(Composite parent)
    {
        Combo combo = new Combo(parent, SWT.NONE);
        combo.setItems(getNamesOfJavaProjects());
        combo.addSelectionListener(this);

        treeViewer = new TreeViewer(parent);
        treeViewer.setContentProvider(new MissingClassTreeContentProvider());
        treeViewer.setLabelProvider(new JavaElementLabelProvider());
        treeViewer.setInput(this);
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
}
