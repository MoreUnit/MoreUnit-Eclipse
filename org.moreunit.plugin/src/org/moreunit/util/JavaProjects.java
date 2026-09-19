package org.moreunit.util;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;

/**
 * Extracts the Java projects referenced by a selection or by an editor.
 */
public final class JavaProjects
{
    private JavaProjects()
    {
    }

    public static Collection<IJavaProject> of(ISelection selection)
    {
        if(! (selection instanceof IStructuredSelection structuredSelection))
        {
            return Collections.emptyList();
        }

        final Collection<IJavaProject> projects = new LinkedHashSet<>();
        for (final Object element : structuredSelection.toList())
        {
            final IJavaProject project = toJavaProject(element);
            if(project != null)
            {
                projects.add(project);
            }
        }
        return projects;
    }

    public static Collection<IJavaProject> of(IEditorPart editor)
    {
        if(editor == null)
        {
            return Collections.emptyList();
        }
        final IFile file = editor.getEditorInput().getAdapter(IFile.class);
        if(file == null)
        {
            return Collections.emptyList();
        }
        final IJavaProject project = toJavaProject(file.getProject());
        return project == null ? Collections.emptyList() : Collections.singleton(project);
    }

    private static IJavaProject toJavaProject(Object element)
    {
        if(element instanceof IJavaProject javaProject)
        {
            return existing(javaProject);
        }
        if(element instanceof IJavaElement javaElement)
        {
            return existing(javaElement.getJavaProject());
        }
        if(element instanceof IProject project)
        {
            return existing(JavaCore.create(project));
        }
        if(element instanceof IResource resource)
        {
            return existing(JavaCore.create(resource.getProject()));
        }
        if(element == null)
        {
            return null;
        }
        final IProject adaptedProject = Adapters.adapt(element, IProject.class);
        return adaptedProject == null ? null : existing(JavaCore.create(adaptedProject));
    }

    private static IJavaProject existing(IJavaProject project)
    {
        return project != null && project.exists() ? project : null;
    }
}
