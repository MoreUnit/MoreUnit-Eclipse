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
import org.moreunit.preferences.Preferences;
import org.moreunit.preferences.Preferences.MethodSearchMode;
import org.moreunit.ui.MethodPage;

/**
 * @author vera
 */
public class MethodTreeContentProvider implements ITreeContentProvider
{
    IType classType;
    List<IMethod> methods = new ArrayList<IMethod>();
    private boolean isPrivateFiltered = false;
    private boolean isGetterFiltered = false;

    public MethodTreeContentProvider(IType javaFileFile)
    {
        this.classType = javaFileFile;
        resetMethods(javaFileFile);
    }

    private void resetMethods(IType javaFileFile)
    {
        methods = new ArrayList<IMethod>();
        if(javaFileFile == null || TypeFacade.isTestCase(javaFileFile))
        {
            return;
        }

        MethodSearchMode searchMode = Preferences.forProject(javaFileFile.getJavaProject()).getMethodSearchMode();
        try
        {
            ClassTypeFacade typeFacade = new ClassTypeFacade(javaFileFile.getCompilationUnit());
            IMethod[] allMethods = javaFileFile.getMethods();

            for (IMethod method : allMethods)
            {
                if(typeFacade.getCorrespondingTestMethods(method, searchMode).size() == 0)
                    methods.add(method);
            }
        }
        catch (JavaModelException e)
        {
            methods = new ArrayList<IMethod>();
        }
    }

    public Object[] getChildren(Object parentElement)
    {
        return null;
    }

    public Object getParent(Object element)
    {
        return null;
    }

    public boolean hasChildren(Object element)
    {
        return false;
    }

    public Object[] getElements(Object inputElement)
    {
        if(inputElement instanceof MethodPage)
            resetMethods(((MethodPage) inputElement).getInputType());

        List<IMethod> resultMethodList = new ArrayList<IMethod>();
        if(isPrivateFiltered)
            resultMethodList.addAll(filterPrivateMethods(methods));
        else
            resultMethodList.addAll(methods);

        if(isGetterFiltered)
        {
            return filterGetterAndSetter(resultMethodList).toArray();
        }

        return resultMethodList.toArray();
    }

    public void dispose()
    {
        methods = null;
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
    }

    private List<IMethod> filterPrivateMethods(List<IMethod> methodList)
    {
        List<IMethod> resultList = new ArrayList<IMethod>();
        FilterMethodVisitor privateMethodVisitor = new FilterMethodVisitor(classType);

        for (IMethod method : methodList)
        {
            if(! privateMethodVisitor.isPrivateMethod(method))
                resultList.add(method);
        }

        return resultList;
    }

    private List<IMethod> filterGetterAndSetter(List<IMethod> methodList)
    {
        List<IMethod> resultList = new ArrayList<IMethod>();
        FilterMethodVisitor getterMethodVisitor = new FilterMethodVisitor(classType);

        for (IMethod method : methodList)
        {
            if(! getterMethodVisitor.isGetterMethod(method) && ! getterMethodVisitor.isSetterMethod(method))
                resultList.add(method);
        }

        return resultList;
    }

    public void setPrivateFiltered(boolean isPrivateFiltered)
    {
        this.isPrivateFiltered = isPrivateFiltered;
    }

    public void setGetterFiltered(boolean isGetterFiltered)
    {
        this.isGetterFiltered = isGetterFiltered;
    }

}
